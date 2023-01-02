<template>
  <div class="app-container">
    <el-row>
      <!-- 主区 -->
      <el-col :span="18">
        <el-row :gutter="20">
          <el-col :span="12">
            <div style="margin-bottom: 20px">
              <el-row>
                <el-col :span="18">
                  入场视频:
                  <el-select v-model="enterMachineId" placeholder="请选择">
                    <el-option
                      v-for="item in machines"
                      :key="item.machineId"
                      :label="item.machineName"
                      :value="item.machineId"
                    >
                    </el-option>
                  </el-select>
                </el-col>
                <el-col :span="6" style="text-align: right">
                  <el-button type="primary">开闸</el-button>
                </el-col>
              </el-row>
            </div>
            <video src="movie.ogg" width="100%" controls="controls">
              您的浏览器不支持 video 标签。
            </video>
          </el-col>
          <el-col :span="12">
            <div style="margin-bottom: 20px">
              <el-row>
                <el-col :span="18">
                  出场视频:
                  <el-select v-model="outMachineId" placeholder="请选择">
                    <el-option
                      v-for="item in machines"
                      :key="item.machineId"
                      :label="item.machineName"
                      :value="item.machineId"
                    >
                    </el-option>
                  </el-select>
                </el-col>
                <el-col :span="6" style="text-align: right">
                  <el-button type="primary">开闸</el-button>
                </el-col>
              </el-row>
            </div>
            <video src="movie.ogg" width="100%" controls="controls">
              您的浏览器不支持 video 标签。
            </video>
          </el-col>
        </el-row>
        <!-- 进出场记录--->
        <div :gutter="20" style="margin-top: 20px">
          <el-tabs v-model="activeName" type="card" @tab-click="handleClick">
            <el-tab-pane label="出入场信息" name="first">
              <el-row :gutter="20">
                <el-col :span="12" style="text-align: center">
                  <el-image
                    src="https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2576718747,3658011797&fm=26&gp=0.jpg"
                  ></el-image>
                </el-col>
                <el-col :span="12" style="text-align: center">
                  <el-image
                    src="https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2576718747,3658011797&fm=26&gp=0.jpg"
                  ></el-image>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane label="在场记录" name="second">在场车辆</el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
      <el-col
        :span="1"
        style="
          background: linear-gradient(to bottom, #efefef, #b6b6b6, #efefef);
          width: 1px;
          min-height: 600px;
          margin-left: 20px;
        "
      ></el-col>
      <el-col :span="5" style="margin-left: 20px">
        <div class="right-title" style="margin: 25px">收费信息</div>
        <el-form label-position="left" label-width="70px" style="width: 90%">
          <el-form-item label="车辆">
            <el-input v-model="fee.carNum" placeholder="请输入道闸名称" />
          </el-form-item>
          <el-form-item label="停车时间">
            <el-input v-model="fee.machineName" placeholder="请输入道闸名称" />
          </el-form-item>
          <el-form-item label="应收" prop="type">
            <el-input v-model="fee.machineCode" placeholder="请输入道闸编码" />
          </el-form-item>
          <el-form-item label="实收" prop="type">
            <el-input v-model="fee.machineMac" placeholder="请输入道闸MAC" />
          </el-form-item>
          <el-form-item style="text-align:center">
            <el-button type="primary" @click="onSubmit">临时车收费</el-button>
          </el-form-item>
        </el-form>
        <!-- 出入信息 -->
        <div class="right-title" style="margin: 25px">出入信息</div>
        <div style="margin:15px">
          <span>20:38 青AGK888 进场</span>
        </div>
        <div style="margin:15px"> 
          <span>20:38 青AGK888 进场</span>
        </div>
        <div style="margin:15px">
          <span>20:38 青AGK888 进场</span>
        </div>
        <div style="margin:15px">
          <span>20:38 青AGK888 进场</span>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  getAccessControlsByCondition,
  restartAccessControls,
} from "@/api/barrierGate";
import Pagination from "@/components/Pagination";

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
  components: { Pagination },
  data() {
    return {
      machines: [],
      enterMachineId: "",
      outMachineId: "",
      activeName: "first",
      listQuery: {
        page: 1,
        row: 10,
        machineTypeCd: "9996",
        machineCode: "",
        machineIp: "",
        machineMac: "",
      },
      fee: {
        carNum: "",
        time: "",
      },
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
