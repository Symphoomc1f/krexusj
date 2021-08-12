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
        v-model="listQuery.logId"
        placeholder="请输入日志ID"
        style="width: 200px;"
        class="filter-item"
      />

      <el-input
        v-model="listQuery.machineCode"
        placeholder="请输入门禁编码"
        style="width: 200px;"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="getAccessControlsLog"
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
        <template slot-scope="scope">{{ scope.row.logId }}</template>
      </el-table-column>
      <el-table-column align="center" label="门禁名称">
        <template slot-scope="scope">{{ scope.row.machineName }}</template>
      </el-table-column>
      <el-table-column label="门禁编码" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.machineCode }}</span>
        </template>
      </el-table-column>
      <el-table-column label="门禁IP" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.machineIp }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作类型" align="center">
        <template slot-scope="scope">{{ scope.row.operateType }}</template>
      </el-table-column>
      <el-table-column label="操作说明" align="center">
        <template slot-scope="scope">{{ scope.row.operateTypeName }}</template>
      </el-table-column>
      <el-table-column label="用户名称" align="center">
        <template slot-scope="scope">{{ scope.row.userName }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="状态" align="center">
        <template slot-scope="scope">{{ scope.row.stateName }}</template>
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
      @pagination="getAccessControlsLog"
    />
  </div>
</template>

<script>
import { getAccessControlsLog } from "@/api/accessControl";
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
      getAccessControlsLog(this.listQuery).then(response => {
        this.list = response.data;
        
        this.total = response.total;
        this.listLoading = false;
      });
    },
    getAccessControlsLog() {
      this.listLoading = true;
      getAccessControlsLog(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    }
  }
};
</script>
