/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.things.extApi.attendance;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.adapt.attendance.IAttendanceService;
import com.java110.things.entity.attendance.AttendanceClassesAttrDto;
import com.java110.things.entity.attendance.AttendanceClassesDto;
import com.java110.things.entity.attendance.AttendanceClassesStaffDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.StaffDto;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.staff.IStaffService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import com.java110.things.util.SeqUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 考勤 控制类
 * <p>
 * 完成考勤添加 修改 删除 查询功能
 * <p>
 * add by wuxw 2020-12-17
 */
@RestController
@RequestMapping(path = "/extApi/attendance")
public class AttendanceExtController extends BaseController {

    @Autowired
    IAttendanceService attendanceServiceImpl;

    @Autowired
    private IStaffService staffServiceImpl;


    @Autowired
    ICommunityService communityServiceImpl;


    /**
     * 添加考勤信息
     * <p>
     *
     * @param reqParam {
     *                 "attendanceCode":""
     *                 attendance_name
     *                 attendance_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_attendance_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addAttendanceClass", method = RequestMethod.POST)
    public ResponseEntity<String> addAttendanceClass(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "classesName", "未包含考勤班组");
        Assert.hasKeyAndValue(reqJson, "timeOffset", "未包含打卡范围");
        Assert.hasKeyAndValue(reqJson, "clockCount", "未包含打卡次数");
        Assert.hasKeyAndValue(reqJson, "clockType", "未包含打卡类型");
        Assert.hasKeyAndValue(reqJson, "clockTypeValue", "未包含打卡规则");
        Assert.hasKeyAndValue(reqJson, "lateOffset", "未包含迟到时间");
        Assert.hasKeyAndValue(reqJson, "leaveOffset", "未包含早退时间");
        Assert.hasKeyAndValue(reqJson, "extClassesId", "未包含外部班次ID");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        if (!reqJson.containsKey("attrs")) {
            throw new IllegalArgumentException("未包含属性");
        }

        JSONArray attrs = reqJson.getJSONArray("attrs");

        if (attrs.size() < 1) {
            throw new IllegalArgumentException("未包含属性");
        }

        AttendanceClassesDto attendanceClassesDto = BeanConvertUtil.covertBean(reqJson, AttendanceClassesDto.class);
        attendanceClassesDto.setClassesId(SeqUtil.getId());

        List<AttendanceClassesAttrDto> attendanceClassesAttrDtos = new ArrayList<>();
        for (int attrIndex = 0; attrIndex < attrs.size(); attrIndex++) {
            JSONObject attrObj = attrs.getJSONObject(attrIndex);
            AttendanceClassesAttrDto attendanceClassesAttrDto = BeanConvertUtil.covertBean(attrObj, AttendanceClassesAttrDto.class);
            attendanceClassesAttrDto.setClassesId(attendanceClassesDto.getClassesId());
            attendanceClassesAttrDto.setAttrId(SeqUtil.getId());
            attendanceClassesAttrDtos.add(attendanceClassesAttrDto);
        }

        ResultDto result = attendanceServiceImpl.insertAttendanceClassesDto(attendanceClassesDto, attendanceClassesAttrDtos);

        return ResultDto.createResponseEntity(result);
    }


    /**
     * 修改考勤信息
     * <p>
     *
     * @param reqParam {
     *                 "name": "HC考勤",
     *                 "address": "青海省西宁市",
     *                 "cityCode": "510104",
     *                 "extPaId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateAttendanceClass", method = RequestMethod.POST)
    public ResponseEntity<String> updateAttendanceClass(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "classesName", "未包含考勤班组");
        Assert.hasKeyAndValue(reqJson, "timeOffset", "未包含打卡范围");
        Assert.hasKeyAndValue(reqJson, "clockCount", "未包含打卡次数");
        Assert.hasKeyAndValue(reqJson, "clockType", "未包含打卡类型");
        Assert.hasKeyAndValue(reqJson, "clockTypeValue", "未包含打卡规则");
        Assert.hasKeyAndValue(reqJson, "lateOffset", "未包含迟到时间");
        Assert.hasKeyAndValue(reqJson, "leaveOffset", "未包含早退时间");
        Assert.hasKeyAndValue(reqJson, "extClassesId", "未包含外部班次ID");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setExtClassesId(reqJson.getString("extClassesId"));
        List<AttendanceClassesDto> attendanceClassesDtos = attendanceServiceImpl.getAttendanceClasses(attendanceClassesDto);

        Assert.listOnlyOne(attendanceClassesDtos, "不存在考勤班组");

        if (!reqJson.containsKey("attrs")) {
            throw new IllegalArgumentException("未包含属性");
        }

        JSONArray attrs = reqJson.getJSONArray("attrs");

        if (attrs.size() < 1) {
            throw new IllegalArgumentException("未包含属性");
        }

        attendanceClassesDto = BeanConvertUtil.covertBean(reqJson, AttendanceClassesDto.class);
        attendanceClassesDto.setStatusCd("0");
        attendanceClassesDto.setClassesId(attendanceClassesDtos.get(0).getClassesId());
        ResultDto result = attendanceServiceImpl.updateAttendanceClasses(attendanceClassesDto);

        if (result.getCode() != ResultDto.SUCCESS) {
            return ResultDto.createResponseEntity(result);
        }

        AttendanceClassesAttrDto attendanceClassesAttrDto = new AttendanceClassesAttrDto();
        attendanceClassesAttrDto.setClassesId(attendanceClassesDtos.get(0).getClassesId());
        attendanceServiceImpl.deleteAttendanceClassesAttrDto(attendanceClassesAttrDto);

        for (int attrIndex = 0; attrIndex < attrs.size(); attrIndex++) {
            JSONObject attrObj = attrs.getJSONObject(attrIndex);
            attendanceClassesAttrDto = BeanConvertUtil.covertBean(attrObj, AttendanceClassesAttrDto.class);
            attendanceClassesAttrDto.setClassesId(attendanceClassesDtos.get(0).getClassesId());
            attendanceClassesAttrDto.setAttrId(SeqUtil.getId());
            attendanceServiceImpl.saveAttendanceClassesAttrDto(attendanceClassesAttrDto);
        }

        return ResultDto.createResponseEntity(result);
    }

    /**
     * 删除考勤信息
     * <p>
     *
     * @param reqParam {
     *                 "extPaId": "702020042194860039"
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteAttendanceClass", method = RequestMethod.POST)
    public ResponseEntity<String> deleteAttendanceClass(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extClassesId", "未包含外部考勤班次ID");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");


        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setExtClassesId(reqJson.getString("extClassesId"));
        List<AttendanceClassesDto> attendanceClassesDtos = attendanceServiceImpl.getAttendanceClasses(attendanceClassesDto);

        Assert.listOnlyOne(attendanceClassesDtos, "不存在考勤班组");

        AttendanceClassesAttrDto attendanceClassesAttrDto = new AttendanceClassesAttrDto();
        attendanceClassesAttrDto.setClassesId(attendanceClassesDtos.get(0).getClassesId());
        attendanceServiceImpl.deleteAttendanceClassesAttrDto(attendanceClassesAttrDto);
        ResultDto resultDto = attendanceServiceImpl.deleteAttendanceClassesDto(attendanceClassesDtos.get(0));

        return ResultDto.createResponseEntity(resultDto);
    }

    /**
     * 添加考勤员工信息
     * <p>
     *
     * @param reqParam {
     *                 "attendanceCode":""
     *                 attendance_name
     *                 attendance_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_attendance_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addAttendanceClassStaffs", method = RequestMethod.POST)
    public ResponseEntity<String> addAttendanceClassStaffs(@RequestBody String reqParam) throws Exception {

        JSONArray datas = JSONArray.parseArray(reqParam);
        for (int dataIndex = 0; dataIndex < datas.size(); dataIndex++) {
            addAttendanceClassStaff(datas.get(dataIndex).toString());
        }
        return ResultDto.success();
    }


    /**
     * 添加考勤员工信息
     * <p>
     *
     * @param reqParam {
     *                 "attendanceCode":""
     *                 attendance_name
     *                 attendance_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_attendance_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addAttendanceClassStaff", method = RequestMethod.POST)
    public ResponseEntity<String> addAttendanceClassStaff(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extClassesId", "未包含外部考勤班组ID");
        Assert.hasKeyAndValue(reqJson, "extStaffId", "未包含外部员工ID");
        Assert.hasKeyAndValue(reqJson, "staffName", "未包含员工名称");
        Assert.hasKeyAndValue(reqJson, "departmentId", "未包含部门ID");
        Assert.hasKeyAndValue(reqJson, "departmentName", "未包含部门名称");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        StaffDto staffDto = new StaffDto();
        staffDto.setExtStaffId(reqJson.getString("extStaffId"));
        //检查员工是否存在
        List<StaffDto> staffDtos = attendanceServiceImpl.queryStaffs(staffDto);

        String staffId = "";

        if (staffDtos == null || staffDtos.size() < 1) {
            StaffDto tmpStaffDto = BeanConvertUtil.covertBean(reqJson, StaffDto.class);
            tmpStaffDto.setStaffId(SeqUtil.getId());
            staffServiceImpl.saveStaff(tmpStaffDto);
            staffId = tmpStaffDto.getStaffId();
        } else {
            staffId = staffDtos.get(0).getStaffId();
        }
        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setExtClassesId(reqJson.getString("extClassesId"));
        List<AttendanceClassesDto> attendanceClassesDtos = attendanceServiceImpl.getAttendanceClasses(attendanceClassesDto);

        Assert.listOnlyOne(attendanceClassesDtos, "不存在考勤班组");

        //判断员工是否在这个考勤班组中
        AttendanceClassesStaffDto attendanceClassesStaffDto = new AttendanceClassesStaffDto();
        attendanceClassesStaffDto.setClassesId(attendanceClassesDtos.get(0).getClassesId());
        attendanceClassesStaffDto.setStaffId(staffId);
        List<AttendanceClassesStaffDto> attendanceClassesStaffDtos = attendanceServiceImpl.queryClassStaffs(attendanceClassesStaffDto);
        //班组中已经存在
        if (attendanceClassesStaffDtos != null && attendanceClassesStaffDtos.size() > 0) {
            return ResultDto.success();
        }
        attendanceClassesStaffDto = BeanConvertUtil.covertBean(reqJson, AttendanceClassesStaffDto.class);
        attendanceClassesStaffDto.setStaffId(staffId);
        attendanceClassesStaffDto.setClassesId(SeqUtil.getId());
        attendanceClassesStaffDto.setCsId(SeqUtil.getId());
        ResultDto resultDto = attendanceServiceImpl.saveClassStaff(attendanceClassesStaffDto);
        return ResultDto.createResponseEntity(resultDto);
    }

    /**
     * 添加考勤员工信息
     * <p>
     *
     * @param reqParam {
     *                 "attendanceCode":""
     *                 attendance_name
     *                 attendance_type_cd
     *                 create_time
     *                 status_cd
     *                 oem
     *                 ext_attendance_id
     *                 community_id
     *                 hm_id
     *                 }
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteAttendanceClassStaff", method = RequestMethod.POST)
    public ResponseEntity<String> deleteAttendanceClassStaff(@RequestBody String reqParam) throws Exception {

        JSONObject reqJson = JSONObject.parseObject(reqParam);

        Assert.hasKeyAndValue(reqJson, "extClassesId", "未包含外部考勤班组ID");
        Assert.hasKeyAndValue(reqJson, "extStaffId", "未包含外部员工ID");
        Assert.hasKeyAndValue(reqJson, "taskId", "未包含任务ID");

        StaffDto staffDto = new StaffDto();
        staffDto.setExtStaffId(reqJson.getString("extStaffId"));
        //检查员工是否存在
        List<StaffDto> staffDtos = attendanceServiceImpl.queryStaffs(staffDto);

        Assert.listOnlyOne(staffDtos, "员工不存在");

        AttendanceClassesDto attendanceClassesDto = new AttendanceClassesDto();
        attendanceClassesDto.setExtClassesId(reqJson.getString("extClassesId"));
        List<AttendanceClassesDto> attendanceClassesDtos = attendanceServiceImpl.getAttendanceClasses(attendanceClassesDto);

        Assert.listOnlyOne(attendanceClassesDtos, "不存在考勤班组");

        //判断员工是否在这个考勤班组中
        AttendanceClassesStaffDto attendanceClassesStaffDto = new AttendanceClassesStaffDto();
        attendanceClassesStaffDto.setClassesId(attendanceClassesDtos.get(0).getClassesId());
        attendanceClassesStaffDto.setStaffId(staffDtos.get(0).getStaffId());
        List<AttendanceClassesStaffDto> attendanceClassesStaffDtos = attendanceServiceImpl.queryClassStaffs(attendanceClassesStaffDto);
        //班组中已经存在
        if (attendanceClassesStaffDtos != null && attendanceClassesStaffDtos.size() > 0) {
            return ResultDto.success();
        }
        attendanceClassesStaffDto = BeanConvertUtil.covertBean(reqJson, AttendanceClassesStaffDto.class);
        attendanceClassesStaffDto.setStaffId(staffDtos.get(0).getStaffId());
        attendanceClassesStaffDto.setClassesId(SeqUtil.getId());
        ResultDto resultDto = attendanceServiceImpl.deleteClassStaff(attendanceClassesStaffDto);
        return ResultDto.createResponseEntity(resultDto);
    }

}
