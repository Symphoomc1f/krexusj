package com.java110.things.entity.task;

import com.java110.things.entity.PageDto;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName TaskTemplateDto
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 9:09
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
public class TaskTemplateDto extends PageDto implements Serializable {

    private String templateId;
    private String templateName;
    private String templateDesc;
    private String createTime;
    private String classBean;
    private String statusCd;

    private List<TaskTemplateSpecDto> taskTemplateSpecDtos;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateDesc() {
        return templateDesc;
    }

    public void setTemplateDesc(String templateDesc) {
        this.templateDesc = templateDesc;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getClassBean() {
        return classBean;
    }

    public void setClassBean(String classBean) {
        this.classBean = classBean;
    }

    public List<TaskTemplateSpecDto> getTaskTemplateSpecDtos() {
        return taskTemplateSpecDtos;
    }

    public void setTaskTemplateSpecDtos(List<TaskTemplateSpecDto> taskTemplateSpecDtos) {
        this.taskTemplateSpecDtos = taskTemplateSpecDtos;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
