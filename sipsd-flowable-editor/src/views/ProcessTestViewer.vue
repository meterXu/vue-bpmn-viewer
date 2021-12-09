<template>
  <div style="height: 100%;padding: 10px">
    <el-container style="height: 100%">
      <el-aside width="300px">
        <el-form :model="form" ref="form">
          <el-form-item label="baseApi">
            <el-input type="textarea" :rows="4" v-model="form.baseApi"></el-input>
          </el-form-item>
        </el-form>
      </el-aside>
      <el-main class="my-main">
        <div class="container">
          <ProcessTest :baseApi="form.baseApi"></ProcessTest>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import {ProcessTest} from "../packages/vue-bpmn-viewer/index.js";
export default {
  name: "ProcessTestViewer",
  components: {ProcessTest},
  data() {
    return {
      form:{
        baseApi:'http://58.210.9.133/iplatform/sipsd-flow-modeler/tool/testProcess'
      }
    }
  }
}
</script>

<style scoped>
.container{
  border-left: 1px solid #e0e0e0;
}
</style>
