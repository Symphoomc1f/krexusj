package com.java110.things.quartz;

import com.java110.things.dao.ITaskServiceDao;
import com.java110.things.entity.task.TaskAttrDto;
import com.java110.things.entity.task.TaskDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author
 */
public abstract class TaskSystemQuartz {

    protected static final Logger logger = LoggerFactory.getLogger(TaskSystemQuartz.class);

    @Autowired
    private ITaskServiceDao taskServiceDao;


    public void initTask() {

    }

    /**
     * 启动任务
     *
     * @param taskDto
     */
    public void startTask(TaskDto taskDto) throws Exception {


        taskDto.setState("002");
        List<TaskDto> taskInfos = taskServiceDao.getTaskInfo(taskDto);
        if (taskInfos == null || taskInfos.size() < 1) {
            return;
        }

        // 这么做是为了，单线程调用，防止多线程导致数据重复处理
        if (!"002".equals(taskInfos.get(0).getState())) {
            return;
        }

        String taskId = taskDto.getTaskId();

        if (logger.isDebugEnabled()) {
            logger.debug("---【TaskSystemQuartz.startFtpTask】：任务【" + taskId + "】开始运行！", taskId);
        }

        try {
            // 1.0空方法，让子类去实现
            prepare(taskDto);

            // 3.0核心业务处理逻辑，需要子类去实现
            process(taskDto);

            // 5.0空方法，让子类去实现
            after(taskDto);
        } catch (Exception ex) {

            // 接续向外抛出去
            logger.error("处理出现问题：", ex);
            return;
        }

    }


    /**
     * 主要业务处理（上传下载）,让子类去实现
     *
     * @param taskDto
     */
    protected abstract void process(TaskDto taskDto) throws Exception;

    /**
     * 空方法，如果在事前过程处理前，还需要做一定的处理，需要子类重写这个方法，实现业务逻辑
     *
     * @param taskDto
     */
    protected void prepare(TaskDto taskDto) {

    }

    /**
     * 空方法，如果在事后过程处理完后，还需要做一定的处理，需要子类重写这个方法，实现业务逻辑
     *
     * @param taskDto
     */
    protected void after(TaskDto taskDto) {

    }


    /**
     * 获取当前属性
     *
     * @param taskDto
     * @param specCd
     * @return
     */
    protected TaskAttrDto getCurTaskAttr(TaskDto taskDto, String specCd) {
        List<TaskAttrDto> taskAttrDtos = taskDto.getTaskAttrDtos();
        for (TaskAttrDto taskAttrDto : taskAttrDtos) {
            if (specCd.equals(taskAttrDto.getSpecCd())) {
                return taskAttrDto;
            }
        }

        return null;
    }
}
