package com.java110.things.service.task.impl;

import com.java110.things.dao.ITaskServiceDao;
import com.java110.things.entity.PageDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.task.TaskAttrDto;
import com.java110.things.entity.task.TaskDto;
import com.java110.things.entity.task.TaskTemplateDto;
import com.java110.things.entity.task.TaskTemplateSpecDto;
import com.java110.things.quartz.TaskSystemJob;
import com.java110.things.service.task.ITaskService;
import com.java110.things.util.Assert;
import com.java110.things.util.SeqUtil;
import com.java110.things.util.StringUtil;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ITaskServiceImpl
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 9:30
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Service("taskServiceImpl")
public class TaskServiceImpl implements ITaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final String defaultCronExpression = "0 * * * * ?";// 每分钟执行一次

    private static final String prefixJobName = "task_"; // job
    private static final String triggerNames = "taskToData_"; // job

    @Autowired
    private Scheduler scheduler;


    @Autowired
    ITaskServiceDao taskServiceDao;

    @Override
    public ResultDto saveTask(TaskDto taskDto) {

        taskDto.setTaskId(SeqUtil.getId());
        int count = taskServiceDao.saveTask(taskDto);

        if (count < 1) {
            return new ResultDto(ResultDto.ERROR, "保存任务失败");
        }

        List<TaskAttrDto> taskAttrDtos = taskDto.getTaskAttrDtos();
        if (taskAttrDtos != null && taskAttrDtos.size() > 0) {
            for (TaskAttrDto taskAttrDto : taskAttrDtos) {
                taskAttrDto.setTaskId(taskDto.getTaskId());
                taskAttrDto.setAttrId(SeqUtil.getId());
                taskServiceDao.saveTaskAttr(taskAttrDto);
            }
        }

        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
    }

    @Override
    public ResultDto updateTask(TaskDto taskDto) {
        int count = taskServiceDao.updateTask(taskDto);

        if (count < 1) {
            return new ResultDto(ResultDto.ERROR, "保存任务失败");
        }

        List<TaskAttrDto> taskAttrDtos = taskDto.getTaskAttrDtos();
        if (taskAttrDtos == null || taskAttrDtos.size() < 1) {
            return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);

        }

        for (TaskAttrDto taskAttrDto : taskAttrDtos) {
            if (StringUtil.isEmpty(taskAttrDto.getAttrId()) || taskAttrDto.getAttrId().startsWith("-")) {
                taskAttrDto.setAttrId(SeqUtil.getId());
                taskAttrDto.setStatusCd("0");
                taskServiceDao.saveTaskAttr(taskAttrDto);
            } else {
                taskAttrDto.setStatusCd("0");
                taskServiceDao.updateTaskAttr(taskAttrDto);
            }
        }
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
    }

    @Override
    public ResultDto getTaskInfo(TaskDto taskDto) {

        int page = taskDto.getPage();

        if (page != PageDto.DEFAULT_PAGE) {
            taskDto.setPage((page - 1) * taskDto.getRow());
        }
        List<TaskDto> taskDtos = taskServiceDao.getTaskInfo(taskDto);

        for (TaskDto tmpTaskDto : taskDtos) {
            TaskAttrDto taskAttrDto = new TaskAttrDto();
            taskAttrDto.setTaskId(tmpTaskDto.getTaskId());
            List<TaskAttrDto> taskAttrDtos = taskServiceDao.getTaskAttr(taskAttrDto);
            tmpTaskDto.setTaskAttrDtos(taskAttrDtos);
        }
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG, taskDtos);
    }

    @Override
    public ResultDto getTaskTemplate(TaskTemplateDto taskTemplateDto) {

        List<TaskTemplateDto> taskTemplateDtos = taskServiceDao.getTaskTemplate(taskTemplateDto);

        for (TaskTemplateDto tmpTaskTemplateDto : taskTemplateDtos) {
            TaskTemplateSpecDto taskTemplateSpecDto = new TaskTemplateSpecDto();
            taskTemplateSpecDto.setTemplateId(tmpTaskTemplateDto.getTemplateId());
            List<TaskTemplateSpecDto> taskTemplateSpecDtos = taskServiceDao.getTaskTemplateSpec(taskTemplateSpecDto);
            tmpTaskTemplateDto.setTaskTemplateSpecDtos(taskTemplateSpecDtos);
        }
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG, taskTemplateDtos);
    }

    /**
     * 启动任务
     *
     * @param taskDto
     * @return
     */
    @Override
    public ResultDto startTask(TaskDto taskDto) {

        TaskAttrDto taskAttrDto = new TaskAttrDto();
        taskAttrDto.setTaskId(taskDto.getTaskId());
        List<TaskAttrDto> attrDtos = taskServiceDao.getTaskAttr(taskAttrDto);
        TaskTemplateDto taskTemplateDto = new TaskTemplateDto();
        taskTemplateDto.setTemplateId(taskDto.getTemplateId());

        ResultDto resultDto = getTaskTemplate(taskTemplateDto);
        List<TaskTemplateDto> taskTemplateDtos = (List<TaskTemplateDto>) resultDto.getData();

        Assert.listOnlyOne(taskTemplateDtos, "模板不存在或存在多个");

        taskDto.setTaskTemplateDto(taskTemplateDtos.get(0));
        taskDto.setTaskAttrDtos(attrDtos);

        try {
            String cronExpression = taskDto.getTaskCron();// 如果没有配置则，每一分运行一次

            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            String jobName = prefixJobName + taskDto.getTaskId();

            String triggerName = triggerNames + taskDto.getTaskId();

            //设置任务名称
            JobKey jobKey = new JobKey(jobName, TaskSystemJob.JOB_GROUP_NAME);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            if (jobDetail != null) {
                return new ResultDto(ResultDto.ERROR, "任务已经存在请勿重复启动");
            }

            String taskCfgName = taskDto.getTaskName();
            JobDetail warnJob = JobBuilder.newJob(TaskSystemJob.class).withIdentity(jobName, TaskSystemJob.JOB_GROUP_NAME).withDescription("任务启动").build();

            warnJob.getJobDataMap().put(TaskSystemJob.JOB_DATA_CONFIG_NAME, taskCfgName);

            warnJob.getJobDataMap().put(TaskSystemJob.JOB_DATA_TASK_ID, taskDto.getTaskId());
            warnJob.getJobDataMap().put(TaskSystemJob.JOB_DATA_TASK, taskDto);
            warnJob.getJobDataMap().put(TaskSystemJob.JOB_DATA_TASK_ATTR, taskDto);

            // 触发时间点
            CronTrigger warnTrigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerName + "_group").withSchedule(cronScheduleBuilder).build();

            // 错过执行后，立即执行
            //warnTrigger(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
            //交由Scheduler安排触发
            scheduler.scheduleJob(warnJob, warnTrigger);
            TaskDto tmpTaskDto = new TaskDto();
            tmpTaskDto.setTaskId(taskDto.getTaskId());
            tmpTaskDto.setState("002");
            tmpTaskDto.setStatusCd("0");
            taskServiceDao.updateTask(tmpTaskDto);
            return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);
        } catch (Exception e) {
            logger.error("启动侦听失败", e);
            return new ResultDto(ResultDto.ERROR, e.getMessage());
        }
    }

    @Override
    public ResultDto stopTask(TaskDto taskDto) {
        try {
            String jobName = prefixJobName + taskDto.getTaskId();

            String triggerName = prefixJobName + taskDto.getTaskId();

            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TaskSystemJob.JOB_GROUP_NAME);
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            // 移除触发器
            scheduler.unscheduleJob(triggerKey);

            JobKey jobKey = new JobKey(jobName, TaskSystemJob.JOB_GROUP_NAME);
            // 删除任务
            scheduler.deleteJob(jobKey);

            TaskDto tmpTaskDto = new TaskDto();
            tmpTaskDto.setTaskId(taskDto.getTaskId());
            tmpTaskDto.setState("001");
            tmpTaskDto.setStatusCd("0");
            taskServiceDao.updateTask(tmpTaskDto);

        } catch (Exception e) {
            logger.error("启动侦听失败", e);
            return new ResultDto(ResultDto.ERROR, e.getMessage());
        }
        return new ResultDto(ResultDto.SUCCESS, ResultDto.SUCCESS_MSG);

    }
}
