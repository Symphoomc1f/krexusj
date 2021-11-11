package com.java110.things.service.task;

import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.task.TaskDto;
import com.java110.things.entity.task.TaskTemplateDto;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface ITaskService {

    /**
     * 保存任务信息
     *
     * @param taskDto 任务信息
     * @return 返回影响记录数
     */
    ResultDto saveTask(TaskDto taskDto);

    /**
     * 修改任务信息
     *
     * @param taskDto 任务信息
     * @return 返回影响记录数
     */
    ResultDto updateTask(TaskDto taskDto);


    /**
     * 获取任务数
     *
     * @param taskDto 任务信息
     * @return 返回影响记录数
     */
    ResultDto getTaskInfo(TaskDto taskDto);


    /**
     * 查询任务模板
     *
     * @param taskTemplateDto
     * @return
     */
    ResultDto getTaskTemplate(TaskTemplateDto taskTemplateDto);


    /**
     * 启动任务
     *
     * @param taskDto
     * @return
     */
    ResultDto startTask(TaskDto taskDto);

    /**
     * 停止任务
     *
     * @param taskDto
     * @return
     */
    ResultDto stopTask(TaskDto taskDto);


}
