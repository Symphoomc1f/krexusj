package com.java110.things.accessControl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.exception.HeartbeatCloudException;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.HttpFactory;
import com.java110.things.service.IAssessControlProcess;
import com.java110.things.service.yld04.Yld04AssessControlProcessAdapt;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                waitTime = DEFAULT_WAIT_SECOND;
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
    private void executeTask() {

        scanMachine.scan();

    }



}
