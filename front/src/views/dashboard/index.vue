<template>
  <div class="dashboard-container">
    <div class="dashboard-text">欢迎访问HC物联网平台</div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

import { getCommunitys } from "@/api/community";
export default {
  name: "HC物联网",
  computed: {
    ...mapGetters(["name"]),
  },
 
  
  methods: {
    
  }
};
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
</style>
