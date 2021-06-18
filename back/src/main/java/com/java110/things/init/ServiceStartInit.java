package com.java110.things.init;

import com.java110.things.accessControl.HeartbeatCloudApiThread;
import com.java110.things.factory.ApplicationContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * 服务启动加载类
 * Created by wuxw on 2018/5/7.
 */
public class ServiceStartInit {

    private final static Logger logger = LoggerFactory.getLogger(ServiceStartInit.class);


    public static void initSystemConfig(ApplicationContext context) {
        //加载配置文件，注册订单处理侦听
        try {
            ApplicationContextFactory.setApplicationContext(context);
            //启动心跳
            startHeartbeatCloudApiThread();
        } catch (Exception ex) {
            logger.error("系统初始化失败", ex);
            throw new IllegalStateException("系统初始化失败", ex);
        }
    }

    /**
     * 启动心跳 线程
     */
    private static void startHeartbeatCloudApiThread(){
        //启动业主信息同步 线程 变更业主的场景
        HeartbeatCloudApiThread heartbeatCloudApiThread = new HeartbeatCloudApiThread(true);
        Thread ownerToMachineThread = new Thread(heartbeatCloudApiThread,"HeartbeatCloudApiThread");
        ownerToMachineThread.start();
    }


}
