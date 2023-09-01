package com.java110.things.factory;


import com.java110.things.cache.BaseCache;
import com.java110.things.entity.user.JwtDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * 说明 该类为登录回话缓存类，因为 这个系统在每个小区部署一套，场景为登录人员不多，这样来设计
 * 如果 确定登录比较大时建议改造为redis 存储会话，请参考HC小区管理系统实现方式
 * https://gitee.com/wuxw7/MicroCommunity
 * Created by wuxw on 2018/5/5.
 */
public class JWTCacheFactory extends BaseCache {

    private static Logger logger = LoggerFactory.getLogger(JWTCacheFactory.class);


    private static final Map<String, JwtDto> cacheJwt = new HashMap<>();


    /**
     * 获取值(用户ID)
     *
     * @returne
     */
    public static String getValue(String jdi) {
        Jedis redis = null;
        try {
            redis = getJedis();
            redis.select(DEFAULT_DB);
            return redis.get(jdi);
        } finally {
            releaseResource(redis);
        }
    }

    /**
     * 保存数据
     *
     * @param jdi
     */
    public static void setValue(String jdi, String userId, int expireTime) {
        Jedis redis = null;
        try {
            redis = getJedis();
            redis.select(DEFAULT_DB);
            redis.set(jdi, userId);
            redis.expire(jdi, expireTime);
        } finally {
            releaseResource(redis);
        }

    }

    /**
     * 删除记录
     *
     * @param jdi
     */
    public static void removeValue(String jdi) {
        Jedis redis = null;
        try {
            redis = getJedis();
            redis.select(DEFAULT_DB);
            redis.del(jdi);
        } finally {
            releaseResource(redis);
        }
    }

    /**
     * 重设超时间
     *
     * @param jdi
     * @param expireTime
     */
    public static void resetExpireTime(String jdi, int expireTime) {

        Jedis redis = null;
        try {
            redis = getJedis();
            redis.select(DEFAULT_DB);
            redis.expire(jdi, expireTime);
        } finally {
            releaseResource(redis);
        }
    }
}


