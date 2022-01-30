package com.java110.things.quartz.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.accessControl.ClearAllFace;
import com.java110.things.car.AddUpdateCar;
import com.java110.things.car.DeleteCar;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.*;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.task.TaskDto;
import com.java110.things.exception.HeartbeatCloudException;
import com.java110.things.exception.Result;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.quartz.TaskSystemQuartz;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AccessControlHeartbeatCloudTemplate
 * @Description TODO 车辆信息同步
 * @Author wuxw
 * @Date 2020/6/8 16:53
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Component
public class CarHeartbeatCloudTemplate extends TaskSystemQuartz {

    /**
     * 初始化 硬件状态
     */
    public static boolean INIT_MACHINE_STATE = false;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Java110Properties java110Properties;


    @Autowired
    private AddUpdateCar addUpdateCar;

    @Autowired
    private DeleteCar deleteCar;
    @Autowired
    private ICommunityService communityService;

    @Autowired
    private IMachineService machineService;

    @Autowired
    private ClearAllFace clearAllFace;

    @Override
    protected void process(TaskDto taskDto) throws Exception {

        if (INIT_MACHINE_STATE) {
            AccessControlProcessFactory.getAssessControlProcessImpl().initAssessControlProcess();
            INIT_MACHINE_STATE = false;
        }


        //查询 小区信息
        CommunityDto communityDto = new CommunityDto();
        ResultDto resultDto = communityService.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "查询小区信息失败");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "当前还没有设置小区，请先设置小区");
        }


        //心跳云端是否下发指令
        heartbeatCloud(communityDtos.get(0));
    }

    /**
     * 心跳云端 接收指令
     *
     * @param communityDto 小区信息
     */
    private void heartbeatCloud(CommunityDto communityDto) {

        try {
            doHeartbeatCloud(communityDto);
        } catch (Exception e) {
            logger.error(communityDto.getCommunityId() + "心跳失败", e);
        }

    }

    /**
     * 心跳云端 接受指令
     *
     * @param communityDto
     */
    private void doHeartbeatCloud(CommunityDto communityDto) throws Exception {

        String url = MappingCacheFactory.getValue("CLOUD_API") + AccessControlConstant.MACHINE_HEARTBEART;

        Map<String, String> headers = new HashMap<>();
        headers.put("command", "gettask");
        headers.put("machinecode", communityDto.getCommunityId());
        headers.put("communityId", communityDto.getCommunityId());

        /**
         * {
         * "machineCode":"test001",
         * "devGroup":"default",
         * "name":"dev1",
         * "authCode":"ab2324f12ca2312b213133bfac",
         * "ip":"192.168.100.33",
         * "mac":"00:00:00:00",
         * "remarks":"test",
         * "faceNum":0,
         * "lastOnTime":15328329,
         * "statCode":1,
         * "deviceType":1,
         * "versionCode":114
         * }
         */
        JSONObject paramIn = new JSONObject();
        paramIn.put("machineCode", communityDto.getCommunityId());
        paramIn.put("devGroup", "default");
        paramIn.put("communityId", communityDto.getCommunityId());
        paramIn.put("name", "车辆道闸");
        paramIn.put("authCode", "");
        paramIn.put("ip", "");
        paramIn.put("mac", "");
        paramIn.put("remarks", "");
        paramIn.put("faceNum", "");
        paramIn.put("lastOnTime", DateUtil.getTime());
        paramIn.put("statCode", "");
        paramIn.put("deviceType", "");
        paramIn.put("versionCode", "v1.0");

        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, paramIn.toJSONString(), headers, HttpMethod.POST);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, responseEntity.getBody());
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        //返回格式
        /**
         * {
         *     "code": 0,
         *     "data": [
         *         {
         *             "taskinfo": "772019120532930001",
         *             "taskcmd": 101,
         *             "taskId": "cfaeb4499689481189a6c73972950185"
         *         },
         *         {
         *             "taskinfo": "772019120931440001",
         *             "taskcmd": 101,
         *             "taskId": "6d7538565f274ab5af3f090505064045"
         *         },
         *         {
         *             "taskinfo": "772019121572460003",
         *             "taskcmd": 101,
         *             "taskId": "3455a25fc1f54410861283e44bb8846e"
         *         },
         *         {
         *             "taskinfo": "772019121549440006",
         *             "taskcmd": 101,
         *             "taskId": "16031ff6bf03470ca26dbfcf0099bcb2"
         *         },
         *         {
         *             "taskinfo": "772019121577720003",
         *             "taskcmd": 101,
         *             "taskId": "9dc4f1075e854b208908007bf8dbf653"
         *         },
         *         {
         *             "taskinfo": "772019121580420009",
         *             "taskcmd": 101,
         *             "taskId": "ed06d2329c774474a05475ac6f3d623d"
         *         }
         *     ],
         *     "message": "success"
         * }
         */

        if (!paramOut.containsKey("code") || ResponseConstant.SUCCESS != paramOut.getInteger("code")) {
            String msg = paramOut.containsKey("message") ? paramOut.getString("message") : ResponseConstant.NO_RESULT;
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, msg);
        }

        JSONArray data = paramOut.getJSONArray("data");

        for (int dataIndex = 0; dataIndex < data.size(); dataIndex++) {
            doHeartbeatCloudResult(data.getJSONObject(dataIndex), communityDto);
        }

    }

    /**
     * 心跳结果处理
     *
     * @param commandInfo 指令内容 {
     *                    "taskinfo": "772019121580420009", 任务对象ID
     *                    "taskcmd": 101, 指令编码
     *                    "taskId": "ed06d2329c774474a05475ac6f3d623d"  任务ID
     *                    }
     */
    private void doHeartbeatCloudResult(JSONObject commandInfo, CommunityDto communityDto) throws Exception {

        Assert.hasKeyAndValue(commandInfo, "taskcmd", "云端返回报文格式错误 未 包含指令编码 taskcmd" + commandInfo.toJSONString());
        Assert.hasKeyAndValue(commandInfo, "taskinfo", "云端返回报文格式错误 未 包含任务内容 taskinfo" + commandInfo.toJSONString());
        Assert.hasKeyAndValue(commandInfo, "taskid", "云端返回报文格式错误 未 包含任务ID taskid" + commandInfo.toJSONString());

        HeartbeatTaskDto heartbeatTaskDto = BeanConvertUtil.covertBean(commandInfo, HeartbeatTaskDto.class);

        switch (commandInfo.getInteger("taskcmd")) {
            case CarConstant.CMD_ADD_CAR:
                addUpdateCar.addUpdateCar(heartbeatTaskDto, communityDto,CarConstant.CMD_ADD_CAR);
                break;
            case CarConstant.CMD_UPDATE_CAR:
                addUpdateCar.addUpdateCar(heartbeatTaskDto, communityDto,CarConstant.CMD_UPDATE_CAR);
                break;
            case CarConstant.CMD_DELETE_CAR:
                deleteCar.deleteCar(heartbeatTaskDto, communityDto);
                break;
            default:
                logger.error("不支持的指令", commandInfo.getInteger("taskcmd"));
        }


    }

    /**
     * 查询设备信息
     *
     * @return
     */
    private List<MachineDto> queryMachines() throws Exception {
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);
        ResultDto resultDto = machineService.getMachine(machineDto);
        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "查询设备失败");
        }
        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();
        return machineDtos;

    }
}
