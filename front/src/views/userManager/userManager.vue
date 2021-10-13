<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.username"
        placeholder="请输入用户名称"
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.tel"
        placeholder="请输入电话"
        style="width: 200px;"
        class="filter-item"
      />
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryUser"
      >查询用户</el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="addUser"
      >添加用户</el-button>
    </div>
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="Loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column align="center" label="编号" width="60">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column align="center" label="名称">
        <template slot-scope="scope">{{ scope.row.username }}</template>
      </el-table-column>
      <el-table-column align="center" label="邮箱">
        <template slot-scope="scope">{{ scope.row.email }}</template>
      </el-table-column>
      <el-table-column align="center" label="电话"> 
        <template slot-scope="scope"> {{ scope.row.tel }}</template>
      </el-table-column>
     <el-table-column align="center" label="年龄"> 
        <template slot-scope="scope"> {{ scope.row.age }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" width="300" align="center">
        <template slot-scope="{row,$index}">
          <el-row>
            <el-button size="mini"  type="primary" @click="editUser(row,$index)">修改</el-button>
            <el-button size="mini"  type="danger" @click="deleteUserControl(row,$index)">删除</el-button>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="queryUser"
    />

    <el-dialog title="添加用户" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;"
      >
      <el-form-item label="ID" v-if="temp.id !=''" prop="type">
          <el-input v-model="temp.id" disabled="true" placeholder="请输入ID" />
        </el-form-item>
        <el-form-item label="用户Id">
          <el-input v-model="temp.userId" placeholder="请输入用户Id" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="temp.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="temp.email" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="temp.tel" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="temp.age" placeholder="请输入年龄" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUserControlInfo()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteUserControlDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前用户吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteUserControlDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doDeleteUserControl">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getUsers,
  getUsersByCondition,
  deleteUserControls,
  updateUser,
  insertUser
} from "@/api/user";
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
   watch: {
    dialogFormVisible: function(val) {
      if (val == false) {
        this.temp= {
        id: "",
        userId: "",
        username: "",
        email: "",
        tel: "",
        age: ""
      };
      }
    }
  },
  components: { Pagination },
  data() {
    return {
      listQuery: {
        page: 1,
        row: 10,
        levelCd: "02",
        username: "",
        tel: ""
      },
      list: null,
      listLoading: true,
      deleteUserControlDailogVisible: false,
      dialogFormVisible: false,
      curAccessControl: {},
      total: 0,
      temp: {
        id: "",
        userId: "",
        username: "",
        email: "",
        tel: "",
        age: ""
      }
    };
  },

  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getUsers().then(response => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    queryUser() {
      this.listLoading = true;
      getUsersByCondition(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    addUser() {
      this.dialogFormVisible = true;
    },
    editUser(_row, _index) {
      this.dialogFormVisible = true;
      this.temp = _row;
    },
    deleteUserControl(_row) {
      this.deleteUserControlDailogVisible = true;
      this.curAccessControl = _row;
    },
    doDeleteUserControl() {
      this.listLoading = true;
      deleteUserControls(this.curAccessControl).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.deleteUserControlDailogVisible = false;
        this.queryUser();
      });
    },

  saveUserControlInfo() {
      this.listLoading = true;
      if (this.temp.id != "") {
        updateUser(this.temp).then(response => {
          this.listLoading = false;
          this._cancleMapping();
          this.queryUser();
        });
        return;
      }
      insertUser(this.temp).then(response => {
        this.listLoading = false;
        this._cancleMapping();
        this.queryUser();
      });
    },
    _cancleMapping() {
      this.dialogFormVisible = false;
    },
    handleCommand(command) {
      command();
    }
  }
};
</script>
