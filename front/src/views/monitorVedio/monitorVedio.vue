<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.machineCode"
        placeholder="请输入监控设备编码"
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.machineIp"
        placeholder="请输入监控设备IP"
        style="width: 200px;"
        class="filter-item"
      />

      <el-input
        v-model="listQuery.machineMac"
        placeholder="请输入监控设备Mac"
        style="width: 200px;"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryMachine"
      >查询监控设备</el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="addMonitor"
      >添加监控设备</el-button>
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
      <el-table-column align="center" label="监控设备名称">
        <template slot-scope="scope">{{ scope.row.machineName }}</template>
      </el-table-column>
      <el-table-column align="center" label="监控设备编码">
        <template slot-scope="scope">{{ scope.row.machineCode }}</template>
      </el-table-column>
      <el-table-column label="监控设备IP" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.machineIp }}</span>
        </template>
      </el-table-column>
      <el-table-column label="监控设备版本号" align="center">
        <template slot-scope="scope">{{ scope.row.machineVersion }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="mac地址" align="center">
        <template slot-scope="scope">{{ scope.row.machineMac }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="监控设备厂商" align="center">
        <template slot-scope="scope">{{ scope.row.oem }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" align="right">
        <template slot-scope="{row,$index}">
          <el-button size="mini" type="primary" @click="restartMonitor(row,$index)">重启</el-button>
          <el-button size="mini" type="danger" @click="deleteMonitor(row,$index)">删除</el-button>
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

    <el-dialog title="监控设备" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="设备编码">
          <el-input v-model="temp.machineCode" />
        </el-form-item>
        <el-form-item label="设备版本">
          <el-input v-model="temp.machineVersion" />
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="temp.machineName" />
        </el-form-item>
        <el-form-item label="设备IP">
          <el-input v-model="temp.machineIp" />
        </el-form-item>
        <el-form-item label="设备mac">
          <el-input v-model="temp.machineMac" />
        </el-form-item>
        <el-form-item label="生产厂商">
          <el-input v-model="temp.oem" />
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">Cancel</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">Confirm</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteMonitorDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前监控设备吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteMonitorDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doDeleteMonitor">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getMonitors,
  getMonitorsByCondition,
  deleteMonitors,
  saveMonitor
} from "@/api/monitorVedio";
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
        machineTypeCd: "9996",
        machineCode: "",
        machineIp: "",
        machineMac: ""
      },
      list: null,
      listLoading: true,
      deleteMonitorDailogVisible: false,
      dialogFormVisible: false,
      curMonitor: {},
      dialogStatus:'create',
      machineTypeCds:[1,2,3],
      temp: {
        id: undefined,
        importance: 1,
        remark: "",
        timestamp: new Date(),
        title: "",
        type: "",
        status: "published"
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
      getMonitors().then(response => {
        this.list = response.data;
        this.listLoading = false;
      });
    },
    queryMachine() {
      this.listLoading = true;
      getMonitorsByCondition(this.listQuery).then(response => {
        this.list = response.data;
        this.listLoading = false;
      });
    },
    addMonitor() {
      this.dialogFormVisible = true;
    },
    deleteMonitor(_row) {
      this.deleteMonitorDailogVisible = true;
      this.curMonitor = _row;
    },
    doDeleteMonitor() {
      this.listLoading = true;
      deleteMonitors(this.curMonitor).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.deleteMonitorDailogVisible = false;
        this.queryMachine();
      });
    },
    restartMonitor(_row) {},
    createData(){
      this.temp.machineTypeCd = '9996';
      saveMonitor(this.temp).then(response => {
        this.listLoading = false;
        this.dialogFormVisible = false;
        this.queryMachine();
      });
    },
    updateData(){}

  }
};
</script>
