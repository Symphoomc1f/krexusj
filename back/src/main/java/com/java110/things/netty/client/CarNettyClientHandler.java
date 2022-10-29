package com.java110.things.netty.client;

import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.CarMachineProcessFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class CarNettyClientHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(CarNettyClientHandler.class);


    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        int port = ipSocket.getPort();
        String host = ipSocket.getHostString();
        logger.info("与设备" + host + ":" + port + "连接成功!");
        ctx.fireChannelActive();
    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        int port = ipSocket.getPort();
        String host = ipSocket.getHostString();
        logger.error("与设备" + host + ":" + port + "连接断开!");
        final EventLoop eventLoop = ctx.channel().eventLoop();

    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            int port = ipSocket.getPort();
            String host = ipSocket.getHostString();

            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);


            logger.info("接受到 " + host + ":" + port + "的数据: " + Arrays.toString(bytes));

            //数据解析
            MachineDto machineDto = CarNettyClient.getMachine(host, port);

            CarMachineProcessFactory.getCarImpl(machineDto.getHmId()).readByte(machineDto, bytes);
        } catch (Exception e) {
            logger.error("接受消息失败", e);
        }


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


}
