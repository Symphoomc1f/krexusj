package com.java110.things.adapt.car;


import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.parkingArea.ResultParkingAreaTextDto;
import com.java110.things.entity.response.ResultDto;

/**
 * 调用车辆 服务类
 */
public interface ICallCarService {

    /**
     * 车辆识别结果 上报接口
     *
     * @param type 类型
     *             //车牌类型
     * #define LT_UNKNOWN  0   //未知车牌
     * #define LT_BLUE     1   //蓝牌小汽车
     * #define LT_BLACK    2   //黑牌小汽车
     * #define LT_YELLOW   3   //单排黄牌
     * #define LT_YELLOW2  4   //双排黄牌（大车尾牌，农用车）
     * #define LT_POLICE   5   //警车车牌
     * #define LT_ARMPOL   6   //武警车牌
     * #define LT_INDIVI   7   //个性化车牌
     * #define LT_ARMY     8   //单排军车牌
     * #define LT_ARMY2    9   //双排军车牌
     * #define LT_EMBASSY  10  //使馆车牌
     * #define LT_HONGKONG 11  //香港进出中国大陆车牌
     * #define LT_TRACTOR  12  //农用车牌
     * #define LT_COACH	13  //教练车牌
     * #define LT_MACAO	14  //澳门进出中国大陆车牌
     * #define LT_ARMPOL2   15 //双层武警车牌
     * #define LT_ARMPOL_ZONGDUI 16  // 武警总队车牌
     * #define LT_ARMPOL2_ZONGDUI 17 // 双层武警总队车牌
     * #define LI_AVIATION  18   //民航
     * #define LI_ENERGY    19   //新能源
     * @param carNum
     * @param machineDto
     * @return resultDto {
     *     code :0 开门，
     *     msg:说明原因
     * }
     */
    public ResultParkingAreaTextDto ivsResult(String type, String carNum, MachineDto machineDto) throws Exception;

}
