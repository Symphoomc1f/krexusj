package com.java110.things.dao;

import com.java110.things.entity.machine.MachineDto;
import com.java110.things.entity.task.TaskAttrDto;
import com.java110.things.entity.task.TaskDto;
import com.java110.things.entity.task.TaskTemplateDto;
import com.java110.things.entity.task.TaskTemplateSpecDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName ITaskServiceDao
 * @Description TODO 任务数据操作层
 * @Author wuxw
 * @Date 2020/6/8 9:02
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Mapper
public interface ITaskServiceDao {

    /**
     * 保存任务信息
     *
     * @param taskDto 任务信息
     * @return 返回影响记录数
     */
    int saveTask(TaskDto taskDto);


    /**
     * 修改任务信息
     *
     * @param taskDto 任务信息
     * @return 返回影响记录数
     */
    int updateTask(TaskDto taskDto);

    /**
     * 获取任务数
     *
     * @param taskDto 任务信息
     * @return 返回影响记录数
     */
    int queryTasksCount(TaskDto taskDto);

    /**
     * 获取任务数
     *
     * @param taskDto 任务信息
     * @return 返回影响记录数
     */
    List<TaskDto> getTaskInfo(TaskDto taskDto);

    /**
     * 保存任务属性信息
     *
     * @param taskAttrDto 任务信息
     * @return 返回影响记录数
     */
    int saveTaskAttr(TaskAttrDto taskAttrDto);

    /**
     * 修改任务属性信息
     *
     * @param taskAttrDto 任务信息
     * @return 返回影响记录数
     */
    int updateTaskAttr(TaskAttrDto taskAttrDto);

    /**
     * 查询任务属性
     * @param taskAttrDto
     * @return
     */
    List<TaskAttrDto> getTaskAttr(TaskAttrDto taskAttrDto);


    /**
     * 查询任务模板
     * @param taskTemplateDto
     * @return
     */
    List<TaskTemplateDto> getTaskTemplate(TaskTemplateDto taskTemplateDto);


    /**
     * 查询任务模板属性
     * @param taskTemplateSpecDto
     * @return
     */
    List<TaskTemplateSpecDto> getTaskTemplateSpec(TaskTemplateSpecDto taskTemplateSpecDto);
}
