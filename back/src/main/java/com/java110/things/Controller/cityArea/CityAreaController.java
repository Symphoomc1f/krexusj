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
package com.java110.things.Controller.cityArea;

import com.java110.things.Controller.BaseController;
import com.java110.things.entity.cityArea.CityAreaDto;
import com.java110.things.entity.response.ResultDto;
import com.java110.things.service.cityArea.ICityAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 城市 控制类
 * <p>
 * add by 吴学文
 *
 * @site http://www.homecommunity.cn
 */
@RestController
@RequestMapping(path = "/api/cityArea")
public class CityAreaController extends BaseController {

    @Autowired
    private ICityAreaService cityAreaServiceImpl;

    /**
     * 查询城市编码
     *
     * url  /api/cityArea/getCityAreas
     *
     * @param areaLevel 级别
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/getCityAreas", method = RequestMethod.GET)
    public ResponseEntity<String> getCityAreas(@RequestParam(value = "areaLevel") String areaLevel,
                                               @RequestParam(value = "parentAreaCode", required = false) String parentAreaCode,
                                               @RequestParam(value = "areaCode", required = false) String areaCode,
                                               HttpServletRequest request) throws Exception {

        CityAreaDto cityAreaDto = new CityAreaDto();
        cityAreaDto.setAreaLevel(areaLevel);
        cityAreaDto.setAreaCode(areaCode);
        cityAreaDto.setParentAreaCode(parentAreaCode);

        List<CityAreaDto> cityAreaDtos = cityAreaServiceImpl.getCityAreas(cityAreaDto);
        return ResultDto.createResponseEntity(cityAreaDtos);
    }
}
