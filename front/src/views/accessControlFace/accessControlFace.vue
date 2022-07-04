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
        @click="getAccessControlFace"
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
      <el-table-column align="center" label="人脸">
        <template slot-scope="scope">
          <el-image
            ref="lazyImg"
            lazy
            class="vx-lazyLoad"
            :src="scope.row.facePath"
            :fit="fit"
            :preview-src-list="[scope.row.facePath]"
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
        <template slot-scope="scope">{{ scope.row.name }}</template>
      </el-table-column>
      <el-table-column align="center" label="身份证">
        <template slot-scope="scope">{{ scope.row.idNumber }}</template>
      </el-table-column>
      <el-table-column align="center" label="门禁名称">
        <template slot-scope="scope">{{ scope.row.machineName }}</template>
      </el-table-column>
      <el-table-column align="center" label="状态">
        <template slot-scope="scope">{{ getStateName(scope.row.state) }}</template>
      </el-table-column>
      <el-table-column align="center" label="描述">
        <template slot-scope="scope">{{ scope.row.message }}</template>
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
      <el-table-column class-name="status-col" label="创建时间" align="center">
        <template slot-scope="scope">{{ scope.row.createTime }}</template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="getAccessControlFace"
    />
  </div>
</template>

<script>
import { getAccessControlFace } from "@/api/accessControl";
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
        machineTypeCd: "9999",
        machineName: "",
        machineCode: "",
        machineId:'',
        name: ""
      },
      list: null,
      listLoading: true,
      total: 0
    };
  },
  created() {
    this.listQuery.machineId = this.$route.query.machineId  //接收参数
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getAccessControlFace(this.listQuery).then(response => {
        this.list = response.data;

        this.total = response.total;
        this.listLoading = false;
      });
    },
    getAccessControlFace() {
      this.listLoading = true;
      getAccessControlFace(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    getStateName(_state){
      if(_state == 'W'){
        return '未同步';
      }else if(_state == 'S'){
        return '同步成功';
      }else{
        return '同步失败';
      }
    }
  }
};
</script>
