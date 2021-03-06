package com.java110.things.quartz.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.quartz.accessControl.ClearAllFace;
import com.java110.things.quartz.car.AddUpdateCar;
import com.java110.things.quartz.car.DeleteCar;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.CarConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.SystemExceptionDto;
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
import com.java110.things.service.machine.ISystemExceptionService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.ExceptionUtil;
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
 * @Description TODO ??????????????????
 * @Author wuxw
 * @Date 2020/6/8 16:53
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Component
public class CarHeartbeatCloudTemplate extends TaskSystemQuartz {

    /**
     * ????????? ????????????
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

    @Autowired
    private ISystemExceptionService systemExceptionService;

    @Override
    protected void process(TaskDto taskDto) throws Exception {

        if (INIT_MACHINE_STATE) {
            AccessControlProcessFactory.getAssessControlProcessImpl("").initAssessControlProcess();
            INIT_MACHINE_STATE = false;
        }
        //??????????????????
        List<MachineDto> machineDtos = queryMachines();

        //?????? ????????????
        CommunityDto communityDto = new CommunityDto();
        ResultDto resultDto = communityService.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "????????????????????????????????????????????????");
        }


        //??????????????????????????????
        heartbeatCloud(machineDtos, communityDtos.get(0));
    }

    /**
     * ???????????? ????????????
     *
     * @param communityDto ????????????
     */
    private void heartbeatCloud(List<MachineDto> machineDtos, CommunityDto communityDto) {
        for (MachineDto machineDto : machineDtos) {
            try {
                doHeartbeatCloud(communityDto, machineDto);
            } catch (Exception e) {
                logger.error(communityDto.getCommunityId() + "????????????", e);
            }
        }
    }

    /**
     * ???????????? ????????????
     *
     * @param communityDto
     */
    private void doHeartbeatCloud(CommunityDto communityDto, MachineDto machineDto) throws Exception {

        String url = MappingCacheFactory.getValue("CLOUD_API") + CarConstant.CAR_HEARTBEART;


        Map<String, String> headers = new HashMap<>();
        headers.put("command", "gettask");
        headers.put("machinecode", machineDto.getMachineCode());
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
        paramIn.put("machineCode", machineDto.getMachineCode());
        paramIn.put("devGroup", "default");
        paramIn.put("communityId", communityDto.getCommunityId());
        paramIn.put("name", machineDto.getMachineName());
        paramIn.put("authCode", machineDto.getAuthCode());
        paramIn.put("ip", machineDto.getMachineIp());
        paramIn.put("mac", machineDto.getMachineMac());
        paramIn.put("remarks", "");
        paramIn.put("faceNum", AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).getFaceNum(machineDto));
        paramIn.put("lastOnTime", DateUtil.getTime());
        paramIn.put("statCode", "");
        paramIn.put("deviceType", machineDto.getMachineTypeCd());
        paramIn.put("versionCode", machineDto.getMachineVersion());

        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, paramIn.toJSONString(), headers, HttpMethod.POST);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new HeartbeatCloudException(ExceptionConstant.ERROR, responseEntity.getBody());
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        //????????????
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
            doHeartbeatCloudResult(machineDto, data.getJSONObject(dataIndex), communityDto);
        }

    }

    /**
     * ??????????????????
     *
     * @param commandInfo ???????????? {
     *                    "taskinfo": "772019121580420009", ????????????ID
     *                    "taskcmd": 101, ????????????
     *                    "taskId": "ed06d2329c774474a05475ac6f3d623d"  ??????ID
     *                    }
     */
    private void doHeartbeatCloudResult(MachineDto machineDto, JSONObject commandInfo, CommunityDto communityDto) throws Exception {
        try {
            Assert.hasKeyAndValue(commandInfo, "taskcmd", "?????????????????????????????? ??? ?????????????????? taskcmd" + commandInfo.toJSONString());
            Assert.hasKeyAndValue(commandInfo, "taskinfo", "?????????????????????????????? ??? ?????????????????? taskinfo" + commandInfo.toJSONString());
            Assert.hasKeyAndValue(commandInfo, "taskid", "?????????????????????????????? ??? ????????????ID taskid" + commandInfo.toJSONString());

            HeartbeatTaskDto heartbeatTaskDto = BeanConvertUtil.covertBean(commandInfo, HeartbeatTaskDto.class);

            switch (commandInfo.getInteger("taskcmd")) {
                case CarConstant.CMD_ADD_CAR:
                    addUpdateCar.addUpdateCar(machineDto, heartbeatTaskDto, communityDto, CarConstant.CMD_ADD_CAR);
                    break;
                case CarConstant.CMD_UPDATE_CAR:
                    addUpdateCar.addUpdateCar(machineDto, heartbeatTaskDto, communityDto, CarConstant.CMD_UPDATE_CAR);
                    break;
                case CarConstant.CMD_DELETE_CAR:
                    deleteCar.deleteCar(heartbeatTaskDto, communityDto);
                    break;
                default:
                    logger.error("??????????????????", commandInfo.getInteger("taskcmd"));
            }
        } catch (Exception e) {
            //????????????????????????????????????
            systemExceptionService.save(SystemExceptionDto.EXCEPTION_TYPE_CAR, commandInfo.getString("taskId"),
                    machineDto.getMachineId(), ExceptionUtil.getStackTrace(e));
            throw e;
        }

    }

    /**
     * ??????????????????
     *
     * @return
     */
    private List<MachineDto> queryMachines() throws Exception {
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_BARRIER_GATE);
        ResultDto resultDto = machineService.getMachine(machineDto);
        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "??????????????????");
        }
        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();
        return machineDtos;

    }
}
