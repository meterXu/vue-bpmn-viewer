<template>
  <div id="bpmn">
    <VueBpmnViewer :baseApi="baseApi"
                   :instanceId="instanceId">
    </VueBpmnViewer>
  </div>
</template>

<script>
import VueBpmnViewer from 'vue-bpmn-viewer'
export default {
  name: "Viewer",
  components:{
    VueBpmnViewer
  },
  data(){
    return {
      baseApi:null,
      instanceId:null
    }
  },
  beforeRouteUpdate(to, from, next) {
    this.instanceId = to.query.instanceId
    this.baseApi = this.$project_bpmn.variable.baseApi
  },
  mounted() {
    this.instanceId = this.$route.query.instanceId
    this.baseApi = this.$project_bpmn.variable.baseApi
  }
}
</script>

<style scoped>
#bpmn{
  width: 100%;
  height: 100%;
  display: flex;
  background: #F5F5F7;
  cursor: grab;
}
#bpmn:active{
  cursor: grabbing;
}
.no-bpmn{
  text-align: center;
}
.no-bpmn img{
  display: inline-block;
  margin-top: 100px;
  width: 700px;
}
</style>
