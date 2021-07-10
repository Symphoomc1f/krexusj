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
        @click="addMachine"
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
  deleteAccessControls
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
      getAccessControls().then(response => {
        this.list = response.data;
        this.listLoading = false;
      });
    },
    queryMachine() {
      this.listLoading = true;
      getAccessControlsByCondition(this.listQuery).then(response => {
        this.list = response.data;
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
    restartAccessControl(_row) {}
  }
};
</script>
