<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.machineCode"
        placeholder="请输入门禁编码"
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.machineIp"
        placeholder="请输入门禁IP"
        style="width: 200px;"
        class="filter-item"
      />

      <el-input
        v-model="listQuery.machineMac"
        placeholder="请输入门禁Mac"
        style="width: 200px;"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryMachine"
      >查询门禁</el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="addAccessControl"
      >添加门禁</el-button>
    </div>
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column align="center" label="编号" width="90">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column align="center" label="门禁名称">
        <template slot-scope="scope">{{ scope.row.machineName }}</template>
      </el-table-column>
      <el-table-column align="center" label="门禁编码">
        <template slot-scope="scope">{{ scope.row.machineCode }}</template>
      </el-table-column>
      <el-table-column label="门禁IP" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.machineIp }}</span>
        </template>
      </el-table-column>
      <el-table-column label="门禁版本号" align="center">
        <template slot-scope="scope">{{ scope.row.machineVersion }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="mac地址" align="center">
        <template slot-scope="scope">{{ scope.row.machineMac }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="门禁厂商" align="center">
        <template slot-scope="scope">{{ scope.row.oem }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" align="right">
        <template slot-scope="{row,$index}">
          <el-button size="mini" type="primary" @click="openDoor(row,$index)">开门</el-button>
          <el-button size="mini" type="primary" @click="restartAccessControl(row,$index)">重启</el-button>
          <el-button size="mini" type="danger" @click="deleteAccessControl(row,$index)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="queryMachine"
    />

    <el-dialog title="门禁" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="编码" prop="type">
          <el-input v-model="temp.machineCode" placeholder="请输入门禁编码" />
        </el-form-item>
        <el-form-item label="Mac" prop="type">
          <el-input v-model="temp.machineMac" placeholder="请输入门禁mac" />
        </el-form-item>
        <el-form-item label="版本" prop="type">
          <el-input v-model="temp.machineVersion" placeholder="请输入门禁版本" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="temp.machineName" placeholder="请输入门禁名称" />
        </el-form-item>
        <el-form-item label="IP" prop="type">
          <el-input v-model="temp.machineIp" placeholder="请输入门禁版本" />
        </el-form-item>
        <el-form-item label="oem">
          <el-input v-model="temp.oem" placeholder="请输入门禁厂家" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAccessControlInfo()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteAccessControlDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前门禁吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteAccessControlDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doDeleteAccessControl">确 定</el-button>
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
  openDoor
} from "@/api/accessControl";
import Pagination from "@/components/Pagination";
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
  components: { Pagination },
  data() {
    return {
      listQuery: {
        page: 1,
        row: 10,
        machineTypeCd: "9998",
        machineCode: "",
        machineIp: "",
        machineMac: ""
      },
      list: null,
      listLoading: true,
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
        oem: ""
      },
      rules: {
        type: [
          { required: true, message: "type is required", trigger: "change" }
        ],
        timestamp: [
          {
            type: "date",
            required: true,
            message: "timestamp is required",
            trigger: "change"
          }
        ],
        title: [
          { required: true, message: "title is required", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getAccessControls().then(response => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    queryMachine() {
      this.listLoading = true;
      getAccessControlsByCondition(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    addAccessControl() {
      this.dialogFormVisible = true;
    },
    deleteAccessControl(_row) {
      this.deleteAccessControlDailogVisible = true;
      this.curAccessControl = _row;
    },
    doDeleteAccessControl() {
      this.listLoading = true;
      deleteAccessControls(this.curAccessControl).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.deleteAccessControlDailogVisible = false;
        this.queryMachine();
      });
    },
    saveAccessControlInfo() {
      this.listLoading = true;
      saveAccessControls(this.temp).then(response => {
        this.listLoading = false;
        this.dialogFormVisible = false;
        this.queryMachine();
      });
    },
    restartAccessControl(_row, _index) {
      this.listLoading = true;
      restartAccessControls(_row).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: "已发送成功指令"
        });
      });
    },
    openDoor(_row, _index) {
      this.listLoading = true;
      openDoor(_row).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: "已发送成功指令"
        });
      });
    }
  }
};
</script>
