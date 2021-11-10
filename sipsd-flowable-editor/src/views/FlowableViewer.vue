<template>
  <VueBpmnViewer :type="2"
                 :baseApi="baseApi"
                 :instanceId="instanceId">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from "@dpark/vue-bpmn-viewer";
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      baseApi:'http://58.210.9.133/iplatform/sipsd-flow-modeler/',
      instanceId:'e6c573bcc99211eba5465e2c421612f0',
    }
  }
}
</script>