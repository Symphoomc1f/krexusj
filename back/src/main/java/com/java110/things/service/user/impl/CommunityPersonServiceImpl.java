package com.java110.things.service.user.impl;


import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.ICommunityPersonServiceDao;
import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.CommunityPersonDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.ImageFactory;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineFaceService;
import com.java110.things.service.user.ICommunityPersonService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CommunityPersonServiceImpl
 * @Description TODO 小区人员管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("communityPersonServiceImpl")
public class CommunityPersonServiceImpl implements ICommunityPersonService {

    @Autowired
    private ICommunityPersonServiceDao communityPersonServiceDao;

    @Autowired
    private ICommunityService communityServiceImpl;

    @Autowired
    private IMachineServiceDao machineServiceDaoImpl;

    @Autowired
    private IMachineFaceService machineFaceServiceImpl;

    public static final String MACHINE_HAS_NOT_FACE = "-1";

    /**
     * 查询小区人员信息
     *
     * @param communityPersonDto 小区人员信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        int page = communityPersonDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            communityPersonDto.setPage((page - 1) * communityPersonDto.getRow());
        }
        long count = communityPersonServiceDao.getCommunityPersonCount(communityPersonDto);
        int totalPage = (int) Math.ceil((double) count / (double) communityPersonDto.getRow());
        List<CommunityPersonDto> communityPersonDtoList = null;
        if (count > 0) {
            communityPersonDtoList = communityPersonServiceDao.getCommunityPersons(communityPersonDto);
            freshUserFace(communityPersonDtoList);
        } else {
            communityPersonDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, communityPersonDtoList);
        return resultDto;
    }

    /**
     * 刷新人脸地址
     *
     * @param communityPersonDtos
     */
    private void freshUserFace(List<CommunityPersonDto> communityPersonDtos) {
        String faceUrl = MappingCacheFactory.getValue("ACCESS_CONTROL_FACE_URL");

        for (CommunityPersonDto communityPersonDto : communityPersonDtos) {
            communityPersonDto.setFacePath(faceUrl + communityPersonDto.getFacePath());
        }
    }

    /**
     * 查询小区人员信息
     *
     * @param communityPersonDto 小区人员信息
     * @return
     * @throws Exception
     */
    @Override
    public List<CommunityPersonDto> queryCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        int page = communityPersonDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            communityPersonDto.setPage((page - 1) * communityPersonDto.getRow());
        }
        List<CommunityPersonDto> communityPersonDtoList = null;
        communityPersonDtoList = communityPersonServiceDao.getCommunityPersons(communityPersonDto);
        return communityPersonDtoList;
    }

    @Override
    public int saveCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        int count = communityPersonServiceDao.saveCommunityPerson(communityPersonDto);
        return count;
    }

    /**
     * 修改小区人员信息
     *
     * @param communityPersonDto 小区人员对象
     * @return
     */
    @Override
    public int updateCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        int count = communityPersonServiceDao.updateCommunityPerson(communityPersonDto);
        return count;
    }

    @Override
    public int deleteCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception {
        communityPersonDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = communityPersonServiceDao.updateCommunityPerson(communityPersonDto);
        return count;
    }

    @Override
    public ResponseEntity<String> personToMachine(String personId, String machineId, String startTime, String endTime) throws Exception {
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineId(machineId);
        List<MachineDto> machineDtos = machineServiceDaoImpl.getMachines(machineDto);

        Assert.listOnlyOne(machineDtos, "设备编码错误，不存在该设备");

        machineDto = machineDtos.get(0);

        CommunityPersonDto communityPersonDto = new CommunityPersonDto();
        communityPersonDto.setPersonId(personId);

        List<CommunityPersonDto> communityPersonDtos = communityPersonServiceDao.getCommunityPersons(communityPersonDto);

        Assert.listOnlyOne(communityPersonDtos, "人员不存在");

        if (StringUtils.isEmpty(communityPersonDtos.get(0).getFacePath())) {
            return ResultDto.error("人员人脸不存在");
        }


        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(communityPersonDtos.get(0).getExtPersonId());
        machineFaceDto.setMachineId(machineId);
        List<MachineFaceDto> machineFaceDtos = machineFaceServiceImpl.queryMachineFace(machineFaceDto);

        String faceUrl = MappingCacheFactory.getValue("ACCESS_CONTROL_FACE_URL");
        UserFaceDto userFaceDto = new UserFaceDto();
        userFaceDto.setUserId(communityPersonDtos.get(0).getExtPersonId());
        userFaceDto.setFaceBase64(ImageFactory.getBase64ByImgUrl(faceUrl + communityPersonDtos.get(0).getFacePath()));
        userFaceDto.setEndTime(endTime);
        userFaceDto.setStartTime(startTime);
        userFaceDto.setFacePath(communityPersonDtos.get(0).getFacePath());
        userFaceDto.setMachineCode(machineDto.getMachineCode());


        //查询 当前用户是否在硬件中存在数据
        String faceId = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).getFace(machineDto, userFaceDto);


        ResultDto resultDto = null;
        //调用新增人脸接口
        if (machineFaceDtos == null || machineFaceDtos.size() < 1 || MACHINE_HAS_NOT_FACE.equals(faceId)) {
            //存储人脸
            saveFace(machineDto, userFaceDto);
            resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).addFace(machineDto, userFaceDto);
        } else { //调用更新人脸接口
            resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).updateFace(machineDto, userFaceDto);
        }

        if (resultDto == null) {
            return ResultDto.success();
        }

        machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(userFaceDto.getUserId());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setState(resultDto.getCode() == ResultDto.SUCCESS ? "S" : "F");
        machineFaceDto.setMessage(resultDto.getMsg());

        machineFaceServiceImpl.updateMachineFace(machineFaceDto);
        return ResultDto.success();
    }

    /**
     * 本地存储人脸
     *
     * @param userFaceDto
     */
    private void saveFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {

        MachineFaceDto machineFaceDto = BeanConvertUtil.covertBean(userFaceDto, MachineFaceDto.class);
        machineFaceDto.setId(SeqUtil.getId());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setFacePath(userFaceDto.getFacePath());
        machineFaceDto.setState("W");
        machineFaceDto.setState("新增人脸待同步设备");
        machineFaceDto.setCommunityId(machineDto.getCommunityId());
        //machineFaceDto.set
        machineFaceServiceImpl.saveMachineFace(machineFaceDto);
    }


}
