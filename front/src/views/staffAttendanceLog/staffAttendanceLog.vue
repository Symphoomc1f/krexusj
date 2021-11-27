<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-select v-model="listQuery.departmentId" class="filter-item" placeholder="请选择部门">
        <el-option label="请选择部门" value />
        <el-option
          v-for="item in departments"
          :key="item.departmentId"
          :label="item.departmentName"
          :value="item.departmentId"
        />
      </el-select>
      <el-input
        v-model="listQuery.staffName"
        placeholder="请输入员工名称"
        style="width: 200px;"
        class="filter-item"
      />
      <el-date-picker
        v-model="listQuery.date"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd"
      ></el-date-picker>

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="fetchData"
      >查询</el-button>
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
      <el-table-column align="center" label="部门名称">
        <template slot-scope="scope">{{ scope.row.departmentName }}</template>
      </el-table-column>
      <el-table-column label="员工名称" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.staffName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="员工编号" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.staffId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="考勤时间" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.clockTime }}</span>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="getStaffAttendanceLog"
    />
  </div>
</template>

<script>
import {
  getDepartment,
  getClasses,
  getStaffAttendanceLog
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
        classesId: "",
        staffName: "",
        date: [],
        departmentId: ""
      },
      classes: [],
      departments: [],
      list: null,
      listLoading: true,
      deleteClassesStaffDailogVisible: false,
      curTask: {},
      total: 0,
      temp: {
        classesId: "",
        departmentId: "",
        staffId: ""
      },
      rules: {}
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      if (this.listQuery.date.length > 0) {
        this.listQuery.startDate = this.listQuery.date[0];
        this.listQuery.endDate = this.listQuery.date[1];
      }
      getStaffAttendanceLog(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
      getDepartment().then(response => {
        this.departments = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    getStaffAttendanceLog() {
      this.listLoading = true;
      if (this.listQuery.date.length > 0) {
        this.listQuery.startDate = this.listQuery.date[0];
        this.listQuery.endDate = this.listQuery.date[1];
      }
      getStaffAttendanceLog(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    }
  }
};
</script>
