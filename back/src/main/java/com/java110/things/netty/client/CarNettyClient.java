/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.things.netty.client;

import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.CarMachineProcessFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 车辆tcp 封装类
 */
public class CarNettyClient {
    private static Logger logger = LoggerFactory.getLogger(CarNettyClient.class);

    private static Map<String, ChannelFuture> channels = new ConcurrentHashMap<>();
    private static Map<String, MachineDto> ipMachines = new ConcurrentHashMap<>();

    static EventLoopGroup eventLoopGroup = new NioEventLoopGroup();


    /**
     * 初始化Bootstrap
     */
    public static final Bootstrap getBootstrap(EventLoopGroup group) {
        if (null == group) {
            group = eventLoopGroup;
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024*1024));
                        socketChannel.pipeline().addLast("framedecoder",new LengthFieldBasedFrameDecoder(5*1024*1024, 4, 4,0,0));
                        socketChannel.pipeline().addLast(new CarNettyClientHandler());
                    }
                });
        return bootstrap;
    }

    //二次重连
    public static final void twoGetChannel(MachineDto machineDto) {

        if (!channels.containsKey(machineDto.getMachineCode())) {
            getChannel(machineDto);
            return;
        }

        ChannelFuture future = channels.get(machineDto.getMachineCode());
        if (future == null) {
            getChannel(machineDto);
            return;
        }
        if (!future.isSuccess() || !future.channel().isActive()) {
            logger.info("连接失败 ！！！");
            getChannel(machineDto);
        }
    }

    //   获取所有连接
    public static final Map<String, ChannelFuture> getChannel(MachineDto machineDto) {

//        if (channels.containsKey(machineDto.getMachineCode())) {
//            return channels;
//        }

        Bootstrap bootstrap = getBootstrap(null);

        String ip = machineDto.getMachineIp();

        String host = ip.indexOf(":") > -1 ? ip.substring(0, ip.indexOf(":")) : ip;
        int port = Integer.valueOf(ip.indexOf(":") > -1 ? ip.substring(ip.indexOf(":") + 1) : "80");
        bootstrap.remoteAddress(host, port);
        //异步连接tcp服务端
        ChannelFuture future = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
            final EventLoop eventLoop = futureListener.channel().eventLoop();
            if (!futureListener.isSuccess()) {
                logger.info("与" + host + ":" + port + "连接失败!");
                //与设备的连接失败-推送mqtt主题
            }
        });
        channels.put(machineDto.getMachineCode(), future);
        ipMachines.put(host + ":" + port, machineDto);
        try {
            CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).initCar(machineDto);
        } catch (Exception e) {
            logger.error("推送配置信息失败", e);
        }
        return channels;
    }

    //发送消息
    public static boolean sendMsg(MachineDto machineDto, byte[] headerBytes, byte[] bytes) throws Exception {
        if (!channels.containsKey(machineDto.getMachineCode())) {
            logger.debug("设备[" + machineDto.getMachineCode() + "]未连接");
            return false;
        }
        ChannelFuture future = channels.get(machineDto.getMachineCode());
        if (future != null && future.channel().isActive()) {
            Channel channel = future.channel();
            InetSocketAddress ipSocket = (InetSocketAddress) channel.remoteAddress();
            String host = ipSocket.getHostString();
            int port = ipSocket.getPort();
            logger.debug("向设备" + host + ":" + port + "发送数据");
            //项目封装的util类
            ByteBuf buf = Unpooled.buffer();
            buf.writeBytes(headerBytes);
            buf.writeBytes(bytes);
            // 2.写数据
            future.channel().writeAndFlush(buf).sync();
        }
        return true;
    }

    //发送消息
    public static boolean sendScreenMsg(MachineDto machineDto, byte[] bytes) throws Exception {
        if (!channels.containsKey(machineDto.getMachineCode())) {
            logger.debug("设备[" + machineDto.getMachineCode() + "]未连接");
            return false;
        }
        ChannelFuture future = channels.get(machineDto.getMachineCode());
        if (future != null && future.channel().isActive()) {
            Channel channel = future.channel();
            InetSocketAddress ipSocket = (InetSocketAddress) channel.remoteAddress();
            String host = ipSocket.getHostString();
            int port = ipSocket.getPort();
            logger.debug("向设备" + host + ":" + port + "发送数据");
            //项目封装的util类
            ByteBuf buf = Unpooled.buffer();
            buf.writeBytes(bytes);
            // 2.写数据
            future.channel().writeAndFlush(buf).sync();
        }
        return true;
    }

    //发送消息
    public static boolean sendMsg(MachineDto machineDto, byte[] bytes) throws Exception {
        if (!channels.containsKey(machineDto.getMachineCode())) {
            logger.debug("设备[" + machineDto.getMachineCode() + "]未连接");
            return false;
        }
        ChannelFuture future = channels.get(machineDto.getMachineCode());
        if (future != null && future.channel().isActive()) {
            Channel channel = future.channel();
            InetSocketAddress ipSocket = (InetSocketAddress) channel.remoteAddress();
            String host = ipSocket.getHostString();
            int port = ipSocket.getPort();
            logger.debug("向设备" + host + ":" + port + "发送数据");
            //项目封装的util类
            ByteBuf buf = Unpooled.buffer();
            buf.writeBytes(bytes);
            // 2.写数据
            future.channel().writeAndFlush(buf).sync();
        }
        return true;
    }


    //发送消息
    public static void sendMsg(ChannelFuture future, byte[] bytes) throws Exception {
        if (future != null && future.channel().isActive()) {
            Channel channel = future.channel();
            InetSocketAddress ipSocket = (InetSocketAddress) channel.remoteAddress();
            String host = ipSocket.getHostString();
            int port = ipSocket.getPort();
            logger.debug("向设备" + host + ":" + port + "发送数据");
            //项目封装的util类
            ByteBuf buf = Unpooled.buffer();
            buf.writeBytes(bytes);
            // 2.写数据
            future.channel().writeAndFlush(buf).sync();
        }
    }

    public static MachineDto getMachine(String ip, int port) {
        return ipMachines.get(ip + ":" + port);
    }


}
