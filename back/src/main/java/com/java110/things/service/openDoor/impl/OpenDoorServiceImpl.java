package com.java110.things.service.openDoor.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IOpenDoorServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.openDoor.IOpenDoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OpenDoorServiceImpl
 * @Description TODO 设备管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("openDoorServiceImpl")
public class OpenDoorServiceImpl implements IOpenDoorService {

    @Autowired
    private IOpenDoorServiceDao openDoorServiceDao;

    /**
     * 添加设备信息
     *
     * @param openDoorDto 设备对象
     * @return
     */
    @Override
    public ResultDto saveOpenDoor(OpenDoorDto openDoorDto) throws Exception {
        int count = openDoorServiceDao.saveOpenDoor(openDoorDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    /**
     * 查询设备信息
     *
     * @param openDoorDto 设备信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getOpenDoor(OpenDoorDto openDoorDto) throws Exception {
        int page = openDoorDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            openDoorDto.setPage((page - 1) * openDoorDto.getRow());
        }
        long count = openDoorServiceDao.getOpenDoorCount(openDoorDto);
        int totalPage = (int) Math.ceil((double) count / (double) openDoorDto.getRow());
        List<OpenDoorDto> openDoorDtoList = null;
        if (count > 0) {
            openDoorDtoList = openDoorServiceDao.getOpenDoors(openDoorDto);
            //刷新人脸地址
            freshUserFace(openDoorDtoList);
        } else {
            openDoorDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, openDoorDtoList);
        return resultDto;
    }

    /**
     * 刷新人脸地址
     *
     * @param openDoorDtoList
     */
    private void freshUserFace(List<OpenDoorDto> openDoorDtoList) {
        String faceUrl = MappingCacheFactory.getValue("ACCESS_CONTROL_FACE_URL");

        for (OpenDoorDto openDoorDto : openDoorDtoList) {
            openDoorDto.setModelFace(faceUrl + openDoorDto.getModelFace());
            openDoorDto.setFace(faceUrl+openDoorDto.getFace());
        }
    }


}
