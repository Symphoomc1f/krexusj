package com.java110.things.service.car.aiCar;


import com.alibaba.fastjson.JSONObject;
import com.java110.things.entity.accessControl.CarResultDto;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.netty.Java110CarProtocol;
import com.java110.things.netty.NettySocketHolder;
import com.java110.things.service.car.ICarProcess;
import com.java110.things.util.DateUtil;
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

    /**
     * {
     * "service": "whitelist_sync",
     * "parkid": "20180001",
     * "car_number": "粤B99999",
     * "car_type": 0,
     * "card_type": 1,
     * "startdate": "2019-05-25 00:00:01",
     * "validdate": "2019-06-24 23:59:59",
     * "cardmoney": 230.50,
     * "period": "月",
     * "carusername": "老陈",
     * "carusertel": "13822220222",
     * "drive_no": "NO111111222",
     * "address": "xxxx市xxx区xxx路112号",
     * "carlocate": "A-1-10",
     * "create_time": "2019-05-25 20:11:48",
     * "modify_time": "2019-05-25 20:11:48",
     * "operator": "李四",
     * "operate_type": 1,
     * "limitdaytype": 0,
     * "remark": ""
     * }
     *
     * @param carResultDto 用户人脸信息
     */
    @Override
    public void addCar(CarResultDto carResultDto) {
        JSONObject data = new JSONObject();
        data.put("service", "whitelist_sync");
        data.put("parkid", "20180001");
        data.put("car_number", carResultDto.getCarNum());
        data.put("car_type", "0");
        data.put("startdate", carResultDto.getStartTime());
        data.put("validdate", carResultDto.getEndTime());
        data.put("cardmoney", 0.00);
        data.put("period", "月");
        data.put("carusername", carResultDto.getName());
        data.put("create_time", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        data.put("modify_time", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        data.put("operate_type", "1");
        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        java110CarProtocol.setId(20180001L);
        java110CarProtocol.setContent(data.toJSONString());
        NettySocketHolder.sendMsg(java110CarProtocol);
    }

    /**
     * {
     *     "service": "whitelist_pay_sync",
     *     "parkid": "20180001",
     *     "car_number": "粤B99999",
     *     "period": "月",
     *     "pay_count": 1,
     *     "pay_money": 230.50,
     *     "pay_time": "2018-07-26 20:11:48",
     *     "trade_no": "PZ000020190528172533",
     *     "remark": "延期一个月"
     *   }
     * @param carResultDto
     */
    @Override
    public void updateCar(CarResultDto carResultDto) {
        JSONObject data = new JSONObject();
        data.put("service", "whitelist_pay_sync");
        data.put("parkid", "20180001");
        data.put("car_number", carResultDto.getCarNum());
        data.put("period", "月");
        data.put("pay_count", (int)Math.floor(carResultDto.getCycles()));
        data.put("pay_money", 0.00);
        data.put("pay_time", DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A));
        data.put("trade_no", carResultDto.getCarId());
        data.put("remark", "");
        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        java110CarProtocol.setId(20180001L);
        java110CarProtocol.setContent(data.toJSONString());
        NettySocketHolder.sendMsg(java110CarProtocol);
    }

    @Override
    public void deleteCar(CarResultDto carResultDto) {
        JSONObject data = new JSONObject();
        data.put("service", "whitelist_sync");
        data.put("parkid", "20180001");
        data.put("car_number", carResultDto.getCarNum());
        data.put("operate_type", "3");
        Java110CarProtocol java110CarProtocol = new Java110CarProtocol();
        java110CarProtocol.setId(20180001L);
        java110CarProtocol.setContent(data.toJSONString());
        NettySocketHolder.sendMsg(java110CarProtocol);
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
        JSONObject data = NettySocketHolder.sendMsgSync(java110CarProtocol, "浙CBB123");
        return data.toJSONString();
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
