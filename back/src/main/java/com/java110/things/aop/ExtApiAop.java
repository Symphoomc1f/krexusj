package com.java110.things.aop;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.NoAuthorityException;
import com.java110.things.factory.AuthenticationFactory;
import com.java110.things.util.StringUtil;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * 数据初始化
 * Created by wuxw on 2018/5/2.
 */
@Aspect
@Component
public class ExtApiAop {

    private static Logger logger = LoggerFactory.getLogger(ExtApiAop.class);

    public static final String ACCESS_TOKEN = "access_token";

    @Pointcut("execution(public * com.java110.things.extApi..*.*Controller.*(..))")
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

    }

    //环绕通知,环绕增强，相当于MethodInterceptor
    @Around("dataProcess()")
    public Object around(ProceedingJoinPoint pjp) {
        ResultDto resultDto = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String accessToken = request.getHeader(ACCESS_TOKEN);
        if (StringUtil.isEmpty(accessToken)) {
            return ResultDto.error("http header 未包含access_token");
        }

        try {
            Map<String, String> paramOut = AuthenticationFactory.verifyToken(accessToken);
            String appId = paramOut.get(AuthenticationFactory.LOGIN_USER_ID);
            request.setAttribute("appId", appId);
            Object o = pjp.proceed();
            return o;
        } catch (NoAuthorityException e) {
            //response.sendRedirect("/flow/login");
            logger.error("请求发生异常", e);
            resultDto = new ResultDto(ResponseConstant.NO_AUTHORITY_ERROR, e.getMessage());
        } catch (JWTVerificationException e) {
            //response.sendRedirect("/flow/login");
            logger.error("请求发生异常", e);
            resultDto = new ResultDto(ResponseConstant.NO_AUTHORITY_ERROR, "无效的access_token");
        } catch (Exception e) {
            logger.error("请求发生异常", e);
            resultDto = new ResultDto(ResponseConstant.ERROR, e.getMessage());
        } catch (Throwable e) {
            logger.error("请求发生异常", e);
            resultDto = new ResultDto(ResponseConstant.ERROR, e.getMessage());
        }

        return new ResponseEntity<String>(resultDto.toString(), HttpStatus.OK);

    }

}
