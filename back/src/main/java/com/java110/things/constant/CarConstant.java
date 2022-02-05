package com.java110.things.constant;

/**
 * @ClassName AccessControlConstant
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/10 21:58
 * @Version 1.0
 * add by wuxw 2020/5/10
 **/
public class CarConstant {

    //HC小区管理系统 api 地址
    //public final static String ACCESS_URL = "https://hc.demo.winqi.cn:8008";

    //心跳
    public final static String CAR_HEARTBEART = "/api/machineTranslate.machineHeartbeart";

    //查询当前小区门禁

    public final static String LIST_MACHINES = "/api/machine.listMachines";

    //查询用户信息
    public final static String MACHINE_QUERY_CAR_INFO="/api/machineTranslate.machineQueryUserInfo";

    //增加/更新人脸
    public final static int CMD_ADD_CAR = 201;
    public final static int CMD_UPDATE_CAR = 203;

    //删除人脸
    public final static int CMD_DELETE_CAR = 202;

}
