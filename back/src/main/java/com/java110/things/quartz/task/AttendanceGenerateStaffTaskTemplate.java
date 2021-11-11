package com.java110.things.quartz.task;

import com.java110.things.entity.task.TaskDto;
import com.java110.things.quartz.TaskSystemQuartz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName AttendanceGenerateStaffTaskTemplate
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 12:29
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Component
public class AttendanceGenerateStaffTaskTemplate extends TaskSystemQuartz {

    protected static final Logger logger = LoggerFactory.getLogger(AttendanceGenerateStaffTaskTemplate.class);

    @Override
    protected void process(TaskDto taskDto) throws Exception {
        logger.debug("AttendanceGenerateStaffTaskTemplate定时任务在执行" + taskDto.getTaskName());
    }
}
