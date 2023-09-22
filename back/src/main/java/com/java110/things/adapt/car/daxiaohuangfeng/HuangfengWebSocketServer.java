package com.java110.things.adapt.car.daxiaohuangfeng;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.car.ICarMachineProcess;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName WebSocketServer
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/25 12:13
 * @Version 1.0
 * add by wuxw 2020/5/25
 **/
@ServerEndpoint("/huangfeng/ServerAgent")
@Component
public class HuangfengWebSocketServer {

    private static Logger logger = LoggerFactory.getLogger(HuangfengWebSocketServer.class);

    public static final String CMD_DEV_HB = "Dev.HB";// 心跳包请求

    public static final String CMD_DEV_HB_REP = "Dev.HB.Rep";// 心跳包请求

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, HuangfengWebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, String> clientMachineMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收clientId
     */
    private String clientId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(clientId)) {
            webSocketMap.remove(clientId);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("用户消息:" + clientId + ",报文:" + message);

        JSONObject data = JSONObject.parseObject(message);
        String cmd = data.getString("cmd");
        if (CMD_DEV_HB.equals(cmd)) {
            heartBeat(data);
        }

        ICarMachineProcess carMachineProcess = ApplicationContextFactory.getBean("huangfengWebSocketCarMachineAdapt", ICarMachineProcess.class);
        carMachineProcess.mqttMessageArrived(cmd, message);

    }

    /**
     * 心跳
     *
     * @param data
     */
    private void heartBeat(JSONObject data) {
        String devNo = data.getString("devNO");
        String devIP = data.getString("devIP");
        this.clientId = devNo;
        if (!webSocketMap.containsKey(clientId)) {
            //加入set中
            webSocketMap.put(clientId, this);
        }

        JSONObject resData = new JSONObject();
        resData.put("cmd", CMD_DEV_HB_REP);
        resData.put("packageID", data.getString("data"));
        resData.put("resCode", 0);
        resData.put("resMsg", "成功");
        resData.put("time", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        resData.put("data", "");
        sendInfo(resData.toJSONString(), devNo);
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("用户错误:" + this.clientId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送设备监控信息
     */
    public static void sendInfo(String message, String clientId) {
        logger.info("发送消息到:" + clientId + "，报文:" + message);
        try {
            webSocketMap.get(clientId).sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        HuangfengWebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        HuangfengWebSocketServer.onlineCount--;
    }
}
