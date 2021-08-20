package com.java110.things.service.openDoor;

import com.java110.things.entity.openDoor.OpenDoorDto;
import com.java110.things.entity.response.ResultDto;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author wuxw
 * @Date 2020/5/14 14:48
 * @Version 1.0
 * add by wuxw 2020/5/14
 **/
public interface IOpenDoorService {

    /**
     * 保存开门信息
     *
     * @param openDoorDto 开门信息
     * @return
     * @throws Exception
     */
    ResultDto saveOpenDoor(OpenDoorDto openDoorDto) throws Exception;

    /**
     * 获取开门信息
     *
     * @param openDoorDto 开门信息
     * @return
     * @throws Exception
     */
    ResultDto getOpenDoor(OpenDoorDto openDoorDto) throws Exception;
    

}
