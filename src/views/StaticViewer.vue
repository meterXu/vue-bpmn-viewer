<template>
  <div style="height: 100%;padding: 10px">
    <el-container style="height: 100%">
      <el-aside width="300px">
        <el-form :model="form" ref="form" label-position="top">
          <el-form-item label="流程图数据源：">
            <el-radio-group v-model="form.type" @change = "dataSourceChange">
              <el-radio :label="1">xmlId</el-radio>
              <el-radio :label="2">instanceId</el-radio>
              <el-radio :label="null">source</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="baseApi" v-if="form.type===1||form.type===2">
            <el-input v-model="form.baseApi"></el-input>
          </el-form-item>
          <el-form-item label="xmlId" v-if="form.type===1">
            <el-select style="width: 290px" v-model="form.xmlId" placeholder="请选择xmlId">
              <el-option :label="item.name" :value="item.id" v-for="item in xmlIds" :key="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="instanceId" v-if="form.type===2">
            <el-input v-model="form.instanceId" placeholder="请输入instanceId">
            </el-input>
          </el-form-item>
          <el-form-item label="source" v-if="form.type===null">
            <el-input type="textarea" :rows="4" v-model="form.source"></el-input>
          </el-form-item>
          <el-form-item label="切换主题：">
            <el-radio-group v-model="form.styl.theme">
              <el-radio label="default">默认主题</el-radio>
              <el-radio label="classic">经典主题</el-radio>
              <el-radio label="dark">深色主题</el-radio>
              <el-radio label="ccp">党建主题</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="timeData">
            <el-input type="textarea" :rows="4" v-model="form.timeData"></el-input>
          </el-form-item>
          <el-form-item label="options">
            <el-input type="textarea" :rows="4" v-model="form.options"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refresh" style="width: 100%">生效</el-button>
          </el-form-item>
        </el-form>
      </el-aside>
      <el-main>
        <VueBpmnViewer v-if="isRefresh" ref="vbv"
                       :type="viewBpmnViewerOps.type"
                       :baseApi="viewBpmnViewerOps.baseApi"
                       :xmlId="viewBpmnViewerOps.xmlId"
                       :instanceId="viewBpmnViewerOps.instanceId"
                       :source="viewBpmnViewerOps.source"
                       :timeData="viewBpmnViewerOps.timeData"
                       :options="viewBpmnViewerOps.options"
                       :styl="viewBpmnViewerOps.styl"
                       style="height: 100%">
          <template v-slot:time="slotProps">
            <p>{{slotProps.item.taskName}}</p>
            <p>审批类型：{{slotProps.item.approveType}}</p>
            <p>状态：{{slotProps.item.status}}</p>
            <p v-if="slotProps.item.status==='已办'">持续时间：{{timeFormat(slotProps.item.duration)}}</p>
            <p v-else>剩余时间：{{timeFormat(slotProps.item.restTime)}}</p>
            <p>下载：<a target="_blank" href="http://www.baidu.com">baidu</a></p>
          </template>
        </VueBpmnViewer>
      </el-main>
    </el-container>
  </div>
</template>
<script>
import VueBpmnViewer from "../packages/vue-bpmn-viewer/index.js";
import ms from 'pretty-ms'
import axios from "axios";
export default {
  components: {VueBpmnViewer},
  data() {
    return {
      isRefresh: false,
      xmlIds: [],
      drawer: false,
      form: {
        type: 1,
        static: true,
        baseApi: null,
        xmlId: "",
        instanceId: "",
        styl: {theme:'default'},
        source:null,
        timeData: null,
        options:null
      },
      viewBpmnViewerOps:{},
      url:{
        pageModel: "/rest/model/page-model"
      },
    }
  },
  watch:{
    'form.baseApi':{
      handler(nv){
        axios.get(nv+this.url.pageModel).then(res=>{
          this.xmlIds = res.data.data.data
          if(this.xmlIds.length>0)
            this.form.xmlId= this.xmlIds[0].id
        })
      },
      deep:true
    }
  },
  methods: {
    handleClick(obj){
    },
    dataSourceChange(nv) {
      if(nv===null){
        this.form.source = this.$project_bpmn.variable.source
        this.form.timeData = "[{\"id\":285,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"3\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF09\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212526,\"endTime\":null,\"updateTime\":null,\"restTime\":-14081326,\"taskId\":\"c87295dfc99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"3\",\"taskDefinitionKey\":\"cgbryqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管办人员现场确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":286,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"2\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF08\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212527,\"endTime\":null,\"updateTime\":null,\"restTime\":-14167726,\"taskId\":\"c872bbf2c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"2\",\"taskDefinitionKey\":\"zfdyqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"执法队员现场检查确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":287,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"4\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF01\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212527,\"endTime\":null,\"updateTime\":null,\"restTime\":-13994926,\"taskId\":\"c872e305c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"4\",\"taskDefinitionKey\":\"jsqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"建设局人员现场确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":280,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"1\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623291833921,\"endTime\":1623292116554,\"updateTime\":null,\"restTime\":-14254505,\"taskId\":\"e6cc5198c99211eba5465e2c421612f0\",\"customTaskMaxDay\":\"1\",\"taskDefinitionKey\":\"widowsA\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"窗口\",\"flowType\":\"sequential\",\"duration\":282,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":281,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"2\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116566,\"endTime\":1623292159158,\"updateTime\":null,\"restTime\":-14167822,\"taskId\":\"8f406749c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"2\",\"taskDefinitionKey\":\"zfdyqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"执法队员现场检查确认\",\"flowType\":\"parallel\",\"duration\":42,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":282,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"3\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116568,\"endTime\":1623292164553,\"updateTime\":null,\"restTime\":-14081422,\"taskId\":\"8f408e5dc99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"3\",\"taskDefinitionKey\":\"cgbryqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管办人员现场确认\",\"flowType\":\"parallel\",\"duration\":47,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":283,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"4\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116569,\"endTime\":1623292170248,\"updateTime\":null,\"restTime\":-13995022,\"taskId\":\"8f40b572c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"4\",\"taskDefinitionKey\":\"jsqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"建设局人员现场确认\",\"flowType\":\"parallel\",\"duration\":53,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":284,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"5\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292170255,\"endTime\":1623292212522,\"updateTime\":null,\"restTime\":-13908569,\"taskId\":\"af408ae6c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"5\",\"taskDefinitionKey\":\"zh\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管局管理员总核\",\"flowType\":\"sequential\",\"duration\":42,\"approveType\":\"驳回\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null}]"
        this.form.options = '{"zoom":true,"timeLine":true,"fit":false,"setline":false,"scrollZoom":true,"static":false,"track":true,"focus":true,"log":false}'
      }else if(nv===2){
        this.form.source = null
        this.form.timeData = null
        this.form.options = '{"timeLine":true}'
      }else{
        this.form.source = null
        this.form.timeData = null
        this.form.options = null
      }
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
      this.viewBpmnViewerOps = JSON.parse(JSON.stringify(this.form))
      try {
        this.viewBpmnViewerOps.timeData = JSON.parse(this.viewBpmnViewerOps.timeData)
      } catch (e) {
        this.viewBpmnViewerOps.timeData = null
      }
      try {
        this.viewBpmnViewerOps.options = JSON.parse(this.viewBpmnViewerOps.options)
      } catch (e) {
        this.viewBpmnViewerOps.options = null
      }
    },
    refresh() {
      this.isRefresh = false
      this.setPro()
      this.$nextTick(()=>{
        this.isRefresh = true
        this.$store.commit("setForm",this.form)
        this.$store.commit("setTimeData",this.timeData)
        this.$store.commit("setOptions",this.options)
      })
    }
  },
  mounted() {
    this.form.baseApi = this.$project_bpmn.variable.baseApi
    axios.get(this.form.baseApi+this.url.pageModel).then(res=>{
      this.xmlIds = res.data.data.data
      if(this.xmlIds.length>0)
        this.form.xmlId= this.xmlIds[0].id
      this.refresh()
    }).catch(err=>{
      this.$message.error(err)
    })
  }
}
</script>
