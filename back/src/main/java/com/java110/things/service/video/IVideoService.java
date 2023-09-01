package com.java110.things.service.video;

import org.springframework.http.ResponseEntity;

public interface IVideoService {

    /**
     * 请求播放视频
     *
     * @param deviceId
     * @param channelId
     * @param mediaProtocol
     * @return
     */
    ResponseEntity<String> doPlay(String deviceId,
                                  String channelId,
                                  String mediaProtocol);


    /**
     * 暂停推流
     *
     * @param callId
     * @return
     */
    ResponseEntity<String> bye(String callId);
}
