package com.java110.things.dao;

import com.java110.things.entity.mapping.MappingDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @ClassName IMappingServiceDao
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/15 21:02
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
@Mapper
public interface IMappingServiceDao {

    /**
     * 保存映射信息
     *
     * @param mappingDto 映射信息
     * @return 返回影响记录数
     */
    int saveMapping(MappingDto mappingDto);


    long getMappingCount(MappingDto mappingDto);

    /**
     * 查询映射信息
     * @param mappingDto 映射信息
     * @return
     */
    List<MappingDto> getMappings(MappingDto mappingDto);


    /**
     * 修改映射信息
     *
     * @param mappingDto 映射信息
     * @return 返回影响记录数
     */
    int updateMapping(MappingDto mappingDto);

}
