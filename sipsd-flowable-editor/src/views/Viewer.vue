<template>
  <div id="bpmn">
    <VueBpmnViewer ref="bpmnObj" :baseApi="baseApi"
                   :type="type"
                   :instanceId="instanceId"
                   :xmlId="xmlId"
                   :static="static"
                   :options="{zoom,timeLine,fit,setline}">
    </VueBpmnViewer>
  </div>
</template>

<script>
import VueBpmnViewer from "../packages/vue-bpmn-viewer/index.js";
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
      fit:false,
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
    this.fit = (to.query.fit||'false')==='true'
    this.static = (to.query.static||'false')==='true'
    if(this.type===1){
      this.timeLine = false
    }
    if(this.$refs.bpmnObj){
      this.$refs.bpmnObj.reload()
    }
    next()
  },
  mounted() {
    this.type = parseInt(this.$route.query.type)
    this.instanceId = this.$route.query.instanceId
    this.xmlId = this.$route.query.xmlId
    this.zoom = (this.$route.query.zoom||'true')==='true'
    this.timeLine = (this.$route.query.timeLine||'true')==='true'
    this.fit = (this.$route.query.fit||'false')==='true'
    this.static = (this.$route.query.static||'false')==='true'
    this.setline = (this.$route.query.setline||'false')==='true'
    this.baseApi = this.$project_bpmn.variable.baseApi
    if(this.type===1){
      this.timeLine = false
    }
    if(this.$refs.bpmnObj){
      this.$refs.bpmnObj.reload()
    }
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
