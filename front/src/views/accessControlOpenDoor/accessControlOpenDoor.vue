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
        v-model="listQuery.name"
        placeholder="请输入用户名称"
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
        @click="getMachineOpenDoors"
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
      <el-table-column align="center" label="模板人脸">
        <template slot-scope="scope">
          <el-image
            ref="lazyImg"
            lazy
            class="vx-lazyLoad"
            :src="scope.row.modelFace"
            :fit="fit"
            :preview-src-list="[scope.row.modelFace]"
          >
            <div slot="placeholder" class="image-slot">
              <i class="el-icon-loading"></i>加载中
            </div>
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column align="center" label="抓拍">
        <template slot-scope="scope">
          <el-image
            ref="lazyImg"
            lazy
            class="vx-lazyLoad"
            :src="scope.row.face"
            :fit="fit"
            :preview-src-list="[scope.row.face]"
          >
            <div slot="placeholder" class="image-slot">
              <i class="el-icon-loading"></i>加载中
            </div>
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column align="center" label="用户名称">
        <template slot-scope="scope">{{ scope.row.userName }}</template>
      </el-table-column>
      <el-table-column align="center" label="开门方式">
        <template slot-scope="scope">{{ scope.row.openTypeCdName }}</template>
      </el-table-column>
      <el-table-column align="center" label="带帽">
        <template slot-scope="scope">{{ scope.row.hatName }}</template>
      </el-table-column>
      <el-table-column align="center" label="相似度">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.similarity > 0" type="success">{{ scope.row.similarity }}</el-tag>
          <el-tag v-else type="danger">开门失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="欠费情况">
        <template slot-scope="scope">{{ scope.row.amountOwed }}</template>
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
      <el-table-column class-name="status-col" label="开门时间" align="center">
        <template slot-scope="scope">{{ scope.row.createTime }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" align="center">
        <template slot-scope="scope">
          <el-button @click="openMonitor(scope.row)">监控</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="getMachineOpenDoors"
    />
  </div>
</template>

<script>
import { getMachineOpenDoors } from "@/api/accessControl";
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
        machineName: "",
        machineCode: "",
        machineId: "",
        name: ""
      },
      list: null,
      listLoading: true,
      total: 0
    };
  },
  created() {
    this.listQuery.machineId = this.$route.query.machineId; //接收参数
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getMachineOpenDoors(this.listQuery).then(response => {
        this.list = response.data;

        this.total = response.total;
        this.listLoading = false;
      });
    },
    getMachineOpenDoors() {
      this.listLoading = true;
      getMachineOpenDoors(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    openMonitor(_row) {
      this.$router.push({
        path: "/accessControl/accessControlOpenDoorMonitor",
        query: { machineId: _row.machineId }
      });
    }
  }
};
</script>
