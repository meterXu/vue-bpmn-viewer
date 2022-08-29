<template>
  <div style="height: 100%;padding: 10px">
    <el-container style="height: 100%">
      <el-aside width="300px">
        <el-form :model="form" ref="form" label-position="top">
          <el-form-item label="流程图数据源：">
            <el-radio-group v-model="dataSource" @change = "dataSourceChange">
              <el-radio :label="1">xmlId</el-radio>
              <el-radio :label="2">source</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="baseApi" v-if="dataSource===1">
            <el-input v-model="form.baseApi"></el-input>
          </el-form-item>
          <el-form-item label="xmlId" v-if="dataSource===1">
            <el-select style="width: 290px" v-model="form.xmlId" placeholder="请选择xmlId">
              <el-option :label="item.name" :value="item.id" v-for="item in xmlIds" :key="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="source" v-if="dataSource===2">
            <el-input type="textarea" :rows="4" v-model="form.source"></el-input>
          </el-form-item>
          <el-form-item label="切换主题：">
            <el-radio-group v-model="theme" @change = "themeChange">
              <el-radio :label="1">默认主题</el-radio>
              <el-radio :label="2">经典主题</el-radio>
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
        <VueBpmnViewer v-if="source" ref="vbv"
                       :type="form.type"
                       :baseApi="form.baseApi"
                       :xmlId="form.xmlId"
                       :source="form.source"
                       :timeData="timeData"
                       :options="options"
                       :styl="form.styl"
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
      timeData: null,
      options: null,
      dataSource: 1,
      source: false,
      xmlIds: [],
      drawer: false,
      theme: 1,
      form: {
        type: 1,
        static: true,
        baseApi: window.project_bpmn.variable.baseApi,
        xmlId: "",
        styl: {theme:'default'},
        source:"",
        timeData: "",
        options: ""
      },
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
          this.form.xmlId= this.xmlIds[0].id
        })
      },
      deep:true
    }
  },
  methods: {
    handleClick(obj){
      console.log(obj)
    },
    dataSourceChange(nv) {
      if(nv===2){
        this.form.xmlId= ""
        this.form.source= `${window.project_bpmn.variable.baseApi}rest/model/loadXmlByModelId/12604728-d007-11eb-a546-5e2c421612f0`
        this.form.timeData= "[{\"id\":285,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"3\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF09\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212526,\"endTime\":null,\"updateTime\":null,\"restTime\":-14081326,\"taskId\":\"c87295dfc99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"3\",\"taskDefinitionKey\":\"cgbryqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管办人员现场确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":286,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"2\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF08\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212527,\"endTime\":null,\"updateTime\":null,\"restTime\":-14167726,\"taskId\":\"c872bbf2c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"2\",\"taskDefinitionKey\":\"zfdyqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"执法队员现场检查确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":287,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"4\",\"assignee\":\"0987A213-D2E2-4D29-BA43-338DABFFCF01\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292212527,\"endTime\":null,\"updateTime\":null,\"restTime\":-13994926,\"taskId\":\"c872e305c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"4\",\"taskDefinitionKey\":\"jsqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"建设局人员现场确认\",\"flowType\":\"parallel\",\"duration\":0,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"待办\",\"db\":null},{\"id\":280,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"1\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623291833921,\"endTime\":1623292116554,\"updateTime\":null,\"restTime\":-14254505,\"taskId\":\"e6cc5198c99211eba5465e2c421612f0\",\"customTaskMaxDay\":\"1\",\"taskDefinitionKey\":\"widowsA\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"窗口\",\"flowType\":\"sequential\",\"duration\":282,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":281,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"2\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116566,\"endTime\":1623292159158,\"updateTime\":null,\"restTime\":-14167822,\"taskId\":\"8f406749c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"2\",\"taskDefinitionKey\":\"zfdyqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"执法队员现场检查确认\",\"flowType\":\"parallel\",\"duration\":42,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":282,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"3\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116568,\"endTime\":1623292164553,\"updateTime\":null,\"restTime\":-14081422,\"taskId\":\"8f408e5dc99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"3\",\"taskDefinitionKey\":\"cgbryqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管办人员现场确认\",\"flowType\":\"parallel\",\"duration\":47,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":283,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"4\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292116569,\"endTime\":1623292170248,\"updateTime\":null,\"restTime\":-13995022,\"taskId\":\"8f40b572c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"4\",\"taskDefinitionKey\":\"jsqr\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"建设局人员现场确认\",\"flowType\":\"parallel\",\"duration\":53,\"approveType\":\"审批\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null},{\"id\":284,\"version\":null,\"executionId\":null,\"processInstanceId\":\"e6c573bcc99211eba5465e2c421612f0\",\"processDefinitionId\":\"mutlitest5:1:1387635bc99211eba5465e2c421612f0\",\"taskMaxDay\":\"5\",\"assignee\":\"04F12C37-E808-46F3-830B-13368AAC73C5\",\"realName\":null,\"groupId\":null,\"groupName\":null,\"startTime\":1623292170255,\"endTime\":1623292212522,\"updateTime\":null,\"restTime\":-13908569,\"taskId\":\"af408ae6c99311eba5465e2c421612f0\",\"customTaskMaxDay\":\"5\",\"taskDefinitionKey\":\"zh\",\"fromKey\":null,\"tenantId\":\"flow\",\"formName\":\"户外广告111\",\"businessKey\":\"123\",\"taskName\":\"城管局管理员总核\",\"flowType\":\"sequential\",\"duration\":42,\"approveType\":\"驳回\",\"businessInfo\":null,\"status\":\"已办\",\"db\":null}]"
        this.form.options= '{"zoom":true,"timeLine":true,"fit":false,"setline":false,"scrollZoom":true,"static":false,"track":true,"focus":true,"log":false}'
      }else{
        if(this.xmlIds.length>0)
          this.form.xmlId= this.xmlIds[0].id
        this.form.source= ""
        this.form.timeData= ""
        this.form.options= ""
      }
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
    themeChange(value) {
      if(value===1) {
        this.form.styl.theme = 'default'
      } else if(value===2){
        this.form.styl.theme = 'classic'
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
      this.source = false
      this.setPro()
      this.$nextTick(()=>{
        this.source = true
        this.$store.commit("setForm",this.form)
        this.$store.commit("setTimeData",this.timeData)
        this.$store.commit("setOptions",this.options)
      })
      this.$logfv.log('点击了生效','action')
    },
    handleClose(done) {
      done();
    }
  },
  mounted() {
    axios.get(this.form.baseApi+this.url.pageModel).then(res=>{
      this.xmlIds = res.data.data.data
      if(this.xmlIds.length>0)
        this.form.xmlId= this.xmlIds[0].id
      this.source = true
      this.setPro()
      this.$store.commit("setForm",this.form)
      this.$store.commit("setTimeData",this.timeData)
      this.$store.commit("setOptions",this.options)

    }).catch(err=>{
      this.$message.error(err)
    })
  }
}
</script>
