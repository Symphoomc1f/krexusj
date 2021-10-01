package com.java110.things.service.attendance;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.MachineConstant;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.community.CommunityDto;
import com.java110.things.entity.machine.MachineCmdDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.Result;
import com.java110.things.exception.ServiceException;
import com.java110.things.exception.ThreadException;
import com.java110.things.factory.HttpFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineCmdService;
import com.java110.things.util.Assert;
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
 * @ClassName CallAttendanceServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/27 16:04
 * @Version 1.0
 * add by wuxw 2020/5/27
 **/
@Service("callAttendanceServiceImpl")
public class CallAttendanceServiceImpl implements ICallAttendanceService {

    private static Logger logger = LoggerFactory.getLogger(CallAttendanceServiceImpl.class);

    private static final int DEFAULT_PAGE = 1; // 默认获取指令 页
    private static final int DEFAULT_ROW = 5; //默认指令获取最大数

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IMachineCmdService machineCmdServiceImpl;


    @Autowired
    IMachineServiceDao machineServiceDao;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public MachineDto getMachine(MachineDto machineDto) {

        List<MachineDto> machineDtos = machineServiceDao.getMachines(machineDto);

        if (machineDtos == null || machineDtos.size() < 1) {
            return null;
        }

        return machineDtos.get(0);

    }


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
        machineDto.setMachineTypeCd(MachineConstant.MACHINE_TYPE_ATTENDANCE);

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

    @Override
    public List<MachineCmdDto> getMachineCmds(MachineDto machineDto) throws Exception {

        Assert.notNull(machineDto, "设备信息不能为空");

        Assert.hasText(machineDto.getMachineCode(), "设备编码不能为空");

        MachineCmdDto machineCmdDto = new MachineCmdDto();
        machineCmdDto.setMachineCode(machineDto.getMachineCode());
        machineCmdDto.setPage(DEFAULT_PAGE);
        if (machineDto.getPage() < 1) {
            machineCmdDto.setRow(DEFAULT_ROW);
        }
        ResultDto resultDto = machineCmdServiceImpl.getMachineCmd(machineCmdDto);

        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return null;
        }

        List<MachineCmdDto> machineCmdDtos = (List<MachineCmdDto>) resultDto.getData();

        return machineCmdDtos;
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
