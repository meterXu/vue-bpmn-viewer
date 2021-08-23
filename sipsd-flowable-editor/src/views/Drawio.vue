<template>
  <vue-bpmn :viewer="true" ref="bpmnObj"  :url="url">

  </vue-bpmn>
</template>

<script>
import {util} from '@dpark/s2-utils'
export default {
  name: "BpmnViewer",
  data(){
    return {
      url:""
    }
  },
  created() {
    this.url = util.getAssetsImg('../assets/cbb.xml')
  }
}
</script>

<style scoped>

</style>
