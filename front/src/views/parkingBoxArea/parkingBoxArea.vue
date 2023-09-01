<template>
  <div class="app-container">
    <div class="filter-container" style="margin-bottom: 10px">
      <el-button
        class="filter-item"
        style="margin-left: 10px"
        type="primary"
        icon="el-icon-edit"
        @click="addParkingBoxArea"
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
      <el-table-column align="center" label="停车场">
        <template slot-scope="scope">{{ scope.row.paNum }}</template>
      </el-table-column>
      <el-table-column align="center" label="停车场编号">
        <template slot-scope="scope">{{ scope.row.paId }}</template>
      </el-table-column>
      <el-table-column label="默认停车场" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.defaultArea == "T" ? "是" : "否" }}</span>
        </template>
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
               v-if="row.defaultArea == 'F'"
              @click="_defaultArea(row, $index)"
              >默认停车场</el-button
            >
            <el-button
              size="mini"
              type="danger"
              v-if="row.defaultArea == 'F'"
              @click="deleteParkingBoxArea(row, $index)"
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


    <el-dialog title="岗亭停车场" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="70px"
        style="width: 400px; margin-left: 50px"
      >
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
        <el-form-item label="默认停车场" prop="type">
          <el-select v-model="temp.defaultArea" placeholder="请选择默认停车场">
            <el-option label="是" value="T"></el-option>
            <el-option label="否" value="F"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveParkingBoxAreaInfo()">提交</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="温馨提示"
      :visible.sync="deleteParkingBoxAreaDailogVisible"
      width="30%"
      :before-close="handleClose"
    >
      <span>您确定删除当前岗亭停车场吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteParkingBoxAreaDailogVisible = false"
          >取 消</el-button
        >
        <el-button type="primary" @click="doDeleteParkingBoxArea">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script src="./parkingBoxArea.js">

</script>
