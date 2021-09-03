package com.java110.things.dao;

import com.java110.things.entity.machine.TransactionLogDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName ITransctionLogServiceDao
 * @Description TODO 传输日志记录表
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface ITransactionLogServiceDao {

    /**
     * 保存交互日志信息
     *
     * @param transactionLog 交互日志信息
     * @return 返回影响记录数
     */
    int saveTransactionLog(TransactionLogDto transactionLog);

    /**
     * 查询交互日志信息
     * @param transactionLog 交互日志信息
     * @return
     */
    List<TransactionLogDto> getTransactionLogs(TransactionLogDto transactionLog);

    /**
     * 查询交互日志总记录数
     * @param transactionLog 交互日志信息
     * @return
     */
    long getTransactionLogCount(TransactionLogDto transactionLog);
    

}
