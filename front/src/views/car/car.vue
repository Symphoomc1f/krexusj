<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.carNum"
        placeholder="请输入车牌号"
        style="width: 200px;"
        class="filter-item"
      />
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryCar"
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
      <el-table-column align="center" label="编号" width="90">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column align="center" label="车牌号" >
        <template slot-scope="scope">{{ scope.row.carNum }}</template>
      </el-table-column>
      <el-table-column align="center" label="开始时间">
        <template slot-scope="scope">{{ formatTime(scope.row.startTime) }}</template>
      </el-table-column>
      <el-table-column align="center" label="结束时间">
        <template slot-scope="scope">{{ formatTime(scope.row.endTime) }}</template>
      </el-table-column>
      <el-table-column label="ID" align="center">
        <template slot-scope="scope">{{ scope.row.carId }}</template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="queryCar"
    />
  </div>
</template>

<script>
import {
  queryCars
} from "@/api/car.js";
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
        carNum: "",
      },
      list: null,
      listLoading: true,
      total:0
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.queryCar();
    },
    queryCar() {
      this.listLoading = true;
      queryCars(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    add0:function(m) {
            return m < 10 ? '0' + m : m
    },
    formatTime:function(shijianchuo){
      let time = new Date(parseInt(shijianchuo));
              let y = time.getFullYear();
              let m = time.getMonth() + 1;
              let d = time.getDate();
              let h = time.getHours();
              let mm = time.getMinutes();
              let s = time.getSeconds();
              return y + '-' + this.add0(m) + '-' + this.add0(d) + ' ' + this.add0(h) + ':' + this.add0(mm) + ':' + this.add0(s);
    }
  }
}
</script>
