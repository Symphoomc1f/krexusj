package com.java110.things.netty;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.CarProcessFactory;
import com.java110.things.service.machine.IMachineService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class HeartBeatSimpleHandle extends SimpleChannelInboundHandler<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        int port = ipSocket.getPort();
        String host = ipSocket.getHostString();

        IMachineService machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineIp(host + ":" + port);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }

        //保存客户端与 Channel 之间的关系
        NettySocketHolder.put(machineDtos.get(0).getMachineId(), (NioSocketChannel) ctx.channel());

    }

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettySocketHolder.remove((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        int port = ipSocket.getPort();
        String host = ipSocket.getHostString();

        IMachineService machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineIp(host + ":" + port);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }
        ByteBuf heartbeat = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(new Java110CarProtocol(machineDtos.get(0).getMachineId(), "http://www.homecommunity.cn").toString(), CharsetUtil.UTF_8));
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                LOGGER.info("已经5秒没有收到信息！");
                //向客户端发送消息
                ctx.writeAndFlush(heartbeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String content) throws Exception {
        LOGGER.info("收到customProtocol={}", content);
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        int port = ipSocket.getPort();
        String host = ipSocket.getHostString();

        IMachineService machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineIp(host + ":" + port);
        List<MachineDto> machineDtos = machineService.queryMachines(machineDto);
        if (machineDtos == null || machineDtos.size() < 1) {
            return;
        }

        Java110CarProtocol java110CarProtocol = CarProcessFactory.getCarImpl(machineDtos.get(0).getHmId()).accept(machineDtos.get(0),content);
        ctx.channel().writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(java110CarProtocol.getContent(), CharsetUtil.UTF_8)));


        judgeSyncQuery(content);
    }

    private void judgeSyncQuery(String contentStr) {

        JSONObject content = JSONObject.parseObject(contentStr);

        if (!content.containsKey("service") || !"query_price".equals(content.getString("service"))) {
            return;
        }

        String carNum = content.getString("car_number");

        if (!ServerTools.checkQuery(carNum)) {
            return;
        }
        ServerTools.appendQueryDequeData(carNum, content);
    }
}
