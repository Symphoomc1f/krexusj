package com.java110.things.accessControl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.exception.HeartbeatCloudException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    void scan() {
        List<MachineDto> machineDtos = getAssessControlProcessImpl().scanMachine();

        if (machineDtos == null) {
            return;
        }

        logger.debug("扫描到的硬件信息为", JSONArray.toJSONString(machineDtos));

    }

}
