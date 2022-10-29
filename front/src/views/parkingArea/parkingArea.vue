<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom: 10px">
      <el-input
        v-model="listQuery.communityId"
        placeholder="请输入停车场编码"
        style="width: 200px"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.name"
        placeholder="请输入停车场名称"
        style="width: 200px"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryParkingArea"
        >查询停车场</el-button
      >
      <el-button
        class="filter-item"
        style="margin-left: 10px"
        type="primary"
        icon="el-icon-edit"
        @click="addParkingArea"
        >添加停车场</el-button
      >
    </div>
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column align="center" label="编号">
        <template slot-scope="scope">{{ scope.row.num }}</template>
      </el-table-column>
      <el-table-column align="center" label="停车场ID">
        <template slot-scope="scope">{{ scope.row.paId }}</template>
      </el-table-column>
      <el-table-column align="center" label="第三方ID">
        <template slot-scope="scope">{{ scope.row.extPaId }}</template>
      </el-table-column>
      <el-table-column label="创建时间" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" align="right">
        <template slot-scope="{ row, $index }">
          <el-button
            size="mini"
            type="danger"
            @click="deleteParkingArea(row, $index)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="停车场" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 60%; margin-left: 50px"
      >
        <el-form-item label="编号" prop="type">
          <el-input v-model="temp.num" placeholder="请输入停车场编号" />
        </el-form-item>
        <el-form-item label="第三方ID" prop="type">
          <el-input v-model="temp.extPaId" placeholder="请输入第三方停车场ID" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveParkingAreaInfo()"
          >提交</el-button
        >
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteParkingAreaDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前停车场吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteParkingAreaDailogVisible = false"
          >取 消</el-button
        >
        <el-button type="primary" @click="doDeleteParkingArea">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getParkingAreas,
  deleteParkingArea,
  saveParkingArea,
} from "@/api/parkingArea";
import { parseTime } from "@/utils";

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: "success",
        draft: "gray",
        deleted: "danger",
      };
      return statusMap[status];
    },
  },
  components: {},
  data() {
    return {
      listQuery: {
        page: 1,
        row: 10,
        communityId: "",
        num: "",
      },
      list: null,
      listLoading: true,
      deleteParkingAreaDailogVisible: false,
      dialogFormVisible: false,
      curParkingArea: {},
      temp: {
        num: "",
        extPaId: "",
      },
    };
  },
  watch: {
    dialogFormVisible: function (val) {
      if (val == false) {
        this.temp = {
          num: "",
          extPaId: "",
        };
      }
    },
  },
  created() {
    this.queryParkingArea();
  },
  methods: {
    queryParkingArea() {
      this.listLoading = true;
      getParkingAreas(this.listQuery).then((response) => {
        this.list = response.data;
        this.listLoading = false;
      });
    },
    addParkingArea() {
      this.dialogFormVisible = true;
    },
    deleteParkingArea(_row) {
      this.deleteParkingAreaDailogVisible = true;
      this.curParkingArea = _row;
    },
    doDeleteParkingArea() {
      this.listLoading = true;
      deleteParkingArea(this.curParkingArea).then((response) => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg,
        });
        this.deleteParkingAreaDailogVisible = false;
        this.queryParkingArea();
      });
    },
    saveParkingAreaInfo() {
      this.listLoading = true;
      let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));
      this.temp.communityId = _currCommunity.communityId;
      saveParkingArea(this.temp).then((response) => {
        this.listLoading = false;
        this.dialogFormVisible = false;
        this.queryParkingArea();
      });
    }
  },
};
</script>
