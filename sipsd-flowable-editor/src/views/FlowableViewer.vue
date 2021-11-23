<template>
<div>
  <VueBpmnViewer ref="vbv" :type="2"
                 :baseApi="baseApi"
                 :instanceId="instanceId"
                 :options="options">
  </VueBpmnViewer>
</div>
</template>
<script>
import VueBpmnViewer from "../packages/vue-bpmn-viewer/components/VueBpmnViewer.vue";
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      baseApi:'http://58.210.9.133/iplatform/sipsd-flow-modeler/',
      instanceId:'e6c573bcc99211eba5465e2c421612f0',
      options:{
        timeLine:true
      }
    }
  },
  methods:{
    refresh(){
      this.$refs.vbv.reload()
    }
  }
}
</script>
