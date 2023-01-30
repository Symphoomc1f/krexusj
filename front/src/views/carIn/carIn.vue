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
        @click="getCarInout"
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
      <el-table-column align="center" label="进场时间">
        <template slot-scope="scope">{{ scope.row.openTime }}</template>
      </el-table-column>
      <el-table-column align="center" label="车辆类型">
        <template slot-scope="scope">{{ scope.row.carType == 0 ? '小车':'大车' }}</template>
      </el-table-column>
      <el-table-column label="进场编号" align="center">
        <template slot-scope="scope">{{ scope.row.inoutId }}</template>
      </el-table-column>
      <el-table-column label="进场通道" align="center">
        <template slot-scope="scope">{{ scope.row.gateName }}</template>
      </el-table-column>
       <el-table-column label="备注" align="center">
        <template slot-scope="scope">{{ scope.row.remark }}</template>
      </el-table-column>
      
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="getCarInout"
    />
  </div>
</template>

<script>
import {
  getCarInouts
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
        inoutType: '1001'
      },
      list: null,
      total: 0,
      listLoading: true,
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.getCarInout();
    },
    getCarInout() {
      this.listLoading = true;
      getCarInouts(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    }
  }
}
</script>
