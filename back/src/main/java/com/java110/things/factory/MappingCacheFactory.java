package com.java110.things.factory;

import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.mapping.MappingDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.FactoryException;
import com.java110.things.exception.Result;
import com.java110.things.service.mapping.IMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 映射缓存工具类
 * Created by wuxw on 2018/4/14.
 */
public class MappingCacheFactory {
    private static Logger logger = LoggerFactory.getLogger(MappingCacheFactory.class);

    //后缀 用来刷缓存时删除 所有以这个为后缀的数据
    public final static String _SUFFIX_MAPPING = "_SUFFIX_MAPPING";

    public final static String SYSTEM_DOMAIN = "DOMAIN.SYSTEM";
    public final static String COMMON_DOMAIN = "DOMAIN.COMMON";

    /**
     * 缓存
     */
    private final static Map<String, MappingDto> cacheMappings = new HashMap<>();

    /**
     * 获取值
     *
     * @param domain
     * @param key
     * @return
     */
    public static String getValue(String domain, String key) {

        if (cacheMappings.isEmpty()) {
            return "";
        }
        if (!cacheMappings.containsKey(domain + key)) {
            return "";
        }
        MappingDto mappingDto = cacheMappings.get(domain + key);
        return mappingDto.getValue();
    }

    /**
     * 获取系统域下的key值
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        MappingDto mapping = getMapping(key);
        return mapping == null ? "" : mapping.getValue();
    }

    public static MappingDto getMapping(String key) {
        if (cacheMappings.isEmpty()) {
            return null;
        }
        if (!cacheMappings.containsKey(SYSTEM_DOMAIN + key)) {
            return null;
        }
        MappingDto mappingDto = cacheMappings.get(SYSTEM_DOMAIN + key);
        return mappingDto;
    }

    /**
     * 加入缓存
     */
    public static void flushCacheMappings() {
        try {
            IMappingService mappingService = ApplicationContextFactory.getBean("mappingServiceImpl", IMappingService.class);
            MappingDto tmpMappingDto = new MappingDto();
            ResultDto resultDto = mappingService.getMapping(tmpMappingDto);
            if (resultDto.getCode() != ResponseConstant.SUCCESS) {
                throw new FactoryException(Result.SYS_ERROR, resultDto.getMsg());
            }
            List<MappingDto> mappingDtos = (List<MappingDto>) resultDto.getData();
            cacheMappings.clear();
            for (MappingDto mappingDto : mappingDtos) {
                cacheMappings.put(mappingDto.getDomain() + mappingDto.getKey(), mappingDto);
            }
        } catch (Exception e) {
            logger.error("加入缓存失败", e);
        }
    }


}
