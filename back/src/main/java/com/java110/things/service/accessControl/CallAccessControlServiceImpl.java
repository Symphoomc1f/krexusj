package com.java110.things.service.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.ICallAccessControlService;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IOperateLogService;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

/**
 * @ClassName NotifyAccessControlServcieImpl
 * @Description TODO 门禁 反向调用接口实现类
 * @Author wuxw
 * @Date 2020/5/15 19:11
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Service("callAccessControlServiceImpl")
public class CallAccessControlServiceImpl implements ICallAccessControlService {
    Logger logger = LoggerFactory.getLogger(CallAccessControlServiceImpl.class);

    @Autowired
    IMachineServiceDao machineServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IOperateLogService operateLogServiceImpl;


    @Override
    public void uploadMachine(MachineDto machineDto) {

        if (machineDto == null) {
            throw new ServiceException(Result.SYS_ERROR, "设备信息不能为空");
        }

        logger.debug("machineDto", machineDto.toString());
        //如果设备ID为空的情况
        if (StringUtil.isEmpty(machineDto.getMachineId())) {
            machineDto.setMachineId(UUID.randomUUID().toString());
        }
        //设备编码
        if (StringUtil.isEmpty(machineDto.getMachineCode())) {
            throw new ServiceException(Result.SYS_ERROR, "未包含设备编码，如果设备没有编码可以写mac或者ip,只要标识唯一就好");
        }
        //设备IP
        if (StringUtil.isEmpty(machineDto.getMachineIp())) {
            throw new ServiceException(Result.SYS_ERROR, "未包含设备ip");
        }
        //设备mac
        if (StringUtil.isEmpty(machineDto.getMachineMac())) {
            machineDto.setMachineMac(machineDto.getMachineIp());
        }
        // 设备名称
        if (StringUtil.isEmpty(machineDto.getMachineName())) {
            machineDto.setMachineName(machineDto.getMachineCode());
        }
        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ACCESS_CONTROL);

        MachineDto tmpMachineDto = new MachineDto();
        tmpMachineDto.setMachineVersion(machineDto.getMachineVersion());
        tmpMachineDto.setMachineCode(machineDto.getMachineCode());
        tmpMachineDto.setMachineIp(machineDto.getMachineIp());
        tmpMachineDto.setMachineMac(machineDto.getMachineMac());

        long machineCnt = machineServiceDao.getMachineCount(tmpMachineDto);
        if (machineCnt > 0) {
            logger.debug("该设备已经添加无需再添加" + tmpMachineDto.toString());
            return;
        }

        machineServiceDao.saveMachine(machineDto);

        try {
            //上报云端
            uploadCloud(machineDto);
        } catch (Exception e) {
            logger.error("上报云端失败", e);
            throw new ServiceException(Result.SYS_ERROR, "上报云端失败" + e);

        }
    }

    /**
     * 操作日志记录
     * @param operateLogDto 日志对象，当logId 在数据库中不存在是做添加，存在时 做修改
     */
    @Override
    public void saveOrUpdateOperateLog(OperateLogDto operateLogDto) {
        if(StringUtil.isEmpty(operateLogDto.getLogId())){
            throw new ServiceException(Result.SYS_ERROR, "未包含日志ID，一般为 和设备交互请求返回的唯一编码 如流水");
        }

        if(StringUtil.isEmpty(operateLogDto.getMachineId())){
            throw new ServiceException(Result.SYS_ERROR, "设备ID，可以从machineDto 对象中获取，如果没有可以用machineCode 查一下库");
        }
//        if(StringUtil.isEmpty(operateLogDto.getState())){
//            throw new ServiceException(Result.SYS_ERROR, "日志状态，请求成功 10001,返回成功10002,操作失败 10003");
//        }

        if(StringUtil.isEmpty(operateLogDto.getOperateType())){
            throw new ServiceException(Result.SYS_ERROR, "操作类型，可以查看t_dict 表");
        }

        if(StringUtil.isEmpty(operateLogDto.getState())){
            operateLogDto.setState("10001"); // 默认设置为请求
        }

        operateLogDto.setMachineTypeCd("9998");


        operateLogServiceImpl.saveOperateLog(operateLogDto);
    }

    /**
     * 上报云端
     *
     * @param machineDto
     */
    private void uploadCloud(MachineDto machineDto) throws Exception {
        //查询 小区信息
        CommunityDto communityDto = new CommunityDto();
        ResultDto resultDto = communityServiceImpl.getCommunity(communityDto);

        if (resultDto.getCode() != ResponseConstant.SUCCESS) {
            throw new ThreadException(Result.SYS_ERROR, "查询小区信息失败");
        }

        List<CommunityDto> communityDtos = (List<CommunityDto>) resultDto.getData();

        if (communityDtos == null || communityDtos.size() < 1) {
            throw new ThreadException(Result.SYS_ERROR, "当前还没有设置小区，请先设置小区");
        }
        String url = MappingCacheFactory.getValue("CLOUD_API") + "/api/machine.listMachines?page=1&row=1&communityId="
                + communityDtos.get(0).getCommunityId() + "&machineCode=" + machineDto.getMachineCode();
        //查询云端是否存在该设备
        ResponseEntity<String> responseEntity = HttpFactory.exchange(restTemplate, url, "", HttpMethod.GET);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new ServiceException(Result.SYS_ERROR, responseEntity.getBody());
        }
        JSONObject result = JSONObject.parseObject(responseEntity.getBody());

        int total = result.getInteger("total");
        //云端已经存在
        if (total > 0) {
            return;
        }
        url = MappingCacheFactory.getValue("CLOUD_API") + "/api/machine.saveMachine";
        JSONObject paramIn = new JSONObject();
        paramIn.put("machineCode", machineDto.getMachineCode());
        paramIn.put("machineVersion", machineDto.getMachineVersion());
        paramIn.put("machineName", machineDto.getMachineName());
        paramIn.put("machineIp", machineDto.getMachineIp());
        paramIn.put("machineMac", machineDto.getMachineMac());
        paramIn.put("machineTypeCd", machineDto.getMachineTypeCd());
        paramIn.put("direction", "3306");//这里默认写成进
        paramIn.put("authCode", "123");
        paramIn.put("locationTypeCd", "4000");
        paramIn.put("locationObjId", "-1");
        paramIn.put("communityId", communityDtos.get(0).getCommunityId());
        ResponseEntity<String> tmpResponseEntity = HttpFactory.exchange(restTemplate, url, paramIn.toJSONString(), HttpMethod.POST);

        if (tmpResponseEntity.getStatusCode() != HttpStatus.OK) {
            logger.error("上报云端门禁失败" + tmpResponseEntity.getBody());
        }


    }
}
