## 后端说明

### 包结构

```
  accessControl 心跳 HC小区管理系统 门禁处理java
  adapt 对接不同的门禁 考勤 停车 适配器 包，重要，二开主要开发适配器
  aop 系统切面处理 登录拦截 外部api token拦截校验等
  api 给设备提供接口 如开门设备上报地址
  car 心跳 HC小区管理系统 车辆处理java
  config 配置相关内容 如mqtt 配置等
  constant 常量类
  Controller 页面端请求控制类
  dao 数据库操作层
  entity 实体对象
  exception 异常相关包
  extApi 对外平台开放API 相关java
  factory 工厂类处理包 如果门禁 认证工厂类等
  init 系统启动初始化包
  mqtt mqtt 相关java类
  netty netty 相关java
  quartz 定时器
  service 业务服务包
  thread 线程包
  util 工具类 如断言 日期 等工具类
```

### 第三方云平台对接协议

第三方平台对接 HC物联网平台 时请按照 [HC 物联网系统对接协议](docs/api.md) 对接


### 如何对接自己的门禁设备

对接自己的门禁设备请参考 文档[对接自己的门禁设备](docs/accessControlInterface.md)

### HC小区管理系统配合实时文档

如果和HC小区管理系统一起实施时参考 文档[HC小区管理系统对接使用指南](docs/linkHc.md)


### 更新日志

[HC 物联网系统更新日志](ChangeLog.md)