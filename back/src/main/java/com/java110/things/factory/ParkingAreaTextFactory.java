package com.java110.things.factory;

import com.java110.things.entity.parkingArea.ParkingAreaDto;
import com.java110.things.entity.parkingArea.ParkingAreaTextCacheDto;
import com.java110.things.entity.parkingArea.ParkingAreaTextDto;
import com.java110.things.service.parkingArea.IParkingAreaTextService;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.DateUtil;
import com.java110.things.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingAreaTextFactory {

    //类型：1001 月租车进场，2002 月租车出场，3003 月租车到期，4004 临时车进场 5005 临时车出场 6006 临时车未缴费
    public static final String TYPE_CD_MONTH_CAR_IN = "1001";
    public static final String TYPE_CD_MONTH_CAR_OUT = "2002";
    public static final String TYPE_CD_MONTH_CAR_EXPIRES = "3003";
    public static final String TYPE_CD_TEMP_CAR_IN = "4004";
    public static final String TYPE_CD_TEMP_CAR_OUT = "5005";
    public static final String TYPE_CD_TEMP_CAR_NO_PAY = "6006";

    public static final Map<String, ParkingAreaTextCacheDto> cache = new HashMap<>();

    public static ParkingAreaTextCacheDto getText(List<ParkingAreaDto> parkingAreaDtos, String typeCd) {
        String paId = "";
        for (ParkingAreaDto parkingAreaDto : parkingAreaDtos) {
            if ("T".equals(parkingAreaDto.getDefaultArea())) {
                paId = parkingAreaDto.getPaId();
            }
        }
        if (StringUtil.isEmpty(paId)) {
            paId = parkingAreaDtos.get(0).getPaId();
        }

        // 从数据库获取 然后缓存
        return getText(paId, typeCd);
    }

    public static ParkingAreaTextCacheDto getText(String paId, String typeCd) {
        if (cache.containsKey(paId + typeCd)) {
            return cache.get(paId + typeCd);
        }

        // 从数据库获取 然后缓存
        return getParkingAreaTextByDb(paId, typeCd);
    }

    /**
     * 从数据库中获取
     *
     * @param paId
     * @param typeCd
     * @return
     */
    private static synchronized ParkingAreaTextCacheDto getParkingAreaTextByDb(String paId, String typeCd) {

        IParkingAreaTextService parkingAreaTextService = ApplicationContextFactory.getBean("parkingAreaTextServiceImpl", IParkingAreaTextService.class);
        ParkingAreaTextDto parkingAreaTextDto = new ParkingAreaTextDto();
        parkingAreaTextDto.setPaId(paId);
        parkingAreaTextDto.setTypeCd(typeCd);
        List<ParkingAreaTextDto> parkingAreaTextDtos = parkingAreaTextService.queryParkingAreaTexts(parkingAreaTextDto);
        if (parkingAreaTextDtos == null || parkingAreaTextDtos.size() < 1) {
            return null;
        }

        ParkingAreaTextCacheDto parkingAreaTextCacheDto = BeanConvertUtil.covertBean(parkingAreaTextDtos.get(0), ParkingAreaTextCacheDto.class);
        parkingAreaTextCacheDto.setCacheTime(DateUtil.getCurrentDate());
        cache.put(paId + typeCd, parkingAreaTextCacheDto);

        return parkingAreaTextCacheDto;
    }
}
