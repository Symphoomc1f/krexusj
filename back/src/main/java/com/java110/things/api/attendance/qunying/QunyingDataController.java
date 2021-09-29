package com.java110.things.api.attendance.qunying;

import com.alibaba.fastjson.JSONArray;
import com.java110.things.api.attendance.BaseAttendanceController;
import com.java110.things.constant.ResponseConstant;
import com.java110.things.entity.attendance.AttendanceUploadDto;
import com.java110.things.entity.attendance.ResultQunyingDto;
import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.exception.ControllerException;
import com.java110.things.exception.Result;
import com.java110.things.service.attendance.IAttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName GetDataController
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/26 16:25
 * @Version 1.0
 * add by wuxw 2020/5/26
 **/
@RestController
@RequestMapping(path = "/api/data")
public class QunyingDataController extends BaseAttendanceController {

    private static Logger logger = LoggerFactory.getLogger(QunyingDataController.class);


    @Autowired
    private IAttendanceService attendanceServiceImpl;

    /**
     * 添加设备接口类
     * <p>
     *
     * @param sn 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public ResponseEntity<String> getData(
            @RequestParam String sn,
            @RequestParam(name = "requesttime", required = false) String requesttime,
            @RequestParam(name = "sign", required = false) String sign
    ) throws Exception {
        logger.debug("考勤机请求接口 get" + sn);
        MachineDto machineDto = new MachineDto();
        machineDto.setMachineCode(sn);

        ResultDto resultDto = null;
        try {
            resultDto = attendanceServiceImpl.heartbeat(machineDto);
            if (resultDto.getCode() != ResponseConstant.SUCCESS) {
                throw new ControllerException(Result.SYS_ERROR, resultDto.getMsg());
            }
        } catch (Exception e) {
            logger.error("考勤处理失败", e);
            ResultQunyingDto qunyingDto = new ResultQunyingDto(1, "ok", new JSONArray());
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, qunyingDto);
        }

        return new ResponseEntity<String>(resultDto.getData().toString(), HttpStatus.OK);
    }


    /**
     * 考勤机信息上报接口
     * <p>
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/post", method = RequestMethod.POST)
    public ResponseEntity<String> postData(
            @RequestBody String param
    ) throws Exception {

        logger.debug("考勤机请求接口 post " + param);
        ResultDto resultDto = null;
        try {

            AttendanceUploadDto attendanceUploadDto = new AttendanceUploadDto();
            attendanceUploadDto.setData(param);
            attendanceUploadDto.setMachineCode("");
            resultDto = attendanceServiceImpl.attendanceUploadData(attendanceUploadDto);
            if (resultDto.getCode() != ResponseConstant.SUCCESS) {
                throw new ControllerException(Result.SYS_ERROR, resultDto.getMsg());
            }
        } catch (Exception e) {
            logger.error("考勤处理失败", e);
            ResultQunyingDto qunyingDto = new ResultQunyingDto(1, "ok", new JSONArray());
            resultDto = new ResultDto(ResponseConstant.ERROR, ResponseConstant.ERROR_MSG, qunyingDto);
        }

        return new ResponseEntity<String>(resultDto.getData().toString(), HttpStatus.OK);
    }

}
