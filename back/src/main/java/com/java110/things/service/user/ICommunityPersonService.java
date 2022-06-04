/*
 * Copyright 2017-2020 吴学文 and java110 team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.java110.things.service.user;

import com.java110.things.entity.response.ResultDto;
import com.java110.things.entity.user.CommunityPersonDto;

import java.util.List;

/**
 * 小区人员服务类 主要完成 人员 增删改查功能
 * <p>
 * add by 吴学文 2020-12-23
 * <p>
 * 小区人员 包括：
 * 业主： 小区 拥有房屋 或者拥有 车位 的个体或者公司 我们称为 业主
 * 访客： 不定期的访问 业主的个体我们认为 访客 主要以拜访业主或者物业为主的人员 具有一定的临时性
 * 员工：物业公司 员工 服务及管理小区
 * <p>
 * 协议： http://www.homecommunity.cn
 */
public interface ICommunityPersonService {

    /**
     * 保存小区人员信息
     *
     * @param communityPersonDto 小区人员信息
     * @return
     * @throws Exception
     */
    int saveCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception;

    /**
     * 保存小区人员信息
     *
     * @param communityPersonDto 小区人员信息
     * @return
     * @throws Exception
     */
    int updateCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception;

    /**
     * 获取小区人员信息
     *
     * @param communityPersonDto 小区人员信息
     * @return
     * @throws Exception
     */
    ResultDto getCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception;

    /**
     * 获取小区人员信息
     *
     * @param communityPersonDto 小区人员信息
     * @return
     * @throws Exception
     */
    List<CommunityPersonDto> queryCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception;

    /**
     * 删除小区人员
     *
     * @param communityPersonDto 小区人员信息
     * @return
     * @throws Exception
     */
    int deleteCommunityPerson(CommunityPersonDto communityPersonDto) throws Exception;


}
