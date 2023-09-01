<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom: 10px">
      <el-input
        v-model="listQuery.boxId"
        placeholder="请输入岗亭编号"
        style="width: 200px"
        class="filter-item"
      />
      <el-input
        v-model="listQuery.boxName"
        placeholder="请输入岗亭名称"
        style="width: 200px"
        class="filter-item"
      />

      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="fetchData"
        >查询岗亭</el-button
      >
      <el-button
        class="filter-item"
        style="margin-left: 10px"
        type="primary"
        icon="el-icon-edit"
        @click="addParkingBox"
        >添加</el-button
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
      <el-table-column align="center" label="编号" width="60">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column align="center" label="岗亭名称">
        <template slot-scope="scope">{{ scope.row.boxName }}</template>
      </el-table-column>
      <el-table-column align="center" label="停车场">
        <template slot-scope="scope">{{ scope.row.paNum }}</template>
      </el-table-column>
      <el-table-column label="临时车进场" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.tempCarIn == "Y" ? "是" : "否" }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否收费" align="center">
        <template slot-scope="scope">{{ scope.row.fee == "Y" ? "是" : "否" }}</template>
      </el-table-column>
      <el-table-column
        class-name="status-col"
        label="蓝牌车进场"
        align="center"
      >
        <template slot-scope="scope">{{
          scope.row.blueCarIn == "Y" ? "是" : "否"
        }}</template>
      </el-table-column>
      <el-table-column
        class-name="status-col"
        label="黄牌车进场"
        align="center"
      >
        <template slot-scope="scope">{{ scope.row.yelowCarIn == "Y" ? "是" : "否" }}</template>
      </el-table-column>
      <el-table-column
        class-name="status-col"
        label="外部编号"
        align="center"
      >
        <template slot-scope="scope">{{ scope.row.extBoxId }}</template>
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
              type="warning"
              @click="_shareParkingBox(row, $index)"
              >共享岗亭</el-button
            >
            <el-button
              size="mini"
              type="warning"
              @click="editParkingBox(row, $index)"
              >修改</el-button
            >
            <el-button
              size="mini"
              type="danger"
              @click="deleteParkingBox(row, $index)"
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
      @pagination="fetchData"
    />


    <el-dialog title="岗亭" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left: 50px"
      >
        <el-form-item label="岗亭名称" prop="type">
          <el-input v-model="temp.boxName" placeholder="请输入岗亭名称码" />
        </el-form-item>
        <el-form-item label="停车场" prop="type">
          <el-select v-model="temp.paId" placeholder="请选择停车场">
            <el-option
              v-for="item in parkingAreas"
              :key="item.paId"
              :label="item.num"
              :value="item.paId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="临时车进场" prop="type">
          <el-select v-model="temp.tempCarIn" placeholder="请选择临时车进场">
            <el-option label="是" value="Y"></el-option>
            <el-option label="否" value="N"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="收费" prop="type">
          <el-select v-model="temp.fee" placeholder="请选择临时车收费">
            <el-option label="是" value="Y"></el-option>
            <el-option label="否" value="N"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="蓝牌车进场">
          <el-select v-model="temp.blueCarIn" placeholder="请选择蓝牌车进场">
            <el-option label="是" value="Y"></el-option>
            <el-option label="否" value="N"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="黄牌车进场">
          <el-select v-model="temp.yelowCarIn" placeholder="请选择蓝牌车进场">
            <el-option label="是" value="Y"></el-option>
            <el-option label="否" value="N"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="外部ID">
          <el-input v-model="temp.extBoxId" placeholder="请输入外部ID" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveParkingBoxInfo()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteParkingBoxDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前岗亭吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteParkingBoxDailogVisible = false"
          >取 消</el-button
        >
        <el-button type="primary" @click="doDeleteParkingBox">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script src="./parkingBox.js">

</script>
