package com.java110.things.entity.task;

import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName TaskDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 9:04
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
public class TaskDto extends TaskTemplateDto implements Serializable {

    private String taskId;
    private String taskName;
    private String templateId;
    private String taskCron;
    private String state;
    private String createTime;

    private String statusCd;

    private List<TaskAttrDto> taskAttrDtos;

    private TaskTemplateDto taskTemplateDto;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTaskCron() {
        return taskCron;
    }

    public void setTaskCron(String taskCron) {
        this.taskCron = taskCron;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<TaskAttrDto> getTaskAttrDtos() {
        return taskAttrDtos;
    }

    public void setTaskAttrDtos(List<TaskAttrDto> taskAttrDtos) {
        this.taskAttrDtos = taskAttrDtos;
    }

    public TaskTemplateDto getTaskTemplateDto() {
        return taskTemplateDto;
    }

    public void setTaskTemplateDto(TaskTemplateDto taskTemplateDto) {
        this.taskTemplateDto = taskTemplateDto;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
