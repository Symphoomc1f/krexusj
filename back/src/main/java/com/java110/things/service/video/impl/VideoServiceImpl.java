package com.java110.things.service.video.impl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.sip.Device;
import com.java110.things.entity.sip.DeviceChannel;
import com.java110.things.entity.sip.PushStreamDevice;
import com.java110.things.service.video.IVideoService;
import com.java110.things.sip.Server;
import com.java110.things.sip.SipLayer;
import com.java110.things.sip.TCPServer;
import com.java110.things.sip.UDPServer;
import com.java110.things.sip.callback.OnProcessListener;
import com.java110.things.sip.message.result.MediaData;
import com.java110.things.sip.message.session.MessageManager;
import com.java110.things.sip.message.session.SyncFuture;
import com.java110.things.sip.play.Play;
import com.java110.things.sip.remux.Observer;
import com.java110.things.sip.remux.RtmpPusher;
import com.java110.things.sip.session.PushStreamDeviceManager;
import com.java110.things.util.IDUtils;
import com.java110.things.util.RedisUtil;
import com.java110.things.util.StreamNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sip.Dialog;
import javax.sip.SipException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("videoServiceImpl")
public class VideoServiceImpl implements IVideoService, OnProcessListener {
    private static Logger logger = LoggerFactory.getLogger(VideoServiceImpl.class);

    @Autowired
    private SipLayer mSipLayer;

    private MessageManager mMessageManager = MessageManager.getInstance();

    private PushStreamDeviceManager mPushStreamDeviceManager = PushStreamDeviceManager.getInstance();

    //String pushRtmpAddress
    public ResponseEntity<String> doPlay(String deviceId,
                                         String channelId,
                                         String mediaProtocol) {
        ResponseEntity<String> result = null;
        try {
            //1.???redis?????????????????????????????????????????????
            String deviceStr = RedisUtil.get(deviceId);
            if (StringUtils.isEmpty(deviceStr)) {
                throw new IllegalArgumentException("????????????");
            }
            //2.??????????????????????????????????????????
            //?????????????????????????????????rtmp??????
            String streamName = StreamNameUtils.play(deviceId, channelId);
            PushStreamDevice pushStreamDevice = mPushStreamDeviceManager.get(streamName);
            if (pushStreamDevice != null) {
                return ResultDto.success(new MediaData(pushStreamDevice.getPullRtmpAddress(), pushStreamDevice.getCallId()));
            }
            //????????????????????????
            Device device = JSONObject.parseObject(deviceStr, Device.class);
            Map<String, DeviceChannel> channelMap = device.getChannelMap();
            if (channelMap == null || !channelMap.containsKey(channelId)) {
                throw new IllegalArgumentException("???????????????");
            }
            boolean isTcp = mediaProtocol.toUpperCase().equals(SipLayer.TCP);
            //3.????????????
            String callId = IDUtils.id();

            //getPort?????????????????????????????????
            int port = mSipLayer.getPort(isTcp);
            String ssrc = mSipLayer.getSsrc(true);
            /*********************??????*******************************/
            String address = streamName;//pushRtmpAddress.concat(streamName);
                Server server = isTcp ? new TCPServer() : new UDPServer();
                Observer observer = new RtmpPusher(address, callId);

                server.subscribe(observer);
                pushStreamDevice = new PushStreamDevice(deviceId, Integer.valueOf(ssrc), callId, streamName, port, isTcp, server,
                        observer, address);


                server.startServer(pushStreamDevice.getFrameDeque(), Integer.valueOf(ssrc), port, false);
                observer.startRemux();

                observer.setOnProcessListener(this);
                mPushStreamDeviceManager.put(streamName, callId, Integer.valueOf(ssrc), pushStreamDevice);
            /*****************************************************/


            mSipLayer.sendInvite(device, SipLayer.SESSION_NAME_PLAY, callId, channelId, port, ssrc, isTcp);
            //4.??????????????????
            SyncFuture<?> receive = mMessageManager.receive(callId);
            Dialog response = (Dialog) receive.get(3, TimeUnit.SECONDS);

            //4.1???????????????????????????session
            if (response != null) {
//                String address = streamName;//pushRtmpAddress.concat(streamName);
//                Server server = isTcp ? new TCPServer() : new UDPServer();
//                Observer observer = new RtmpPusher(address, callId);
//
//                server.subscribe(observer);
//                pushStreamDevice = new PushStreamDevice(deviceId, Integer.valueOf(ssrc), callId, streamName, port, isTcp, server,
//                        observer, address);
//
//                pushStreamDevice.setDialog(response);
//                server.startServer(pushStreamDevice.getFrameDeque(), Integer.valueOf(ssrc), port, false);
//                observer.startRemux();
//
//                observer.setOnProcessListener(this);
//                mPushStreamDeviceManager.put(streamName, callId, Integer.valueOf(ssrc), pushStreamDevice);
                pushStreamDevice.setDialog(response);
                result = ResultDto.success(new MediaData(pushStreamDevice.getPullRtmpAddress(), pushStreamDevice.getCallId()));
            } else {
                //3.2???????????????????????????session
                mMessageManager.remove(callId);
                throw new IllegalArgumentException("????????? ???????????????");
            }

        } catch (Exception e) {
            logger.error("????????????", e);
            throw new IllegalArgumentException("????????????");
        }
        return result;
    }

    public ResponseEntity<String> bye(String callId) {
        try {
            mSipLayer.sendBye(callId);
        } catch (SipException e) {
            e.printStackTrace();
        }
        return ResultDto.success();
    }

    @Override
    public void onError(String callId) {
        try {
            mSipLayer.sendBye(callId);
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

}
