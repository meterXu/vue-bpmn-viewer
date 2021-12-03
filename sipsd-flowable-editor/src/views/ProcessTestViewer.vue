<template>
  <div class="process">
    <div style="margin-bottom: 20px">
      baseAPI：<el-input v-model="baseApi"></el-input>
    </div>

    <ProcessTest :baseApi="baseApi"></ProcessTest>
  </div>
</template>

<script>
import {ProcessTest} from "../packages/vue-bpmn-viewer/index.js";
export default {
  name: "ProcessTestViewer",
  components: {ProcessTest},
  data() {
    return {
      baseApi:'http://58.210.9.133/iplatform/sipsd-flow-modeler/tool/testProcess'
    }
  }
}
</script>

<style scoped>
.process {
  width: 600px;
  margin: 20px auto;
}
</style>
