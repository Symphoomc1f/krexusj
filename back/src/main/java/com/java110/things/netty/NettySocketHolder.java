package com.java110.things.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class NettySocketHolder {

    private static final Map<Long, NioSocketChannel> MAP = new ConcurrentHashMap<>(16);

    public static void put(Long id, NioSocketChannel socketChannel) {
        MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(Long id) {
        return MAP.get(id);
    }

    public static Map<Long, NioSocketChannel> getMAP() {
        return MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> MAP.remove(entry.getKey()));
    }


    public static ChannelFuture sendMsg(Java110CarProtocol java110CarProtocol){
        NioSocketChannel channel = get(java110CarProtocol.getId());
        if(channel == null ){
            throw new NoSuchElementException("未包含该链接");
        }

        return channel.writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(java110CarProtocol.getContent(), CharsetUtil.UTF_8)));
    }
}