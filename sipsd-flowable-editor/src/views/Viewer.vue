<template>
  <div id="bpmn">
    <VueBpmnViewer :baseApi="baseApi"
                   :instanceId="instanceId"
                   :options="{zoom,timeLine}">
    </VueBpmnViewer>
  </div>
</template>

<script>
import VueBpmnViewer from '../packages/vue-bpmn-viewer/VueBpmnViewer'
export default {
  name: "Viewer",
  components:{
    VueBpmnViewer
  },
  data(){
    return {
      baseApi:null,
      instanceId:null,
      zoom:true,
      timeLine:true
    }
  },
  beforeRouteUpdate(to, from, next) {
    this.instanceId = to.query.instanceId
    this.baseApi = this.$project_bpmn.variable.baseApi
    this.zoom = (to.query.zoom||'true')==='true'
    this.timeLine = (to.query.timeLine||'true')==='true'
  },
  mounted() {
    this.instanceId = this.$route.query.instanceId
    this.zoom = (this.$route.query.zoom||'true')==='true'
    this.timeLine = (this.$route.query.timeLine||'true')==='true'
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
