<template>
  <div class="dashboard-container">
    <div class="dashboard-text">欢迎访问HC小区物联网平台</div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'HC物联网',
  computed: {
    ...mapGetters([
      'name'
    ])
  }
}
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
