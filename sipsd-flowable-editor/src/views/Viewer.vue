<template>
  <div id="bpmn">
    <VueBpmnViewer :baseApi="baseApi"
                   :type="type"
                   :instanceId="instanceId"
                   :xmlId="xmlId"
                   :static="static"
                   :options="{zoom,timeLine,center,setline}">
    </VueBpmnViewer>
  </div>
</template>

<script>
import VueBpmnViewer from '@dpark/vue-bpmn-viewer'
// import VueBpmnViewer from "../packages/vue-bpmn-viewer/VueBpmnViewer";
export default {
  name: "Viewer",
  components:{
    VueBpmnViewer
  },
  data(){
    return {
      type:null,
      baseApi:null,
      xmlId:null,
      instanceId:null,
      zoom:true,
      timeLine:true,
      setline:false,
      center:true,
      static:false,
      log:false
    }
  },
  beforeRouteUpdate(to, from, next) {
    this.type = parseInt(to.query.type)
    this.instanceId = to.query.instanceId
    this.xmlId = to.query.xmlId
    this.baseApi = this.$project_bpmn.variable.baseApi
    this.zoom = (to.query.zoom||'true')==='true'
    this.timeLine = (to.query.timeLine||'true')==='true'
    this.setline = (to.query.setline||'false')==='true'
    this.center = (to.query.center||'true')==='true'
    this.static = (to.query.static||'false')==='true'
    next()
  },
  mounted() {
    this.type = parseInt(this.$route.query.type)
    this.instanceId = this.$route.query.instanceId
    this.xmlId = this.$route.query.xmlId
    this.zoom = (this.$route.query.zoom||'true')==='true'
    this.timeLine = (this.$route.query.timeLine||'true')==='true'
    this.center = (this.$route.query.center||'true')==='true'
    this.static = (this.$route.query.static||'false')==='true'
    this.setline = (this.$route.query.setline||'false')==='true'
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
