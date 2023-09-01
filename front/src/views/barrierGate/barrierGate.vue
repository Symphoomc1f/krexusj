<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom: 10px">
      <el-input
        v-model="listQuery.machineCode"
        placeholder="请输入道闸编码"
        style="width: 200px"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.machineIp"
        placeholder="请输入道闸IP"
        style="width: 200px"
        class="filter-item"
      />

      <el-input
        v-model="listQuery.machineMac"
        placeholder="请输入道闸Mac"
        style="width: 200px"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryMachine"
        >查询道闸</el-button
      >
      <el-button
        class="filter-item"
        style="margin-left: 10px"
        type="primary"
        icon="el-icon-edit"
        @click="addAccessControl"
        >添加道闸</el-button
      >
    </div>
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column align="center" label="编号" width="60">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column align="center" label="名称">
        <template slot-scope="scope">{{ scope.row.machineName }}</template>
      </el-table-column>
      <el-table-column align="center" label="道闸编码">
        <template slot-scope="scope">{{ scope.row.machineCode }}</template>
      </el-table-column>
      <el-table-column label="道闸IP" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.machineIp }}</span>
        </template>
      </el-table-column>
      <el-table-column label="版本号" align="center">
        <template slot-scope="scope">{{ scope.row.machineVersion }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="方向" align="center">
        <template slot-scope="scope">{{
          scope.row.direction == "3306" ? "进场" : "出场"
        }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="MAC地址" align="center">
        <template slot-scope="scope">{{ scope.row.machineMac }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="厂商" align="center">
        <template slot-scope="scope">{{ scope.row.oem }}</template>
      </el-table-column>
      <el-table-column
        class-name="status-col"
        label="操作"
        width="300"
        align="center"
      >
        <template slot-scope="{ row, $index }">
          <el-row
            ><!--
            <el-button size="mini"  type="primary" @click="openDoor(row,$index)">开门</el-button>
            <el-button size="mini"  type="primary" @click="viewFace(row,$index)">人脸</el-button>-->
            <el-button
              size="mini"
              type="warning"
              @click="restartAccessControl(row, $index)"
              >重启</el-button
            >
            <el-button
              size="mini"
              type="danger"
              @click="deleteAccessControl(row, $index)"
              >删除</el-button
            >
          </el-row>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="queryMachine"
    />

    <el-dialog title="道闸" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left: 50px"
      >
        <el-form-item label="编码" prop="type">
          <el-input v-model="temp.machineCode" placeholder="请输入道闸编码" />
        </el-form-item>
        <el-form-item label="Mac" prop="type">
          <el-input v-model="temp.machineMac" placeholder="请输入道闸MAC" />
        </el-form-item>
        <el-form-item label="版本" prop="type">
          <el-input
            v-model="temp.machineVersion"
            placeholder="请输入道闸版本"
          />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="temp.machineName" placeholder="请输入道闸名称" />
        </el-form-item>
        <el-form-item label="IP" prop="type">
          <el-input v-model="temp.machineIp" placeholder="请输入道闸IP" />
        </el-form-item>
        <el-form-item label="oem">
          <el-input v-model="temp.oem" placeholder="请输入道闸厂家" />
        </el-form-item>
         <el-form-item label="停车场">
          <el-select v-model="temp.locationObjId" placeholder="请选择停车场">
            <el-option
              v-for="item in locationObjIds"
              :key="item.paId"
              :label="item.num"
              :value="item.paId"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="厂家协议">
          <el-select v-model="temp.hmId" placeholder="请选择厂家协议">
            <el-option
              v-for="item in protocols"
              :key="item.hmId"
              :label="item.hmName"
              :value="item.hmId"
            >
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="门禁方向">
          <el-select v-model="temp.direction" placeholder="请选择设备方向">
            <el-option label="进场" value="3306">进场</el-option>
            <el-option label="出场" value="3307">出场</el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAccessControlInfo()"
          >提交</el-button
        >
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteAccessControlDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前道闸吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteAccessControlDailogVisible = false"
          >取 消</el-button
        >
        <el-button type="primary" @click="doDeleteAccessControl"
          >确 定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getAccessControls,
  getAccessControlsByCondition,
  deleteAccessControls,
  saveAccessControls,
  restartAccessControls,
} from "@/api/barrierGate";
import {
  getParkingAreas
} from "@/api/parkingArea";
import Pagination from "@/components/Pagination";
import { getProtocols } from "@/api/manufacturerProtocol";

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
      listQuery: {
        page: 1,
        row: 10,
        machineTypeCd: "9996",
        machineCode: "",
        machineIp: "",
        machineMac: "",
      },
      list: null,
      listLoading: true,
      protocols: null, //设备厂商
      locationObjIds: null, //停车场
      deleteAccessControlDailogVisible: false,
      dialogFormVisible: false,
      curAccessControl: {},
      total: 0,
      temp: {
        machineCode: "",
        machineMac: "",
        machineVersion: "",
        machineName: "",
        machineIp: "",
        locationType: "4000",
        locationObjId: "",
        oem: "",
        hmId: "",
        direction: "",
      },
      rules: {
        
      },
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    queryProtocol() {
      getProtocols({
        hmType: "2002",
      }).then((response) => {
        this.protocols = response.data;
        console.log(this.protocols);
      });
    },
    queryParkingAreas() {
      this.listLoading = true;
      getParkingAreas({
        hmType: "2002",
      }).then((response) => {
        this.locationObjIds = response.data;
        this.listLoading = false;
      });
    },
    
    fetchData() {
      this.listLoading = true;
      getAccessControls().then((response) => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    queryMachine() {
      this.listLoading = true;
      getAccessControlsByCondition(this.listQuery).then((response) => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    addAccessControl() {
      this.dialogFormVisible = true;
      this.queryProtocol();
      this.queryParkingAreas();
    },
    deleteAccessControl(_row) {
      this.deleteAccessControlDailogVisible = true;
      this.curAccessControl = _row;
    },
    doDeleteAccessControl() {
      this.listLoading = true;
      deleteAccessControls(this.curAccessControl).then((response) => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg,
        });
        this.deleteAccessControlDailogVisible = false;
        this.queryMachine();
      });
    },
    saveAccessControlInfo() {
      this.listLoading = true;
      saveAccessControls(this.temp).then((response) => {
        this.listLoading = false;
        this.dialogFormVisible = false;
        this.queryMachine();
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

    viewFace(_row, _index) {
      this.$router.push({
        path: "/accessControl/accessControlFace",
        query: { machineId: _row.machineId },
      });
    },
    handleCommand(command) {
      command();
    },
  },
};
</script>
