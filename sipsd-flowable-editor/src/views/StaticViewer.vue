<template>
  <VueBpmnViewer :source="source" :timeData="timeData" :options="options">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from "@dpark/vue-bpmn-viewer";
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