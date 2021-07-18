<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.hmName"
        placeholder="请输入商家名称"
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.hmId"
        placeholder="请输入商家编码"
        style="width: 200px;"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryProtocol"
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
      <el-table-column align="center" label="厂商编码" >
        <template slot-scope="scope">{{ scope.row.hmId }}</template>
      </el-table-column>
      <el-table-column align="center" label="厂商名称">
        <template slot-scope="scope">{{ scope.row.hmName }}</template>
      </el-table-column>
      <el-table-column align="center" label="协议版本">
        <template slot-scope="scope">{{ scope.row.version }}</template>
      </el-table-column>
      <el-table-column label="开发者" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.author }}</span>
        </template>
      </el-table-column>
      <el-table-column label="联系方式" align="center">
        <template slot-scope="scope">{{ scope.row.link }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="产品" align="center">
        <template slot-scope="scope">{{ scope.row.prodUrl }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="协议许可" align="center">
        <template slot-scope="{row,$index}">
          <el-button size="mini" type="primary" @click="viewLicense(row,$index)">查看</el-button>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" align="right">
        <template slot-scope="{row,$index}">
          <el-button size="mini" type="primary" @click="restartAccessControl(row,$index)">已经启用</el-button>
          <el-button size="mini" type="danger" @click="startProtocol(row,$index)">启用</el-button>
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
    <el-dialog
      title="温馨提示"
      :visible.sync="startProtocolDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定已经阅读许可并启用该协议吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="startProtocolDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doStartProtocol">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getProtocols,
  startManufacturer
} from "@/api/manufacturerProtocol";
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
        hmType: "1001",
        hmName: "",
        hmId: ""
      },
      list: null,
      listLoading: true,
      startProtocolDailogVisible: false,
      dialogFormVisible: false,
      curProtocol: {},
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.queryProtocol();
    },
    queryProtocol() {
      this.listLoading = true;
      getProtocols(this.listQuery).then(response => {
        this.list = response.data;
        this.listLoading = false;
      });
    },
    startProtocol(_row) {
      this.startProtocolDailogVisible = true;
      this.curProtocol = _row;
    },
    doStartProtocol() {
      this.listLoading = true;
      startManufacturer(this.curProtocol).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.startProtocolDailogVisible = false;
        this.queryMachine();
      });
    },
    viewLicense() {
      
    },
    restartAccessControl(_row) {}
  }
};
</script>
