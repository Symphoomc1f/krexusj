package com.java110.things.netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class NettySocketHolder {
    private static Logger logger = LoggerFactory.getLogger(NettySocketHolder.class);

    private static final Map<String, NioSocketChannel> MAP = new ConcurrentHashMap<>();

    public static void put(String id, NioSocketChannel socketChannel) {
        MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(String id) {
        return MAP.get(id);
    }

    public static Map<String, NioSocketChannel> getMAP() {
        return MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> MAP.remove(entry.getKey()));
    }


    public static JSONObject sendMsgSync(Java110CarProtocol java110CarProtocol, String queryId) {
        NioSocketChannel channel = get(java110CarProtocol.getId());
        if (channel == null) {
            throw new NoSuchElementException("未包含该链接");
        }

        JSONObject data = null;

        try {

            LinkedBlockingDeque<JSONObject> deque = ServerTools.addQueryDeque(queryId, ServerTools.MIN_TIME);
            channel.writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(java110CarProtocol.getContent(), CharsetUtil.UTF_8)));
            //查询数据
            if (ServerTools.checkQuery(queryId)) {
                //等待查询结果
                data = deque.take();
            }
        } catch (Exception e) {
            logger.error("查询失败", e);
        }
        return data;
    }

    public static void sendMsg(Java110CarProtocol java110CarProtocol) {
        String lo = java110CarProtocol.getId();
        NioSocketChannel channel = get(java110CarProtocol.getId());
        if (channel == null) {
            throw new NoSuchElementException("未包含该链接");
        }

        channel.writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(java110CarProtocol.getContent(), CharsetUtil.UTF_8)));
    }
}