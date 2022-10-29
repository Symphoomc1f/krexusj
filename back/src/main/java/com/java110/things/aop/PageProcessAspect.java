package com.java110.things.aop;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.FilterException;
import com.java110.things.exception.NoAuthorityException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 数据初始化
 * Created by wuxw on 2018/5/2.
 */
@Aspect
@Component
public class PageProcessAspect {

    private static Logger logger = LoggerFactory.getLogger(PageProcessAspect.class);

    @Pointcut("execution(public * com.java110..*.*Controller.*(..)) || execution(public * com.java110..*.*Rest.*(..))")
    public void dataProcess() {
    }

    /**
     * 初始化数据
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("dataProcess()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String reqData = "";
        String userId = "";
        String userName = "";
        String appId = "";
        String sessionId = request.getSession().getId();

        logger.debug("请求头信息：" + request.getHeaderNames());
        // 获取 userId
        if (request.getAttribute("claims") != null && request.getAttribute("claims") instanceof Map) {
            Map<String, String> userInfo = (Map<String, String>) request.getAttribute("claims");
            if (userInfo.containsKey(SystemConstant.LOGIN_USER_ID)) {
                userId = userInfo.get(SystemConstant.LOGIN_USER_ID);
                userName = userInfo.get(SystemConstant.LOGIN_USER_NAME);
                appId = userInfo.get(SystemConstant.LOGIN_APP_ID);
                request.setAttribute(SystemConstant.LOGIN_USER_ID, userId);
                request.setAttribute(SystemConstant.LOGIN_USER_NAME, userName);
                request.setAttribute(SystemConstant.LOGIN_APP_ID, appId);
            }
        }
    }

    @AfterReturning(returning = "ret", pointcut = "dataProcess()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
    }

    //后置异常通知
    @AfterThrowing("dataProcess()")
    public void throwException(JoinPoint jp) {
    }

    //后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
    @After("dataProcess()")
    public void after(JoinPoint jp) throws IOException {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();
        String token = request.getAttribute(SystemConstant.COOKIE_AUTH_TOKEN) != null ? request.getAttribute(SystemConstant.COOKIE_AUTH_TOKEN).toString() : null;
        //保存日志处理
        if (token == null) {
            return;
        }

        //写cookies信息
        writeCookieInfo(token, attributes);

    }

    //环绕通知,环绕增强，相当于MethodInterceptor
    @Around("dataProcess()")
    public Object around(ProceedingJoinPoint pjp) {
        ResultDto resultDto = null;
        try {
            Object o = pjp.proceed();
            return o;
        } catch (NoAuthorityException e) {
            //response.sendRedirect("/flow/login");
            logger.error("请求发生异常", e);
            resultDto = new ResultDto(ResponseConstant.ERROR, e.getMessage());
        } catch (Exception e) {
            logger.error("请求发生异常", e);
            resultDto = new ResultDto(ResponseConstant.ERROR, e.getMessage());
        } catch (Throwable e) {
            logger.error("请求发生异常", e);
            resultDto = new ResultDto(ResponseConstant.ERROR, e.getMessage());
        }

        return new ResponseEntity<String>(resultDto.toString(), HttpStatus.OK);

    }


    /**
     * 获取TOKEN
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) throws FilterException {
        String token = "";
        if (request.getCookies() == null || request.getCookies().length == 0) {
            return token;
        }
        for (Cookie cookie : request.getCookies()) {
            if (SystemConstant.COOKIE_AUTH_TOKEN.equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }
        return token;
    }


    /**
     * 写cookie 信息
     *
     * @param token      页面封装信息
     * @param attributes
     * @throws IOException
     */
    private void writeCookieInfo(String token, ServletRequestAttributes attributes) throws IOException {
        // 这里目前只写到组件级别，如果需要 写成方法级别
        HttpServletResponse response = attributes.getResponse();
        Cookie cookie = new Cookie(SystemConstant.COOKIE_AUTH_TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        response.flushBuffer();
    }


}
