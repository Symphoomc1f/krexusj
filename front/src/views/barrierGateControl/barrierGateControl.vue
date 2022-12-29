<template>
  <div class="app-container">
    <el-row>
      <!-- 主区 -->
      <el-col :span="18">
        <el-row v-for="(item, index) in machines" v-bind:key="index">
          <el-col :span="12">
            <div>设备：{{ item.machineName }}</div>
            <video-player
              class="video-player-box"
              ref="videoPlayer"
              :options="playerOptions"
              :playsinline="true"
              customEventName="customstatechangedeventname"
            >
            </video-player>
          </el-col>
          <el-col :span="12"> </el-col>
        </el-row>
      </el-col>
      <el-col :span="6"> 456 </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  getAccessControlsByCondition,
  restartAccessControls,
} from "@/api/barrierGate";
import Pagination from "@/components/Pagination";
import "video.js/dist/video-js.css";

import { videoPlayer } from "vue-video-player";
export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: "success",
        draft: "gray",
        deleted: "danger",
      };
      return statusMap[status];
    },
  },
  components: { Pagination, videoPlayer },
  data() {
    return {
      machines: [],
      listQuery: {
        page: 1,
        row: 10,
        machineTypeCd: "9996",
        machineCode: "",
        machineIp: "",
        machineMac: "",
      },
      playerOptions: {
          // videojs options
          muted: true,
          live: true,
          language: 'en',
          playbackRates: [0.7, 1.0, 1.5, 2.0],
          sources: [{
            type: "rtmp/flv",
            src: "rtsp://192.168.1.100:8557/h264"
          }],
          poster: "/static/images/author.jpg",
        }
    };
  },
  created() {
    this.queryMachine();
  },
  methods: {
    queryMachine() {
      this.listLoading = true;
      getAccessControlsByCondition(this.listQuery).then((response) => {
        this.machines = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    restartAccessControl(_row, _index) {
      this.listLoading = true;
      restartAccessControls(_row).then((response) => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: "已发送成功指令",
        });
      });
    },
  },
};
</script>
