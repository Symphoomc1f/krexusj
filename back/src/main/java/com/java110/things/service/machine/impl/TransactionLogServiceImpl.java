package com.java110.things.service.machine.impl;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.dao.ITransactionLogServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.machine.TransactionLogDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.machine.ITransactionLogService;
import com.java110.things.service.machine.ITransactionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TransactionLogServiceImpl
 * @Description TODO 操作日志服务类
 * @Author wuxw
 * @Date 2020/5/22 14:54
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
@Service("transactionLogServiceImpl")
public class TransactionLogServiceImpl implements ITransactionLogService {

    @Autowired
    ITransactionLogServiceDao transactionLogServiceDao;

    @Override
    public ResultDto saveTransactionLog(TransactionLogDto transactionLogDto) {

        long count = transactionLogServiceDao.saveTransactionLog(transactionLogDto);

        ResultDto resultDto = null;
        if (count < 1) {
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG);
        } else {
            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG);
        }
        return resultDto;
    }

    @Override
    public ResultDto getTransactionLogs(TransactionLogDto transactionLogDto) {
        int page = transactionLogDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            transactionLogDto.setPage((page - 1) * transactionLogDto.getRow());
        }
        long count = transactionLogServiceDao.getTransactionLogCount(transactionLogDto);
        int totalPage = (int) Math.ceil((double) count / (double) transactionLogDto.getRow());
        List<TransactionLogDto> transactionLogDtoList = null;
        if (count > 0) {
            transactionLogDtoList = transactionLogServiceDao.getTransactionLogs(transactionLogDto);
        } else {
            transactionLogDtoList = new ArrayList<>();
        }
        ResultDto resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, count, totalPage, transactionLogDtoList);
        return resultDto;
    }
}
