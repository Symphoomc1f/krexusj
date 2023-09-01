package com.java110.things.sip.handler;


import com.java110.things.sip.callback.OnChannelStatusListener;
import com.java110.things.sip.codec.Frame;
import com.java110.things.util.BitUtils;
import org.bytedeco.ffmpeg.global.avcodec;

import com.java110.things.sip.codec.Parser;
import com.java110.things.util.HexStringUtils;
import com.java110.things.ws.VideoWebSocketServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 接受TCP音视频媒体流
 *
 * @author yangjie
 * 2020年3月13日
 */
public class TCPHandler extends ChannelInboundHandlerAdapter {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ConcurrentLinkedDeque<Frame> mFrameDeque = null;

    private boolean mIsCheckSsrc = false;

    private int mSsrc;

    /**
     * 第一帧是否为I帧
     * 不为I帧，先丢弃
     */
    private boolean mIsFirstI;

    private OnChannelStatusListener onChannelStatusListener;

    private Parser mParser;


    public void setOnChannelStatusListener(OnChannelStatusListener onChannelStatusListener) {
        this.onChannelStatusListener = onChannelStatusListener;
    }

    public TCPHandler(ConcurrentLinkedDeque<Frame> frameDeque, int ssrc, boolean checkSsrc,
                      Parser parser) {
        this.mFrameDeque = frameDeque;
        this.mSsrc = ssrc;
        this.mParser = parser;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (mFrameDeque == null) {
                log.error("frame deque can not null");
                return;
            }

            ByteBuf byteBuf = (ByteBuf) msg;
            int readableBytes = byteBuf.readableBytes();
            if (readableBytes <= 0) {
                return;
            }
            byte[] copyData = new byte[readableBytes];
            byteBuf.readBytes(copyData);
            //log.error("TCPHandler channelRead >>> {}", HexStringUtils.toHexString(copyData));
            log.error("开始推流");

         //   doDealData(copyData);


            //log.error("TCPHandler channelRead >>> {}",HexStringUtils.toHexString(copyData));
            if (mIsCheckSsrc) {
                int uploadSsrc = BitUtils.byte4ToInt(copyData[10], copyData[11], copyData[12], copyData[13]);
                if (uploadSsrc != mSsrc) {
                    return;
                }
            }
            int seq = BitUtils.byte2ToInt(copyData[4], copyData[5]);
            Frame frame;
            //有ps头,判断是否是i帧或者p帧
            if (readableBytes > 18 && copyData[14] == 0 && copyData[15] == 0 && copyData[16] == 01 && (copyData[17] & 0xff) == 0xba) {
                //pack_stuffing_length
                int stuffingLength = copyData[27] & 7;
                int startIndex = 27 + stuffingLength + 1;

                //有ps系统头为i帧
                if (copyData[startIndex] == 0 && copyData[startIndex + 1] == 0 && copyData[startIndex + 2] == 01 && (copyData[startIndex + 3] & 0xff) == 0xbb) {
                    frame = new Frame(Frame.I);
                    mIsFirstI = true;
                }
                //p帧
                else {
                    if (!mIsFirstI) {
                        return;
                    }
                    frame = new Frame(Frame.P);
                }
                frame.addPacket(seq, copyData);
                frame.setFirstSeq(seq);
                mFrameDeque.add(frame);
            }
            //音频数据
            else if (readableBytes > 18 && copyData[14] == 0 && copyData[15] == 0 && copyData[16] == 01 && (copyData[17] & 0xff) == 0xc0) {
                if (!mIsFirstI) {
                    return;
                }
                frame = new Frame(Frame.AUDIO);
                frame.addPacket(seq, copyData);
                frame.setFirstSeq(seq);
                mFrameDeque.add(frame);
            }
            //分包数据
            else {
                if (mFrameDeque.size() > 0 && mIsFirstI) {
                    frame = mFrameDeque.getLast();
                    if (frame != null) {
                        frame.addPacket(seq, copyData);
                        frame.setEndSeq(seq);
                    }

                    if (mFrameDeque.size() > 1) {
                        Frame pop = mFrameDeque.pop();
                        log.error("解析tcp");
                        mParser.parseTcp(pop);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("TCPHandler 异常 >>> ", e);
        } finally {
            release(msg);
        }


    }

    private void doDealData(byte[] copyData) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(copyData);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputStream);
        // 设置打开协议tcp / udp
        frameGrabber.setOption("rtsp_transport", "tcp");
        // 首选TCP进行RTP传输
        frameGrabber.setOption("rtsp_flags", "prefer_tcp");
        frameGrabber.start();

        FFmpegFrameRecorder recorder = null;
        org.bytedeco.javacv.Frame captured_frame = null;
        recorder = new FFmpegFrameRecorder(outputStream, frameGrabber.getImageWidth(), frameGrabber.getImageHeight(),
                frameGrabber.getAudioChannels());
// 转码
        recorder.setFormat("flv");
        recorder.setInterleaved(false);
        recorder.setVideoOption("tune", "zerolatency");
        recorder.setVideoOption("preset", "ultrafast");
        recorder.setVideoOption("crf", "26");
        recorder.setVideoOption("threads", "1");
        recorder.setFrameRate(25);// 设置帧率
        recorder.setGopSize(25);// 设置gop,与帧率相同，相当于间隔1秒chan's一个关键帧
//			recorder.setVideoBitrate(500 * 1000);// 码率500kb/s
//			recorder.setVideoCodecName("libx264");	//javacv 1.5.5无法使用libx264名称，请使用下面方法
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
//			recorder.setAudioCodecName("aac");
        /**
         * 启用RDOQ算法，优化视频质量 1：在视频码率和视频质量之间取得平衡 2：最大程度优化视频质量（会降低编码速度和提高码率）
         */
        recorder.setTrellis(1);
        recorder.setMaxDelay(0);// 设置延迟
        recorder.start();
        while ((captured_frame = frameGrabber.grabFrame()) != null) {
            try {
                recorder.record(captured_frame);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(outputStream.toByteArray());
        VideoWebSocketServer.sendInfo(byteBuffer, "34020000001320000001");
        recorder.stop();
        recorder.release();
        frameGrabber.stop();
        inputStream.close();
        outputStream.close();
    }

    private void release(Object msg) {
        try {
            ReferenceCountUtil.release(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TCP建立连接
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if (onChannelStatusListener != null) {
            onChannelStatusListener.onConnect();
        }
    }

    /**
     * TCP连接断开
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (onChannelStatusListener != null) {
            onChannelStatusListener.onDisconnect();
        }
    }
}
