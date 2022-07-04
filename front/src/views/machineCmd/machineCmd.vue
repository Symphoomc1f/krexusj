<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom:10px">
      <el-input
        v-model="listQuery.machineCode"
        placeholder="请输入设备编码"
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.cmdName"
        placeholder="请输入指令名称"
        style="width: 200px;"
        class="filter-item"
      />
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryMachineCmd"
      >查询指令</el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-edit"
        @click="addForm"
      >添加指令</el-button>
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
      <el-table-column align="center" label="设备类型" prop="scope.row.machineCode" :formatter="typecdFormat">
      </el-table-column>
      <el-table-column align="center" label="设备编码">
        <template slot-scope="scope">{{ scope.row.machineCode }}</template>
      </el-table-column>
      <el-table-column align="center" label="指令编码"> 
        <template slot-scope="scope"> {{ scope.row.cmdCode }}</template>
      </el-table-column>
      <el-table-column align="center" label="指令名称"> 
        <template slot-scope="scope"> {{ scope.row.cmdName }}</template>
      </el-table-column>
     <el-table-column align="center" label="状态" prop="scope.row.state" :formatter="stateFormat"> 
      </el-table-column>
      <el-table-column align="center" label="创建时间"> 
        <template slot-scope="scope"> {{ scope.row.createTime }}</template>
      </el-table-column>
       <el-table-column align="center" label="读取时间"> 
        <template slot-scope="scope"> {{ scope.row.readTime }}</template>
      </el-table-column>
       <el-table-column align="center" label="完成时间"> 
        <template slot-scope="scope"> {{ scope.row.finishTime }}</template>
      </el-table-column>
      
      <el-table-column class-name="status-col" label="操作" width="100" align="center">
        <template slot-scope="{row,$index}">
          <el-row>
            <el-button size="mini"  type="danger" @click="deleteCmdControl(row,$index)">删除</el-button>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="queryMachineCmd"
    />

    <el-dialog title="添加指令" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 300px; margin-left:50px;"
      >
         <el-form-item label="设备类型">
          <el-select v-model="temp.machineTypeCd" @change="selectMachineCode($event)"  class="filter-item" placeholder="请选择设备类型">
            <el-option v-for="item in machineTypeCds" 
              :key="item.value"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </el-form-item>
       
        <el-form-item label="设备">
          <el-select v-model="temp.machineCodeSel"  @change="selectModel($event)" clearable class="filter-item" placeholder="请选择设备">
            <el-option v-for="item in machineCodes" 
              :key="item.value"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </el-form-item>

         <el-form-item label="设备指令">
          <el-select v-model="temp.cmdCodes"  @change="selectCmdsModel($event)" clearable class="filter-item" placeholder="请选择设备指令">
            <el-option v-for="item in cmdCodess" 
              :key="item.value"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="temp.state" clearable class="filter-item" placeholder="请选择设备状态">
            <el-option v-for="item in machineStatus" 
              :key="item.value"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMachineCmdFrom()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteDailogVisible"
      width="30%"
    >
      <span>您确定删除当前指令吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteDailogVisible = false">取 消</el-button>
        <el-button type="primary" @click="doDeleteMachineCmd">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>


import {
  getMachineCmds,
  getMachineCmdsByCondition,
  deleteMachineCmd,
  saveMachineCmd,
  getMachineCodes,
  getMachineCode
} from "@/api/accessControl";
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
        machineTypeCd: "",
        machineId: "",
        machineCode:"",
        machineCodeSel: "",
        cmdCode: "",
        cmdName: "",
        state: ""
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
        machineCode: "",
        cmdName: ""
      },
      machineCodeQuery: {
        machineTypeCd: ""
      },
      list: null,
      listLoading: true,
      deleteDailogVisible: false,
      dialogFormVisible: false,
      curCmdControl: {},
      total: 0,
      machineTypeCds: [
         { value: "9999", label: "门禁" },
         { value: "9998", label: "摄像头" },
         { value: "9997", label: "考勤机" },
         { value: "9996", label: "道闸" }
      ],
      cmdCodess: [
         { value: "1", label: "开机" },
         { value: "2", label: "重启设备" },
         { value: "3", label: "关机" }
      ],
      machineStatus: [
         { value: "1000", label: "未处理" },
         { value: "2000", label: "处理中" },
         { value: "3000", label: "处理完成" },
         { value: "4000", label: "处理失败" }
      ],
      machineCodes: [],
      temp: {
        machineTypeCd: "",
        machineId: "",
        machineCode:"",
        machineCodeSel: "",
        cmdCode: "",
        cmdName: "",
        state: ""
      }
    };
  },

  created() {
    this.fetchData();
  },
  methods: {
    selectCmdsModel(id){
       let obj = {};
      obj = this.cmdCodess.find((item)=>{
          return item.value === id;
      });
      this.temp.cmdCode = id;
      this.temp.cmdName = obj.label;
    },
    selectModel(id){
       let obj = {};
      obj = this.machineCodes.find((item)=>{
          return item.value === id;
      });
      this.temp.machineId = id;
      this.temp.machineCode = obj.label;
    },
    selectMachineCode(val){
      this.temp.machineCodeSel = "";
      if(val != null && val != '' && val != undefined){
      this.temp.machineTypeCd = val;
      this.machineCodeQuery.machineTypeCd = val;
      getMachineCodes(this.machineCodeQuery).then(response => {
          var dynamicTags1 = [];
          response.data.forEach(item =>{
                var mcode = {};
                mcode['value'] = item.machineId;
                mcode['label'] = item.machineCode;
                dynamicTags1.push(mcode);
              })
           this.machineCodes = dynamicTags1;
      });
      }
    },
    stateFormat(row, column) {
      var rename = "";
      this.machineStatus.forEach(item =>{
                if(item.value == row.state){
                   rename = item.label;
                }
              })
    return rename;
  },
    typecdFormat(row, column) {
      var rename = "";
      this.machineTypeCds.forEach(item =>{
                if(item.value == row.machineTypeCd){
                   rename = item.label;
                }
              })
    return rename;
  },
    fetchData() {
      this.listLoading = true;
      getMachineCmds().then(response => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    queryMachineCmd() {
      this.listLoading = true;
      getMachineCmdsByCondition(this.listQuery).then(response => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    addForm() {
      this.dialogFormVisible = true;
    },
    deleteCmdControl(_row) {
      this.deleteDailogVisible = true;
      this.curCmdControl = _row;
    },
    doDeleteMachineCmd() {
      this.listLoading = true;
      deleteMachineCmd(this.curCmdControl).then(response => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg
        });
        this.deleteDailogVisible = false;
        this.queryMachineCmd();
      });
    },

  saveMachineCmdFrom() {
      this.listLoading = true;
      saveMachineCmd(this.temp).then(response => {
        this.listLoading = false;
        this._cancleMapping();
        this.queryMachineCmd();
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
