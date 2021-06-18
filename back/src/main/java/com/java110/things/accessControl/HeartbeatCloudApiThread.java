package com.java110.things.accessControl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.HttpFactory;
import com.java110.things.util.BeanConvertUtil;
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
 * @Description 心跳检测 云端HC api 服务
 * @Author wuxw
 * @Date 2020/5/10 21:01
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class HeartbeatCloudApiThread implements Runnable {
    Logger logger = LoggerFactory.getLogger(HeartbeatCloudApiThread.class);
    public static final long DEFAULT_WAIT_SECOND = 5000 * 6; // 默认30秒执行一次
    public static boolean HEARTBEAT_STATE = false;

    private RestTemplate restTemplate;
    private Java110Properties java110Properties;

    public HeartbeatCloudApiThread(boolean state) {
        HEARTBEAT_STATE = state;
        //orderInnerServiceSMOImpl = ApplicationContextFactory.getBean(IOrderInnerServiceSMO.class.getName(), IOrderInnerServiceSMO.class);
        restTemplate = ApplicationContextFactory.getBean("restTemplate", RestTemplate.class);

        java110Properties = ApplicationContextFactory.getBean("java110Properties", Java110Properties.class);
    }

    @Override
    public void run() {
        long waitTime = DEFAULT_WAIT_SECOND;
        while (HEARTBEAT_STATE) {
            try {
                executeTask();
                waitTime = DEFAULT_WAIT_SECOND;
                Thread.sleep(waitTime);
            } catch (Throwable e) {
                logger.error("执行订单中同步业主信息至设备中失败", e);
            }
        }
    }

    /**
     * 执行任务
     */
    private void executeTask() {
        //查询设备信息
        List<MachineDto> machineDtos = queryMachines();

        //心跳云端是否下发指令
        heartbeatCloud(machineDtos);

    }

    /**
     * 心跳云端 接收指令
     * @param machineDtos 设备信息
     */
    private void heartbeatCloud(List<MachineDto> machineDtos) {
    }

    private List<MachineDto> queryMachines() {
        String communityId = java110Properties.getCommunityId();
        Map<String, Object> paramIn = new HashMap<>();
        paramIn.put("communityId", communityId);
        paramIn.put("page", 1);
        paramIn.put("row", 100);
        paramIn.put("machineTypeCd", 9996);

        String url = java110Properties.getCloudApiUrl() + AccessControlConstant.LIST_MACHINES + HttpFactory.mapToUrlParam(paramIn);

        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, "", HttpMethod.GET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("查询小区设备信息失败", responseEntity.getBody());
            return null;
        }

        String body = responseEntity.getBody();

        JSONArray machines = JSONObject.parseObject(body).getJSONArray("machines");

        JSONObject machine = null;
        List<MachineDto> machineDtos = new ArrayList<>();
        for (int machineIndex = 0; machineIndex < machines.size(); machineIndex++) {
            machine = machines.getJSONObject(machineIndex);

            machineDtos.add(BeanConvertUtil.covertBean(machine, MachineDto.class));
        }

        return machineDtos;

    }

}
