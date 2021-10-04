<template>
  <div class="app-container">
     <div class="filter-container" style="margin-bottom:10px">
      <el-form
        ref="temp"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="100px"
        modal = false
        style="width: 400px; "
      >
        <el-form-item label="旧密码" prop="oldpwd">
          <el-input v-model="temp.oldpwd" type="password" placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newpwd">
          <el-input v-model="temp.newpwd" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmnewpwd">
          <el-input v-model="temp.confirmnewpwd" type="password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
     </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="saveNewPwd()">保存</el-button>
      </div>

  </div>
</template>

<script>
import {
  changePwd
} from "@/api/user";
import { parseTime } from "@/utils";
export default {
  data() {
    return {
      temp: {
        oldpwd: "",
        newpwd: "",
        confirmnewpwd: ""
      },
      rules: {
         oldpwd: [
          { required: true, message: "不能为空", trigger: "change" }
        ],
        newpwd: [
          { required: true, message: "不能为空", trigger: "change" }
        ],
        confirmnewpwd: [
          { required: true, message: "不能为空", trigger: "change" }
        ]
      }
    };
  },

  methods: {
    saveNewPwd() {
       if(this.temp.confirmnewpwd =='' ||this.temp.newpwd == '' ||this.temp.oldpwd == ''){
         this.$message({
          type: "error",
          message: "必填项不能为空。"
        });
        return;
      }
      if(this.temp.confirmnewpwd != this.temp.newpwd){
         this.$message({
          type: "error",
          message: "两次输入的新密码不一致。"
        });
        return;
      }
      changePwd(this.temp).then(response => {
        if(response.code == 0){
         this.$message({
          type: "success",
          message: "密码修改成功。"
        });
        this.$refs['temp'].resetFields();
      }
      });
    }

  }
};
</script>
