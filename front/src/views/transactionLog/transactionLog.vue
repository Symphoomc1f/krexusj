<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.machineName"
        placeholder="请输入门禁名称"
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.tranId"
        placeholder="请输入日志ID"
        style="width: 200px;"
        class="filter-item"
      />

      <el-input
        v-model="listQuery.url"
        placeholder="请输入调用地址"
        style="width: 200px;"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="getTranLog"
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
      <el-table-column align="center" label="日志流水">
        <template slot-scope="scope">{{ scope.row.tranId }}</template>
      </el-table-column>
      <el-table-column align="center" label="门禁名称">
        <template slot-scope="scope">{{ scope.row.machineName }}</template>
      </el-table-column>
      <el-table-column label="门禁编码" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.machineCode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="调用地址" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.url }}</span>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center">
        <template slot-scope="scope">{{ scope.row.reqTime }}</template>
      </el-table-column>
      <el-table-column label="结束时间" align="center">
        <template slot-scope="scope">{{ scope.row.resTime }}</template>
      </el-table-column>
     
      <el-table-column class-name="status-col" label="操作时间" align="center">
        <template slot-scope="scope">{{ scope.row.createTime }}</template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="getTranLog"
    />
  </div>
</template>

<script>
import { getTranLog } from "@/api/accessControl";
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
        machineName: "",
        machineCode: "",
        logId: ""
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
      this.listLoading = true;
      getTranLog(this.listQuery).then(response => {
        this.list = response.data;
        
        this.total = response.total;
        this.listLoading = false;
      });
    },
    getTranLog() {
      this.listLoading = true;
      getTranLog(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    }
  }
};
</script>
