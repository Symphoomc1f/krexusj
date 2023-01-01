package com.java110.things.Controller.vedio;


import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;

@RestController
@RequestMapping(path = "/api/vedio")
public class VedioController {


    @RequestMapping(path = "/rtspToRtmp", method = RequestMethod.GET)
    public ResponseEntity<String> rtspToRtmp() throws Exception {

        String link = "rtsp://192.168.1.100:8557/h264";
        // 读取RTSP视频资源 传入Rtsp连接
        final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(link);
        // 设置分辨率宽度
        int captureWidth = 1280;
        grabber.setImageWidth(captureWidth);
        // 设置分辨率高度
        int captureHeight = 720;
        grabber.setImageHeight(captureHeight);
        // rtsp格式一般添加TCP配置，否则丢帧会比较严重
        // 该参数改成udp可以解决部分电脑出现的下列报警，但是丢帧比较严重 不采用
        // av_interleaved_write_frame() error -22 while writing interleaved video packet.
        grabber.setOption("rtsp_transport", "tcp");
        grabber.start();
        // 流媒体输出地址,最后一个参数是"音频AudioChannels 0:不录制,1:录制"，建议通过grabber获取
        // 地址格式为 rtmp:rtmp://{ip}:{nginx端口}/{该name为传过来的任意值}
        final FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("rtmp://127.0.0.1:1935/myapp/123", captureWidth, captureHeight, grabber.getAudioChannels());
        recorder.setInterleaved(true);
        // 降低编码延时
        recorder.setVideoOption("tune", "zerolatency");
        // 提升编码速度
        recorder.setVideoOption("preset", "ultrafast");
        // 视频质量参数(详见 https://trac.ffmpeg.org/wiki/Encode/H.264)
        recorder.setVideoOption("crf", "28");
        // 分辨率
        recorder.setVideoBitrate(2000000);
        // 视频编码格式
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        // 视频格式
        recorder.setFormat("flv");
        // 视频帧率
        recorder.setFrameRate(15);
        recorder.setGopSize(60);
        recorder.setAudioOption("crf", "0");
        recorder.setAudioQuality(0);
        recorder.setAudioBitrate(192000);
        recorder.setSampleRate(44100);
        // 建议从grabber获取AudioChannels
        recorder.setAudioChannels(grabber.getAudioChannels());
        recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
        recorder.start();

        // 解决音视频同步导致的延时问题
        Field field = recorder.getClass().getDeclaredField("oc");
        field.setAccessible(true);
        AVFormatContext oc = (AVFormatContext) field.get(recorder);
        oc.max_interleave_delta(100);

        // 用来测试的frame窗口  这个地方对应的注释释放开,可以进行代码测试
        // final CanvasFrame cFrame = new CanvasFrame("frame");
        Frame capturedFrame = null;

        // 有些时候，程序执行回报下列错误，添加一行代码解决此问题
        // av_interleaved_write_frame() error -22 while writing interleaved video packet.
        // grabber.flush();

        while (grabber.grab() != null) {
            capturedFrame = grabber.grab();
            // if (cFrame.isVisible()) {
            // 设置图像
            // cFrame.showImage(capturedFrame);
            // } else {
            // 获取每帧的编号 -- 时间戳
            System.out.println(grabber.getFrameNumber() + "--" + capturedFrame.timestamp);
            recorder.setTimestamp(capturedFrame.timestamp);
            recorder.record(capturedFrame);
            // }
        }
        // cFrame.dispose();
        recorder.close();
        grabber.close();
        return ResultDto.success();
    }
}
