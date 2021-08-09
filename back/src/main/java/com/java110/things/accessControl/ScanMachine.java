package com.java110.things.accessControl;

import com.alibaba.fastjson.JSONArray;
import com.java110.things.config.Java110Properties;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.AccessControlProcessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 添加更新人脸
 */
@Component
public class ScanMachine extends BaseAccessControl {

    Logger logger = LoggerFactory.getLogger(ScanAccessControlThread.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Java110Properties java110Properties;


    /**
     * 扫描设备
     */
    void scan() throws Exception {
        List<MachineDto> machineDtos = AccessControlProcessFactory.getAssessControlProcessImpl().scanMachine();

        if (machineDtos == null) {
            return;
        }

        logger.debug("扫描到的硬件信息为", JSONArray.toJSONString(machineDtos));

    }

}
