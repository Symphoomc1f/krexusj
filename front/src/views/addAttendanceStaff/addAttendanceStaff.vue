<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-select v-model="listQuery.classesId" class="filter-item" placeholder="请选择班次">
        <el-option
          v-for="item in classes"
          :key="item.classesId"
          :label="item.classesName"
          :value="item.classesId"
        />
      </el-select>
      <el-input
        v-model="listQuery.staffName"
        placeholder="请输入员工名称"
        style="width: 200px;"
        class="filter-item"
      />
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="fetchData"
      >查询</el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="addClassesStaff"
      >添加员工</el-button>
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
      <el-table-column align="center" label="班次名称">
        <template slot-scope="scope">{{ scope.row.classesName }}</template>
      </el-table-column>
      <el-table-column align="center" label="部门名称">
        <template slot-scope="scope">{{ scope.row.departmentName }}</template>
      </el-table-column>
      <el-table-column label="员工名称" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.staffName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="员工编号" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.staffId }}</span>
        </template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" width="300" align="center">
        <template slot-scope="{row,$index}">
          <el-row>
            <el-button size="mini" type="danger" @click="deleteClassesStaff(row,$index)">删除</el-button>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="fetchData"
    />

    <el-dialog title="员工信息" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="班次" prop="type">
          <el-select v-model="temp.classesId" class="filter-item" placeholder="请选择班次">
            <el-option
              v-for="item in classes"
              :key="item.classesId"
              :label="item.classesName"
              :value="item.classesId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="type">
          <el-select v-model="temp.departmentId" class="filter-item" placeholder="请选择部门" @change="changeDepartment">
            <el-option
              v-for="item in departments"
              :key="item.departmentId"
              :label="item.departmentName"
              :value="item.departmentId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="员工" prop="type">
          <el-select v-model="temp.staffId" class="filter-item" placeholder="请选择员工">
            <el-option
              v-for="item in staffs"
              :key="item.staffId"
              :label="item.staffName"
              :value="item.staffId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveClassesStaffInfo()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteClassesStaffDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前员工吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteClassesStaffDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doDeleteClassesStaff">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getClasses, getDepartment,getStaffs ,getClassesStaffs,saveClassesStaffs,deleteClassesStaff} from "@/api/attendance";
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
        classesId: "",
        staffName: ""
      },
      classes: [],
      departments: [],
      staffs: [],
      list: null,
      listLoading: true,
      deleteClassesStaffDailogVisible: false,
      dialogFormVisible: false,
      curClassesStaff: {},
      total: 0,
      temp: {
        classesId: "",
        departmentId: "",
        staffId: ""
      },
      rules: {}
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getClasses({
         page: 1,
        row: 50,
      }).then(response => {
        this.classes = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
      getClassesStaffs(this.listQuery).then(response =>{
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      })
    },
    addClassesStaff() {
      this.dialogFormVisible = true;
      getDepartment().then(response => {
        this.departments = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    changeDepartment(e){
      let _departmentId = e;
      getStaffs({
        departmentId:_departmentId
      }).then(response => {
        this.staffs = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    deleteClassesStaff(_row) {
      this.deleteClassesStaffDailogVisible = true;
      this.curClassesStaff = _row;
    },
    doDeleteClassesStaff() {
      this.listLoading = true;
      deleteClassesStaff(this.curClassesStaff).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.deleteClassesStaffDailogVisible = false;
        this.fetchData();
      });
    },
    saveClassesStaffInfo() {
      this.listLoading = true;
      this.staffs.forEach(item => {
          if(item.staffId == this.temp.staffId){
            this.temp.staffName = item.staffName;
          }
      });
      this.departments.forEach(item => {
          if(item.departmentId == this.temp.departmentId){
            this.temp.departmentName = item.departmentName;
          }
      });
      saveClassesStaffs(this.temp).then(response => {
        this.listLoading = false;
        this.dialogFormVisible = false;
        this.fetchData();
      });
    },
  }
};
</script>
