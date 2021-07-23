package com.java110.things.accessControl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.config.Java110Properties;
import com.java110.things.constant.AccessControlConstant;
import com.java110.things.constant.ExceptionConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.HeartbeatCloudException;
import com.java110.things.exception.Result;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.ApplicationContextFactory;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.IAssessControlProcess;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.NumberUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName HeartbeatCloudApiThread
 * @Description 心跳检测 云端HC api 服务
 * @Author wuxw
 * @Date 2020/5/10 21:01
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class HeartbeatCloudApiThread extends BaseAccessControl implements Runnable {
    Logger logger = LoggerFactory.getLogger(HeartbeatCloudApiThread.class);
    public static final long DEFAULT_WAIT_SECOND = 5000 * 6; // 默认30秒执行一次
    public static boolean HEARTBEAT_STATE = false;

    /**
     * 初始化 硬件状态
     */
    public static boolean INIT_MACHINE_STATE = false;

    private RestTemplate restTemplate;
    private Java110Properties java110Properties;

    private IAssessControlProcess assessControlProcessImpl;

    private AddUpdateFace addUpdateFace;

    private DeleteFace deleteFace;

    private ICommunityService communityService;

    private IMachineService machineService;

    private ClearAllFace clearAllFace;

    public HeartbeatCloudApiThread(boolean state) {
        HEARTBEAT_STATE = state;
        INIT_MACHINE_STATE = true;
        //orderInnerServiceSMOImpl = ApplicationContextFactory.getBean(IOrderInnerServiceSMO.class.getName(), IOrderInnerServiceSMO.class);
        restTemplate = ApplicationContextFactory.getBean("restTemplate", RestTemplate.class);
        addUpdateFace = ApplicationContextFactory.getBean("addUpdateFace", AddUpdateFace.class);
        deleteFace = ApplicationContextFactory.getBean("deleteFace", DeleteFace.class);
        clearAllFace = ApplicationContextFactory.getBean("clearAllFace", ClearAllFace.class);
        communityService = ApplicationContextFactory.getBean("communityServiceImpl", ICommunityService.class);
        machineService = ApplicationContextFactory.getBean("machineServiceImpl", IMachineService.class);
        java110Properties = ApplicationContextFactory.getBean("java110Properties", Java110Properties.class);
    }

    @Override
    public void run() {
        long waitTime = DEFAULT_WAIT_SECOND;
        while (HEARTBEAT_STATE) {
            try {
                String heartBeatTime = MappingCacheFactory.getValue("ACCESS_CONTROL_HEARTBEAT_TIME");
                waitTime = StringUtils.isNumber(heartBeatTime) ? Long.parseLong(heartBeatTime) * 1000 : DEFAULT_WAIT_SECOND;
                Thread.sleep(waitTime);
                executeTask();

            } catch (Throwable e) {
                logger.error("执行订单中同步业主信息至设备中失败", e);
            }
        }
    }

    /**
     * 执行任务
     */
    private void executeTask() throws Exception {
        if (INIT_MACHINE_STATE) {
            getAssessControlProcessImpl().initAssessControlProcess();
            INIT_MACHINE_STATE = false;
        }
        //查询设备信息
        List<MachineDto> machineDtos = queryMachines();

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
        heartbeatCloud(machineDtos, communityDtos.get(0));

    }

    /**
     * 心跳云端 接收指令
     *
     * @param machineDtos 设备信息
     */
    private void heartbeatCloud(List<MachineDto> machineDtos, CommunityDto communityDto) {

        if (machineDtos == null || machineDtos.size() == 0) {
            return;
        }

        for (MachineDto machineDto : machineDtos) {
            try {
                doHeartbeatCloud(machineDto, communityDto);
            } catch (Exception e) {
                logger.error(machineDto.getMachineCode() + "心跳失败", e);
            }
        }

    }

    /**
     * 心跳云端 接受指令
     *
     * @param machineDto
     */
    private void doHeartbeatCloud(MachineDto machineDto, CommunityDto communityDto) throws Exception {

        String url = MappingCacheFactory.getValue("CLOUD_API") + AccessControlConstant.MACHINE_HEARTBEART;

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
        paramIn.put("faceNum", getAssessControlProcessImpl().getFaceNum(machineDto));
        paramIn.put("lastOnTime", DateUtil.getTime());
        paramIn.put("statCode", "");
        paramIn.put("deviceType", machineDto.getMachineTypeCd());
        paramIn.put("versionCode", machineDto.getMachineVersion());

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
            doHeartbeatCloudResult(machineDto, data.getJSONObject(dataIndex), communityDto);
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
    private void doHeartbeatCloudResult(MachineDto machineDto, JSONObject commandInfo, CommunityDto communityDto) throws Exception {

        Assert.hasKeyAndValue(commandInfo, "taskcmd", "云端返回报文格式错误 未 包含指令编码 taskcmd" + commandInfo.toJSONString());
        Assert.hasKeyAndValue(commandInfo, "taskinfo", "云端返回报文格式错误 未 包含任务内容 taskinfo" + commandInfo.toJSONString());
        Assert.hasKeyAndValue(commandInfo, "taskId", "云端返回报文格式错误 未 包含任务ID taskId" + commandInfo.toJSONString());

        HeartbeatTaskDto heartbeatTaskDto = BeanConvertUtil.covertBean(commandInfo, HeartbeatTaskDto.class);

        switch (commandInfo.getInteger("taskcmd")) {
            case AccessControlConstant.CMD_ADD_UPDATE_FACE:
                addUpdateFace.addUpdateFace(machineDto, heartbeatTaskDto, communityDto);
                break;
            case AccessControlConstant.CMD_DELETE_FACE:
                deleteFace.deleteFace(machineDto, heartbeatTaskDto, communityDto);
                break;
            case AccessControlConstant.CMD_CLEAR_ALL_FACE:
                clearAllFace.clearFace(machineDto, heartbeatTaskDto, communityDto);
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
        ResultDto resultDto = machineService.getMachine(machineDto);
        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "查询设备失败");
        }
        List<MachineDto> machineDtos = (List<MachineDto>) resultDto.getData();
        return machineDtos;

    }


}
