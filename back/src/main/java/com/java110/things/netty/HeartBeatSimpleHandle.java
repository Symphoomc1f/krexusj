package com.java110.things.netty;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.factory.CarProcessFactory;
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

public class HeartBeatSimpleHandle extends SimpleChannelInboundHandler<String> {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatSimpleHandle.class);
    private static final ByteBuf HEART_BEAT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(new Java110CarProtocol(123456L, "http://www.homecommunity.cn").toString(), CharsetUtil.UTF_8));

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
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                LOGGER.info("已经5秒没有收到信息！");
                //向客户端发送消息
                ctx.writeAndFlush(HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String content) throws Exception {
        LOGGER.info("收到customProtocol={}", content);

        Java110CarProtocol java110CarProtocol = CarProcessFactory.getCarImpl().accept(content);
        ctx.channel().writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(java110CarProtocol.getContent(), CharsetUtil.UTF_8)));
        //保存客户端与 Channel 之间的关系
        NettySocketHolder.put(java110CarProtocol.getId(), (NioSocketChannel) ctx.channel());

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
