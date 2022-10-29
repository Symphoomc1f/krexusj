package com.java110.things.quartz.task;

import com.java110.things.quartz.accessControl.AddUpdateFace;
import com.java110.things.quartz.accessControl.DeleteFace;
import com.java110.things.quartz.accessControl.ScanMachine;
import com.java110.things.config.Java110Properties;
import com.java110.things.entity.task.TaskDto;
import com.java110.things.quartz.TaskSystemQuartz;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @ClassName ScanAccessControlMachineTemplate
 * @Description TODO
 * @Author wuxw
 * @Date 2020/6/8 16:53
 * @Version 1.0
 * add by wuxw 2020/6/8
 **/
@Component
public class ScanAccessControlMachineTemplate extends TaskSystemQuartz {

    /**
     * 初始化 硬件状态
     */
    public static boolean INIT_MACHINE_STATE = false;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Java110Properties java110Properties;


    @Autowired
    private AddUpdateFace addUpdateFace;

    @Autowired
    private DeleteFace deleteFace;
    @Autowired
    private ICommunityService communityService;

    @Autowired
    private IMachineService machineService;

    @Autowired
    private ScanMachine scanMachine;
    @Override
    protected void process(TaskDto taskDto) throws Exception {
        scanMachine.scan();

    }


}
