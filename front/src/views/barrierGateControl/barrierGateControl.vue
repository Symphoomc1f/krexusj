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
                  <el-select
                    v-model="enterMachineId"
                    placeholder="请选择"
                    @change="_swatchVedio()"
                  >
                    <el-option
                      v-for="item in inMachines"
                      :key="item.machineId"
                      :label="item.machineName"
                      :value="item.machineId"
                    >
                    </el-option>
                  </el-select>
                </el-col>
                <el-col :span="6" style="text-align: right">
                  <el-button type="primary" @click="openDoor(enterMachineId)"
                    >开闸</el-button
                  >
                </el-col>
              </el-row>
            </div>
            <div style="height: 500px" id="receiver1Div">
              <img id="receiver1" style="width: 100%; height: 100%" />
            </div>
          </el-col>
          <el-col :span="12">
            <div style="margin-bottom: 20px">
              <el-row>
                <el-col :span="18">
                  出场视频:
                  <el-select
                    v-model="outMachineId"
                    placeholder="请选择"
                    @change="_swatchOutVedio()"
                  >
                    <el-option
                      v-for="item in outMachines"
                      :key="item.machineId"
                      :label="item.machineName"
                      :value="item.machineId"
                    >
                    </el-option>
                  </el-select>
                </el-col>
                <el-col :span="6" style="text-align: right">
                  <el-button type="primary" @click="openDoor(outMachineId)"
                    >开闸</el-button
                  >
                </el-col>
              </el-row>
            </div>
            <div style="height: 500px" id="receiver2Div">
              <img id="receiver2" style="width: 100%; height: 100%" />
            </div>
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
            <el-tab-pane label="在场记录" name="second">
              <el-table
                :data="carInDatas"
                element-loading-text="Loading"
                border
                fit
                highlight-current-row
              >
                <el-table-column align="center" label="车牌号">
                  <template slot-scope="scope">{{ scope.row.carNum }}</template>
                </el-table-column>
                <el-table-column align="center" label="进场时间">
                  <template slot-scope="scope">{{
                    scope.row.openTime
                  }}</template>
                </el-table-column>
                <el-table-column align="center" label="车辆类型">
                  <template slot-scope="scope">{{
                    scope.row.carType == 0 ? "小车" : "大车"
                  }}</template>
                </el-table-column>
                <el-table-column label="停车场" align="center">
                  <template slot-scope="scope">{{
                    scope.row.areaNum
                  }}</template>
                </el-table-column>
                <el-table-column label="停车时间" align="center">
                  <template slot-scope="scope">{{
                    changeHourMinutestr(scope.row.min)
                  }}</template>
                </el-table-column>
                <el-table-column label="停车费用" align="center">
                  <template slot-scope="scope">{{
                    scope.row.payCharge
                  }}</template>
                </el-table-column>
              </el-table>
              <pagination
                v-show="total > 0"
                :total="total"
                :page.sync="listQuery.page"
                :limit.sync="listQuery.row"
                @pagination="getCarIns"
              />
            </el-tab-pane>
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
            <el-input
              v-model="fee.carNum"
              placeholder="请输入车牌号"
              disabled="disabled"
            />
          </el-form-item>
          <el-form-item label="停车时间">
            <el-input
              v-model="fee.machineName"
              placeholder="请输入停车时间"
              disabled="disabled"
            />
          </el-form-item>
          <el-form-item label="应收" prop="type">
            <el-input
              v-model="fee.machineCode"
              placeholder="请输入映射"
              disabled="disabled"
            />
          </el-form-item>
          <el-form-item label="实收" prop="type">
            <el-input v-model="fee.machineMac" placeholder="请输入实收" />
          </el-form-item>
          <el-form-item label="备注" prop="type">
            <el-input
              v-model="fee.remark"
              type="textarea"
              :row="2"
              placeholder="请输入备注"
            />
          </el-form-item>
          <el-form-item style="text-align: center">
            <el-button type="primary" @click="onSubmit">临时车收费</el-button>
          </el-form-item>
        </el-form>
        <!-- 出入信息 -->
        <div class="right-title" style="margin: 25px">出入信息</div>
        <div style="margin: 15px">
          <span>20:38 青AGK888 进场</span>
        </div>
        <div style="margin: 15px">
          <span>20:38 青AGK888 进场</span>
        </div>
        <div style="margin: 15px">
          <span>20:38 青AGK888 进场</span>
        </div>
        <div style="margin: 15px">
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
  openDoor,
} from "@/api/barrierGate";
import { getCarInouts } from "@/api/car.js";
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
      inMachines: [],
      outMachines: [],
      carInDatas: [],
      enterMachineId: "",
      outMachineId: "",
      activeName: "first",
      total: 0,
      listQuery: {
        page: 1,
        row: 10,
        inoutType: "1001",
        state: 1,
      },
      fee: {
        carNum: "",
        time: "",
      },
    };
  },
  created() {
    this.queryMachine(3306);
    this.queryMachine(3307);
  },
  methods: {
    queryMachine(_direction) {
      this.listLoading = true;
      let _data = {
        page: 1,
        row: 30,
        machineTypeCd: "9996",
        direction: _direction,
      };
      getAccessControlsByCondition(_data).then((response) => {
        if (_direction == "3306") {
          this.inMachines = response.data;
        } else {
          this.outMachines = response.data;
        }

        this.listLoading = false;
      });
    },
    _swatchVedio: function () {
      //创建一个socket实例
      let wsUrl = "";
      let _enterMachineId = this.enterMachineId;
      this.inMachines.forEach((item) => {
        if (item.machineId == _enterMachineId) {
          wsUrl = item.wsUrl;
        }
      });

      let image = document.getElementById("receiver1");
      if (wsUrl.endsWith(".flv")) {
        image = document.getElementById("receiver1Div");
        let jessibuca = new Jessibuca({
          container:image,
          videoBuffer: 0.2,
          isResize: false,
        });
        jessibuca.onLoad = function () {
          this.play(wsUrl);
        };
        return;
      }
      let receiver_socket = new WebSocket(wsUrl);
      // 监听消息
      receiver_socket.onmessage = function (data) {
        //image.src = 'data:image/png;' + data.data;

        let reader = new FileReader();
        reader.onload = function (evt) {
          if (evt.target.readyState == FileReader.DONE) {
            let url = evt.target.result;
            image.src = "data:image/png;" + url;
          }
        };
        reader.readAsDataURL(data.data);
      };
    },
    _swatchOutVedio: function () {
      //创建一个socket实例
      let wsUrl = "";
      let _outMachineId = this.outMachineId;
      this.outMachines.forEach((item) => {
        if (item.machineId == _outMachineId) {
          wsUrl = item.wsUrl;
        }
      });

      let image = document.getElementById("receiver2");
      if (wsUrl.endsWith(".flv")) {
        image = document.getElementById("receiver2Div");
        let jessibuca = new Jessibuca({
          container:image,
          videoBuffer: 0.2,
          isResize: false,
        });
        jessibuca.onLoad = function () {
          this.play(wsUrl);
        };
        return;
      }
      let receiver_socket = new WebSocket(wsUrl);
      // 监听消息
      receiver_socket.onmessage = function (data) {
        //image.src = 'data:image/png;' + data.data;
        let reader = new FileReader();
        reader.onload = function (evt) {
          if (evt.target.readyState == FileReader.DONE) {
            let url = evt.target.result;
            image.src = "data:image/png;" + url;
          }
        };
        reader.readAsDataURL(data.data);
      };
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
    getCarIns: function () {
      getCarInouts(this.listQuery).then((response) => {
        this.carInDatas = response.data;
      });
    },
    handleClick: function () {
      if (this.activeName == "second") {
        this.getCarIns();
      }
    },
    changeHourMinutestr: function (str) {
      if (str !== "0" && str !== "" && str !== null) {
        return (
          (Math.floor(str / 60).toString().length < 2
            ? "0" + Math.floor(str / 60).toString()
            : Math.floor(str / 60).toString()) +
          ":" +
          ((str % 60).toString().length < 2
            ? "0" + (str % 60).toString()
            : (str % 60).toString())
        );
      } else {
        return "";
      }
    },

    openDoor: function (_machineId) {
      if (_machineId == null || _machineId == "" || _machineId == undefined) {
        this.$message({
          type: "info",
          message: "请先选择视频",
        });
        return;
      }

      openDoor({
        machineId: _machineId,
      }).then((response) => {
        this.$message({
          type: "info",
          message: "已发送成功指令",
        });
      });
    },
  },
};
</script>
