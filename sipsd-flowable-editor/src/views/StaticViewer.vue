<template>
  <div style="height: 100%;padding: 10px">
    <el-container style="height: 100%">
      <el-aside width="500px">
        <el-form>
          <el-form-item label="source">
            <el-input type="textarea" :rows="4" v-model="form.source"></el-input>
          </el-form-item>
          <el-form-item label="timeData">
            <el-input type="textarea" :rows="6" v-model="form.timeData"></el-input>
          </el-form-item>
          <el-form-item label="options">
            <el-input type="textarea" :rows="4" v-model="form.options"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refresh">刷新</el-button>
            <el-button>清空</el-button>
          </el-form-item>
        </el-form>
      </el-aside>
      <el-main>
        <VueBpmnViewer ref="vbv" :source="source" :timeData="timeData" :options="options">
        </VueBpmnViewer>
      </el-main>
    </el-container>
  </div>
</template>
<script>
import VueBpmnViewer from "../packages/vue-bpmn-viewer/components/VueBpmnViewer.vue";
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      source:null,
      timeData:null,
      options:null,
      form:{
        source:"http://58.210.9.133/iplatform/sipsd-flow-modeler/rest/formdetail/getprocessXml/e6c573bcc99211eba5465e2c421612f0",
        timeData:"[{\"id\":285,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"3\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF09\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212526,\"endTime\":null,\"updateTime\":null,\"restTime\":-14081326,\"taskId\":\"c87295dfc99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"3\",\"taskDefinitionKey\":\"cgbryqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管办人员现场确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":286,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"2\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF08\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212527,\"endTime\":null,\"updateTime\":null,\"restTime\":-14167726,\"taskId\":\"c872bbf2c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"2\",\"taskDefinitionKey\":\"zfdyqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"执法队员现场检查确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":287,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"4\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF01\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212527,\"endTime\":null,\"updateTime\":null,\"restTime\":-13994926,\"taskId\":\"c872e305c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"4\",\"taskDefinitionKey\":\"jsqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"建设局人员现场确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":280,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"1\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623291833921,\"endTime\":1623292116554,\"updateTime\":null,\"restTime\":-14254505,\"taskId\":\"e6cc5198c99211eba5465e2c421612f0\",\"customTaskMaxDay\":\"1\",\"taskDefinitionKey\":\"widowsA\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"窗口\",\"flowType\":\"sequential\",\"duration\":282,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":281,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"2\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116566,\"endTime\":1623292159158,\"updateTime\":null,\"restTime\":-14167822,\"taskId\":\"8f406749c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"2\",\"taskDefinitionKey\":\"zfdyqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"执法队员现场检查确认\",\"flowType\":\"parallel\",\"duration\":42,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":282,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"3\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116568,\"endTime\":1623292164553,\"updateTime\":null,\"restTime\":-14081422,\"taskId\":\"8f408e5dc99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"3\",\"taskDefinitionKey\":\"cgbryqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管办人员现场确认\",\"flowType\":\"parallel\",\"duration\":47,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":283,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"4\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116569,\"endTime\":1623292170248,\"updateTime\":null,\"restTime\":-13995022,\"taskId\":\"8f40b572c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"4\",\"taskDefinitionKey\":\"jsqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"建设局人员现场确认\",\"flowType\":\"parallel\",\"duration\":53,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":284,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"5\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292170255,\"endTime\":1623292212522,\"updateTime\":null,\"restTime\":-13908569,\"taskId\":\"af408ae6c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"5\",\"taskDefinitionKey\":\"zh\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管局管理员总核\",\"flowType\":\"sequential\",\"duration\":42,\"approveType\":\"驳回\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null}]",
        options:"{\"timeLine\":true}"
      }
    }
  },
  methods:{
    setPro(){
      this.source = this.form.source
      this.timeData = JSON.parse(this.form.timeData)
      this.options = JSON.parse(this.form.options)
    },
    refresh(){
      this.setPro()
      this.$refs.vbv.reload()
    }
  },
  created() {
   this.setPro()
  }
}
</script>
