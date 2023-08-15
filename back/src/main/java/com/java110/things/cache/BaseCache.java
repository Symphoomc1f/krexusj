package com.java110.things.cache;

import com.java110.things.factory.ApplicationContextFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

public class BaseCache {
    public static int DEFAULT_DB = 3;
    protected  static Jedis getJedis() {
        JedisPool jedisPool = (JedisPool) ApplicationContextFactory.getBean("jedisPool");
        return jedisPool.getResource();
    }

    /**
     * 删除数据
     *
     * @param pattern
     */
    public static void removeData(String pattern) {
        Jedis redis = null;
        try {
            redis = getJedis();
            Set<String> keys = redis.keys("*" + pattern);
            if (keys == null || keys.size() == 0) {
                return;
            }
            for (String key : keys) {
                redis.del(key);
            }
        } finally {
            if (redis != null) {
                redis.close();
            }
        }
    }


    /**
     * 释放Jedis
     *
     * @param jedis
     */
    public static void releaseResource(Jedis jedis) {
        if (jedis != null)
            jedis.close();
    }
}
