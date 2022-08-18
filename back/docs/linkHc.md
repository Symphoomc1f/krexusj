# HC小区管理系统对接使用指南

## 说明
HC小区管理系统是一套开源免费商用的小区物业管理系统，目前已经在多家物业多个小区商用，用户反馈良好，HC物联网系统独立于物业系统
而设计的管理门禁，道闸考勤等设备的物联网管理系统，它可以和其他第三方系统对接而独立运行（可以参考api.md 文档对接），不依赖HC小区管理系统

## 通信流程说明

1、在HC小区管理系统添加小区修改小区时 会通过HC小区管理系统的databus 将数据 同步（或异步）方式同步至HC物联网系统
2、在HC小区管理系统添加设备编辑设备和删除设备也会将设备信息通过databus 的方式同步至HC物联网系统，这里需要注意，在
HC小区管理系统添加设备时需要正确的填写厂家协议，HC物联网系统是根据HC小区管理系统同步的协议去找相应的设备的适配器和
设备或者平台通讯
3、在HC小区管理系统添加业主修改业主删除业主时通过HC小区管理系统的databus将数据同步至HC物联网系统，其中包括用户基本信息
和用户头像信息，具体是按照api.md 协议同步的（此时业主的同步会根据业主的位置同步到 单元门禁，大门门禁）
4、物联网系统收到设备的开门记录时会将记录上报给HC小区管理系统是通过api.md协议中的4.1 开门上报
5、HC物联网系统收到设备心跳信息时也会将心跳时间上报给HC小区管理系统，此时可能设备的时钟和HC小区管理系统的时钟不同步，所以
HC小区管理系统在入库时没有取设备上报时间，而是获取了服务器当前时间
6、当在HC小区管理系统或者业主手机版远程开门时 HC小区管理系统将调用HC物联网系统接口实现远程开门

## 配置流程

1、首先开启databus,登录HC小区管理系统开发这账户（dev/密码您自己设置的），编码映射中找到域为DOMAIN.COMMON键为DATABUS_SWITCH
的记录，点击修改将其值修改为ON，点击缓存管理刷新所有缓存

2、打开编码映射databus 页面是否存在以下适配器，如果不存在 请添加

```
名称	                业务类型	             适配器
业主解绑房屋同步IOT	111100050001	ownerUnBindRoomToIotAdapt
业主绑定房屋同步IOT	111100030001	ownerBindRoomToIotAdapt
删除业主同步IOT		110100050001	deleteOwnerToIotAdapt
修改业主同步IOT		110100040001	editOwnerToIotAdapt
添加业主同步IOT		110100030001	addOwnerToIotAdapt
删除设备同步IOT		200200050001	deleteMachineToIotAdapt
修改设备同步IOT		200200040001	modifyMachineToIotAdapt
保存设备同步IOT		200200030001	addMachineToIotAdapt
删除小区同步IOT		500100050001	deleteCommunityToIotAdapt
修改小区同步IOT		500100040001	editCommunityToIotAdapt
添加小区同步IOT	    500100030001	    addCommunityToIotAdapt

```

3、在HC物联网系统库中 添加一条应用记录(当然测试您可以直接用HC测试这个应用)

```sql

INSERT INTO c_app(`app_id`, `app_secret`, `app_name`)
VALUES ('e86a6a373c354927bea5fd21a0bec617', 'ead9a2f67f96e2b8ed2fe38cc9709463', 'HC测试');
```

app_id 和 app_secret 请自行生成 推荐为两个uuId

4、在HC小区管理系统编码映射中根据域为IOT查询 到APP_ID、APP_SECRET和IOT_URL，将APP_ID 和APP_SECRET 的值
修改为HC物联网系统中app_id 和 app_secret的值，IOT_URL请填写为物联网系统地址如：http://proxy.homecommunity.cn:9006/

5、在HC物联网系统中配置开门记录上报地址 心跳地址等配置（当然测试可以直接用HC测试这个应用的配置，只需要将域名修改即可）

```sql

INSERT INTO c_app_attr (`attr_id`, `app_id`, `spec_cd`, `value`)
VALUES ('1', 'e86a6a373c354927bea5fd21a0bec617', '3001001', 'https://api.demo.winqi.cn/api/machine/openDoorLog');
INSERT INTO c_app_attr (`attr_id`, `app_id`, `spec_cd`, `value`)
VALUES ('2', 'e86a6a373c354927bea5fd21a0bec617', '3002001', 'https://api.demo.winqi.cn/api/machine/cmdResult');
INSERT INTO c_app_attr (`attr_id`, `app_id`, `spec_cd`, `value`)
VALUES ('3', 'e86a6a373c354927bea5fd21a0bec617', '3003001', '992020051967020024');
INSERT INTO c_app_attr (`attr_id`, `app_id`, `spec_cd`, `value`)
VALUES ('4', 'e86a6a373c354927bea5fd21a0bec617', '3004001', 'g3kE9ggkM4Jqrs576rJS0CYg7dbtMXPT');
INSERT INTO c_app_attr (`attr_id`, `app_id`, `spec_cd`, `value`)
VALUES ('5', 'e86a6a373c354927bea5fd21a0bec617', '3005001', 'https://api.demo.winqi.cn/api/machine/heartbeat');

```
spec_cd 说明：

3001001 开门记录上报

3002001 设备执行命令结果上报（当设备是mqtt方式时，设备同步数据结果告知为异步，需要通过此地址上报HC小区管理系统）

3003001 HC小区管理系统中的应用ID，在开发者账户应用中查找HC小区物联网平台应用ID（一般不需要修改）

3004001 HC小区管理系统中的应用秘钥，在开发者账户应用中查找HC小区物联网平台应用秘钥

3005001 设备心跳上报HC小区管理系统地址

以上地址的就该只需要将https://api.demo.winqi.cn替换为HC小区管理系统api服务真实的url即可。



