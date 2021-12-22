<template  slot="header" slot-scope="scope">
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
        <template slot-scope="scope">{{ scope.row.timeOffset }}分钟</template>
      </el-table-column>
      <el-table-column align="center" label="打卡次数">
        <template slot-scope="scope">{{ scope.row.clockCount }}</template>
      </el-table-column>
      <el-table-column align="center" label="打卡类型">
        <template slot-scope="scope">{{ scope.row.clockType }}</template>
      </el-table-column>
      <el-table-column align="center" label="打卡规则">
        <template slot-scope="scope">{{ scope.row.clockTypeValue }}</template>
      </el-table-column>
      <el-table-column align="center" label="迟到时间范围">
        <template slot-scope="scope">{{ scope.row.lateOffset }}分钟</template>
      </el-table-column>
      <el-table-column align="center" label="早退时间范围">
        <template slot-scope="scope">{{ scope.row.leaveOffset }}分钟</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" width="180" align="center">
        <template slot-scope="{row,$index}">
          <el-row>
            <el-button size="mini" type="primary" @click="editAttClass(row,$index)">修改</el-button>
            <el-button size="mini" type="danger" @click="deleteAttClassControl(row,$index)">删除</el-button>
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
          <el-select
            v-model="temp.clockCount"
            @change="selectClockCount($event)"
            class="filter-item"
            placeholder="请选择打卡次数"
          >
            <el-option
              v-for="item in clockCountSel"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上下班打卡1" v-show="startendtime1">
          <el-time-select
            placeholder="上班时间1"
            v-model="temp.startTime1"
            :picker-options="{
                start: '00:00',
                step: '00:05',
                end: '23:55'
              }"
          ></el-time-select>
          <el-time-select
            placeholder="下班时间1"
            v-model="temp.endTime1"
            :picker-options="{
                 start: '00:00',
                step: '00:05',
                end: '23:55',
                minTime: temp.startTime1
              }"
          ></el-time-select>
        </el-form-item>
        <el-form-item label="上下班打卡2" v-show="startendtime2">
          <el-time-select
            placeholder="上班时间2"
            v-model="temp.startTime2"
            :picker-options="{
                start: '00:00',
                step: '00:05',
                end: '23:55'
              }"
          ></el-time-select>
          <el-time-select
            placeholder="下班时间2"
            v-model="temp.endTime2"
            :picker-options="{
                 start: '00:00',
                step: '00:05',
                end: '23:55',
                minTime: temp.startTime2
              }"
          ></el-time-select>
        </el-form-item>
        <el-form-item label="上下班打卡3" v-show="startendtime3">
          <el-time-select
            placeholder="上班时间3"
            v-model="temp.startTime3"
            :picker-options="{
                start: '00:00',
                step: '00:05',
                end: '23:55'
              }"
          ></el-time-select>
          <el-time-select
            placeholder="下班时间3"
            v-model="temp.endTime3"
            :picker-options="{
                 start: '00:00',
                step: '00:05',
                end: '23:55',
                minTime: temp.startTime3
              }"
          ></el-time-select>
        </el-form-item>
        <el-form-item label="打卡类型">
          <el-select
            v-model="temp.clockType"
            @change="selectClockType($event)"
            class="filter-item"
            placeholder="请选择打卡类型"
          >
            <el-option
              v-for="item in clockTypeSel"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择打卡" v-show="clockTypeShow">
          <el-checkbox-group v-model="checkWeekList" @change="selectCheckWeek">
            <el-checkbox label="0">星期一</el-checkbox>
            <el-checkbox label="1">星期二</el-checkbox>
            <el-checkbox label="2">星期三</el-checkbox>
            <el-checkbox label="3">星期四</el-checkbox>
            <el-checkbox label="4">星期五</el-checkbox>
            <el-checkbox label="5">星期六</el-checkbox>
            <el-checkbox label="6">星期日</el-checkbox>
          </el-checkbox-group>
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

    <el-dialog title="温馨提示" :visible.sync="deleteAttClassControlDailogVisible" width="30%">
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
  insertAttClass,
  getAttendanceClassesAttr
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
        this.startendtime1 = false;
        this.startendtime2 = false;
        this.startendtime3 = false;
        this.clockTypeShow = false;
        this.checkWeekList = [];
        this.temp = {
          startTime1: "",
          endTime1: "",
          startTime2: "",
          endTime2: "",
          startTime3: "",
          endTime3: "",
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
      startendtime1: false,
      startendtime2: false,
      startendtime3: false,
      clockTypeShow: false,
      curAccessControl: {},
      checkWeekList: [],
      total: 0,
      clockTypeSel: [
        { value: "1001", label: "每天打卡" },
        { value: "1002", label: "隔天打卡" },
        { value: "1003", label: "自定义" }
      ],
      clockCountSel: [
        { value: "2", label: "2次" },
        { value: "4", label: "4次" },
        { value: "6", label: "6次" }
      ],
      temp: {
        startTime1: "",
        endTime1: "",
        startTime2: "",
        endTime2: "",
        startTime3: "",
        endTime3: "",
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
    selectClockCount(val) {
      if (val == 2) {
        this.startendtime1 = true;
        this.startendtime2 = false;
        this.startendtime3 = false;
      } else if (val == 4) {
        this.startendtime1 = true;
        this.startendtime2 = true;
        this.startendtime3 = false;
      } else if (val == 6) {
        this.startendtime1 = true;
        this.startendtime2 = true;
        this.startendtime3 = true;
      }
    },
    selectClockType(val) {
      if (val == "1003") {
        this.clockTypeShow = true;
        this.temp.checkWeekList = [];
      } else {
        this.clockTypeShow = false;
        if (val == "1001") {
          this.temp.clockTypeValue = "*";
        }
        if (val == "1002") {
          this.temp.clockTypeValue = "?";
        }
      }
    },
    selectCheckWeek(val) {
      this.temp.clockTypeValue = this.checkWeekList.sort().join(",");  
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
      getAttendanceClassesAttr(_row.classesId).then(response => {
        let arr = response.data;
        arr.forEach(function(item, index) {
          if (item.specCd == "10000") {
            _row.startTime1 = item.value;
          }
          if (item.specCd == "20000") {
            _row.endTime1 = item.value;
          }
          if (item.specCd == "21000") {
            _row.startTime2 = item.value;
          }
          if (item.specCd == "11000") {
            _row.endTime2 = item.value;
          }
          if (item.specCd == "12000") {
            _row.startTime3 = item.value;
          }
          if (item.specCd == "22000") {
            _row.endTime3 = item.value;
          }
        });
        this.selectClockCount(arr.length);
        if (_row.clockType == "1003") {
          this.clockTypeShow = true;
          this.checkWeekList = _row.clockTypeValue.split(",");
          console.log("888", this.checkWeekList);
        } else {
          this.clockTypeShow = false;
        }
        console.log("999999", _row);
        this.temp = _row;
      });
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
      if (this.temp.classesId != "") {
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
