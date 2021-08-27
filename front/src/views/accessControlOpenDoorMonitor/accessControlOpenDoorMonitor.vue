<template>
  <div class="app-container">
    <el-row :gutter="20" justify="center" type="flex">
      <el-col :span="8" justify="center">
        <el-tag>模板图片</el-tag>
        <div class="grid-content bg-purple" style="width:300px;height:350px">
          <el-image
            ref="lazyImg"
            lazy
            class="vx-lazyLoad"
            :src="monitor.modelFace"
            :fit="fit"
            :preview-src-list="[monitor.modelFace]"
            style="width:300px"
          >
            <div slot="placeholder" class="image-slot">
              <i class="el-icon-loading"></i>加载中
            </div>
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </div>
        <el-tag>抓拍图片</el-tag>
        <div class="grid-content bg-purple">
          <el-image
            ref="lazyImg"
            lazy
            class="vx-lazyLoad"
            :src="monitor.face"
            :fit="fit"
            :preview-src-list="[monitor.face]"
            style="width:300px"
          >
            <div slot="placeholder" class="image-slot">
              <i class="el-icon-loading"></i>加载中
            </div>
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="grid-content bg-purple">
          <el-form ref="form" :model="form" label-width="80px">
            <el-form-item label="姓名">
              <el-input v-model="monitor.userName" disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="开门方式">
              <el-input v-model="monitor.openTypeCdName" disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="是否带帽">
              <el-input v-model="monitor.hatName" disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="相似度">
              <el-tag v-if="monitor.similarity > 0" type="success">{{ monitor.similarity }}</el-tag>
              <el-tag v-else type="danger">开门失败</el-tag>
            </el-form-item>
            <el-form-item label="欠费情况">
              <el-input v-model="monitor.amountOwed" disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="门禁名称">
              <el-input v-model="monitor.machineName" disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="门禁编码">
              <el-input v-model="monitor.machineCode" disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="门禁IP">
              <el-input v-model="monitor.machineIp" disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="开门时间">
              <el-input v-model="monitor.createTime" disabled="true"></el-input>
            </el-form-item>
          </el-form>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getMachineOpenDoors } from "@/api/accessControl";

import { parseTime } from "@/utils";

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: "success",
        draft: "gray",
        deleted: "danger"
      };
      return statusMap[status];
    }
  },
  data() {
    return {
      monitor: {
        modelFace: "",
        face: "",
        userName: "",
        openTypeCdName: "",
        hatName: "",
        similarity: "",
        amountOwed: "",
        machineName: "",
        machineId: "",
        machineCode: "",
        machineIp: "",
        createTime: ""
      },
      listLoading: true
    };
  },
  created() {
    this.monitor.machineId = this.$route.query.machineId; //接收参数
    this.fetchData();
  },
  methods: {
    fetchData() {
      var websocket = null;

      var _that = this;

      var url =
        "ws://localhost:9999/openDoorMonitor/" +
        this.uuid() +
        "/" +
        this.monitor.machineId;
      if ("WebSocket" in window) {
        websocket = new WebSocket(url);
      } else if ("MozWebSocket" in window) {
        websocket = new MozWebSocket(url);
      } else {
        websocket = new SockJS(url);
      }

      //连接发生错误的回调方法
      websocket.onerror = function() {
        console.log("初始化失败");
        this.$notify.error({
          title: "错误",
          message: "连接失败，请检查网络"
        });
      };

      //连接成功建立的回调方法
      websocket.onopen = function() {
        console.log("ws初始化成功");
      };

      //接收到消息的回调方法
      websocket.onmessage = function(event) {
        console.log("event", event);
        //let _data = event.data;
        _that.freshMonitor(event.data);
      };

      //连接关闭的回调方法
      websocket.onclose = function() {
        console.log("初始化失败");
        this.$notify.error({
          title: "错误",
          message: "连接关闭，请刷新浏览器"
        });
      };

      //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
      window.onbeforeunload = function() {
        websocket.close();
      };
    },
    freshMonitor(_data) {
      console.log("_data", _data);
      _data = JSON.parse(_data);
      this.monitor = {
        modelFace: _data.modelFace,
        face: _data.face,
        userName: _data.userName,
        openTypeCdName: _data.openTypeCd == "1000" ? "人脸" : "其他",
        hatName: _data.hat == "1" ? "无" : "有",
        similarity: _data.similarity,
        amountOwed: "0",
        machineName: _data.machineName,
        machineId: _data.machineId,
        machineCode: _data.machineCode,
        machineIp: _data.machineIp,
        createTime: _data.createTime
      };
    },
    uuid() {
      var s = [];
      var hexDigits = "0123456789abcdef";
      for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
      }
      s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
      s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
      s[8] = s[13] = s[18] = s[23] = "-";

      var uuid = s.join("");
      return uuid;
    }
  }
};
</script>
