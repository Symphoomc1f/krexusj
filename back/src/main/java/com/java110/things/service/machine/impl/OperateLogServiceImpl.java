package com.java110.things.service.machine.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.IOperateLogServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.machine.IOperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OperateLogServiceImpl
 * @Description TODO 操作日志服务类
 * @Author wuxw
 * @Date 2020/5/22 14:54
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
@Service("operateLogServiceImpl")
public class OperateLogServiceImpl implements IOperateLogService {

    @Autowired
    IOperateLogServiceDao operateLogServiceDao;

    @Override
    public ResultDto saveOperateLog(OperateLogDto operateLogDto) {
        OperateLogDto tmpOperateLogDto = new OperateLogDto();
        tmpOperateLogDto.setLogId(operateLogDto.getLogId());
        long cnt = operateLogServiceDao.getOperateLogCount(tmpOperateLogDto);
        int count = 0;
        if (cnt > 0) {
            count = operateLogServiceDao.updateOperateLog(operateLogDto);
        } else {
            count = operateLogServiceDao.saveOperateLog(operateLogDto);
        }
        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public ResultDto getOperateLogs(OperateLogDto operateLogDto) {
        int page = operateLogDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            operateLogDto.setPage((page - 1) * operateLogDto.getRow());
        }
        long count = operateLogServiceDao.getOperateLogCount(operateLogDto);
        int totalPage = (int) Math.ceil((double) count / (double) operateLogDto.getRow());
        List<OperateLogDto> operateLogDtoList = null;
        if (count > 0) {
            operateLogDtoList = operateLogServiceDao.getOperateLogs(operateLogDto);
        } else {
            operateLogDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, operateLogDtoList);
        return resultDto;
    }

    @Override
    public List<OperateLogDto> queryOperateLogs(OperateLogDto operateLogDto) {
        int page = operateLogDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            operateLogDto.setPage((page - 1) * operateLogDto.getRow());
        }
        List<OperateLogDto> operateLogDtoList = null;
        operateLogDtoList = operateLogServiceDao.getOperateLogs(operateLogDto);
        return operateLogDtoList;
    }
}
