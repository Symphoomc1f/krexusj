<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.machineCode"
        placeholder="请输入考勤机编码"
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.machineIp"
        placeholder="请输入考勤机IP"
        style="width: 200px;"
        class="filter-item"
      />

      <el-input
        v-model="listQuery.machineMac"
        placeholder="请输入考勤机Mac"
        style="width: 200px;"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryMachine"
      >查询考勤机</el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="addAttendance"
      >添加考勤机</el-button>
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
      <el-table-column align="center" label="考勤机名称">
        <template slot-scope="scope">{{ scope.row.machineName }}</template>
      </el-table-column>
      <el-table-column align="center" label="考勤机编码">
        <template slot-scope="scope">{{ scope.row.machineCode }}</template>
      </el-table-column>
      <el-table-column label="考勤机IP" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.machineIp }}</span>
        </template>
      </el-table-column>
      <el-table-column label="考勤机版本号" align="center">
        <template slot-scope="scope">{{ scope.row.machineVersion }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="mac地址" align="center">
        <template slot-scope="scope">{{ scope.row.machineMac }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="考勤机厂商" align="center">
        <template slot-scope="scope">{{ scope.row.oem }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" align="right">
        <template slot-scope="{row,$index}">
          <el-button size="mini" type="primary" @click="restartAttendance(row,$index)">重启</el-button>
          <el-button size="mini" type="danger" @click="deleteAttendance(row,$index)">删除</el-button>
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

    <el-dialog title="考勤机" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="Type" prop="type">
          <el-select v-model="temp.type" class="filter-item" placeholder="Please select">
            <el-option
              v-for="item in calendarTypeOptions"
              :key="item.key"
              :label="item.display_name"
              :value="item.key"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="Date" prop="timestamp">
          <el-date-picker
            v-model="temp.timestamp"
            type="datetime"
            placeholder="Please pick a date"
          />
        </el-form-item>
        <el-form-item label="Title" prop="title">
          <el-input v-model="temp.title" />
        </el-form-item>
        <el-form-item label="Status">
          <el-select v-model="temp.status" class="filter-item" placeholder="Please select">
            <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="Imp">
          <el-rate
            v-model="temp.importance"
            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            :max="3"
            style="margin-top:8px;"
          />
        </el-form-item>
        <el-form-item label="Remark">
          <el-input
            v-model="temp.remark"
            :autosize="{ minRows: 2, maxRows: 4}"
            type="textarea"
            placeholder="Please input"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">Cancel</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">Confirm</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteAttendanceDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前考勤机吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteAttendanceDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doDeleteAttendance">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getAttendances,
  getAttendancesByCondition,
  deleteAttendances,
  restartAttendances
} from "@/api/attendance";
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
        machineTypeCd: "9997",
        machineCode: "",
        machineIp: "",
        machineMac: ""
      },
      list: null,
      total: 0,
      listLoading: true,
      deleteAttendanceDailogVisible: false,
      dialogFormVisible: false,
      curAttendance: {},
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
      getAttendances().then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    queryMachine() {
      this.listLoading = true;
      getAttendancesByCondition(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    addAttendance() {
      this.dialogFormVisible = true;
    },
    deleteAttendance(_row) {
      this.deleteAttendanceDailogVisible = true;
      this.curAttendance = _row;
    },
    doDeleteAttendance() {
      this.listLoading = true;
      deleteAttendances(this.curAttendance).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.deleteAttendanceDailogVisible = false;
        this.queryMachine();
      });
    },
    restartAttendance(_row) {
      restartAttendances(_row).then(response => {
        this.$message({
          type: "info",
          message: '重启成功'
        });
      });
    }
  }
};
</script>
