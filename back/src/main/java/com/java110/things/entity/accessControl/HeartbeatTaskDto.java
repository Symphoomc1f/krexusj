package com.java110.things.entity.accessControl;

import com.java110.things.entity.machine.MachineDto;

import java.io.Serializable;

public class HeartbeatTaskDto implements Serializable {


    //云端下发指令
    private int taskcmd;
    //云端下发 任务流水
    private String taskid;
    //云端下发 内容
    private String taskinfo;


    public int getTaskcmd() {
        return taskcmd;
    }

    public void setTaskcmd(int taskcmd) {
        this.taskcmd = taskcmd;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTaskinfo() {
        return taskinfo;
    }

    public void setTaskinfo(String taskinfo) {
        this.taskinfo = taskinfo;
    }
    
}
