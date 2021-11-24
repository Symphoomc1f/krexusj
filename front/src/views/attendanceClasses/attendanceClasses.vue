<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.classesName"
        placeholder="请输入班次名称"
        style="width: 200px;"
        class="filter-item"
      />
      
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryAttClass"
      >查询班次</el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="addAttClass"
      >添加班次</el-button>
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
      <el-table-column align="center" label="班次名称">
        <template slot-scope="scope">{{ scope.row.classesName }}</template>
      </el-table-column>
      <el-table-column align="center" label="打卡范围">
        <template slot-scope="scope">{{ scope.row.timeOffset }}</template>
      </el-table-column>
      <el-table-column align="center" label="打卡次数"> 
        <template slot-scope="scope"> {{ scope.row.clockCount }}</template>
      </el-table-column>
     <el-table-column align="center" label="打卡类型"> 
        <template slot-scope="scope"> {{ scope.row.clockType }}</template>
      </el-table-column>
       <el-table-column align="center" label="打卡规则"> 
        <template slot-scope="scope"> {{ scope.row.clockTypeValue }}</template>
      </el-table-column>
      <el-table-column align="center" label="迟到时间范围"> 
        <template slot-scope="scope"> {{ scope.row.lateOffset }}</template>
      </el-table-column>
      <el-table-column align="center" label="早退时间范围"> 
        <template slot-scope="scope"> {{ scope.row.leaveOffset }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" width="180" align="center">
        <template slot-scope="{row,$index}">
          <el-row>
            <el-button size="mini"  type="primary" @click="editAttClass(row,$index)">修改</el-button>
            <el-button size="mini"  type="danger" @click="deleteAttClassControl(row,$index)">删除</el-button>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="queryAttClass"
    />

    <el-dialog title="添加班次" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :model="temp"
        label-position="left"
        label-width="100px"
        style="width: 400px; margin-left:50px;"
      >
      <el-form-item label="ID" v-if="temp.classesId !=''" prop="type">
          <el-input v-model="temp.classesId" disabled="true" placeholder="请输入ID" />
        </el-form-item>
        <el-form-item label="班次名">
          <el-input v-model="temp.classesName" placeholder="请输入班次名" />
        </el-form-item>
        <el-form-item label="打卡范围">
          <el-input v-model="temp.timeOffset" placeholder="请输入打卡范围" />
        </el-form-item>
        <el-form-item label="打卡次数">
          <el-input v-model="temp.clockCount" placeholder="请输入打卡次数" />
        </el-form-item>
        <el-form-item label="打卡类型">
          <el-input v-model="temp.clockType" placeholder="请输入打卡类型" />
        </el-form-item>
         <el-form-item label="打卡规则">
          <el-input v-model="temp.clockTypeValue" placeholder="请输入打卡规则" />
        </el-form-item>
           <el-form-item label="迟到时间范围">
          <el-input v-model="temp.lateOffset" placeholder="请输入迟到时间范围" />
        </el-form-item>
           <el-form-item label="早退时间范围">
          <el-input v-model="temp.leaveOffset" placeholder="请输入早退时间范围" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAttclassControlInfo()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteAttClassControlDailogVisible"
      width="30%"
    >
      <span>您确定删除当前班次吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteAttClassControlDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doDeleteAttClassControl">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getAttendanceClasses,
  getClasses,
  deleteAttClassControls,
  updateAttClass,
  insertAttClass
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
   watch: {
    dialogFormVisible: function(val) {
      if (val == false) {
        this.temp= {
           classesId: "",
          classesName: "",
          timeOffset: "",
          clockCount: "",
          clockType: "",
          clockTypeValue: "",
          lateOffset: "",
          leaveOffset: ""
      };
      }
    }
  },

  components: { Pagination },
  data() {
    return {
      listQuery: {
        page: 1,
        row: 10,
        classesName: ""
      },
      list: null,
      listLoading: true,
      deleteAttClassControlDailogVisible: false,
      dialogFormVisible: false,
      curAccessControl: {},
      total: 0,
      temp: {
        classesId: "",
        classesName: "",
        timeOffset: "",
        clockCount: "",
        clockType: "",
        clockTypeValue: "",
        lateOffset: "",
        leaveOffset: ""
      }
    };
  },

  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getAttendanceClasses().then(response => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    queryAttClass() {
      this.listLoading = true;
      getClasses(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    addAttClass() {
      this.dialogFormVisible = true;
    },
    editAttClass(_row, _index) {
      this.dialogFormVisible = true;
      this.temp = _row;
    },
    deleteAttClassControl(_row) {
      this.deleteAttClassControlDailogVisible = true;
      this.curAccessControl = _row;
    },
    doDeleteAttClassControl() {
      this.listLoading = true;
      deleteAttClassControls(this.curAccessControl).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.deleteAttClassControlDailogVisible = false;
        this.queryAttClass();
      });
    },

  saveAttclassControlInfo() {
      this.listLoading = true;
      if (this.temp.id != "") {
        updateAttClass(this.temp).then(response => {
          this.listLoading = false;
          this._cancleMapping();
          this.queryAttClass();
        });
        return;
      }
      insertAttClass(this.temp).then(response => {
        this.listLoading = false;
        this._cancleMapping();
        this.queryAttClass();
      });
    },
    _cancleMapping() {
      this.dialogFormVisible = false;
    },
    handleCommand(command) {
      command();
    }
  }
};
</script>
