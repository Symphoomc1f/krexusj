package com.java110.things.service.car.aiCar;


import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.service.car.ICarProcess;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

/**
 * 智能停车系统
 */
@Service("aiCarSocketProcessAdapt")
public class AiCarSocketProcessAdapt implements ICarProcess {
    @Override
    public void initCar() {

    }

    @Override
    public int getCarNum() {
        return 0;
    }

    @Override
    public String getCar(UserFaceDto userFaceDto) {
        return null;
    }

    @Override
    public void addAndUpdateCar(UserFaceDto userFaceDto) {

    }

    @Override
    public void deleteCar(HeartbeatTaskDto heartbeatTaskDto) {

    }

    @Override
    public void restartMachine(MachineDto machineDto) {

    }

    @Override
    public void openDoor(MachineDto machineDto) {

    }

    @Override
    public String httpFaceResult(String data) {
        return null;
    }

    @Override
    public Long accept(String content, ChannelHandlerContext ctx) {
        JSONObject acceptJson = JSONObject.parseObject(content);

        String parkId = acceptJson.getString("parkid");
        String service = acceptJson.getString("service");


        switch (service){
            case "checkKey":
                checkKey(ctx);
                break;
            case "heartbeat":
                heartbeat(ctx);
                break;
            default:
                break;
        }
        return Long.parseLong(parkId);
    }

    /**
     * 认证接口
     * @param ctx
     */
    private void checkKey(ChannelHandlerContext ctx){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("service","checkKey");
        jsonObject.put("result_code",0);
        jsonObject.put("message","认证成功");
        ctx.channel().writeAndFlush(jsonObject.toJSONString());
    }

    /**
     * 心跳接口
     * @param ctx
     */
    private void heartbeat(ChannelHandlerContext ctx){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("service","heartbeat");
        jsonObject.put("result_code",0);
        jsonObject.put("message","在线");

        System.out.printf("结果返回"+jsonObject.toJSONString());
        //ctx.channel().writeAndFlush(jsonObject.toJSONString());
    }
}
