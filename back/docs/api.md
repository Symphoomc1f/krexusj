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

根据 appId+appSecret 换取 access_token。access_token 是平台全局接口的唯一凭证，调用其他接口均需携带 access_token
参数。

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

请求示例：
```json
{
    "name": "HC小区",
    "address": "青海省西宁市",
    "cityCode": "510104",
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

### 1.1 修改小区

请求方式： POST

请求地址：/extApi/community/updateCommunity

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| name | String | 是 | HC小区 | 小区名称 |
| address | String | 是 | 青海省西宁市 | 小区地址 |
| cityCode | String | 是 | 510104 | 地区码 |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |

请求示例：
```json
{
    "name": "HC小区",
    "address": "青海省西宁市",
    "cityCode": "510104",
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

### 1.1 删除小区

请求方式： POST

请求地址：/extApi/community/deleteCommunity

请求参数：

| 名称 | 类型 | 是否必填 | 示例值 | 描述 |
| :----:| :----: | :----: | :----: | :----: |
| extCommunityId | String | 是 | 702020042194860039 | 小区外部编码 |

请求示例：
```json
{
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



