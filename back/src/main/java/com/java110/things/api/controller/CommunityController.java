package com.java110.things.api.controller;

import com.java110.things.Controller.BaseController;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.adapt.accessControl.IAssessControlProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping(path = "/extApi/community")
public class CommunityController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(CommunityController.class);

    /**
     * 添加设备接口类
     * <p>
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/faceResult", method = RequestMethod.POST)
    public ResponseEntity<String> faceResult(@RequestBody String param) throws Exception {
        logger.debug("请求报文：" + param);
        IAssessControlProcess assessControlProcess = AccessControlProcessFactory.getAssessControlProcessImpl();

        return new ResponseEntity<String>(assessControlProcess.httpFaceResult(param), HttpStatus.OK);

    }

}
