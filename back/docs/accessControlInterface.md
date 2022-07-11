# HC物联网系统对接门禁

## 说明

如何将自己的门禁设备对接至HC物联网系统，然后提供给别人使用是本文想要解决的问题，一般的门禁对接常见两种方式：
一种为门禁平台，这一种 不直接和门禁设备对接 而是和第三方提供的平台对接 根据平台提供的接口 将数据推送给第三方
平台；另一种则为直接和设备交互，设备会提供比较常见的 http 接口 mqtt接口 socket 接口等，两种的对接方式基本
一样 将数据同步给第三方平台或者 设备，一般第一种的对接难度会比较大一些，第三方平台一般需要的数据较多，比如小区
楼栋 单元 房屋 位置信息 设备信息 业主信息等需要将大量的数据推送给第三方平台，不推荐对接。第二种较简单写，只需要
将人脸信息上传设备即可，所需数据较少，以下为第二种方式说明整个对接过程。

## 需要对接的接口清单

### 1、获取设备上人脸接口

该接口的目的是 在 人脸同步 新增或者修改时 判断设备上是否存在 该人脸 如果存在 则修改 如果不存在则新增。
有些设备 如果已经存在时新增会失败，通过此接口判断是否存在，如果设备没有这个能力可以不对接，默认HC物联网系统
自己也有判断能力，对应于IAssessControlProcess.java 接口类的 getFace方法。

### 2、添加人脸

顾名思义，添加人脸为在界面添加人脸时将人脸信息送给设备，目前HC物联网系统本身不具备人脸识别的能力，人脸识别都是
需要设备自己实现。 对应于 IAssessControlProcess.java 接口类 的 addFace方法。

### 3、修改人脸

修改人脸为在人员的信息发生变化时将人脸信息重新同步门禁设备，对应于 IAssessControlProcess.java 接口类 的
 updateFace方法 。

### 4、删除人脸

删除人脸为在人员信息删除后需要在设备上删除相应的人脸，不能再让该人员通信。对应于 IAssessControlProcess.java
接口类 的deleteFace方法。

 
### 5、清空人脸

当界面清空人脸时需要门禁设备清楚设备上人脸，对应于 IAssessControlProcess.java 接口类 的clearFace方法。

### 6、重启设备

当在界面操作重启时需要调用设备接口 重启设备 对应于 IAssessControlProcess.java 接口类 的restartMachine方法。

### 7、远程开门

当业主 物业人员在小程序或者 系统上 点击远程开门时门禁设备需要开门放行 对应于 IAssessControlProcess.java 接口类 的
openDoor方法。

### 8.0、开门记录上报

当人员通信时，设备需要将人脸信息上报给HC物联网系统，供HC物联网系统查看  对应于 IAssessControlProcess.java 接口类 的
httpFaceResult方法。


## 如何对接

门禁对接只需要关注 后端代码下的 src\main\java\com\java110\things\adapt\accessControl 目录就可以

如果对接新的门禁可以建一个目录例如yufan 对接宇泛的门禁。

在accessControl目录下存在 如下的java类：

ICallAccessControlService 提供给适配器调用的接口 提供如查询设备信息等方法

IAssessControlProcess 门禁适配器需要实现的接口类 里面包含了需要实现的方法 如 添加人脸，修改人脸等接口
对接新的门禁需要实现该接口 实现每个方法即可

IAssessControlProcess 方法 入参和出参介绍

### getFace 方法

描述： 从设备中获取人脸信息

入参： MachineDto machineDto, UserFaceDto userFaceDto

machineDto 对象包含了设备相关信息，可以根据这个对象获取设备名称 编码 ip mac 等相关信息

userFaceDto 对象包含了人员 人脸相关信息 人员名称 人脸base64 等

方法说明： 该方法中只需要完成 根据门禁的协议 查询当前的人员是否在门禁中如果在返回人员ID，如果不在 返回-1

返回参数：字符串类型 如果有人员返回人员ID 没有返回 -1

举例：
```java

    public String getFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_ADD_FACE_FIND;
        
        //准备参数 json格式参数
        JSONObject param = new JSONObject();
        param.put("operator", "SearchPerson");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("SearchType", 0);
        info.put("SearchID", userFaceDto.getUserId());
        info.put("Picture", 1);
        param.put("info", info);
        
        // 调用门禁设备同步人脸信息
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), getHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_FACE_FIND, param.toJSONString(), responseEntity.getBody());

        //返回参数解析
        if (HttpStatus.OK != responseEntity.getStatusCode()) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        JSONObject outParam = JSONObject.parseObject(responseEntity.getBody());

        if (!outParam.containsKey("picinfo")) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        String picinfo = outParam.getString("picinfo");

        if (StringUtil.isEmpty(picinfo)) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        String personId = outParam.getJSONObject("info").getString("CustomizeID");

        if (StringUtil.isEmpty(personId)) {
            return AddUpdateFace.MACHINE_HAS_NOT_FACE;
        }

        return personId;
    }
```

### addFace 方法

描述： 添加人脸至门禁设备

入参：MachineDto machineDto, UserFaceDto userFaceDto

machineDto 对象包含了设备相关信息，可以根据这个对象获取设备名称 编码 ip mac 等相关信息

userFaceDto 对象包含了人员 人脸相关信息 人员名称 人脸base64 等

方法说明： 该方法中只需要完成 根据门禁的协议 将人员信息和人脸信息长传至门禁设备即可

返回参数：ResultDto 对象 成功code填写为0 失败填写为-1 并填写msg 失败原因

举例：

```java

public ResultDto addFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_ADD_USER;
        //根据门禁协议准备参数
        JSONObject param = new JSONObject();
        param.put("operator", "AddPerson");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("PersonType", 0);
        info.put("IdType", 0);
        info.put("CustomizeID", userFaceDto.getUserId());
        info.put("PersonUUID", userFaceDto.getUserId());
        info.put("Name", userFaceDto.getName());
        info.put("CardType", 0);
        info.put("IdCard", userFaceDto.getIdNumber());
        info.put("Tempvalid", 0);
        info.put("isCheckSimilarity", 0);
        param.put("info", info);
        //param.put("picinfo", userFaceDto.getFaceBase64()); // 人脸base64
        //或者时 图片下载地址
        param.put("picURI", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getMachineCode() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);
        
        //同步门禁设备
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), getHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_ADD_USER, param.toJSONString(), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResultDto(ResultDto.ERROR, "调用设备失败");
        }
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getInteger("code") == 200 ? ResultDto.SUCCESS : ResultDto.ERROR, "同步成功");

    }
```

### updateFace 方法

描述： 将门禁中的人员人脸信息修改

入参：MachineDto machineDto, UserFaceDto userFaceDto

machineDto 对象包含了设备相关信息，可以根据这个对象获取设备名称 编码 ip mac 等相关信息

userFaceDto 对象包含了人员 人脸相关信息 人员名称 人脸base64 等

方法说明： 该方法中只需要完成 根据门禁的协议 将人员信息和人脸信息更新门禁设备即可

返回参数：ResultDto 对象 成功code填写为0 失败填写为-1 并填写msg 失败原因

举例：

```java

public ResultDto updateFace(MachineDto machineDto, UserFaceDto userFaceDto) {
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_EDIT_USER;
        //根据门禁协议准备参数
        JSONObject param = new JSONObject();
        param.put("operator", "EditPerson");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("PersonType", 0);
        info.put("IdType", 0);
        info.put("CustomizeID", userFaceDto.getUserId());
        info.put("PersonUUID", userFaceDto.getUserId());
        info.put("Name", userFaceDto.getName());
        info.put("CardType", 0);
        info.put("IdCard", userFaceDto.getIdNumber());
        info.put("Tempvalid", 0);
        info.put("isCheckSimilarity", 0);
        param.put("info", info);
        //param.put("picinfo", userFaceDto.getFaceBase64());
        param.put("picURI", MappingCacheFactory.getValue(FACE_URL) + "/" + machineDto.getMachineCode() + "/" + userFaceDto.getUserId() + IMAGE_SUFFIX);

        //同步门禁设备
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), getHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_EDIT_USER, param.toJSONString(), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResultDto(ResultDto.ERROR, "调用设备失败");
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getInteger("code") == 200 ? ResultDto.SUCCESS : ResultDto.ERROR, "同步成功");
    }
```


### deleteFace 方法

描述： 将门禁中的人员人脸信息删除

入参：MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto

machineDto 对象包含了设备相关信息，可以根据这个对象获取设备名称 编码 ip mac 等相关信息

heartbeatTaskDto 对象包含了人员ID信息 id 为此对象的taskId

方法说明： 该方法中只需要完成 根据门禁的协议 将人员信息和人脸信息从门禁中删除

返回参数：ResultDto 对象 成功code填写为0 失败填写为-1 并填写msg 失败原因

举例：

```java
public ResultDto deleteFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_DELETE_FACE;
        //根据门禁协议准备参数
        JSONArray userIds = new JSONArray();
        userIds.add(heartbeatTaskDto.getTaskid());
        JSONObject param = new JSONObject();
        param.put("operator", "DeletePerson");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("TotalNum", 1);
        info.put("IdType", 0);
        info.put("CustomizeID", userIds);
        param.put("info", info);
        //同步门禁设备
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), getHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_DELETE_FACE, param.toJSONString(), responseEntity.getBody());

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResultDto(ResultDto.ERROR, "调用设备失败");
        }

        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getInteger("code") == 200 ? ResultDto.SUCCESS : ResultDto.ERROR, "同步成功");
    }
```

### clearFace 方法

描述： 将门禁中的人员人脸信息全部删除

入参：MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto

machineDto 对象包含了设备相关信息，可以根据这个对象获取设备名称 编码 ip mac 等相关信息

heartbeatTaskDto 在这个方法中这个对象其实没有任何用处

方法说明： 该方法中只需要完成 根据门禁的协议 将人员信息和人脸信息从门禁中全部删除

返回参数：ResultDto 对象 成功code填写为0 失败填写为-1 并填写msg 失败原因

举例：

```java
public ResultDto clearFace(MachineDto machineDto, HeartbeatTaskDto heartbeatTaskDto) {
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_RESET;
        //根据门禁协议准备参数
        JSONObject param = new JSONObject();
        param.put("operator", "DeleteAllPerson");
        JSONObject info = new JSONObject();
        info.put("DeleteAllPersonCheck", 1);
        param.put("info", info);
        //同步门禁设备
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), getHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_RESET, param.toJSONString(), responseEntity.getBody());
        JSONObject paramOut = JSONObject.parseObject(responseEntity.getBody());
        return new ResultDto(paramOut.getInteger("code") == 200 ? ResultDto.SUCCESS : ResultDto.ERROR, "同步成功");
    }
```

### restartMachine 方法

描述： 远程重启设备

入参：MachineDto machineDto

machineDto 对象包含了设备相关信息，可以根据这个对象获取设备名称 编码 ip mac 等相关信息

方法说明： 该方法中只需要完成 根据门禁的协议 调用 重启设备

返回参数：ResultDto 对象 成功code填写为0 失败填写为-1 并填写msg 失败原因

举例：

```java
public void restartMachine(MachineDto machineDto) {
        //组装门禁协议
        JSONObject param = new JSONObject();
        param.put("operator", "RebootDevice");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("IsRebootDevice", 1);
        param.put("info", info);
        //同步门禁设备
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_REBOOT;
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), getHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_REBOOT, param.toJSONString(), responseEntity.getBody());

    }
```


### openDoor 方法

描述： 远程开门接口，在小程序 或者 web端 远程开门

入参：MachineDto machineDto

machineDto 对象包含了设备相关信息，可以根据这个对象获取设备名称 编码 ip mac 等相关信息

方法说明： 该方法中只需要完成 根据门禁的协议 调用 远程开门

返回参数：ResultDto 对象 成功code填写为0 失败填写为-1 并填写msg 失败原因

举例：

```java
public void openDoor(MachineDto machineDto) {
        //组装远程开门 协议
        JSONObject param = new JSONObject();
        param.put("operator", "OpenDoor");
        JSONObject info = new JSONObject();
        info.put("DeviceID", machineDto.getMachineCode());
        info.put("Chn", 0);
        info.put("status", 0);
        info.put("msg", "请通行");
        param.put("info", info);
        
        String url = "http://" + machineDto.getMachineIp() + ":" + DEFAULT_PORT + CMD_OPEN_DOOR;
        // 调用 门禁远程开门
        HttpEntity httpEntity = new HttpEntity(param.toJSONString(), getHeaders());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        logger.debug("请求信息 ： " + httpEntity + "，返回信息:" + responseEntity);
        saveLog(SeqUtil.getId(), machineDto.getMachineId(), CMD_OPEN_DOOR, param.toJSONString(), responseEntity.getBody());
    }
```

### httpFaceResult 方法

描述： 门禁开门人脸推送

入参：String data

data 为具体门禁上报时的协议

方法说明： 该方法中只需要完成 根据门禁的协议 将数据保存至开门记录表即可

返回参数：String 根据门禁要求返回即可

举例：

```java
public String httpFaceResult(String data) {
        //获取上报接口类
        ICallAccessControlService notifyAccessControlService = NotifyAccessControlFactory.getCallAccessControlService();
        JSONObject resultParam = new JSONObject();
        try {
            JSONObject body = JSONObject.parseObject(data);

            JSONObject info = body.getJSONObject("info");


            MachineDto machineDto = new MachineDto();
            machineDto.setMachineCode(info.getString("DeviceID"));
            List<MachineDto> machineDtos = notifyAccessControlService.queryMachines(machineDto);

            if (machineDtos.size() < 0) {
                resultParam.put("code", 404);
                resultParam.put("desc", "设备不存在");
                return resultParam.toJSONString();//未找到设备
            }

            String userId = info.containsKey("PersonUUID") ? info.getString("PersonUUID") : "";
            String userName = "";
            if (!StringUtils.isEmpty(userId)) {
                MachineFaceDto machineFaceDto = new MachineFaceDto();
                machineFaceDto.setUserId(userId);
                machineFaceDto.setMachineId(machineDtos.get(0).getMachineId());
                List<MachineFaceDto> machineFaceDtos = notifyAccessControlService.queryMachineFaces(machineFaceDto);
                if (machineFaceDtos != null && machineFaceDtos.size() > 0) {
                    userName = machineFaceDtos.get(0).getName();
                }

            }

            //组装开门 对象
            OpenDoorDto openDoorDto = new OpenDoorDto();
            openDoorDto.setFace(body.getString("SanpPic").replace("data:image/jpeg;base64,", ""));
            openDoorDto.setUserName(userName);
            openDoorDto.setHat("3");
            openDoorDto.setMachineCode(machineDtos.get(0).getMachineCode());
            openDoorDto.setUserId(userId);
            openDoorDto.setOpenId(SeqUtil.getId());
            openDoorDto.setOpenTypeCd(OPEN_TYPE_FACE);
            openDoorDto.setSimilarity(info.getString("Similarity1"));
            //保存数据
            notifyAccessControlService.saveFaceResult(openDoorDto);

        } catch (Exception e) {
            logger.error("推送人脸失败", e);
            resultParam.put("code", 404);
            resultParam.put("desc", "异常");
            return resultParam.toJSONString();//未找到设备
        }
        resultParam.put("code", 200);
        resultParam.put("desc", "OK");
        return resultParam.toJSONString();//未找到设备

    }
```


### 门禁上报地址配置

官方通用的上报地址为 http://ip:port/api/accessControl/faceResult/设备编码

接口类型为http POST

协议为 String类型

具体java为：src\main\java\com\java110\things\api\accessControl\AccessControlController.java

注意：如果 门禁特殊 通用方法无法实现可以自己写接口类供 门禁使用


### 添加至协议表 hardware_manufacturer

```roomsql


INSERT INTO `hardware_manufacturer` (`hm_id`, `hm_name`, `version`, `protocol_impl`, `create_time`, `status_cd`, `hm_type`, `author`, `link`, `license`, `prod_url`, `default_protocol`) VALUES ('4', '宇泛协议', 'v1.0', 'cjHttpAssessControlProcessAdapt', '2020-12-20 12:36:28', '0', '1001', '吴学文', '17797173942', '官方暂无说明', 'http://www.baidu.com', 'F');


```

hm_id 协议ID自增

hm_name 协议名称

protocol_impl：协议实现了 是您写了那个协议适配器类的spring bean name

hm_type 门禁写1001


