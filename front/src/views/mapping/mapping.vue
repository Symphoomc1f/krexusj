<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
        <el-select v-model="listQuery.domain" placeholder="请选择域">
            <el-option
              label="请选择域"
              value=""
            ></el-option>
            <el-option
              v-for="item in domainOption"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
      <el-input
        v-model="listQuery.name"
        placeholder="请输入名称"
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.key"
        placeholder="请输入键"
        style="width: 200px;"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryMapping"
      >查询配置</el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="addMapping"
      >添加配置</el-button>
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
        <template slot-scope="scope">{{ scope.$index + 1}}</template>
      </el-table-column>
      <el-table-column align="center" label="域">
        <template slot-scope="scope">{{ scope.row.domain }}</template>
      </el-table-column>
      <el-table-column align="center" label="名称">
        <template slot-scope="scope">{{ scope.row.name }}</template>
      </el-table-column>
      <el-table-column align="center" label="键">
        <template slot-scope="scope">{{ scope.row.key }}</template>
      </el-table-column>
      <el-table-column align="center" label="值">
        <template slot-scope="scope">{{ scope.row.value }}</template>
      </el-table-column>
      <el-table-column align="center" label="备注">
        <template slot-scope="scope">{{ scope.row.remark }}</template>
      </el-table-column>
      <el-table-column label="创建时间" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" align="right">
        <template slot-scope="{row,$index}">
          <el-button size="mini" type="primary" @click="editMapping(row,$index)">修改</el-button>
          <el-button
            v-if="row.domain != 'DOMAIN.SYSTEM'"
            key="deleteMapping"
            size="mini"
            type="danger"
            @click="deleteMapping(row,$index)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="配置" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="ID" v-if="temp.id !=''" prop="type">
          <el-input v-model="temp.id" disabled="true" placeholder="请输入ID" />
        </el-form-item>
        <el-form-item label="域" prop="type">
          <el-select v-model="temp.domain" :disabled="temp.id != ''?true:false" placeholder="请选择域">
            <el-option
              v-for="item in domainOption"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="type">
          <el-input v-model="temp.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="键" prop="type">
          <el-input v-model="temp.key" :disabled="temp.id != ''?true:false" placeholder="请输入键" />
        </el-form-item>
        <el-form-item label="值" prop="type">
          <el-input v-model="temp.value" placeholder="请输入值" />
        </el-form-item>
        <el-form-item label="备注" prop="type">
          <el-input type="textarea" :rows="2" placeholder="请输入备注" v-model="temp.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="_cancleMapping()">取消</el-button>
        <el-button type="primary" @click="saveMappingInfo()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteMappingDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前配置吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteMappingDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doDeleteMapping">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getMappings,
  getMappingsByCondition,
  deleteMappings,
  saveMappings,
  updateMappings
} from "@/api/mapping";
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
  components: {},
  data() {
    return {
      listQuery: {
        page: 1,
        row: 10,
        id: "",
        name: "",
        domain: "",
        key: ""
      },
      list: null,
      listLoading: true,
      deleteMappingDailogVisible: false,
      dialogFormVisible: false,
      curMapping: {},
      temp: {
        id: "",
        domain: "",
        name: "",
        key: "",
        value: "",
        remark: ""
      },
      rules: {},
      domainOption: [
        { value: "DOMAIN.SYSTEM", label: "系统配置" },
        { value: "DOMAIN.COMMON", label: "普通配置" }
      ]
    };
  },
  watch: {
    dialogFormVisible: function(val) {
      if (val == false) {
        this.temp = {
          id: "",
          domain: "",
          name: "",
          key: "",
          value: "",
          remark: ""
        };
      }
    }
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getMappings().then(response => {
        this.list = response.data;
        this.listLoading = false;
      });
    },
    queryMapping() {
      this.listLoading = true;
      getMappingsByCondition(this.listQuery).then(response => {
        this.list = response.data;
        this.listLoading = false;
      });
    },
    addMapping() {
      this.dialogFormVisible = true;
    },
    editMapping(_row, _index) {
      this.dialogFormVisible = true;
      this.temp = _row;
    },
    deleteMapping(_row) {
      this.deleteMappingDailogVisible = true;
      this.curMapping = _row;
    },
    doDeleteMapping() {
      this.listLoading = true;
      deleteMappings(this.curMapping).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.deleteMappingDailogVisible = false;
        this.queryMapping();
      });
    },
    saveMappingInfo() {
      this.listLoading = true;
      if (this.temp.id != "") {
        updateMappings(this.temp).then(response => {
          this.listLoading = false;
          this._cancleMapping();
          this.queryMapping();
        });
        return ;
      }
      saveMappings(this.temp).then(response => {
        this.listLoading = false;
        this._cancleMapping();
        this.queryMapping();
      });
    },
    _cancleMapping() {
      this.dialogFormVisible = false;
    }
  }
};
</script>
