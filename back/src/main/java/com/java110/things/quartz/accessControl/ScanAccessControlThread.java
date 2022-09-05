package com.java110.things.quartz.accessControl;

import com.alibaba.druid.util.StringUtils;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName HeartbeatCloudApiThread
 * @Description 扫描局域网内的门禁
 * @Author wuxw
 * @Date 2020/5/10 21:01
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class ScanAccessControlThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(ScanAccessControlThread.class);
    public static final long DEFAULT_WAIT_SECOND = 1000 * 60 * 1; // 默认30秒执行一次
    public static boolean HEARTBEAT_STATE = false;

    private IAssessControlProcess assessControlProcessImpl;
    private ScanMachine scanMachine;

    public ScanAccessControlThread(boolean state) {
        HEARTBEAT_STATE = state;
        //orderInnerServiceSMOImpl = ApplicationContextFactory.getBean(IOrderInnerServiceSMO.class.getName(), IOrderInnerServiceSMO.class);
        scanMachine = ApplicationContextFactory.getBean("scanMachine", ScanMachine.class);
    }

    @Override
    public void run() {
        long waitTime = DEFAULT_WAIT_SECOND;
        while (HEARTBEAT_STATE) {
            try {
                String heartBeatTime = MappingCacheFactory.getValue("ACCESS_CONTROL_SCAN_TIME");
                waitTime = StringUtils.isNumber(heartBeatTime) ? Long.parseLong(heartBeatTime) * 1000 : DEFAULT_WAIT_SECOND;
                Thread.sleep(waitTime);
                executeTask();
            } catch (Throwable e) {
                logger.error("扫描门禁失败", e);
            }
        }
    }

    /**
     * 执行任务
     */
    private void executeTask() throws Exception {

        scanMachine.scan();

    }



}
