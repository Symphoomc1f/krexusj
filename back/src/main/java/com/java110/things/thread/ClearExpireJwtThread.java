package com.java110.things.thread;

import com.alibaba.fastjson.JSONArray;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.LocalCacheFactory;
import com.java110.things.factory.RedisCacheFactory;
import com.java110.things.sip.SipLayer;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sip.SipException;

/**
 * @ClassName ClearExpireJwtThread
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 22:07
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public class ClearExpireJwtThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(ClearExpireJwtThread.class);

    public static final long DEFAULT_WAIT_SECOND = 1 * 60 * 1000; // 默认5分钟执行一次

    public static boolean CLEAR_EXPIRE_STATE = false;

    private SipLayer sipLayer = null;


    public ClearExpireJwtThread(boolean state) {
        CLEAR_EXPIRE_STATE = true;

        sipLayer = ApplicationContextFactory.getBean("sipLayer", SipLayer.class);
    }

    @Override
    public void run() {
        while (CLEAR_EXPIRE_STATE) {
            try {
                executeTask();
                Thread.sleep(DEFAULT_WAIT_SECOND);
            } catch (Throwable e) {
                logger.error("清理 失效会话失败", e);
            }
        }
    }

    private void executeTask() {

        LocalCacheFactory.clearExpireJwt();

        //检查摄像头是否有人访问
        String calls = RedisCacheFactory.getValue("VEDIO_CALLS");
        JSONArray callIds = null;
        JSONArray newCallIds = null;
        if (StringUtil.isEmpty(calls)) {
            return;
        }
        callIds = JSONArray.parseArray(calls);
        if (callIds == null || callIds.size() < 1) {
            return;
        }
        newCallIds = callIds;
        String tmpCallId;
        String value;
        String pushStream;
        for (int callIndex = 0; callIndex < callIds.size(); callIndex++) {
            tmpCallId = callIds.getString(callIndex);
            value = RedisCacheFactory.getValue(tmpCallId + "_callId");
            //摄像头推流标识
            pushStream = RedisCacheFactory.getValue( tmpCallId+"_pushStream");

            if (StringUtil.isEmpty(value)|| StringUtil.isEmpty(pushStream)) {
                try {
                    sipLayer.sendBye(tmpCallId);
                } catch (SipException e) {
                    e.printStackTrace();
                }
            }
            newCallIds.remove(tmpCallId);
        }
        //这里存在并发问题 后期优化，目前来说 摄像头 不是很多 后期支持
        RedisCacheFactory.setValue("VEDIO_CALLS", newCallIds.toJSONString());

    }
}