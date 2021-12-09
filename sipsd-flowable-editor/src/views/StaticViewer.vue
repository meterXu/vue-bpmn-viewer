<template>
  <div style="height: 100%;padding: 10px">
    <el-container style="height: 100%">
      <el-aside width="300px">
        <el-form :model="form" ref="form">
          <el-form-item label="type">
            <el-radio-group v-model="form.type">
              <el-radio :label="1">流程图模式</el-radio>
              <el-radio :label="2">实例模式</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="baseApi">
            <el-input v-model="form.baseApi"></el-input>
          </el-form-item>
          <el-form-item label="instanceId">
            <el-input v-model="form.instanceId"></el-input>
          </el-form-item>
          <el-form-item label="xmlId">
            <el-input v-model="form.xmlId"></el-input>
          </el-form-item>
          <el-form-item label="source">
            <el-input type="textarea" :rows="4" v-model="form.source"></el-input>
          </el-form-item>
          <el-form-item label="timeData">
            <el-input type="textarea" :rows="4" v-model="form.timeData"></el-input>
          </el-form-item>
          <el-form-item label="options">
            <el-input type="textarea" :rows="4" v-model="form.options"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refresh">刷新</el-button>
            <el-button type="primary" @click="openDialog">弹出框</el-button>
          </el-form-item>
        </el-form>
      </el-aside>
      <el-main>
        <VueBpmnViewer ref="vbv"
                       :type="form.type"
                       :baseApi="form.baseApi"
                       :instanceId="form.instanceId"
                       :xmlId="form.xmlId"
                       :source="form.source"
                       :timeData="timeData"
                       :options="options"
                       @click="handleClick"
                       @viewChange="handleViewChange">
          <template v-slot:time="slotProps">
            <p>{{slotProps.item.taskName}}</p>
            <p>审批类型：{{slotProps.item.approveType}}</p>
            <p>状态：{{slotProps.item.status}}</p>
            <p v-if="slotProps.item.status==='已办'">持续时间：{{timeFormat(slotProps.item.duration)}}</p>
            <p v-else>剩余时间：{{timeFormat(slotProps.item.restTime)}}</p>
            <p>下载：<a target="_blank" href="http://www.baidu.com">baidu</a></p>
          </template>
        </VueBpmnViewer>
        <el-dialog
            title="提示"
            :visible.sync="dialogVisible"
            width="90%"
            :before-close="handleClose">
          <div style="height:600px">
            <VueBpmnViewer ref="vbv2"
                           :type="form.type"
                           :baseApi="form.baseApi"
                           :instanceId="form.instanceId"
                           :xmlId="form.xmlId"
                           :source="form.source"
                           :timeData="timeData"
                           :options="options">
            </VueBpmnViewer>
          </div>
          <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
  </span>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>
<script>
import VueBpmnViewer from "../packages/vue-bpmn-viewer/index.js";
import ms from 'pretty-ms'
export default {
  components: {VueBpmnViewer},
  data() {
    return {
      dialogVisible: false,
      timeData: null,
      options: null,
      form: {
        type: 1,
        static: true,
        baseApi: 'http://58.210.9.133/iplatform/sipsd-flow-modeler/',
        instanceId: 'e6c573bcc99211eba5465e2c421612f0',
        xmlId: "4b99159a-bc63-11eb-b2ee-5e2c421612f0",
        source:"http://58.210.9.133/iplatform/sipsd-flow-modeler/rest/formdetail/getprocessXml/e6c573bcc99211eba5465e2c421612f0",
        // source:"http://81.69.202.201:8989/sipsd-flow-modeler/rest/formdetail/getprocessXml/009f924c53e011ec907ebe62ef6b62a5",
        timeData: "[{\"id\":285,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"3\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF09\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212526,\"endTime\":null,\"updateTime\":null,\"restTime\":-14081326,\"taskId\":\"c87295dfc99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"3\",\"taskDefinitionKey\":\"cgbryqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管办人员现场确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":286,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"2\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF08\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212527,\"endTime\":null,\"updateTime\":null,\"restTime\":-14167726,\"taskId\":\"c872bbf2c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"2\",\"taskDefinitionKey\":\"zfdyqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"执法队员现场检查确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":287,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"4\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF01\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212527,\"endTime\":null,\"updateTime\":null,\"restTime\":-13994926,\"taskId\":\"c872e305c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"4\",\"taskDefinitionKey\":\"jsqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"建设局人员现场确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":280,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"1\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623291833921,\"endTime\":1623292116554,\"updateTime\":null,\"restTime\":-14254505,\"taskId\":\"e6cc5198c99211eba5465e2c421612f0\",\"customTaskMaxDay\":\"1\",\"taskDefinitionKey\":\"widowsA\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"窗口\",\"flowType\":\"sequential\",\"duration\":282,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":281,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"2\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116566,\"endTime\":1623292159158,\"updateTime\":null,\"restTime\":-14167822,\"taskId\":\"8f406749c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"2\",\"taskDefinitionKey\":\"zfdyqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"执法队员现场检查确认\",\"flowType\":\"parallel\",\"duration\":42,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":282,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"3\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116568,\"endTime\":1623292164553,\"updateTime\":null,\"restTime\":-14081422,\"taskId\":\"8f408e5dc99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"3\",\"taskDefinitionKey\":\"cgbryqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管办人员现场确认\",\"flowType\":\"parallel\",\"duration\":47,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":283,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"4\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116569,\"endTime\":1623292170248,\"updateTime\":null,\"restTime\":-13995022,\"taskId\":\"8f40b572c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"4\",\"taskDefinitionKey\":\"jsqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"建设局人员现场确认\",\"flowType\":\"parallel\",\"duration\":53,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":284,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"5\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292170255,\"endTime\":1623292212522,\"updateTime\":null,\"restTime\":-13908569,\"taskId\":\"af408ae6c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"5\",\"taskDefinitionKey\":\"zh\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管局管理员总核\",\"flowType\":\"sequential\",\"duration\":42,\"approveType\":\"驳回\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null}]",
        // timeData: "[\n" +
        //     "        {\n" +
        //     "          \"id\": 1,\n" +
        //     "          \"taskMaxDay\": \"4\",\n" +
        //     "          \"realName\": null,\n" +
        //     "          \"startTime\": 1623292212527,\n" +
        //     "          \"restTime\": -12913873,\n" +
        //     "          \"customTaskMaxDay\": \"4\",\n" +
        //     "          \"taskDefinitionKey\": \"sid-12C9C97B-AE53-4EC4-9D0A-ACECF79AE9A7\",\n" +
        //     "          \"taskName\": \"建设局人员现场确认\",\n" +
        //     "          \"duration\": 0,\n" +
        //     "          \"approveType\": \"审批\",\n" +
        //     "          \"status\": \"待办\"\n" +
        //     "        },\n" +
        //     "        {\n" +
        //     "          \"id\": 2,\n" +
        //     "          \"taskMaxDay\": \"1\",\n" +
        //     "          \"realName\": null,\n" +
        //     "          \"startTime\": 1623291833921,\n" +
        //     "          \"restTime\": -13173452,\n" +
        //     "          \"customTaskMaxDay\": \"1\",\n" +
        //     "          \"taskDefinitionKey\": \"widowsA\",\n" +
        //     "          \"taskName\": \"窗口\",\n" +
        //     "          \"duration\": 282,\n" +
        //     "          \"approveType\": \"审批\",\n" +
        //     "          \"status\": \"已办\"\n" +
        //     "        }\n" +
        //     "      ]",
        options: '{"zoom":true,"timeLine":true,"fit":false,"setline":false,"scrollZoom":true,"static":false,"track":true,"focus":true,"log":false}'
      }
    }
  },
  methods: {
    handleClick(obj){
      console.log(obj)
    },
    handleViewChange(event){
      console.log(event)
    },
    timeFormat(s){
      if(s){
        return ms(s*1000)
            .replace(/ -/g,'')
            .replace('d','天')
            .replace('h','小时')
            .replace('m','分')
            .replace('s','秒')
      }else{
        return '-'
      }
    },
    setPro() {
      try {
        this.timeData = JSON.parse(this.form.timeData)
      } catch (e) {
        this.timeData = null
      }
      try {
        this.options = JSON.parse(this.form.options)
      } catch (e) {
        this.options = null
      }
    },
    refresh() {
      this.setPro()
      this.$refs.vbv.reload()
    },
    openDialog() {
      this.dialogVisible = true
      this.setPro()
      this.$nextTick(()=>{
        this.$refs.vbv2.reload()
      })
    },
    handleClose(done) {
      done();
    }
  },
  created() {
    this.setPro()
  }
}
</script>
