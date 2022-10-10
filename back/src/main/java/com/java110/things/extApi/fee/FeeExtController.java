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
package com.java110.things.extApi.fee;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 费用对外提供接口类
 * add by 吴学文 2021-01-17
 * <p>
 * 该接口类为 需要将临时车费用等信息 同步时需要调用
 */

@RestController
@RequestMapping(path = "/extApi/fee")
public class FeeExtController {

    /**
     * 添加临时车费用
     * <p>
     *
     * @param reqParam
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/addTempCarFee", method = RequestMethod.POST)
    public ResponseEntity<String> addTempCarFee(@RequestBody String reqParam) throws Exception {
        return null;
    }

    /**
     * 修改临时车费用
     * <p>
     *
     * @param reqParam
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/updateTempCarFee", method = RequestMethod.POST)
    public ResponseEntity<String> updateTempCarFee(@RequestBody String reqParam) throws Exception {
        return null;
    }


    /**
     * 删除临时车费用
     * <p>
     *
     * @param reqParam
     * @return 成功或者失败
     * @throws Exception
     */
    @RequestMapping(path = "/deleteTempCarFee", method = RequestMethod.POST)
    public ResponseEntity<String> deleteTempCarFee(@RequestBody String reqParam) throws Exception {
        return null;
    }
}
