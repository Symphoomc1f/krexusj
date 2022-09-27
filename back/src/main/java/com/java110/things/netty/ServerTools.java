package com.java110.things.netty;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class ServerTools {
    private static Logger logger = LoggerFactory.getLogger(ServerTools.class);


    public static final int MIN_TIME = 1000;

    /**
     * 数据存取队列Map
     */
    private static final Map<String, LinkedBlockingDeque<JSONObject>> queryDatas = new ConcurrentHashMap<>();

    /**
     * 新增一个查询数据队列
     * 设置超时时间小于2000 则默认为2000
     *
     * @param queryId
     * @param time    毫秒
     */
    public static LinkedBlockingDeque<JSONObject> addQueryDeque(String queryId, int time) {
        LinkedBlockingDeque<JSONObject> deque = new LinkedBlockingDeque<JSONObject>();
        //新增查询队列
        queryDatas.put(queryId, deque);
        //启动超时定时器
        if (time < MIN_TIME) {
            time = MIN_TIME;
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // 超时处理， 指定时间后 模拟添加一条数据
                appendQueryDequeData(queryId, new JSONObject());
            }
        }, time);
        return deque;
    }

    public static boolean checkQuery(String queryId) {
        return queryDatas.containsKey(queryId);
    }

    public static void appendQueryDequeData(String queryId, JSONObject data) {
        LinkedBlockingDeque<JSONObject> deque = queryDatas.get(queryId);
        try {
            logger.debug("存入数据{},{}",queryId,data);
            deque.put(data);
        } catch (InterruptedException e) {
            logger.error("存入队列失败", e);
        }
    }
}
