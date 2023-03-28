package com.java110.things.quartz.task;

import com.java110.things.adapt.car.zeroOne.ZeroOneQueryCarInoutLogAdapt;
import com.java110.things.config.Java110Properties;
import com.java110.things.entity.task.TaskDto;
import com.java110.things.factory.MappingCacheFactory;
import com.java110.things.quartz.TaskSystemQuartz;
import com.java110.things.quartz.accessControl.AddUpdateFace;
import com.java110.things.quartz.accessControl.DeleteFace;
import com.java110.things.quartz.accessControl.ScanMachine;
import com.java110.things.service.community.ICommunityService;
import com.java110.things.service.machine.IMachineService;
import com.java110.things.util.StringUtil;
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
public class ZeroOneQueryCarInoutLogTemplate extends TaskSystemQuartz {

    /**
     * 初始化 硬件状态
     */
    public static boolean INIT_MACHINE_STATE = false;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ZeroOneQueryCarInoutLogAdapt zeroOneQueryCarInoutLogAdapt;

    @Override
    protected void process(TaskDto taskDto) throws Exception {

        String parkingIds = MappingCacheFactory.getValue("ZERO_ONE_PARKING_IDS");

        if(StringUtil.isEmpty(parkingIds)){
            return ;
        }

        String [] pIds = parkingIds.split(",");

        for(String pId :pIds){
            zeroOneQueryCarInoutLogAdapt.query(pId);
        }


    }


}
