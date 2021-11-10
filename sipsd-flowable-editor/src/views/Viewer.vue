<!--<template>-->
<!--  <div id="bpmn">-->
<!--    <VueBpmnViewer :baseApi="baseApi"-->
<!--                   :type="type"-->
<!--                   :instanceId="instanceId"-->
<!--                   :xmlId="xmlId"-->
<!--                   :static="static"-->
<!--                   :options="{zoom,timeLine,center,setline}">-->
<!--    </VueBpmnViewer>-->
<!--  </div>-->
<!--</template>-->

<!--<script>-->
<!--import VueBpmnViewer from '@dpark/vue-bpmn-viewer'-->
<!--import VueBpmnViewer from "../packages/vue-bpmn-viewer/VueBpmnViewer";-->
<!--export default {-->
<!--  name: "Viewer",-->
<!--  components:{-->
<!--    VueBpmnViewer-->
<!--  },-->
<!--  data(){-->
<!--    return {-->
<!--      type:null,-->
<!--      baseApi:null,-->
<!--      xmlId:null,-->
<!--      instanceId:null,-->
<!--      zoom:true,-->
<!--      timeLine:true,-->
<!--      setline:false,-->
<!--      center:true,-->
<!--      static:false,-->
<!--      log:false-->
<!--    }-->
<!--  },-->
<!--  beforeRouteUpdate(to, from, next) {-->
<!--    this.type = parseInt(to.query.type)-->
<!--    this.instanceId = to.query.instanceId-->
<!--    this.xmlId = to.query.xmlId-->
<!--    this.baseApi = this.$project_bpmn.variable.baseApi-->
<!--    this.zoom = (to.query.zoom||'true')==='true'-->
<!--    this.timeLine = (to.query.timeLine||'true')==='true'-->
<!--    this.setline = (to.query.setline||'false')==='true'-->
<!--    this.center = (to.query.center||'true')==='true'-->
<!--    this.static = (to.query.static||'false')==='true'-->
<!--    next()-->
<!--  },-->
<!--  mounted() {-->
<!--    this.type = parseInt(this.$route.query.type)-->
<!--    this.instanceId = this.$route.query.instanceId-->
<!--    this.xmlId = this.$route.query.xmlId-->
<!--    this.zoom = (this.$route.query.zoom||'true')==='true'-->
<!--    this.timeLine = (this.$route.query.timeLine||'true')==='true'-->
<!--    this.center = (this.$route.query.center||'true')==='true'-->
<!--    this.static = (this.$route.query.static||'false')==='true'-->
<!--    this.setline = (this.$route.query.setline||'false')==='true'-->
<!--    this.baseApi = this.$project_bpmn.variable.baseApi-->
<!--  }-->
<!--}-->

<!--</script>-->

<template>
  <VueBpmnViewer :source="source" :timeData="timeData" :options="options">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from "../packages/vue-bpmn-viewer/VueBpmnViewer"
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      source:"http://58.210.9.133/iplatform/sipsd-flow-modeler/rest/formdetail/getprocessXml/e6c573bcc99211eba5465e2c421612f0",
      timeData:[
        {
          "id": 1,
          "taskMaxDay": "4",
          "realName": null,
          "startTime": 1623292212527,
          "restTime": -12913873,
          "customTaskMaxDay": "4",
          "taskDefinitionKey": "jsqr",
          "taskName": "建设局人员现场确认",
          "duration": 0,
          "approveType": "审批",
          "status": "待办"
        },
        {
          "id": 2,
          "taskMaxDay": "1",
          "realName": null,
          "startTime": 1623291833921,
          "restTime": -13173452,
          "customTaskMaxDay": "1",
          "taskDefinitionKey": "widowsA",
          "taskName": "窗口",
          "duration": 282,
          "approveType": "审批",
          "status": "已办"
        }
      ],
      options:{
        timeLine:true
      }
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
