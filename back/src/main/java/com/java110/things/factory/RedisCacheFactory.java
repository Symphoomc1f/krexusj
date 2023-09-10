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
public class RedisCacheFactory extends BaseCache {

    private static Logger logger = LoggerFactory.getLogger(RedisCacheFactory.class);


    private static final Map<String, JwtDto> cacheJwt = new HashMap<>();

    /**
     * 获取值(用户ID)
     *
     * @returne
     */
    public static String getValue(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = getJedis();
            jedis.select(DEFAULT_DB);
            result = jedis.get(key);
        } catch (Exception e) {
            logger.error("查询redis 失败", e);
        } finally {
            releaseResource(jedis);
        }

        return result;

    }

    /**
     * 保存数据
     *
     * @param key        token 主键ID
     * @param value      用户ID
     * @param expireTime 失效时间
     */
    public static void setValue(String key, String value, int expireTime) {

        Jedis redis = null;
        try {
            redis = getJedis();
            redis.select(DEFAULT_DB);
            redis.set(key, value);
            redis.expire(key, expireTime);
        } finally {
            if (redis != null) {
                releaseResource(redis);
            }
        }

    }

    /**
     * 删除记录
     *
     * @param key
     */
    public static void removeValue(String key) {
        Jedis redis = null;
        try {
            redis = getJedis();
            redis.select(DEFAULT_DB);
            redis.del(key);
        } finally {
            if (redis != null) {
                redis.close();
            }
        }
    }

}


