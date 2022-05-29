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

/**
 * HC 物联网 java代码
 * <p>
 * 目录解释：
 * accessControl 心跳 HC小区管理系统 门禁处理java
 * adapt 对接不同的门禁 考勤 停车 适配器 包，重要，二开主要开发适配器
 * aop 系统切面处理 登录拦截 外部api token拦截校验等
 * api 给设备提供接口 如开门设备上报地址
 * car 心跳 HC小区管理系统 车辆处理java
 * config 配置相关内容 如mqtt 配置等
 * constant 常量类
 * Controller 页面端请求控制类
 * dao 数据库操作层
 * entity 实体对象
 * exception 异常相关包
 * extApi 对外平台开放API 相关java
 * factory 工厂类处理包 如果门禁 认证工厂类等
 * init 系统启动初始化包
 * mqtt mqtt 相关java类
 * netty netty 相关java
 * quartz 定时器
 * service 业务服务包
 * thread 线程包
 * <p>
 * util 工具类 如断言 日期 等工具类
 */
package com.java110.things;