<template>
  <div class="process">
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
      baseApi:'http://192.168.75.13:9001/sipsd-flow-modeler/tool/testProcess'
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
