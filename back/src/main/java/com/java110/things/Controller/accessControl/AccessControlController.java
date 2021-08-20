package com.java110.things.Controller.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.ThingsApplicationStart;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.service.IAssessControlProcess;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.openDoor.IOpenDoorService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CommunityController
 * @Description TODO 门禁推送地址类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/accessControl")
public class AccessControlController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AccessControlController.class);

    /**
     * 添加设备接口类
     * <p>
     *
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/faceResult", method = RequestMethod.POST)
    public ResponseEntity<String> faceResult(@RequestBody String param) throws Exception {
        logger.debug("请求报文：" + param);
        IAssessControlProcess assessControlProcess = AccessControlProcessFactory.getAssessControlProcessImpl();

        boolean save = assessControlProcess.httpFaceResult(param);

        if (save) {
            return super.createResponseEntity(new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG));
        }

        return super.createResponseEntity(new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG));
    }

}
