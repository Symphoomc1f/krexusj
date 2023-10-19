package com.java110.things.service.machine.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.constant.SystemConstant;
import com.java110.things.dao.IMachineFaceServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.machine.MachineFaceDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.service.machine.IMachineFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName MachineFaceServiceImpl
 * @Description TODO 设备管理服务类
 * @Author wuxw
 * @Date 2020/5/14 14:49
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/

@Service("machineFaceServiceImpl")
public class MachineFaceServiceImpl implements IMachineFaceService {

    @Autowired
    private IMachineFaceServiceDao machineFaceServiceDao;

    /**
     * 添加设备信息
     *
     * @param machineFaceDto 设备对象
     * @return
     */
    @Override
    public ResultDto saveMachineFace(MachineFaceDto machineFaceDto) throws Exception {
        int count = machineFaceServiceDao.saveMachineFace(machineFaceDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public ResultDto updateMachineFace(MachineFaceDto machineFaceDto) throws Exception {
        int count = machineFaceServiceDao.updateMachineFace(machineFaceDto);
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
     * @param machineFaceDto 设备信息
     * @return
     * @throws Exception
     */
    @Override
    public ResultDto getMachineFace(MachineFaceDto machineFaceDto) throws Exception {
        int page = machineFaceDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            machineFaceDto.setPage((page - 1) * machineFaceDto.getRow());
        }
        long count = machineFaceServiceDao.getMachineFaceCount(machineFaceDto);
        int totalPage = (int) Math.ceil((double) count / (double) machineFaceDto.getRow());
        List<MachineFaceDto> machineFaceDtoList = null;
        if (count > 0) {
            machineFaceDtoList = machineFaceServiceDao.getMachineFaces(machineFaceDto);
            //刷新人脸地址
            freshUserFace(machineFaceDtoList);
        } else {
            machineFaceDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, machineFaceDtoList);
        return resultDto;
    }

    @Override
    public List<MachineFaceDto> queryMachineFace(MachineFaceDto machineFaceDto)  {
        int page = machineFaceDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            machineFaceDto.setPage((page - 1) * machineFaceDto.getRow());
        }

        List<MachineFaceDto> machineFaceDtoList = null;
        machineFaceDtoList = machineFaceServiceDao.getMachineFaces(machineFaceDto);

        return machineFaceDtoList;
    }


    /**
     * 刷新人脸地址
     *
     * @param machineFaceDtoList
     */
    private void freshUserFace(List<MachineFaceDto> machineFaceDtoList) {
        String faceUrl = MappingCacheFactory.getValue("ACCESS_CONTROL_FACE_URL");

        for (MachineFaceDto machineFaceDto : machineFaceDtoList) {
            machineFaceDto.setFacePath(faceUrl + machineFaceDto.getFacePath());
        }
    }

    @Override
    public ResultDto deleteMachineFace(MachineFaceDto machineFaceDto) throws Exception {
        machineFaceDto.setStatusCd(SystemConstant.STATUS_INVALID);
        int count = machineFaceServiceDao.updateMachineFace(machineFaceDto);
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }


}
