package com.java110.things.dao;

import com.java110.things.entity.car.TempCarFeeConfigAttrDto;
import com.java110.things.entity.car.TempCarFeeConfigDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName ITempTempCarFeeConfigFeeConfigServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface ITempCarFeeConfigServiceDao {

    /**
     * 保存黑白名单信息
     *
     * @param carDto 黑白名单信息
     * @return 返回影响记录数
     */
    int saveTempCarFeeConfig(TempCarFeeConfigDto carDto);

    /**
     * 查询黑白名单信息
     *
     * @param carDto 黑白名单信息
     * @return
     */
    List<TempCarFeeConfigDto> getTempCarFeeConfigs(TempCarFeeConfigDto carDto);

    /**
     * 查询黑白名单总记录数
     *
     * @param carDto 黑白名单信息
     * @return
     */
    long getTempCarFeeConfigCount(TempCarFeeConfigDto carDto);

    /**
     * 修改黑白名单信息
     *
     * @param carDto 黑白名单信息
     * @return 返回影响记录数
     */
    int updateTempCarFeeConfig(TempCarFeeConfigDto carDto);

    /**
     * 删除指令
     *
     * @param carDto 指令id
     * @return 返回影响记录数
     */
    int delete(TempCarFeeConfigDto carDto);

    int saveTempCarFeeConfigAttr(TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto);

    /**
     * 查询临时车属性
     *
     * @param tempCarFeeConfigAttrDto
     * @return
     */
    List<TempCarFeeConfigAttrDto> getTempCarFeeConfigAttrs(TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto);

    int deleteTempCarFeeConfigAttr(TempCarFeeConfigAttrDto tempCarFeeConfigAttrDto);
}
