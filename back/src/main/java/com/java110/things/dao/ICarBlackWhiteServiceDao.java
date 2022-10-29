package com.java110.things.dao;

import com.java110.things.entity.car.CarAttrDto;
import com.java110.things.entity.car.CarBlackWhiteDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName ICarServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface ICarBlackWhiteServiceDao {

    /**
     * 保存黑白名单信息
     *
     * @param carBlackWhiteDto 黑白名单信息
     * @return 返回影响记录数
     */
    int saveCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto);

    /**
     * 查询黑白名单信息
     *
     * @param carBlackWhiteDto 黑白名单信息
     * @return
     */
    List<CarBlackWhiteDto> getCarBlackWhites(CarBlackWhiteDto carBlackWhiteDto);

    /**
     * 查询黑白名单总记录数
     *
     * @param carBlackWhiteDto 黑白名单信息
     * @return
     */
    long getCarBlackWhiteCount(CarBlackWhiteDto carBlackWhiteDto);

    /**
     * 修改黑白名单信息
     *
     * @param carBlackWhiteDto 黑白名单信息
     * @return 返回影响记录数
     */
    int updateCarBlackWhite(CarBlackWhiteDto carBlackWhiteDto);

    /**
     * 删除指令
     *
     * @param carBlackWhiteDto 指令id
     * @return 返回影响记录数
     */
    int delete(CarBlackWhiteDto carBlackWhiteDto);

}
