# HC 物联网系统对接协议

## 开发前必读

### 协议规范设计

第三方调用开放接口时，需使用 https 协议、Json 数据格式、UTF8 编码

注：建议封装统一的 API 请求接口，便于进行 token 容错处理，以及相关日志打
  印等
  
 请求参数：默认使用 json 方式（access_token 必须传至http header 中）
 
 通用响应报文格式：
 
 ```json
    {
    "code":0,
    "msg":"返回码描述内容",
    "data": {}
    }
```

通用响应报文字段说明：

| 名称 | 类型 | 描述 |
| :----:| :----: | :----: |
| code | Integer | 返回码 |
| msg | String | 对返回码的文本描述内容 |
| data | Object | 返回数据（部分接口无返回数据） |

通用错误码说明

| 状态 | 描述 |
| :----:|  :----: |
| 0 |  成功 |
| -1 |  失败 |

### 授权认证-获取access_token

根据 appId+appSecret 换取 access_token。access_token 是平台全局接口的唯一凭证，调用其他接口均需传 access_token
参数到Http header 中 如 access_token 12312312。

请求方式：GET

请求地址： /extApi/auth/getAccessToken?appId=APPID&appSecret=APPSECRET

注：此处标注大写的单词 APPID 和 APPSECRET，为需要替换的变量，根据实际获取值更新。其它接口也采用相同的标注，不
再说明。

请求参数:

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| appId | String | 是 | 2fe1465ce30f4bc1b71978d42349c6 | APPID |
| appSecret | String | 是 | 2fe1465ce30f4bc1b71978d42349c2 | APP 秘钥 |

响应参数:

| 名称 | 类型  | 示例值 | 描述 |
| :----:| :----: | :----: | :----: |
| access_token | String | 2fe1465ce30f4bc1b71978d42349c6 | accessToken 访问凭据 |
| expires_in | Integer | 7200 | 过期时间为 7200 秒，即 2 小时 |

响应示例:

```json
    {
        "code":0,
        "msg":"成功",
        "data":{
            "access_token": "225262e8b548b77f76f018d218291647",
            "expires_in": 7200
            }
    }
```

参考代码：
```java
    //@Java110Synchronized 为分布式全局锁 根据 实际替换为自己的全局锁代码
    @Java110Synchronized(value = "hc_get_token")
    public static String get(RestTemplate restTemplate,boolean refreshAccessToken) {
        //先从缓存中获取是否存在access_token
        String token = CommonCache.getValue(IotConstant.HC_TOKEN);
        if (!StringUtil.isEmpty(token) && !refreshAccessToken) {
            return token;
        }
        HttpHeaders headers = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(headers);
        String url = IotConstant.getUrl(IotConstant.GET_TOKEN_URL.replace("APP_ID", IotConstant.getAppId()).replace("APP_SECRET", IotConstant.getAppSecret()));
        ResponseEntity<String> tokenRes = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);

        if (tokenRes.getStatusCode() != HttpStatus.OK) {
            throw new IllegalArgumentException("获取token失败" + tokenRes.getBody());
        }
        JSONObject tokenObj = JSONObject.parseObject(tokenRes.getBody());

        if (!tokenObj.containsKey("code") || ResultVo.CODE_OK != tokenObj.getInteger("code")) {
            throw new IllegalArgumentException("获取token失败" + tokenRes.getBody());
        }

        token = tokenObj.getJSONObject("data").getString("access_token");
        int expiresIn = tokenObj.getJSONObject("data").getInteger("expires_in");

        CommonCache.setValue(IotConstant.HC_TOKEN, token, expiresIn - 200);

        return token;
    }
```

## 1.小区资源操作

### 1.1 新增小区

请求方式： POST

请求地址：/extApi/community/addCommunity

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| name | String | 是 | HC小区 | 小区名称 |
| address | String | 是 | 青海省西宁市 | 小区地址 |
| cityCode | String | 是 | 510104 | 地区码 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "name": "HC小区",
    "address": "青海省西宁市",
    "cityCode": "510104",
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 1.2 修改小区

请求方式： POST

请求地址：/extApi/community/updateCommunity

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| name | String | 是 | HC小区 | 小区名称 |
| address | String | 是 | 青海省西宁市 | 小区地址 |
| cityCode | String | 是 | 510104 | 地区码 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "name": "HC小区",
    "address": "青海省西宁市",
    "cityCode": "510104",
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 1.3 删除小区

请求方式： POST

请求地址：/extApi/community/deleteCommunity

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

## 2.设备资源操作

### 2.1 新增设备

请求方式： POST

请求地址：/extApi/machine/addMachine

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| machineCode | String | 是 | 101010 | 设备编码 |
| machineVersion | String | 否 | v1.0 | 设备版本 |
| machineName | String | 是 | HC门禁 | 设备名称 |
| machineTypeCd | String | 是 | 9999 | 设备类型 9999	门禁9996	道闸9997	考勤机 |
| locationType | String | 是 | 9999 | 位置类型 1000 小区 4000 停车场 |
| locationObjId | String | 是 | 9999 | 位置ID |
| machineIp | String | 否 | 192.168.1.1 | 设备IP |
| machineMac | String | 否 | 11:1:11:1 | 设备mac |
| extMachineId | String | 是 | 702020042194860037 | 外部设备编码 |
| extCommunityId | String | 是 | 702020042194860039 | 外部小区编码 |
| hmId | String | 是 | 1 | 设备协议 HC物联网系统提供 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |


请求示例：
```json
{
    "machineCode": "101010",
    "machineName": "HC门禁",
    "machineTypeCd": "9999",
    "locationType": "1000",
    "locationObjId": "702020042194860039",
    "extMachineId": "702020042194860037",
    "extCommunityId": "702020042194860039",
    "hmId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 2.2 修改设备

请求方式： POST

请求地址：/extApi/machine/updateMachine

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| machineCode | String | 是 | 101010 | 设备编码 |
| machineVersion | String | 否 | v1.0 | 设备版本 |
| machineName | String | 是 | HC门禁 | 设备名称 |
| machineTypeCd | String | 是 | 9999 | 设备类型 门禁9999 |
| locationType | String | 是 | 9999 | 位置类型 1000 小区 4000 停车场 |
| locationObjId | String | 是 | 9999 | 位置ID |
| machineIp | String | 否 | 192.168.1.1 | 设备IP |
| machineMac | String | 否 | 11:1:11:1 | 设备mac |
| extMachineId | String | 是 | 702020042194860037 | 外部设备编码 |
| extCommunityId | String | 是 | 702020042194860039 | 外部小区编码 |
| hmId | String | 是 | 1 | 设备协议 HC物联网系统提供 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |


请求示例：
```json
{
    "machineCode": "101010",
    "machineName": "HC门禁",
    "machineTypeCd": "9999",
    "locationType": "1000",
    "locationObjId": "702020042194860039",
    "extMachineId": "702020042194860037",
    "extCommunityId": "702020042194860039",
    "hmId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 2.3 删除设备

请求方式： POST

请求地址：/extApi/machine/deleteMachine

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| extMachineId | String | 是 | 702020042194860037 | 外部设备编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |


请求示例：
```json
{
    "extMachineId": "702020042194860037",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```
### 2.4 远程开门

请求方式： POST

请求地址：/extApi/machine/openDoor

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| extMachineId | String | 是 | 702020042194860037 | 外部设备编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |


请求示例：
```json
{
    "machineCode": "101010",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 2.5 重启设备

请求方式： POST

请求地址：/extApi/machine/restartMachine

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| extMachineId | String | 是 | 702020042194860037 | 外部设备编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |


请求示例：
```json
{
    "machineCode": "101010",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```


## 3.用户资源操作

### 3.1 添加用户

请求方式： POST

请求地址：/extApi/user/addUser

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| userId | String | 是 | 702020042194860037 | 用户ID |
| faceBase64 | String | 是 | base64 | base64图片 |
| startTime | String | 是 | 2020-12-01 00:00:00 | 开始时间 |
| endTime | String | 是 | 2020-12-31 00:00:00| 结束时间 |
| name | String | 是 | 张三 | 名称 |
| idNumber | String | 否 | 63216111111111111 | 身份证 |
| personType | String | 是 | 2002 | 人员类型，1001 员工 2002 业主 3003 访客 |
| machineCode | String | 是 | 101010 | 设备编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |


请求示例：
```json
{
    "userId": "702020042194860037",
    "faceBase64": "base64",
    "startTime": "2020-12-01 00:00:00",
    "endTime": "2020-12-31 00:00:00",
    "name": "张三",
    "idNumber": "63216111111111111",
    "personType": "2002",
    "machineCode": "101010",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 3.2 编辑用户

请求方式： POST

请求地址：/extApi/user/updateUser

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| userId | String | 是 | 702020042194860037 | 用户ID |
| faceBase64 | String | 是 | base64 | base64图片 |
| startTime | String | 是 | 2020-12-01 00:00:00 | 开始时间 |
| endTime | String | 是 | 2020-12-31 00:00:00| 结束时间 |
| name | String | 是 | 张三 | 名称 |
| idNumber | String | 否 | 63216111111111111 | 身份证 |
| machineCode | String | 是 | 101010 | 设备编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |


请求示例：
```json
{
    "userId": "702020042194860037",
    "faceBase64": "base64",
    "startTime": "2020-12-01 00:00:00",
    "endTime": "2020-12-31 00:00:00",
    "name": "张三",
    "idNumber": "63216111111111111",
    "machineCode": "101010",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 3.3 删除用户

请求方式： POST

请求地址：/extApi/user/deleteUser

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| userId | String | 是 | 702020042194860037 | 用户ID |
| machineCode | String | 是 | 101010 | 设备编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |


请求示例：
```json
{
    "userId": "702020042194860037",
    "machineCode": "101010",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 3.4 清空用户

请求方式： POST

请求地址：/extApi/user/clearUser

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| machineCode | String | 是 | 101010 | 设备编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |


请求示例：
```json
{
    "machineCode": "101010",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

## 4. 开门记录资源

### 4.1 开门上报

说明：HC物联网系统将开门记录上报第三方平台，需要物联网平台配置

请求方式： POST

请求地址：第三方平台提供

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| userId | String | 是 | 702020042194860037 | 用户ID |
| userName | String | 是 | 张三 | 用户名称 |
| machineCode | String | 是 | 101010 | 设备编码 |
| openTypeCd | String | 是 | 1000 | 开门方式 1000 人脸开门 2000 钥匙开门 |
| similar | String | 是 | 100 | 开门相似度 |
| photo | String | 是 | base64,xxx | 抓拍照片 |
| dateTime | String | 是 | 2020-12-27 00:00:00 | 开门时间 |
| extCommunityId | String | 是 | 702020042194860039 | 小区ID |
| recordTypeCd | String | 是 | 8888 | 记录类型，8888 开门记录 6666 访客留影 |


请求示例：
```json
{
    "userId": "702020042194860037",
    "userName": "张三",
    "machineCode": "101010",
    "openTypeCd": "1000",
    "similar": "100",
    "photo": "base64,xxx",
    "dateTime": "2020-12-27 00:00:00",
    "extCommunityId": "702020042194860039",
    "recordTypeCd": "8888"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":""
}
```

### 4.2 指令上报执行结果

说明：当和物联网系统异步交互时 或者物联网系统采用mqtt 协议时，执行结果 需要异步方式上报，需要物联网系统配置地址

请求方式： POST

请求地址：第三方平台提供

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| taskId | String | 是 | 702020042194860037 | 任务ID，第三方系统调用结果时传递 |
| code | Integer | 是 | 0 | 指令执行状态码 0 成功 其他失败 |
| msg | String | 是 | 成功 | 指令执行说明 |

请求示例：
```json
{
    "taskId": "702020042194860037",
    "code": 0,
    "msg": "成功"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":""
}
```

### 4.3 设备心跳存活上报

说明：设备定时心跳第三方系统表明设备在线 基本心跳在30秒至1分钟左右

请求方式： POST

请求地址：第三方平台提供

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| taskId | String | 是 | 702020042194860037 | 任务ID，物联网系统生成 |
| machineCode | String | 是 | 101010 | 设备编码  |
| heartbeatTime | String | 是 | 2020-05-10 19:43:34 | 心跳时间 |
| extCommunityId | String | 是 | 702020042194860039 | 小区ID |

请求示例：
```json
{
    "taskId": "702020042194860037",
    "machineCode": "101010",
    "heartbeatTime": "2020-05-10 19:43:34",
    "extCommunityId": "702020042194860039"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":""
}
```

## 5.停车场资源操作

### 5.1 新增停车场

请求方式： POST

请求地址：/extApi/parkingArea/addParkingArea

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| num | String | 是 | 001 | 停车场编号 |
| extPaId | String | 是 | 510104 | 停车场外部编码 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "num": "001",
    "extPaId": "510104",
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 5.2 修改停车场

请求方式： POST

请求地址：/extApi/parkingArea/updateParkingArea

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| num | String | 是 | 001 | 停车场编号 |
| extPaId | String | 是 | 510104 | 停车场外部编码 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "num": "001",
    "extPaId": "510104",
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 5.3 删除停车场

请求方式： POST

请求地址：/extApi/parkingArea/deleteParkingArea

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| extPaId | String | 是 | 510104 | 停车场外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "extPaId": "510104",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

## 6.车辆资源操作

### 6.1 新增车辆

请求方式： POST

请求地址：/extApi/car/addCar

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| carNum | String | 是 | 青A12345 | 车牌号 |
| startTime | String | 是 | 2020-05-01 00:00:00 | 开始时间 |
| endTime | String | 是 | 2020-06-01 00:00:00 | 结束时间 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| extCarId | String | 是 | 702020042194860034 | 车辆外部编码 |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| personName | String | 是 | 张三 | 联系人名称 |
| personTel | String | 是 | 18909711234 | 联系人电话 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "carNum": "001",
    "startTime": "2020-05-01 00:00:00",
    "endTime": "2020-06-01 00:00:00",
    "extCarId": "702020042194860034",
    "extPaId": "602020042194860039",
    "personName": "张三",
    "personTel": "18909711234",
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 6.2 修改车辆

请求方式： POST

请求地址：/extApi/car/updateCar

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| carNum | String | 是 | 青A12345 | 车牌号 |
| startTime | String | 是 | 2020-05-01 00:00:00 | 开始时间 |
| endTime | String | 是 | 2020-06-01 00:00:00 | 结束时间 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| extCarId | String | 是 | 702020042194860034 | 车辆外部编码 |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| personName | String | 是 | 张三 | 联系人名称 |
| personTel | String | 是 | 18909711234 | 联系人电话 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "carNum": "001",
    "startTime": "2020-05-01 00:00:00",
    "endTime": "2020-06-01 00:00:00",
    "extCarId": "702020042194860034",
    "extPaId": "602020042194860039",
    "personName": "张三",
    "personTel": "18909711234",
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 6.3 删除车辆

请求方式： POST

请求地址：/extApi/car/deleteCar

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| extCarId | String | 是 | 702020042194860034 | 车辆外部编码 |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "extCarId": "702020042194860034",
    "extPaId": "602020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

## 7.车辆黑白名单

### 7.1 新增名单

请求方式： POST

请求地址：/extApi/car/addBlackWhite

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| carNum | String | 是 | 青A12345 | 车牌号 |
| startTime | String | 是 | 2020-05-01 00:00:00 | 开始时间 |
| endTime | String | 是 | 2020-06-01 00:00:00 | 结束时间 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| blackWhite | String | 是 | 1111 | 名单类型 1111 黑名单 2222 白名单 |
| extBwId | String | 是 | 1120201228259128634 | 名单类型 1111 黑名单 2222 白名单 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "carNum": "001",
    "startTime": "2020-05-01 00:00:00",
    "endTime": "2020-06-01 00:00:00",
    "extCommunityId": "702020042194860039",
    "extPaId": "602020042194860039",
    "blackWhite": "1111",
    "extBwId": "1120201228259128634",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 7.2 删除名单

请求方式： POST

请求地址：/extApi/car/deleteBlackWhite

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| carNum | String | 是 | 青A12345 | 车牌号 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| extBwId | String | 是 | 1120201228259128634 | 名单类型 1111 黑名单 2222 白名单 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "carNum": "001",
    "extCommunityId": "702020042194860039",
    "extPaId": "602020042194860039",
    "extBwId": "1120201228259128634",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

## 8.临时车费用资源操作

### 8.1 新增费用

请求方式： POST

请求地址：/extApi/fee/addTempCarFee

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| feeName | String | 是 | 小车收费 | 收费标准名称 |
| carType | String | 是 | 9901 | 车辆类型，9901 家用小汽车，9904 电动车 9905 三轮车 |
| ruleId | String | 是 | 6700012001 | 标准收费 6700012001 |
| startTime | String | 是 | 2020-05-01 00:00:00 | 开始时间 |
| endTime | String | 是 | 2020-06-01 00:00:00 | 结束时间 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| extConfigId | String | 是 | 922021011685670005 | 外部费用ID |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "feeName": "小车收费",
    "carType": "9901",
    "ruleId": "6700012001",
    "startTime": "2020-05-01 00:00:00",
    "endTime": "2020-06-01 00:00:00",
    "extPaId": "602020042194860039",
    "extConfigId": "922021011685670005",
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045",
    "attrs": [
                {
                    "specCd": "5600012001",
                    "value": "31"
                }, {
                    "specCd": "5600012009",
                    "value": "21"
                }, {	
                    "specCd": "5600012002",
                    "value": "120"
                }, {
                    "specCd": "5600012003",
                    "value": "5"
                }, {
                    "specCd": "5600012004",
                    "value": "60"
                }, {
                    "specCd": "5600012005",
                    "value": "1.5"
                }, {
                    "specCd": "5600012006",
                    "value": "480"
                }, {
                    "specCd": "5600012007",
                    "value": "60"
                }, {
                    "specCd": "5600012008",
                    "value": "1.5"
                }
    ]
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 8.2 修改费用

请求方式： POST

请求地址：/extApi/fee/updateTempCarFee

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| feeName | String | 是 | 小车收费 | 收费标准名称 |
| carType | String | 是 | 9901 | 车辆类型，9901 家用小汽车，9904 电动车 9905 三轮车 |
| ruleId | String | 是 | 6700012001 | 标准收费 6700012001 |
| startTime | String | 是 | 2020-05-01 00:00:00 | 开始时间 |
| endTime | String | 是 | 2020-06-01 00:00:00 | 结束时间 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| extConfigId | String | 是 | 922021011685670005 | 外部费用ID |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "feeName": "小车收费",
    "carType": "9901",
    "ruleId": "6700012001",
    "startTime": "2020-05-01 00:00:00",
    "endTime": "2020-06-01 00:00:00",
    "extPaId": "602020042194860039",
    "extConfigId": "922021011685670005",
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045",
    "attrs": [
                {
                    "specCd": "5600012001",
                    "value": "31"
                }, {
                    "specCd": "5600012009",
                    "value": "21"
                }, {	
                    "specCd": "5600012002",
                    "value": "120"
                }, {
                    "specCd": "5600012003",
                    "value": "5"
                }, {
                    "specCd": "5600012004",
                    "value": "60"
                }, {
                    "specCd": "5600012005",
                    "value": "1.5"
                }, {
                    "specCd": "5600012006",
                    "value": "480"
                }, {
                    "specCd": "5600012007",
                    "value": "60"
                }, {
                    "specCd": "5600012008",
                    "value": "1.5"
                }
    ]
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

### 8.3 删除费用

请求方式： POST

请求地址：/extApi/fee/deleteTempCarFee

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| extConfigId | String | 是 | 922021011685670005 | 费用标准ID |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "extConfigId": "922021011685670005",
    "extPaId": "602020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```

## 9.道闸问候语资源操作

### 9.1 问候语修改

请求方式： POST

请求地址：/extApi/parkingAreaText/addParkingAreaText

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| typeCd | String | 是 | 1001 | 类型：1001 月租车进场，2002 月租车出场，3003 月租车到期，4004 临时车进场 5005 临时车出场 6006 临时车未缴费 |
| text1 | String | 是 | 你好 | 文字显示第一行 |
| text2 | String | 是 | 你好 | 文字显示第二行 |
| text3 | String | 是 | 你好 | 文字显示第三行 |
| text4 | String | 是 | 你好 | 文字显示第四行 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |
| voice | String | 是 | 你好 | 语音 |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "typeCd": "1001",
    "text1": "你好",
    "text2": "你好",
    "text3": "你好",
    "text4": "你好",
    "extPaId": "602020042194860039",
    "voice": "你好",
    "extCommunityId": "702020042194860039",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```


### 9.2 手工进出场

请求方式： POST

请求地址：/extApi/machine/customCarInOut

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| extMachineId | String | 是 | 702020042194860037 | 外部设备编码 |
| carNum | String | 是 | 青AGK123 | 车牌号 |
| type | String | 是 | 1101 | 类型 1101 进场 1102 出场 |
| amount | String | 否 | 10 | 出场金额 |
| payCharge | String | 否 | 10 | 出场应收 |
| extPaId | String | 是 | 602020042194860039 | 停车场外部编码 |
| taskId | String | 是 | 102020042194860045 | 任务ID，第三方生成唯一值 |

请求示例：
```json
{
    "extMachineId": "123123123",
    "carNum": "青AGK123",
    "type": "1102",
    "amount": "10",
    "payCharge": "10",
    "taskId": "102020042194860045"
}
```

返回示例：
```json
{
    "code":0,
    "msg":"成功",
    "data":{
       "taskId": "102020042194860045"
    }
}
```
