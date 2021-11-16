<template>
  <VueBpmnViewer :source="source" :timeData="timeData" :options="options">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from "../packages/vue-bpmn-viewer/components/VueBpmnViewer.vue";
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      source:"http://58.210.9.133/iplatform/sipsd-flow-modeler/rest/formdetail/getprocessXml/e6c573bcc99211eba5465e2c421612f0",
      timeData:null,
      options:{
        timeLine:true
      }
    }
  }
}
</script>
