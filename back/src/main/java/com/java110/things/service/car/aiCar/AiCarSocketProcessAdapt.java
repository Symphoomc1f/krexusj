package com.java110.things.service.car.aiCar;


import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.netty.Java110CarProtocol;
import com.java110.things.netty.NettySocketHolder;
import com.java110.things.service.car.ICarProcess;
import io.netty.channel.ChannelFuture;
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
    public Java110CarProtocol accept(String content) {
        JSONObject acceptJson = JSONObject.parseObject(content);

        String parkId = acceptJson.getString("parkid");
        String service = acceptJson.getString("service");

        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        JSONObject resObj = null;
        switch (service) {
            case "checkKey":
                resObj = checkKey();
                break;
            case "heartbeat":
                resObj = heartbeat();
                break;
            default:
                resObj = new JSONObject();
                resObj.put("service", service);
                resObj.put("result_code", -1);
                resObj.put("message", "当前不支持");
                break;
        }
        java110CarProtocol.setContent(resObj.toJSONString());
        java110CarProtocol.setId(Long.parseLong(parkId));
        return java110CarProtocol;
    }

    @Override
    public String getNeedPayOrder() {
        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        java110CarProtocol.setId(20180001L);
        java110CarProtocol.setContent("{\n" +
                "    \"service\": \"query_price\",\n" +
                "    \"parkid\": \"20180001\",\n" +
                "    \"car_number\": \"浙CBB123\",\n" +
                "    \"pay_scene\": 0\n" +
                "  }");
        ChannelFuture channelFuture = NettySocketHolder.sendMsg(java110CarProtocol);
        return null;
    }

    /**
     * 认证接口
     */
    private JSONObject checkKey() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("service", "checkKey");
        jsonObject.put("result_code", 0);
        jsonObject.put("message", "认证成功");
        return jsonObject;
    }

    /**
     * 心跳接口
     */
    private JSONObject heartbeat() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("service", "heartbeat");
        jsonObject.put("result_code", 0);
        jsonObject.put("message", "在线");
        return jsonObject;
    }
}
