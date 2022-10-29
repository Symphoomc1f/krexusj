package com.java110.things.service.machine;

import com.java110.things.entity.machine.TransactionLogDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName ITransactionLogService
 * @Description TODO 硬件交互日志记录类
 * @Author wuxw
 * @Date 2020/5/22 14:50
 * @Version 1.0
 * add by wuxw 2020/5/22
 **/
public interface ITransactionLogService {

    /**
     * 保存交互日志
     * @param transactionLogDto 交互日志对象
     * @return
     */
    ResultDto saveTransactionLog(TransactionLogDto transactionLogDto);

    /**
     * 查询交互日志
     * @param transactionLogDto 交互日志对象
     * @return
     */
    ResultDto getTransactionLogs(TransactionLogDto transactionLogDto);
}
