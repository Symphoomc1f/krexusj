package com.java110.things.adapt.accessControl.sxoa;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.DefaultAbstractAccessControlAdapt;
import com.java110.things.adapt.accessControl.ICallAccessControlService;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.cloud.MachineHeartbeatDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.ImageFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

/**
 * 智家狗 门禁对接文档
 * 文档：http://wiki.sxoa.com/index.html?file=home-%E9%A6%96%E9%A1%B5
 * 作者：吴学文
 * QQ:928255095
 */
@Service("sxoaAssessControlProcessAdapt")
public class SxoaAssessControlProcessAdapt extends DefaultAbstractAccessControlAdapt {

    private static Logger logger = LoggerFactory.getLogger(SxoaAssessControlProcessAdapt.class);
    //public static Function fun=new Function();

    private static final String DEFAULT_PORT = "8090"; //端口


    @Autowired
    private RestTemplate outRestTemplate;

    @Autowired
    private ICallAccessControlService callAccessControlServiceImpl;

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IMachineService machineService;

    public static final long START_TIME = new Date().getTime() - 1000 * 60 * 60;
    public static final long END_TIME = new Date().getTime() + 1000 * 60 * 60 * 24 * 365;

    public static final String OPEN_TYPE_FACE = "1000"; // 人脸开门

    //平台名称
    public static final String MANUFACTURER = "CJ_";

    public static final String VERSION = "0.2";


    public static final String CMD_ADD_FACE = "/face"; // 创建人脸

    public static final String ADD_MACHINE = "/v1.0/devicesmanage/create";
    public static final String ADD_PRIVILET = "/v1.0/auth/add";
    public static final String ADD_PRIVILET_BIND_MACHINE = "/v1.0/devicebind/bind";
    public static final String DELETE_PRIVILET_BIND_MACHINE = "/v1.0/devicebind/unbind";
    public static final String ADD_PRIVILET_BIND_USER = " /v1.0/userfeature/bind";


    public static final String CMD_OPEN_DOOR = "/api/device/bs/face/remoteOpenDoor"; // 开门

    public static final String CMD_REBOOT = "/api/device/bs/face/deviceReboot";// 重启设备

    public static final String CMD_ADD_USER = "/api/device/hqvtPerson/save"; // 添加人员
    public static final String CMD_SINGLE_PERSON_AUTH = "/api/device/bs/face/auth/singlePersonAuth"; // 添加人员
    public static final String CMD_UPDATE_USER = "/api/device/hqvtPerson/alter"; // 修改人员


    public static final String CMD_DELETE_FACE = "/person/delete"; //删除人脸
    public static final String CMD_RESET = "/device/reset"; //设备重置


    public static final String SN = "{sn}";

    public static final String FACE_URL = "ACCESS_CONTROL_FACE_URL";

    public static final String FACE_RESULT = "face_result";

    //图片后缀
    public static final String IMAGE_SUFFIX = ".jpg";


    @Override
    public ResultDto initAssessControlProcess(MachineDto machineDto) {
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
    }

    @Override
    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        return null;
    }

    @Override
    public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {

        FaceFeatureResultDto faceFeatureResultDto = SxCommomFactory.facefeature(outRestTemplate, machineDto, communityServiceImpl, userFaceDto);
        //添加设备
        JSONObject paramIn = new JSONObject();
        paramIn.put("residentId", userFaceDto.getUserId());
        paramIn.put("userId", faceFeatureResultDto.getFfUserId());
        paramIn.put("key", faceFeatureResultDto.getFfKey());
        paramIn.put("name", userFaceDto.getName());
        paramIn.put("roomNo", userFaceDto.getName() + "-1-1");
        paramIn.put("status", 1);
        paramIn.put("type", 1);

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), SxCommomFactory.getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + ADD_MACHINE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求添加设备失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加设备失败" + responseEntity);
        }

        Long id = paramOut.getJSONObject("data").getLong("id");

        paramIn = new JSONObject();
        paramIn.put("id", machineDto.getThirdMachineId());
        paramIn.put("userFeatureId", id);
        paramIn.put("type", 1);

        httpEntity = new HttpEntity(paramIn.toJSONString(), SxCommomFactory.getHeader(outRestTemplate));
        responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + ADD_PRIVILET_BIND_USER, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求添加设备失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加设备失败" + responseEntity);
        }

        return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("msg"));
    }


    @Override
    public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        FaceFeatureResultDto faceFeatureResultDto = SxCommomFactory.facefeature(outRestTemplate, machineDto, communityServiceImpl, userFaceDto);
        //添加设备
        JSONObject paramIn = new JSONObject();
        paramIn.put("residentId", userFaceDto.getUserId());
        paramIn.put("userId", faceFeatureResultDto.getFfUserId());
        paramIn.put("key", faceFeatureResultDto.getFfKey());
        paramIn.put("name", userFaceDto.getName());
        paramIn.put("roomNo", userFaceDto.getName() + "-1-1");
        paramIn.put("status", 1);
        paramIn.put("type", 1);

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), SxCommomFactory.getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + ADD_MACHINE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求添加设备失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加设备失败" + responseEntity);
        }
        return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("msg"));
    }

    @Override
    public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        //刷入外部编码
        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineCode(machineDto.getMachineCode());
        List<MachineFaceDto> faceDtos = callAccessControlServiceImpl.queryMachineFaces(machineFaceDto);
        if (faceDtos.size() < 1) {
            throw new IllegalArgumentException("该人脸还没有添加");
        }
        String appId = MappingCacheFactory.getValue("appId");
        HttpHeaders httpHeaders = SxCommomFactory.getHeader(outRestTemplate);
        HttpEntity httpEntity = new HttpEntity("", httpHeaders);
        //判断人员是否存在
        String url = MappingCacheFactory.getValue("BISEN_URL")
                + "/api/device/hqvtPerson/page?appId=" + appId + "&personGuid=" + faceDtos.get(0).getExtUserId();
        logger.debug("判断人员是否存在" + url);

        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResultDto(ResultDto.ERROR, "调用设备失败");
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getIntValue("code") != 0) {
            return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("msg"));
        }

        int total = paramOut.getJSONObject("data").getIntValue("total");

        if (total < 1) {
            return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
        }

        url = MappingCacheFactory.getValue("BISEN_URL")
                + "/api/device/hqvtPerson/delete/appId/" + appId + "/personGuid/" + faceDtos.get(0).getExtUserId();
        logger.debug("删除人员" + url);
        responseEntity = outRestTemplate.exchange(url, HttpMethod.DELETE, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_UPDATE_USER, url, responseEntity.getBody());
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getIntValue("code"), paramOut.getString("msg"));
    }

    @Override
    public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String password = MappingCacheFactory.getValue(MappingCacheFactory.SYSTEM_DOMAIN, "ASSESS_PASSWORD");
        String url = "http://" + machineDto.getMachineIp() + CMD_RESET;
        JSONObject param = new JSONObject();
        param.put("delete", false);
        param.put("pass", password);
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), httpHeaders);
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_RESET, param.toJSONString(), responseEntity.getBody());
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getBoolean("success") ? ResultDto.SUCCESS : ResultDto.ERROR, paramOut.getString("code") + paramOut.getString("msg"));
    }


    /**
     * 扫描设备
     *
     * @return
     */
    @Override
    public List<MachineDto> scanMachine() throws Exception {
        return null;
    }


    @Override
    public void restartMachine(MachineDto machineDto) {
        String url = MappingCacheFactory.getValue("BISEN_URL") + CMD_REBOOT;
        String appId = MappingCacheFactory.getValue("appId");
        JSONObject postParameters = new JSONObject();
        postParameters.put("appId", appId);
        postParameters.put("deviceNo", machineDto.getMachineCode());
        HttpHeaders httpHeaders = SxCommomFactory.getHeader(outRestTemplate);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_REBOOT, postParameters.toJSONString(), responseEntity.getBody());
    }

    @Override
    public void openDoor(MachineDto machineDto) {
        String url = MappingCacheFactory.getValue("BISEN_URL") + CMD_OPEN_DOOR;
        String appId = MappingCacheFactory.getValue("appId");

        JSONObject postParameters = new JSONObject();
        postParameters.put("appId", appId);
        postParameters.put("deviceNo", machineDto.getMachineCode());

        HttpHeaders httpHeaders = SxCommomFactory.getHeader(outRestTemplate);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity(postParameters, httpHeaders);
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, postParameters.toJSONString(), responseEntity.getBody());
    }

    @Override
    public String httpFaceResult(MachineDto machineDto, String data) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject body = JSONObject.parseObject(data);

            String userId = body.containsKey("customId") ? body.getString("customId") : "";
            String userName = "";
            if (!StringUtils.isEmpty(userId)) {
                MachineFaceDto machineFaceDto = new MachineFaceDto();
                machineFaceDto.setExtUserId(userId);
                machineFaceDto.setMachineId(machineDto.getMachineId());
                List<MachineFaceDto> machineFaceDtos = notifyAccessControlService.queryMachineFaces(machineFaceDto);
                if (machineFaceDtos != null && machineFaceDtos.size() > 0) {
                    userName = machineFaceDtos.get(0).getName();
                    userId = machineFaceDtos.get(0).getUserId();
                }
            } else {
                userName = body.getString("personName");
            }
            if (StringUtil.isEmpty(userId)) {
                userId = "-1";
            }
            if (StringUtil.isEmpty(userName)) {
                userName = "门禁未上报";
            }

            OpenDoorDto openDoorDto = new OpenDoorDto();
            openDoorDto.setFace(ImageFactory.encodeImageToBase64(body.getString("picture")));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setMachineCode(machineDto.getMachineCode());
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity(body.getString("similarityList"));

            freshOwnerFee(openDoorDto);

            notifyAccessControlService.saveFaceResult(openDoorDto);

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
        }
        resultParam.put("result", 1);
        resultParam.put("success", true);
        return resultParam.toJSONString();//未找到设备

    }

    public static void main(String[] args) {
        String img = ImageFactory.getBase64ByImgUrl("https://bisen-temp-iot.oss-cn-beijing.aliyuncs.com/bisen-temp-iot/bs/face/40230b92623d42b59446a07185a1df2f.jpg");
        System.out.println(img);
    }

    @Override
    public String heartbeat(String data, String machineCode) throws Exception {
        JSONObject info = JSONObject.parseObject(data);

        //设备ID
        //String machineCode = info.getString("deviceKey");
        String heartBeatTime = null;
        //heartBeatTime = info.getString("time");
        heartBeatTime = DateUtil.getNow(DateUtil.DATE_FORMATE_STRING_A);
        MachineHeartbeatDto machineHeartbeatDto = new MachineHeartbeatDto(machineCode, heartBeatTime);
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        notifyAccessControlService.machineHeartbeat(machineHeartbeatDto);
        JSONObject resultParam = new JSONObject();
        resultParam.put("result", 1);
        resultParam.put("success", true);
        return resultParam.toJSONString();//未找到设备
    }

    @Override
    public ResultDto addMachine(MachineDto machineDto) {

        //根据地区查询
        //SxAreaCodeDto sxAreaCodeDto = SxCommomFactory.getSxAreaCode(outRestTemplate);

        //判断小区是否 存在
        if (!SxCommomFactory.hasSxCommunity(outRestTemplate, machineDto.getCommunityId(), communityServiceImpl)) {
            //添加小区
            SxCommomFactory.addSxCommunity(outRestTemplate, machineDto, communityServiceImpl);
        }

        SxCommunityDto sxCommunityDto = SxCommomFactory.getSxCommunity(outRestTemplate, machineDto.getCommunityId(), communityServiceImpl);

        //添加设备
        JSONObject paramIn = new JSONObject();
        paramIn.put("dDtId", machineDto.getMachineId());
        paramIn.put("dName", machineDto.getMachineName());
        paramIn.put("dDescription", machineDto.getCommunityId());
        paramIn.put("dDatId", machineDto.getMachineVersion());
        paramIn.put("dCode", machineDto.getMachineCode());
        paramIn.put("dViId", sxCommunityDto.getViId());

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), SxCommomFactory.getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + ADD_MACHINE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求添加设备失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加设备失败" + responseEntity);
        }

        SxAddMachineResultDto sxAddMachineResultDto = BeanConvertUtil.covertBean(paramOut.getJSONArray("data").getJSONObject(0), SxAddMachineResultDto.class);

        //添加权限组
        paramIn = new JSONObject();
        paramIn.put("name", machineDto.getMachineName());
        paramIn.put("status", 1);
        paramIn.put("locationId", sxCommunityDto.getLocationId());
        paramIn.put("villageId", SxCommomFactory.getSxCommunity(outRestTemplate, machineDto.getCommunityId(), communityServiceImpl).getViId());

        httpEntity = new HttpEntity(paramIn.toJSONString(), SxCommomFactory.getHeader(outRestTemplate));
        responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + ADD_PRIVILET, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求添加权限组失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加权限组失败" + responseEntity);
        }

        String locationId = paramOut.getJSONObject("data").getString("locationId");

        //添加权限组
        paramIn = new JSONObject();
        paramIn.put("id", locationId);
        paramIn.put("type", 1);
        paramIn.put("deviceId", machineDto.getMachineId());

        httpEntity = new HttpEntity(paramIn.toJSONString(), SxCommomFactory.getHeader(outRestTemplate));
        responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + ADD_PRIVILET_BIND_MACHINE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求权限组设备失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加权限组设备失败" + responseEntity);
        }

        MachineDto machineDto1 = new MachineDto();
        machineDto1.setMachineId(machineDto.getMachineId());
        machineDto1.setThirdMachineId(locationId);

        try {
            machineService.updateMachine(machineDto1);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDto(ResultDto.ERROR, "更新第三方平台ID失败");
        }
        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto updateMachine(MachineDto machineDto) {

        return new ResultDto(ResultDto.SUCCESS, "成功");
    }

    @Override
    public ResultDto deleteMachine(MachineDto machineDto) {

        //解绑 权限组和 设备
        //添加权限组
        JSONObject paramIn = new JSONObject();
        paramIn.put("id", machineDto.getThirdMachineId());
        paramIn.put("type", 1);
        paramIn.put("deviceId", machineDto.getMachineId());

        HttpEntity httpEntity = new HttpEntity(paramIn.toJSONString(), SxCommomFactory.getHeader(outRestTemplate));
        ResponseEntity<String> responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + DELETE_PRIVILET_BIND_MACHINE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求权限组设备失败" + responseEntity);
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加权限组设备失败" + responseEntity);
        }

        paramIn = new JSONObject();
        paramIn.put("dtId", machineDto.getMachineId());

        httpEntity = new HttpEntity(paramIn.toJSONString(), SxCommomFactory.getHeader(outRestTemplate));
        responseEntity = outRestTemplate.exchange(MappingCacheFactory.getValue("SXOA_URL") + DELETE_PRIVILET_BIND_MACHINE, HttpMethod.POST, httpEntity, String.class);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("请求权限组设备失败" + responseEntity);
        }

        paramOut = JSONObject.parseObject(responseEntity.getBody());

        if (paramOut.getInteger("code") != 0) {
            throw new IllegalStateException("添加权限组设备失败" + responseEntity);
        }


        return new ResultDto(ResultDto.SUCCESS, "成功");
    }


}