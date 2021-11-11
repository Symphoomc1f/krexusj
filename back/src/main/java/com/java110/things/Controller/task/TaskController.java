package com.java110.things.Controller.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.things.Controller.BaseController;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.task.TaskAttrDto;
import com.java110.things.entity.task.TaskDto;
import com.java110.things.entity.task.TaskTemplateDto;
import com.java110.things.service.task.ITaskService;
import com.java110.things.util.Assert;
import com.java110.things.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TaskController
 * @Description TODO 任务控制类
 * @Author wuxw
 * @Date 2020/5/16 10:36
 * @Version 1.0
 * add by wuxw 2020/5/16
 **/
@RestController
@RequestMapping(path = "/api/task")
public class TaskController extends BaseController {

    @Autowired
    private ITaskService taskServiceImpl;


    /**
     * 启动任务
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/startTask", method = RequestMethod.POST)
    public ResponseEntity<String> startTask(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "taskId", "请求报文中未包含硬件任务ID");
        TaskDto taskDto = BeanConvertUtil.covertBean(paramObj, TaskDto.class);
        ResultDto resultDto = taskServiceImpl.getTaskInfo(taskDto);

        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return super.createResponseEntity(resultDto);
        }
        List<TaskDto> taskDtos = (List<TaskDto>) resultDto.getData();

        if (taskDtos == null || taskDtos.size() < 1) {
            resultDto = new ResultDto(ResultDto.ERROR, "不存在该任务");
            return super.createResponseEntity(resultDto);
        }

        resultDto = taskServiceImpl.startTask(taskDtos.get(0));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 停止任务
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/stopTask", method = RequestMethod.POST)
    public ResponseEntity<String> stopTask(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "taskId", "请求报文中未包含硬件任务ID");
        TaskDto taskDto = BeanConvertUtil.covertBean(paramObj, TaskDto.class);
        ResultDto resultDto = taskServiceImpl.getTaskInfo(taskDto);

        if (resultDto.getCode() != ResultDto.SUCCESS) {
            return super.createResponseEntity(resultDto);
        }
        List<TaskDto> taskDtos = (List<TaskDto>) resultDto.getData();

        if (taskDtos == null || taskDtos.size() < 1) {
            resultDto = new ResultDto(ResultDto.ERROR, "不存在该任务");
            return super.createResponseEntity(resultDto);
        }

        resultDto = taskServiceImpl.stopTask(taskDtos.get(0));
        return super.createResponseEntity(resultDto);
    }


    /**
     * 启动任务
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/saveTask", method = RequestMethod.POST)
    public ResponseEntity<String> saveTask(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "taskName", "请求报文中未包含任务名称");
        Assert.hasKeyAndValue(paramObj, "templateId", "请求报文中未包含模板ID");
        Assert.hasKeyAndValue(paramObj, "taskCron", "请求报文中未包含定时时间");

        TaskDto taskDto = BeanConvertUtil.covertBean(paramObj, TaskDto.class);
        taskDto.setState("001");
        if (paramObj.containsKey("taskAttrDtos")) {
            List<TaskAttrDto> taskAttrDtos = new ArrayList<>();
            JSONArray tmpTaskAttrDtos = paramObj.getJSONArray("taskAttrDtos");
            for (int taskAttrIndex = 0; taskAttrIndex < tmpTaskAttrDtos.size(); taskAttrIndex++) {
                JSONObject taskAttr = tmpTaskAttrDtos.getJSONObject(taskAttrIndex);
                TaskAttrDto taskAttrDto = BeanConvertUtil.covertBean(taskAttr, TaskAttrDto.class);
                taskAttrDtos.add(taskAttrDto);
            }
            taskDto.setTaskAttrDtos(taskAttrDtos);
        }


        ResultDto resultDto = taskServiceImpl.saveTask(taskDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 启动任务
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateTask", method = RequestMethod.POST)
    public ResponseEntity<String> updateTask(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "taskId", "请求报文中未包含任务ID");
        Assert.hasKeyAndValue(paramObj, "taskName", "请求报文中未包含任务名称");
        Assert.hasKeyAndValue(paramObj, "templateId", "请求报文中未包含模板ID");
        Assert.hasKeyAndValue(paramObj, "taskCron", "请求报文中未包含定时时间");

        TaskDto taskDto = BeanConvertUtil.covertBean(paramObj, TaskDto.class);
        taskDto.setStatusCd("0");
        if (paramObj.containsKey("taskAttrDtos")) {
            List<TaskAttrDto> taskAttrDtos = new ArrayList<>();
            JSONArray tmpTaskAttrDtos = paramObj.getJSONArray("taskAttrDtos");
            for (int taskAttrIndex = 0; taskAttrIndex < tmpTaskAttrDtos.size(); taskAttrIndex++) {
                JSONObject taskAttr = tmpTaskAttrDtos.getJSONObject(taskAttrIndex);
                TaskAttrDto taskAttrDto = BeanConvertUtil.covertBean(taskAttr, TaskAttrDto.class);
                taskAttrDto.setTaskId(taskDto.getTaskId());
                taskAttrDtos.add(taskAttrDto);
            }
            taskDto.setTaskAttrDtos(taskAttrDtos);
        }
        ResultDto resultDto = taskServiceImpl.updateTask(taskDto);
        return super.createResponseEntity(resultDto);
    }

    /**
     * 启动任务
     *
     * @param param 请求报文 包括设备 前台填写信息
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteTask", method = RequestMethod.POST)
    public ResponseEntity<String> deleteTask(@RequestBody String param) throws Exception {

        JSONObject paramObj = super.getParamJson(param);

        Assert.hasKeyAndValue(paramObj, "taskId", "请求报文中未包含任务ID");

        TaskDto taskDto = new TaskDto();
        taskDto.setTaskId(paramObj.getString("taskId"));
        taskDto.setStatusCd("1");
        ResultDto resultDto = taskServiceImpl.updateTask(taskDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 根据类型查询设备
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getTasks", method = RequestMethod.GET)
    public ResponseEntity<String> getTasks(@RequestParam int page,
                                           @RequestParam int row,
                                           @RequestParam(name = "taskId", required = false) String taskId,
                                           @RequestParam(name = "taskName", required = false) String taskName) throws Exception {


        TaskDto taskDto = new TaskDto();
        taskDto.setPage(page);
        taskDto.setRow(row);
        taskDto.setTaskId(taskId);
        taskDto.setTaskName(taskName);
        ResultDto resultDto = taskServiceImpl.getTaskInfo(taskDto);
        return super.createResponseEntity(resultDto);
    }


    /**
     * 根据类型查询设备
     *
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getTaskTemplates", method = RequestMethod.GET)
    public ResponseEntity<String> getTaskTemplates(
            @RequestParam(name = "taskTemplateId", required = false) String taskTemplateId,
            @RequestParam(name = "taskTemplateName", required = false) String taskTemplateName) throws Exception {


        TaskTemplateDto taskTemplateDto = new TaskTemplateDto();

        taskTemplateDto.setTemplateId(taskTemplateId);
        taskTemplateDto.setTemplateName(taskTemplateName);
        ResultDto resultDto = taskServiceImpl.getTaskTemplate(taskTemplateDto);
        return super.createResponseEntity(resultDto);
    }

}
