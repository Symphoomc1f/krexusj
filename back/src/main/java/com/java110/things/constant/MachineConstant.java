package com.java110.things.constant;

/**
 * @ClassName MachineConstant
 * @Description TODO 设备门禁常量类
 * @Author wuxw
 * @Date 2020/5/15 20:35
 * @Version 1.0
 * add by wuxw 2020/5/15
 **/
public class MachineConstant {

    // 门禁
    public static  final String MACHINE_TYPE_ACCESS_CONTROL = "9999";

    // 道闸
    public static  final String MACHINE_TYPE_BARRIER_GATE = "9996";

    // 考勤机
    public static  final String MACHINE_TYPE_ATTENDANCE = "9997";

    // 摄像头
    public static  final String MACHINE_TYPE_CAMERA = "9998";


    //上报设备
    public static final String CMD_REGISTER = "1";

    //重启设备
    public static final String CMD_RESTART = "2";

    //设置设备
    public static final String CMD_SETTINGS = "3";
    //升级设备
    public static final String CMD_UPGRADE = "4";

    //开门
    public static final String CMD_OPEN_DOOR = "5";

    //受控
    public static final String CMD_CONTROLLED = "6";

    //增加更新人脸
    public static final String CMD_CREATE_FACE = "101";

    //增加更新人脸
    public static final String CMD_UPDATE_FACE = "104";

    //删除人脸
    public static final String CMD_DELETE_FACE = "102";

    //清空人脸
    public static final String CMD_CLEAR_FACE = "103";


    public static final String MACHINE_CMD_STATE_NO_DEAL = "1000";//未处理

    public static final String MACHINE_CMD_STATE_DEALING = "2000";//处理中

    public static final String MACHINE_CMD_STATE_DEALED = "3000";//处理完成

    public static final String MACHINE_CMD_STATE_DEAL_ERROR = "4000";//处理失败

    public static final String MACHINE_CMD_OBJ_TYPE_SYSTEM = "001"; //系统操控指令

    public static final String MACHINE_CMD_OBJ_TYPE_FACE = "002"; //人脸处理指令

}
