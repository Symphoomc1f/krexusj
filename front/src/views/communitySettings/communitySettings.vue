<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom: 10px">
      <el-input
        v-model="listQuery.communityId"
        placeholder="请输入小区编码"
        style="width: 200px"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.name"
        placeholder="请输入小区名称"
        style="width: 200px"
        class="filter-item"
      />

      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryCommunity"
        >查询小区</el-button
      >
      <el-button
        class="filter-item"
        style="margin-left: 10px"
        type="primary"
        icon="el-icon-edit"
        @click="addCommunity"
        >添加小区</el-button
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
      <el-table-column align="center" label="编号" width="90">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
       <el-table-column align="center" label="小区名称">
        <template slot-scope="scope">{{ scope.row.name }}</template>
      </el-table-column>
      <el-table-column align="center" label="城市">
        <template slot-scope="scope">{{ scope.row.provName+scope.row.cityName +scope.row.areaName}}</template>
      </el-table-column>
      <el-table-column align="center" label="外部ID">
        <template slot-scope="scope">{{ scope.row.extCommunityId }}</template>
      </el-table-column>

       <el-table-column align="center" label="小区编码">
        <template slot-scope="scope">{{ scope.row.communityId }}</template>
      </el-table-column>
      <el-table-column label="小区地址" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.address }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" align="right" width="250">
        <template slot-scope="{ row, $index }">
          <el-button
            size="mini"
            type="success"
            v-if="curCommunity.communityId != row.communityId"
            @click="changeCommunity(row, $index)"
            >切换小区</el-button
          >
          <el-button size="mini"  type="primary" @click="editCommunity(row,$index)">修改</el-button>
          <el-button
            size="mini"
            type="danger"
            @click="deleteCommunity(row, $index)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="queryCommunity"
    />
    <el-dialog title="小区" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 60%; margin-left: 50px"
      >
        <el-form-item label="小区名称" prop="type">
          <el-input v-model="temp.name" placeholder="请输入小区名称" />
        </el-form-item>

        <el-form-item label="小区地区" v-show="temp.communityId ==''" prop="type">
          <el-row>
            <el-col :span="8">
              <el-select
                v-model="provCode"
                placeholder="请选择省份"
                @change="changeProv"
              >
                <el-option
                  v-for="item in provs"
                  :key="item.areaCode"
                  :label="item.areaName"
                  :value="item.areaCode"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-select
                v-model="cityCode"
                @change="changeCity"
                collapse-tags
                style="margin-left: 20px"
                placeholder="请选择城市"
              >
                <el-option
                  v-for="item in citys"
                  :key="item.areaCode"
                  :label="item.areaName"
                  :value="item.areaCode"
                >
                </el-option>
              </el-select>
            </el-col>
            <el-col :span="8">
              <el-select
                v-model="temp.cityCode"
                collapse-tags
                style="margin-left: 20px"
                placeholder="请选择区县"
              >
                <el-option
                  v-for="item in areas"
                  :key="item.areaCode"
                  :label="item.areaName"
                  :value="item.areaCode"
                >
                </el-option>
              </el-select>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="小区地址" prop="type">
          <el-input v-model="temp.address" placeholder="请输入小区地址" />
        </el-form-item>
        <el-form-item label="外部编码" prop="type">
          <el-input
            v-model="temp.extCommunityId"
            placeholder="请输入外部小区编码"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCommunityInfo()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteCommunityDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前小区吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteCommunityDailogVisible = false"
          >取 消</el-button
        >
        <el-button type="primary" @click="doDeleteCommunity">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getCommunitys,
  getCommunitysByCondition,
  deleteCommunitys,
  saveCommunitys,
  updateCommunitys,
  getCityArea,
} from "@/api/community";
import Pagination from "@/components/Pagination";
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
  components: { Pagination },
  data() {
    return {
      listQuery: {
        page: 1,
        row: 10,
        communityId: "",
        name: "",
      },
      list: null,
      listLoading: true,
      deleteCommunityDailogVisible: false,
      dialogFormVisible: false,
      curCommunity: {},
      total: 0,
      temp: {
        communityId: "",
        name: "",
        extCommunityId: "",
        cityCode: "",
        address: "",
      },
      rules: {},
      provs: [],
      citys: [],
      areas: [],
      provCode: "",
      cityCode: "",
    };
  },
  watch: {
    dialogFormVisible: function (val) {
      if (val == false) {
        this.temp = {
          communityId: "",
          name: "",
          extCommunityId: "",
          cityCode: "",
          address: "",
        };
      }
    },
  },
  created() {
    this.fetchData();
  },
  methods: {
    handleClose(){
      handleCloses()
    },
    fetchData() {
      this.listLoading = true;
      getCommunitys(this.temp).then((response) => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
      getCityArea({
        areaLevel: "101",
      }).then((res) => {
        this.provs = res.data;
      });

      let _currCommunity = JSON.parse(window.localStorage.getItem("curCommunity"));

      if(_currCommunity != null && _currCommunity != undefined){
        this.curCommunity = _currCommunity;
      }
   
    },
    changeProv() {
      getCityArea({
        areaLevel: "202",
        parentAreaCode: this.provCode,
      }).then((res) => {
        this.citys = res.data;
      });
    },
    changeCity() {
      getCityArea({
        areaLevel: "303",
        parentAreaCode: this.cityCode,
      }).then((res) => {
        this.areas = res.data;
      });
    },
    queryCommunity() {
      this.listLoading = true;
      getCommunitysByCondition(this.listQuery).then((response) => {
        this.list = response.data;
        this.listLoading = false;
      });
    },
    editCommunity(_row, _index) {
      this.dialogFormVisible = true;
      this.temp = _row;
    },
    addCommunity() {
      this.dialogFormVisible = true;
    },
    deleteCommunity(_row) {
      this.deleteCommunityDailogVisible = true;
      this.curCommunity = _row;
    },
    doDeleteCommunity() {
      this.listLoading = true;
      deleteCommunitys(this.curCommunity).then((response) => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg,
        });
        this.deleteCommunityDailogVisible = false;
        this.queryCommunity();
      });
    },
    saveCommunityInfo() {
      this.listLoading = true;
      if (this.temp.communityId != "") {
        this.temp.cityCode = null;
        this.temp.appId = null;
        updateCommunitys(this.temp).then(response => {
          this.listLoading = false;
          this.dialogFormVisible = false;
          this.queryCommunity();
        });
        return;
      }
      saveCommunitys(this.temp).then((response) => {
        this.listLoading = false;
        this.dialogFormVisible = false;
        this.queryCommunity();
      });
    },
    changeCommunity(_row) {
      //存储默认小区信息
          window.localStorage.setItem(
            "curCommunity",
            JSON.stringify(_row)
          );
          location.reload();

    },
  },
};
</script>
