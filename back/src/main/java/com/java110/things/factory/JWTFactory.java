package com.java110.things.factory;


import com.java110.things.entity.user.JwtDto;
import com.java110.things.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 说明 该类为登录回话缓存类，因为 这个系统在每个小区部署一套，场景为登录人员不多，这样来设计
 * 如果 确定登录比较大时建议改造为redis 存储会话，请参考HC小区管理系统实现方式
 * https://gitee.com/wuxw7/MicroCommunity
 * Created by wuxw on 2018/5/5.
 */
public class JWTFactory {

    private static Logger logger = LoggerFactory.getLogger(JWTFactory.class);


    private static final Map<String, JwtDto> cacheJwt = new HashMap<>();

    /**
     * 获取值(用户ID)
     *
     * @returne
     */
    public static String getValue(String jdi) {
        if (!cacheJwt.containsKey("jdi")) {
            return null;
        }

        JwtDto jwtDto = cacheJwt.get(jdi);
        if (jwtDto == null) {
            return null;
        }

        if (jwtDto.getExpireTime() > DateUtil.getTime()) {
            return jwtDto.getUserId();
        }

        return null;

    }

    /**
     * 保存数据
     *
     * @param jdi        token 主键ID
     * @param userId     用户ID
     * @param expireTime 失效时间
     */
    public static void setValue(String jdi, String userId, int expireTime) {

        long expire = DateUtil.getTime() + expireTime * 1000;

        JwtDto jwtDto = new JwtDto();
        jwtDto.setJdi(jdi);
        jwtDto.setUserId(userId);
        jwtDto.setExpireTime(expire);
        cacheJwt.put(jdi, jwtDto);

    }

    /**
     * 删除记录
     *
     * @param jdi
     */
    public static void removeValue(String jdi) {
        if (cacheJwt.containsKey(jdi)) {
            cacheJwt.remove(jdi);
        }
    }

    /**
     * 重设超时间
     *
     * @param jdi
     * @param expireTime
     */
    public static void resetExpireTime(String jdi, int expireTime) {
        if (!cacheJwt.containsKey("jdi")) {
            return;
        }

        JwtDto jwtDto = cacheJwt.get(jdi);
        if (jwtDto == null) {
            return;
        }
        long expire = DateUtil.getTime() + expireTime * 1000;
        jwtDto.setExpireTime(expire);
        cacheJwt.put(jdi, jwtDto);
    }

    /**
     * 清理失效的 会话
     */
    public static void clearExpireJwt() {

        JwtDto jwtDto = null;
        for (String key : cacheJwt.keySet()) {
            try {
                jwtDto = cacheJwt.get(key);
                if (jwtDto == null) {
                    continue;
                }
                if (jwtDto.getExpireTime() < DateUtil.getTime()) {
                    cacheJwt.remove(key);
                }
            } catch (Exception e) {
                logger.error("清理过期回话失败", e);
            }

        }

    }
}


