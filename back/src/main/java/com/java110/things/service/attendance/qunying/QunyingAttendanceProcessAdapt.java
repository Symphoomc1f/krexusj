package com.java110.things.service.attendance.qunying;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.attendance.AttendanceUploadDto;
import com.java110.things.entity.attendance.ResultQunyingDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.factory.CallAttendanceFactory;
import com.java110.things.service.attendance.IAttendanceProcess;
import com.java110.things.service.attendance.ICallAttendanceService;
import com.java110.things.util.SeqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @ClassName QunyingHeartbeatServiceImpl
 * @Description TODO 群英适配器
 * @Author wuxw
 * @Date 2020/5/26 17:50
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
@Service("qunyingAttendanceProcessAdapt")
public class QunyingAttendanceProcessAdapt implements IAttendanceProcess {

    private static Logger logger = LoggerFactory.getLogger(QunyingAttendanceProcessAdapt.class);

    private static final String MANUFACTURER = "QY";

    private static final int DEFAULT_STATUS = 1;

    private static final String INFO_OK = "ok";

    //推送指纹
    private static final String UPLOAD_DATA_FINGERPRINT = "fingerprint";

    //推送员工
    private static final String UPLOAD_DATA_USER = "user";

    //推送人脸
    private static final String UPLOAD_DATA_FACE = "face";

    //推送头像
    private static final String UPLOAD_DATA_HEAD_PIC = "headpic";

    //推送打卡记录
    private static final String UPLOAD_DATA_HEAD_CLOCKIN = "clockin";

    //推送设备信息
    private static final String UPLOAD_DATA_HEAD_INFO = "info";

    //处理结果推送
    private static final String UPLOAD_DATA_HEAD_RETURN = "return";


    //推送解除绑定
    private static final String UPLOAD_DATA_HEAD_UNBOUND = "unbound";


    @Override
    public ResultDto heartbeat(MachineDto machineDto) {

        ResultDto resultDto = null;
        //查询是否存在该设备
        ResultQunyingDto resultQunyingDto = getMachine(machineDto);
        //下发上传设备信息指令
//        if (resultQunyingDto != null) {
//            resultDto = new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, resultQunyingDto);
//            return resultDto;
//        }

        resultQunyingDto = new ResultQunyingDto(DEFAULT_STATUS, INFO_OK, "");

        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, resultQunyingDto);
    }

    @Override
    public ResultDto attendanceUploadData(AttendanceUploadDto attendanceUploadDto) {

        JSONArray uploadData = JSONArray.parseArray(attendanceUploadDto.getData());
        JSONArray ids = new JSONArray();
        String id = "";

        for (int uploadIndex = 0; uploadIndex < uploadData.size(); uploadIndex++) {
            id = doAttendanceUploadData(uploadData.getJSONObject(uploadIndex));
            ids.add(id);
        }
        ResultQunyingDto resultQunyingDto = new ResultQunyingDto(DEFAULT_STATUS, INFO_OK, ids);
        return new ResultDto(ResponseConstant.SUCCESS, ResponseConstant.SUCCESS_MSG, resultQunyingDto.toString());
    }

    private String doAttendanceUploadData(JSONObject dataObj) {

        String data = dataObj.getString("data");

        switch (data) {
            case UPLOAD_DATA_HEAD_INFO:
                uploadMachine(dataObj);
                break;
        }

        return dataObj.getString("id");

    }

    /**
     * 上传设备
     *
     * @param machineInfo 设备信息
     */
    private void uploadMachine(JSONObject machineInfo) {

        logger.debug("设备上报的设备信息" + machineInfo.toJSONString());

        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(machineInfo.getString(""));

    }


    /**
     * 查询设备是否存在
     *
     * @param machineDto 设备信息
     * @return
     */
    private ResultQunyingDto getMachine(MachineDto machineDto) {
        ICallAttendanceService callAttendanceService = CallAttendanceFactory.getCallAttendanceService();
        MachineDto tmpMachineDto = callAttendanceService.getMachine(machineDto);
        if (tmpMachineDto != null) {
            return null;
        }

        String machineName = MANUFACTURER + SeqUtil.getMachineSeq();

        machineDto.setMachineId(UUID.randomUUID().toString());
        machineDto.setMachineIp("设备未上报");
        machineDto.setMachineMac(machineDto.getMachineCode());
        machineDto.setMachineName(machineName);
        machineDto.setMachineVersion("v2.1_2.0.3");
        machineDto.setOem("群英");
        callAttendanceService.uploadMachine(machineDto);

        //上传硬件指令
        ResultQunyingDto resultQunyingDto = new ResultQunyingDto(DEFAULT_STATUS, INFO_OK, "info");

        return resultQunyingDto;
    }
}
