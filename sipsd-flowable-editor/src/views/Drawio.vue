<template>
  <div>
    xxx
    <vue-bpmn :viewer="true" ref="bpmnObj"  :url="url">
    </vue-bpmn>
  </div>
</template>

<script>
import {util} from '@dpark/s2-utils'
import VueBpmn from '@dpark/vue-bpmn';
export default {
  name: "BpmnViewer",
  data(){
    return {
      url:"/cbb.xml"
    }
  },
  components:{VueBpmn}
}
</script>

<style scoped>

</style>
