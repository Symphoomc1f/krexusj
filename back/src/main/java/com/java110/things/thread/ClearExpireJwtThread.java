package com.java110.things.thread;

import com.java110.things.factory.JWTFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static final long DEFAULT_WAIT_SECOND = 5 * 60 * 1000; // 默认5分钟执行一次

    public static boolean CLEAR_EXPIRE_STATE = false;

    public ClearExpireJwtThread(boolean state) {
        CLEAR_EXPIRE_STATE = true;
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
        JWTFactory.clearExpireJwt();
    }
}