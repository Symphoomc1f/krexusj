<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.taskId"
        placeholder="请输入任务ID"
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.taskName"
        placeholder="请输入任务名称"
        style="width: 200px;"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryTask"
      >查询任务</el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="addTask"
      >添加任务</el-button>
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
      <el-table-column align="center" label="任务名称">
        <template slot-scope="scope">{{ scope.row.taskName }}</template>
      </el-table-column>
      <el-table-column align="center" label="任务ID">
        <template slot-scope="scope">{{ scope.row.taskId }}</template>
      </el-table-column>
      <el-table-column label="模板名称" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.templateName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="定时时间" align="center">
        <template slot-scope="scope">{{ scope.row.taskCron }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="状态" align="center">
        <template slot-scope="scope">{{ scope.row.state == '002'?'运行':'停止' }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="操作" width="300" align="center">
        <template slot-scope="{row,$index}">
          <el-row>
            <el-button
              size="mini"
              v-if="row.state=='001'"
              type="primary"
              @click="startTask(row,$index)"
            >启动</el-button>
            <el-button
              size="mini"
              v-if="row.state=='002'"
              type="primary"
              @click="stopTask(row,$index)"
            >停止</el-button>
            <el-button size="mini" type="warning" @click="updateTask(row,$index)">修改</el-button>
            <el-button
              size="mini"
              v-if="row.state=='001'"
              type="danger"
              @click="deleteTask(row,$index)"
            >删除</el-button>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="queryTask"
    />

    <el-dialog title="定时任务" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="名称" prop="type">
          <el-input v-model="temp.taskName" placeholder="请输入定时任务名称" />
        </el-form-item>
        <el-form-item label="模板" prop="type">
          <el-select
            v-model="temp.templateId"
            class="filter-item"
            placeholder="请选择模板"
            @change="_changeTemplate"
          >
            <el-option
              v-for="item in taskTemplates"
              :key="item.templateId"
              :label="item.templateName"
              :value="item.templateId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时间" prop="type">
          <el-input v-model="temp.taskCron" placeholder="请输入定时时间" />
        </el-form-item>
        <el-form-item v-for="item in templateSpecs" :key="item.specId" :label="item.specName">
          <el-input v-model="item.value" :placeholder="item.specDesc" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTaskInfo()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteTaskDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前门禁吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteTaskDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doDeleteTask">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getTasks,
  getTasksByCondition,
  deleteTask,
  saveTask,
  getTaskTemplates,
  updateTask,
  startTask,
  stopTask
} from "@/api/task";
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
        taskName: "",
        taskId: ""
      },
      list: null,
      listLoading: true,
      deleteTaskDailogVisible: false,
      dialogFormVisible: false,
      curTask: {},
      taskTemplates: [],
      templateSpecs: [],
      total: 0,
      temp: {
        taskId: "",
        taskName: "",
        taskCron: "",
        templateId: ""
      },
      rules: {}
    };
  },
  created() {
    this.fetchData();
  },
  watch: {
    dialogFormVisible: function(val) {
      if (val == false) {
        this.temp = {
          taskId: "",
          taskName: "",
          taskCron: "",
          templateId: ""
        };
      }
    }
  },
  methods: {
    fetchData() {
      this.listLoading = true;
      getTasks().then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
      getTaskTemplates({}).then(response => {
        this.taskTemplates = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    queryTask() {
      this.listLoading = true;
      getTasksByCondition(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.listLoading = false;
      });
    },
    addTask() {
      this.dialogFormVisible = true;
    },
    updateTask(_row) {
      this.temp.taskId = _row.taskId;
      this.temp.taskName = _row.taskName;
      this.temp.templateId = _row.templateId;
      this.temp.taskCron = _row.taskCron;
      this.taskTemplates.forEach(item => {
        if (item.templateId == _row.templateId) {
          this.templateSpecs = item.taskTemplateSpecDtos;
        }
      });
      this.templateSpecs.forEach(item => {
        _row.taskAttrDtos.forEach(itemNew => {
          if (item.specCd == itemNew.specCd) {
            item.value = itemNew.value;
            item.attrId = itemNew.attrId;
          }
        });
      });
      this.dialogFormVisible = true;
    },
    deleteTask(_row) {
      this.deleteTaskDailogVisible = true;
      this.curTask = _row;
    },
    doDeleteTask() {
      this.listLoading = true;
      deleteTask(this.curTask).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.deleteTaskDailogVisible = false;
        this.queryTask();
      });
    },
    saveTaskInfo() {
      this.listLoading = true;
      this.temp.taskAttrDtos = this.templateSpecs;
      if (this.temp.taskId != "-1" && this.temp.taskId != "") {
        updateTask(this.temp).then(response => {
          this.listLoading = false;
          this.dialogFormVisible = false;
          this.queryTask();
        });
      } else {
        saveTask(this.temp).then(response => {
          this.listLoading = false;
          this.dialogFormVisible = false;
          this.queryTask();
        });
      }
    },
    startTask(_row, _index) {
      this.listLoading = true;
      startTask(_row).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.queryTask();
      });
    },
    stopTask(_row, _index) {
      this.listLoading = true;
      stopTask(_row).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.queryTask();
      });
    },

    _changeTemplate(e) {
      this.taskTemplates.forEach(item => {
        if (item.templateId == e) {
          this.templateSpecs = item.taskTemplateSpecDtos;
        }
      });
    },
    handleCommand(command) {
      command();
    }
  }
};
</script>
