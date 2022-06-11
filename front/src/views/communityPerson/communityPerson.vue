<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom: 10px">
      <el-input
        v-model="listQuery.CommunityPersonIp"
        placeholder="请输入人员名称"
        style="width: 200px"
        class="filter-item"
      />
      <el-select
        v-model="listQuery.personType"
        class="filter-item"
        placeholder="请选择人员类型"
      >
        <el-option key="1001" label="员工" value="1001" />
        <el-option key="2002" label="业主" value="2002" />
        <el-option key="3003" label="访客" value="3003" />
      </el-select>

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="queryCommunityPerson"
        >查询人员</el-button
      >
      <el-button
        class="filter-item"
        style="margin-left: 10px"
        type="primary"
        icon="el-icon-edit"
        @click="addCommunityPerson"
        >添加人员</el-button
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
      <el-table-column align="center" label="名称" width="60">
        <template slot-scope="scope">{{ scope.row.name }}</template>
      </el-table-column>
      <el-table-column align="center" label="电话">
        <template slot-scope="scope">{{ scope.row.tel }}</template>
      </el-table-column>
      <el-table-column align="center" label="身份证">
        <template slot-scope="scope">{{ scope.row.idNumber }}</template>
      </el-table-column>
      <el-table-column label="人员类型" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.personTypeName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="外部人员ID" align="center">
        <template slot-scope="scope">{{ scope.row.extPersonId }}</template>
      </el-table-column>
      <el-table-column class-name="status-col" label="创建时间" align="center">
        <template slot-scope="scope">{{ scope.row.createTime }}</template>
      </el-table-column>
      <el-table-column
        class-name="status-col"
        label="操作"
        width="300"
        align="center"
      >
        <template slot-scope="{ row, $index }">
          <el-row>
            <el-button
              size="mini"
              type="success"
              @click="openToMachine(row, $index)"
              >同步门禁</el-button
            >
            <el-button
              size="mini"
              type="danger"
              @click="deleteCommunityPerson(row, $index)"
              >删除</el-button
            >
          </el-row>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.row"
      @pagination="queryCommunityPerson"
    />

    <el-dialog title="添加人员" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left: 50px"
      >
        <el-form-item label="名称" prop="type">
          <el-input v-model="temp.name" placeholder="请输入人员名称" />
        </el-form-item>
        <el-form-item label="手机号" prop="type">
          <el-input v-model="temp.tel" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证" prop="type">
          <el-input v-model="temp.idNumber" placeholder="请输入身份证" />
        </el-form-item>
        <el-form-item label="人员类型">
          <el-select
            v-model="temp.personType"
            class="filter-item"
            placeholder="请选择人员类型"
          >
            <el-option key="1001" label="员工" value="1001" />
            <el-option key="2002" label="业主" value="2002" />
            <el-option key="3003" label="访客" value="3003" />
          </el-select>
        </el-form-item>
        <el-form-item label="人脸" prop="type">
          <el-upload
            action=""
            :on-change="getFile"
            :limit="1"
            list-type="picture"
            :auto-upload="false"
            accept=".jpg"
          >
            <el-button size="small" type="primary">选择图片</el-button>
            <div slot="tip" class="el-upload__tip">
              只能上传jpg文件，且不超过500kb
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="外部ID" prop="type">
          <el-input v-model="temp.extPersonId" placeholder="请输入外部人员ID" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCommunityPersonInfo()"
          >提交</el-button
        >
      </div>
    </el-dialog>

    <el-dialog title="同步人脸" :visible.sync="dialogFormToMachineVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left: 50px"
      >
        <el-form-item label="门禁" prop="type">
          <el-select
            v-model="toMachinePerson.machineId"
            class="filter-item"
            placeholder="请选择门禁"
          >
            <el-option
              v-for="item in machines"
              :key="item.machineId"
              :label="item.name"
              :value="item.machineId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormToMachineVisible = false">取消</el-button>
        <el-button type="primary" @click="doToMachine()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteCommunityPersonDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前人员吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteCommunityPersonDailogVisible = false"
          >取 消</el-button
        >
        <el-button type="primary" @click="doDeleteCommunityPerson"
          >确 定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getCommunityPersons,
  deleteCommunityPersons,
  saveCommunityPersons,
  getBase64,
  personToMachine,
} from "@/api/communityPerson";
import Pagination from "@/components/Pagination";
import { parseTime } from "@/utils";

export default {
  components: { Pagination },
  data() {
    return {
      listQuery: {
        page: 1,
        row: 10,
        personType: "",
      },
      list: null,
      listLoading: true,
      deleteCommunityPersonDailogVisible: false,
      dialogFormToMachineVisible: false,
      dialogFormVisible: false,
      curCommunityPerson: {},
      total: 0,
      temp: {
        name: "",
        tel: "",
        idNumber: "",
        personType: "",
        extPersonId: "",
        photo: "",
      },
      machines: [],
      toMachinePerson: {
        personId: "",
        machineId: "",
      },
    };
  },
  created() {
    this.queryCommunityPerson();
  },
  methods: {
    queryCommunityPerson() {
      this.listLoading = true;
      getCommunityPersons(this.listQuery).then((response) => {
        this.list = response.data;
        this.total = response.total;

        this.listLoading = false;
      });
    },
    addCommunityPerson() {
      this.dialogFormVisible = true;
    },
    deleteCommunityPerson(_row) {
      this.deleteCommunityPersonDailogVisible = true;
      this.curCommunityPerson = _row;
    },
    doDeleteCommunityPerson() {
      this.listLoading = true;
      deleteCommunityPersons(this.curCommunityPerson).then((response) => {
        this.listLoading = false;
        this.$message({
          type: "info",
          message: response.msg,
        });
        this.deleteCommunityPersonDailogVisible = false;
        this.queryCommunityPerson();
      });
    },
    saveCommunityPersonInfo() {
      this.listLoading = true;
      saveCommunityPersons(this.temp).then((response) => {
        this.listLoading = false;
        this.dialogFormVisible = false;
        this.queryCommunityPerson();
      });
    },
    viewFace(_row, _index) {
      this.$router.push({
        path: "/CommunityPerson/CommunityPersonFace",
        query: { CommunityPersonId: _row.CommunityPersonId },
      });
    },
    handleCommand(command) {
      command();
    },
    getFile(file, fileList) {
      let _that = this;
      getBase64(file.raw).then((res) => {
        _that.temp.photo = res;
      });
    },
    openToMachine(_row) {
      this.toMachinePerson.personId = _row.personId;
      this.dialogFormToMachineVisible = true;
    },
    doToMachine() {
      this.listLoading = true;
      personToMachine(this.temp).then((response) => {
        this.listLoading = false;
        this.dialogFormToMachineVisible = false;
      });
    },
  },
};
</script>
