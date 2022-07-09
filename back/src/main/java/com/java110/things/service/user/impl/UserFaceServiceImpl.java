/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.things.service.user.impl;

import com.java110.things.dao.IMachineServiceDao;
import com.java110.things.entity.accessControl.HeartbeatTaskDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.CommunityPersonDto;
import com.java110.things.factory.AccessControlProcessFactory;
import com.java110.things.factory.ImageFactory;
import com.java110.things.service.machine.IMachineFaceService;
import com.java110.things.service.user.ICommunityPersonService;
import com.java110.things.service.user.IUserFaceService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @ClassName UserFaceServiceImpl
 * @Description TODO 用户人脸 处理 服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("userFaceServiceImpl")
public class UserFaceServiceImpl implements IUserFaceService {
    private Logger logger = LoggerFactory.getLogger(UserFaceServiceImpl.class);


    public static final String MACHINE_HAS_NOT_FACE = "-1"; // 设备没有人脸

    @Autowired
    private IMachineServiceDao machineServiceDaoImpl;

    @Autowired
    private ICommunityPersonService communityPersonServiceImpl;

    @Autowired
    private IMachineFaceService machineFaceService;

    @Override
    public ResultDto saveUserFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {

        List<MachineDto> machineDtos = machineServiceDaoImpl.getMachines(machineDto);

        Assert.listOnlyOne(machineDtos, "设备编码错误，不存在该设备");

        machineDto = machineDtos.get(0);

        //查询 当前用户是否在硬件中存在数据
        String faceId = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).getFace(machineDto, userFaceDto);

        if (faceId == null) {
            // 从本地磁盘中检查是否有人脸存在
            boolean exists = ImageFactory.existsImage( machineDto.getCommunityId() + File.separatorChar + userFaceDto.getUserId() + ".jpg");
            faceId = exists ? userFaceDto.getUserId() : null;
        }

        ResultDto resultDto = null;
        //调用新增人脸接口
        if (StringUtil.isEmpty(faceId) || MACHINE_HAS_NOT_FACE.equals(faceId)) {
            //存储人脸
            saveFace(machineDto, userFaceDto);
            resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).addFace(machineDto, userFaceDto);
        } else { //调用更新人脸接口
            updateFace(machineDto, userFaceDto);
            resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).updateFace(machineDto, userFaceDto);
        }

        if (resultDto == null) {
            return resultDto;
        }

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(userFaceDto.getUserId());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setState(resultDto.getCode() == ResultDto.SUCCESS ? "S" : "F");
        machineFaceDto.setMessage(resultDto.getMsg());

        machineFaceService.updateMachineFace(machineFaceDto);
        return resultDto;
    }

    @Override
    public ResultDto updateUserFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {
        return this.saveUserFace(machineDto, userFaceDto);
    }

    @Override
    public ResultDto deleteUserFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) throws Exception {
        List<MachineDto> machineDtos = machineServiceDaoImpl.getMachines(machineDto);
        Assert.listOnlyOne(machineDtos, "设备编码错误，不存在该设备");
        machineDto = machineDtos.get(0);

        ImageFactory.deleteImage(machineDto.getCommunityId() + File.separatorChar + heartbeatTaskDto.getTaskid() + ".jpg");
        ResultDto resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).deleteFace(machineDto, heartbeatTaskDto);

        if (resultDto == null || resultDto.getCode() != ResultDto.SUCCESS) {
            return resultDto;
        }


        MachineFaceDto machineFaceDto = null;
        machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(heartbeatTaskDto.getTaskinfo());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setUserId(heartbeatTaskDto.getTaskid());
        //machineFaceDto.set
        machineFaceService.deleteMachineFace(machineFaceDto);

        //删除 小区人员
        CommunityPersonDto communityPersonDto = new CommunityPersonDto();
        communityPersonDto.setExtPersonId(heartbeatTaskDto.getTaskid());
        communityPersonDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityPersonDto> communityPersonDtos = communityPersonServiceImpl.queryCommunityPerson(communityPersonDto);
        if (communityPersonDtos != null && communityPersonDtos.size() > 0) {
            communityPersonServiceImpl.deleteCommunityPerson(communityPersonDto);
        }
        return resultDto;
    }

    /**
     * 清空人脸信息
     *
     * @param machineDto
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto clearUserFace(MachineDto machineDto) throws Exception {

        List<MachineDto> machineDtos = machineServiceDaoImpl.getMachines(machineDto);
        Assert.listOnlyOne(machineDtos, "设备编码错误，不存在该设备");
        machineDto = machineDtos.get(0);
        //清空硬件下的人脸
        ImageFactory.clearImage(machineDto.getMachineCode());

        HeartbeatTaskDto heartbeatTaskDto = new HeartbeatTaskDto();

        ResultDto resultDto = AccessControlProcessFactory.getAssessControlProcessImpl(machineDto.getHmId()).clearFace(machineDto, heartbeatTaskDto);

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setMachineCode(machineDto.getMachineCode());
        machineFaceService.deleteMachineFace(machineFaceDto);

        return resultDto;
    }


    /**
     * 本地存储人脸
     *
     * @param userFaceDto
     */
    private void saveFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {

        String faceBase = userFaceDto.getFaceBase64();
        if (faceBase.contains("base64,")) {
            faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
        }

        String img = ImageFactory.GenerateImage(faceBase,  machineDto.getCommunityId() + File.separatorChar + userFaceDto.getUserId() + ".jpg");
        userFaceDto.setFaceBase64(img);
        MachineFaceDto machineFaceDto = BeanConvertUtil.covertBean(userFaceDto, MachineFaceDto.class);
        machineFaceDto.setId(SeqUtil.getId());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setFacePath("/" + machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + ".jpg");
        machineFaceDto.setState("W");
        machineFaceDto.setState("新增人脸待同步设备");
        machineFaceDto.setCommunityId(machineDto.getCommunityId());


        //machineFaceDto.set
        machineFaceService.saveMachineFace(machineFaceDto);

        //保存 小区人员
        CommunityPersonDto communityPersonDto = new CommunityPersonDto();
        communityPersonDto.setExtPersonId(userFaceDto.getUserId());
        communityPersonDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityPersonDto> communityPersonDtos = communityPersonServiceImpl.queryCommunityPerson(communityPersonDto);
        communityPersonDto.setPersonType(StringUtil.isEmpty(userFaceDto.getPersonType()) ? "2002" : userFaceDto.getPersonType());
        communityPersonDto.setFacePath("/" +  machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + ".jpg");
        communityPersonDto.setName(userFaceDto.getName());
        communityPersonDto.setIdNumber(userFaceDto.getIdNumber());
        communityPersonDto.setExtPersonId(userFaceDto.getUserId());
        if (communityPersonDtos == null || communityPersonDtos.size() < 1) {
            communityPersonDto.setPersonId(SeqUtil.getId());
            communityPersonServiceImpl.saveCommunityPerson(communityPersonDto);
        } else {
            communityPersonDto.setPersonId(communityPersonDtos.get(0).getPersonId());
            communityPersonServiceImpl.updateCommunityPerson(communityPersonDto);
        }
    }


    /**
     * 本地修改人脸
     *
     * @param userFaceDto
     */
    private void updateFace(MachineDto machineDto, UserFaceDto userFaceDto) throws Exception {

        ImageFactory.deleteImage(machineDto.getMachineCode() + File.separatorChar + userFaceDto.getUserId() + ".jpg");

        String faceBase = userFaceDto.getFaceBase64();
        if (faceBase.contains("base64,")) {
            faceBase = faceBase.substring(faceBase.indexOf("base64,") + 7);
        }

        String img = ImageFactory.GenerateImage(faceBase,  machineDto.getCommunityId() + File.separatorChar + userFaceDto.getUserId() + ".jpg");
        userFaceDto.setFaceBase64(img);

        MachineFaceDto machineFaceDto = new MachineFaceDto();
        machineFaceDto.setUserId(userFaceDto.getUserId());
        machineFaceDto.setMachineId(machineDto.getMachineId());
        machineFaceDto.setState("W");
        machineFaceDto.setState("更新人脸待同步设备");

        machineFaceService.updateMachineFace(machineFaceDto);


        //保存 小区人员
        CommunityPersonDto communityPersonDto = new CommunityPersonDto();
        communityPersonDto.setExtPersonId(userFaceDto.getUserId());
        communityPersonDto.setCommunityId(machineDto.getCommunityId());
        List<CommunityPersonDto> communityPersonDtos = communityPersonServiceImpl.queryCommunityPerson(communityPersonDto);
        communityPersonDto.setPersonType(StringUtil.isEmpty(userFaceDto.getPersonType()) ? "2002" : userFaceDto.getPersonType());
        communityPersonDto.setFacePath("/" +  machineDto.getCommunityId() + "/" + userFaceDto.getUserId() + ".jpg");
        communityPersonDto.setName(userFaceDto.getName());
        communityPersonDto.setIdNumber(userFaceDto.getIdNumber());
        communityPersonDto.setExtPersonId(userFaceDto.getUserId());
        if (communityPersonDtos == null || communityPersonDtos.size() < 1) {
            communityPersonDto.setPersonId(SeqUtil.getId());
            communityPersonServiceImpl.saveCommunityPerson(communityPersonDto);
        } else {
            communityPersonDto.setPersonId(communityPersonDtos.get(0).getPersonId());
            communityPersonServiceImpl.updateCommunityPerson(communityPersonDto);
        }

    }

}
