package com.java110.things.util;

import com.java110.things.cache.BaseCache;
import com.java110.things.sip.message.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis操作工具类
 * @author yangjie
 * 2020年3月9日
 */

public class RedisUtil extends BaseCache {

	private static Logger logger = LoggerFactory.getLogger("RedisUtil");

	public static  int EXPIRE;

	private static int PERSISTENCE = -1;

	private static int DEFAULT_DB = 0;

	public static String set(String key,String value){
		return set(key,PERSISTENCE,value,DEFAULT_DB);
	}
	public static String set(String key,int expire,String value){
		return set(key,expire,value,DEFAULT_DB);
	}
	public static String set(String key,String value,int index){
		return set(key,PERSISTENCE,value,index);
	}
	public static String set(String key,int expire,String value,int dbIndex){
		Jedis jedis = getJedis();
		if(jedis == null){
			return null;
		}
		jedis.select(dbIndex);
		String result = null;
		try{
			if(expire != PERSISTENCE){
				result = jedis.setex(key, expire, value);
			}else {
				result = jedis.set(key, value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			releaseResource(jedis);
		}
		return result;
	}
	public static String get(String key) {
		return get(key,0);
	}
	public static String get(String key,int dbIndex){
		Jedis jedis = getJedis();
		if(jedis == null){
			return null;
		}
		jedis.select(dbIndex);
		String result = null;
		try { 
			result = jedis.get(key);
		} catch (Exception e) {
		} finally {
			releaseResource(jedis);
		}

		return result;
	}
	public static Long expire(String key,int expire){
		return expire(key, expire,DEFAULT_DB);
	}
	public static Long expire(String key,int expire,int dbIndex){
		Jedis jedis = getJedis();
		if(jedis == null){
			return null;
		}
		jedis.select(dbIndex);
		Long result = null;
		try { 
			result = jedis.expire(key, expire);
		}catch(Exception e){

		}finally {
			releaseResource(jedis);
		}
		return result;
	}
	public static boolean checkExist(String key){
		return checkExist(key,DEFAULT_DB);
	}
	public static boolean checkExist(String key,int index){
		Jedis jedis = getJedis();
		if(jedis == null){
			return false;
		}
		jedis.select(index);
		try { 
			return jedis.exists(key);
		} catch (Exception e) {
		} finally {
			releaseResource(jedis);
		}
		return false;
	}
	/**
	 * 释放Jedis
	 * @param jedis
	 */
	public static void releaseResource( Jedis jedis){
		if (jedis != null)  
			jedis.close();  
	}  


}
