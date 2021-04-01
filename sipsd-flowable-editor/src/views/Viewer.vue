<template>
  <div id="bpmn">
    <VueBpmnViewer :baseApi="baseApi"
                :xmlId="xmlId"
                :instanceId="instanceId"
    ></VueBpmnViewer>
  </div>
</template>

<script>
import VueBpmnViewer from '../packages/vue-bpmn-viewer/Index'
export default {
  name: "Viewer",
  components:{
    VueBpmnViewer
  },
  data(){
    return {
      baseApi:null,
      xmlId:null,
      instanceId:null
    }
  },
  beforeRouteUpdate(to, from, next) {
    this.xmlId = this.$route.query.xmlId
    this.instanceId = this.$route.query.instanceId
    this.baseApi = this.$project_bpmn.variable.baseApi
  },
  mounted() {
    this.xmlId = this.$route.query.xmlId
    this.instanceId = this.$route.query.instanceId
    this.baseApi = this.$project_bpmn.variable.baseApi
  }
}
</script>

<style scoped>
#bpmn{
  width: 100%;
  height: 100%;
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
