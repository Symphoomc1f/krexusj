<template>
  <div class="app-container">
    <el-row>
      <!-- 主区 -->
      <el-col :span="18">
        <el-row :gutter="20">
          <el-col :span="12">
            <div style="margin-bottom: 20px">
              <el-row>
                <el-col :span="16">
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
                <el-col :span="4" style="text-align: center; margin-right: 5px">
                  <el-button @click="openDoorCarIn()">手工进场</el-button>
                </el-col>
                <el-col :span="3" style="text-align: right">
                  <el-button type="primary" @click="openDoor(enterMachineId)"
                    >开闸</el-button
                  >
                </el-col>
              </el-row>
            </div>
            <div style="height: 400px" id="receiver1Div">
              <img id="receiver1" style="width: 100%; height: 100%" />
            </div>
          </el-col>
          <el-col :span="12">
            <div style="margin-bottom: 20px">
              <el-row>
                <el-col :span="16">
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
                <el-col :span="4" style="text-align: center; margin-right: 5px">
                  <el-button @click="openDoorCarOut(enterMachineId)"
                    >手工出场</el-button
                  >
                </el-col>
                <el-col :span="3" style="text-align: right">
                  <el-button type="primary" @click="openDoor(outMachineId)"
                    >开闸</el-button
                  >
                </el-col>
              </el-row>
            </div>
            <div style="height: 400px" id="receiver2Div">
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
                    :src="inImage"
                    style="height: 350px; width: 100%"
                  ></el-image>
                </el-col>
                <el-col :span="12" style="text-align: center">
                  <el-image
                    :src="outImage"
                    style="height: 350px; width: 100%"
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
              v-model="fee.time"
              placeholder="请输入停车时间"
              disabled="disabled"
            />
          </el-form-item>
          <el-form-item label="开门" prop="type">
            <el-input
              v-model="fee.open"
              placeholder="是否开门成功"
              disabled="disabled"
            />
          </el-form-item>
          <el-form-item label="开门说明" prop="type">
            <el-input
              v-model="fee.openMsg"
              type="textarea"
              :row="2"
              placeholder="开门说明"
              disabled="disabled"
            />
          </el-form-item>
          <el-form-item label="应收" prop="type">
            <el-input
              v-model="fee.payCharge"
              placeholder="请输入映射"
              disabled="disabled"
            />
          </el-form-item>
          <el-form-item label="实收" prop="type">
            <el-input v-model="fee.pay" placeholder="请输入实收" />
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
            <el-button
              type="primary"
              @click="onSubmit"
              :disabled="fee.pay <= 0 ? true : false"
              >临时车收费</el-button
            >
          </el-form-item>
        </el-form>
        <!-- 出入信息 -->
        <div class="right-title" style="margin: 25px">出入信息</div>
        <div
          style="margin: 15px"
          v-for="(item, index) in inoutMsg"
          :key="index"
        >
          <span>{{ item }}</span>
        </div>
      </el-col>
    </el-row>
    <el-dialog
      :title="carInOut.type == '1101' ? '手工入场' : '手工出场'"
      :visible.sync="dialogFormVisible"
    >
      <el-form
        ref="dataForm"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left: 50px"
      >
        <el-form-item label="车牌号" prop="type">
          <el-input v-model="carInOut.carNum" placeholder="请输入车牌号" />
        </el-form-item>
        <el-form-item
          label="出场收费"
          prop="type"
          v-if="carInOut.type == '1102'"
        >
          <el-input v-model="carInOut.amount" placeholder="请输入出场收费" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="_customOpenDoor()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getAccessControlsByCondition,
  restartAccessControls,
  openDoor,
  customCarInOut,
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
        payCharge: 0.0,
        pay: 0.0,
        remark: "",
        open: "",
        openMsg: "",
      },
      messageWebSocket: null,
      inImage:
        "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2576718747,3658011797&fm=26&gp=0.jpg",
      outImage:
        "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2576718747,3658011797&fm=26&gp=0.jpg",
      inoutMsg: [],
      dialogFormVisible: false,
      carInOut: {
        type: "1101", //1101 手动入场 1102 手动出场
        carNum: "",
        amount: 0.0,
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
          container: image,
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
      let boxId = "";
      this.outMachines.forEach((item) => {
        if (item.machineId == _outMachineId) {
          wsUrl = item.wsUrl;
          boxId = item.locationObjId;
        }
      });

      this._initCarInOutMessage(boxId);

      let image = document.getElementById("receiver2");
      if (wsUrl.endsWith(".flv")) {
        image = document.getElementById("receiver2Div");
        let jessibuca = new Jessibuca({
          container: image,
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
    },
    openDoorCarIn: function () {
      this.dialogFormVisible = true;
      this.carInOut.type = "1101";
      this.carInOut.machineId = this.enterMachineId;
    },
    openDoorCarOut: function () {
      this.dialogFormVisible = true;
      this.carInOut.type = "1102";
      this.carInOut.machineId = this.outMachineId;
    },
    _customOpenDoor: function () {
      let _that = this;
      customCarInOut(this.carInOut).then((res) => {
        _that.dialogFormVisible = false;
        this.$message({
          type: "info",
          message: "开门成功",
        });
        _that.carInOut = {
          type: "1101", //1101 手动入场 1102 手动出场
          carNum: "",
          amount: 0.0,
        };
      });
    },
    onSubmit: function () {
      customCarInOut({
        type: "1102", //1101 手动入场 1102 手动出场
        carNum: this.fee.carNum,
        amount: this.fee.pay,
        payCharge: this.fee.payCharge,
      }).then((res) => {
        this.$message({
          type: "info",
          message: "开门成功",
        });
        _that.fee = {
          carNum: "",
          time: "",
          payCharge: 0.0,
          pay: 0.0,
          remark: "",
          open: "",
          openMsg: "",
        };
      });
    },
    _initCarInOutMessage: function (_boxId) {
      let _that = this;

      if (this.messageWebSocket) {
        this.messageWebSocket.close();
      }
      var url =
        "ws://" +
        window.location.host +
        "/barrierGateControl/" +
        this.uuid() +
        "/" +
        _boxId;
      let websocket = null;
      if ("WebSocket" in window) {
        websocket = new WebSocket(url);
      } else if ("MozWebSocket" in window) {
        websocket = new MozWebSocket(url);
      } else {
        websocket = new SockJS(url);
      }

      //连接发生错误的回调方法
      websocket.onerror = function () {
        console.log("初始化失败");
        this.$notify.error({
          title: "错误",
          message: "连接失败，请检查网络",
        });
      };

      //连接成功建立的回调方法
      websocket.onopen = function () {
        console.log("ws初始化成功");
      };

      //接收到消息的回调方法
      websocket.onmessage = function (event) {
        console.log("event", event);
        let _data = JSON.parse(event.data);
        if (_data.action == "IN_OUT") {
          if (_that.enterMachineId == _data.machineId) {
            _that.inImage = "data:image/png;base64," + _data.img;
          }
          if (_that.outMachineId == _data.machineId) {
            _that.outImage = "data:image/png;base64," + _data.img;
          }
        } else {
          if (
            _that.enterMachineId != _data.machineId &&
            _that.outMachineId != _data.machineId
          ) {
            return;
          }
          _that.fee.carNum = _data.carNum;
          _that.fee.time = _data.hours + "小时" + _data.min + "分钟";
          _that.fee.payCharge = _data.payCharge;
          _that.fee.pay = _data.payCharge;
          _that.fee.openMsg = _data.remark;
          _that.fee.open = _data.open;

          if (_data.open != "开门成功") {
            return;
          }
          let _msg = _data.inOutTime + " " + _data.carNum;
          if (_that.enterMachineId == _data.machineId) {
            _msg += "进场";
          }
          if (_that.outMachineId == _data.machineId) {
            _msg += "出场";
          }
          if (_that.inoutMsg.length == 5) {
            _that.inoutMsg.pop();
          }
          _that.inoutMsg.unshift(_msg);
        }
      };

      //连接关闭的回调方法
      websocket.onclose = function () {
        console.log("初始化失败");
        this.$notify.error({
          title: "错误",
          message: "连接关闭，请刷新浏览器",
        });
      };

      //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
      window.onbeforeunload = function () {
        websocket.close();
      };
      this.messageWebSocket = websocket;
    },
  },
};
</script>
