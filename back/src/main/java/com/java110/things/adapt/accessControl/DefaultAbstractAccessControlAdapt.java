package com.java110.things.adapt.accessControl;

import com.alibaba.fastjson.JSONObject;
import com.java110.things.adapt.accessControl.chuangjiang.CjHttpAssessControlProcessAdapt;
import com.java110.things.entity.accessControl.QRCodeDto;
import com.java110.things.entity.accessControl.UserFaceDto;
import com.java110.things.entity.fee.FeeDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.machine.OperateLogDto;
import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.room.RoomDto;
import com.java110.things.entity.user.UserAttrDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.factory.NotifyAccessControlFactory;
import com.java110.things.factory.RedisCacheFactory;
import com.java110.things.util.DateUtil;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 门禁抽象类 将一些 不是必须实现的放到抽象类中
 */
public abstract class DefaultAbstractAccessControlAdapt implements IAssessControlProcess {

    private static Logger logger = LoggerFactory.getLogger(CjHttpAssessControlProcessAdapt.class);

    @Override
    public void initAssessControlProcess() {
        logger.debug("初始化是配置器");


    }

    @Override
    public ResultDto addMachine(MachineDto machineDto) {
        return null;
    }

    @Override
    public ResultDto updateMachine(MachineDto machineDto) {
        return null;
    }

    @Override
    public ResultDto deleteMachine(MachineDto machineDto) {
        return null;
    }

    @Override
    public int getFaceNum(MachineDto machineDto) {
        return 0;
    }

    @Override
    public ResultDto getQRcode(UserFaceDto userFaceDto) {
        QRCodeDto qrCodeDto = new QRCodeDto();
        qrCodeDto.setUserId(userFaceDto.getUserId());
        qrCodeDto.setMachineCode(userFaceDto.getMachineCode());
        qrCodeDto.setCreateDate(DateUtil.getCurrentDate());
        qrCodeDto.setOpenCount(0);
        String qrCode = SeqUtil.getId();
        RedisCacheFactory.setValue(qrCode, JSONObject.toJSONString(qrCodeDto), 24 * 60 * 60);
        return new ResultDto(ResultDto.SUCCESS, "成功", qrCode);
    }

    /**
     * 二维码核验接口
     *
     * @param machineDto
     * @param param
     * @return
     */
    public String qrCode(MachineDto machineDto, String param){
        return new ResultDto(ResultDto.SUCCESS, "开门成功").toString();
    }

    protected ResultDto checkQRCode(String qrCode, String machineCode) {
        String qr = RedisCacheFactory.getValue(qrCode);

        if (StringUtil.isEmpty(qr)) {
            return new ResultDto(ResultDto.ERROR, "二维码过期");
        }

        QRCodeDto qrCodeDto = JSONObject.parseObject(qr, QRCodeDto.class);

        if (!qrCodeDto.getMachineCode().equals(machineCode)) {
            return new ResultDto(ResultDto.ERROR, "没有权限开门");
        }

        String openDoorCountStr = MappingCacheFactory.getValue("OPEN_DOOR_COUNT");
        int openDoorCount = 2;
        if (StringUtil.isNumber(openDoorCountStr)) {
            openDoorCount = Integer.parseInt(openDoorCountStr);
        }

        if (openDoorCount > qrCodeDto.getOpenCount()) {
            return new ResultDto(ResultDto.ERROR, "开门次数已超限");
        }

        qrCodeDto.setOpenCount(qrCodeDto.getOpenCount() - 1);
        RedisCacheFactory.setValue(qrCode, JSONObject.toJSONString(qrCodeDto), 24 * 60 * 60);

        return new ResultDto(ResultDto.SUCCESS, "开门成功");

    }

    @Override
    public void mqttMessageArrived(String topic, String data) {

    }


    protected String getIdNumber(UserFaceDto userFaceDto) {
        List<UserAttrDto> userAttrDtos = userFaceDto.getUserAttrDtos();

        if (userAttrDtos == null) {
            return userFaceDto.getLink();
        }

        for (UserAttrDto userAttrDto : userAttrDtos) {
            if (UserAttrDto.SPEC_CD_ACCESS_KEY.equals(userAttrDto.getSpecCd())) {
                return userAttrDto.getValue();
            }
        }
        return userFaceDto.getLink();
    }

    /**
     * 重启
     */
    public void setUiTitle(MachineDto machineDto) {


    }

    protected void openDoorResult(String data) {


    }

    /**
     * 设备上线
     *
     * @param data {
     *             "cmd": "mqtt_online",
     *             "sn": "fffffff",
     *             "result": "mqtt is online"
     *             }
     */
    protected void machineOnline(String data) {


    }


    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param cmd       操作命令
     * @param reqParam  请求报文
     * @param resParam  返回报文
     */
    protected void saveLog(String logId, String machineId, String cmd, String reqParam, String resParam) {
        saveLog(logId, machineId, cmd, reqParam, resParam, "", "", "");
    }

    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param cmd       操作命令
     * @param reqParam  请求报文
     * @param resParam  返回报文
     * @param state     状态
     */
    protected void saveLog(String logId, String machineId, String cmd, String reqParam, String resParam, String state) {
        saveLog(logId, machineId, cmd, reqParam, resParam, state, "", "");
    }

    /**
     * 存储日志
     *
     * @param logId     日志ID
     * @param machineId 设备ID
     * @param cmd       操作命令
     * @param reqParam  请求报文
     * @param resParam  返回报文
     * @param state     状态
     * @param userId    业主ID
     * @param userName  业主名称
     */
    protected void saveLog(String logId, String machineId, String cmd, String reqParam, String resParam, String state, String userId, String userName) {
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        OperateLogDto operateLogDto = new OperateLogDto();
        operateLogDto.setLogId(logId);
        operateLogDto.setMachineId(machineId);
        operateLogDto.setOperateType(cmd);
        operateLogDto.setReqParam(reqParam);
        operateLogDto.setResParam(resParam);
        operateLogDto.setState(state);
        operateLogDto.setUserId(userId);
        operateLogDto.setUserName(userName);
        notifyAccessControlService.saveOrUpdateOperateLog(operateLogDto);
    }

    /**
     * 查询费用信息
     *
     * @param openDoorDto
     */
    protected void freshOwnerFee(OpenDoorDto openDoorDto) {

        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        List<FeeDto> feeDtos = new ArrayList<>();
        try {
            //查询业主房屋信息
            UserFaceDto userFaceDto = new UserFaceDto();
            userFaceDto.setUserId(openDoorDto.getUserId());
            List<RoomDto> roomDtos = notifyAccessControlService.getRooms(userFaceDto);

            if (roomDtos == null || roomDtos.size() < 1) {
                return;
            }

            for (RoomDto roomDto : roomDtos) {
                List<FeeDto> tmpFeeDtos = notifyAccessControlService.getFees(roomDto);
                if (tmpFeeDtos == null || tmpFeeDtos.size() < 1) {
                    continue;
                }
                feeDtos.addAll(tmpFeeDtos);
            }
        } catch (Exception e) {
            logger.error("云端查询物业费失败", e);
        }

        if (feeDtos.size() < 1) {
            openDoorDto.setAmountOwed("0");
            return;
        }
        double own = 0.00;
        for (FeeDto feeDto : feeDtos) {
            logger.debug("查询费用信息" + JSONObject.toJSONString(feeDto));
            own += feeDto.getAmountOwed();
        }

        openDoorDto.setAmountOwed(own + "");
    }


}
