package com.java110.things.sip;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;

import javax.swing.*;

public class TestVideo1 {
    public static void frameRecord(String inputFile, String outputFile, boolean AUDIO_ENABLED)
            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
        // 是否录制音频
        int audioChannel = AUDIO_ENABLED ? 1 : 0;
        // 获取视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);
        // 开始取视频源
        recordByFrame(grabber, recorder, AUDIO_ENABLED);
    }
    private static void recordByFrame(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, Boolean status)
            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
        try {  //建议在线程中使用该方法

            grabber.start();
            recorder.start();
            Frame frame = null;
            while (status && (frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
            }
            recorder.stop();
            grabber.stop();
        } finally {
            if (grabber != null) {
                grabber.stop();
            }
        }
    }


    public static void main(String[] args)
            throws Exception {

        String inputFile = "rtsp://admin:hndd12345@192.168.1.106:554";
        // Decodes-encodes
        String outputFile = "recorde.mp4";
        frameRecord(inputFile, outputFile,true);
    }

}
