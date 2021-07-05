package com.java110.things.Controller.user;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.constant.SystemConstant;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.UserDto;
import com.java110.things.service.user.IUserService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.sun.xml.internal.rngom.parse.host.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:20
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
@RestController
@RequestMapping(path = "/api/user")
public class UserController extends BaseController {

    @Autowired
    private IUserService userServiceImpl;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestBody String param, HttpServletRequest request) throws Exception{

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "username", "请求报文中未包含用户名信息");

        Assert.hasKeyAndValue(paramObj, "password", "请求报文中未包含密码信息");

        ResultDto resultDto = userServiceImpl.login(BeanConvertUtil.covertBean(paramObj, UserDto.class));
        request.setAttribute(SystemConstant.COOKIE_AUTH_TOKEN, ((JSONObject) resultDto.getData()).getString("token"));
        return super.createResponseEntity(resultDto);
    }

    public IUserService getUserServiceImpl() {
        return userServiceImpl;
    }

    public void setUserServiceImpl(IUserService userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }
}
