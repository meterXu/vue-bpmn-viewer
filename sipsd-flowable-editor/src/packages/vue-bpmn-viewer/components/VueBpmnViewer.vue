<template>
  <div id="bpmn-viewer">
    <Spin :spinning="showSpining" tip="加载中..."  wrapperClassName="bpmn-viewer-canvas">
      <vue-bpmn :viewer="static" v-if="showBpmn" ref="bpmnObj" :options="bpmnOptions" :url="xml" @shown="bpmnLoadDone" @loading="bpmnLoadDone" @error="bpmnLoadError"></vue-bpmn>
      <div v-else class="no-bpmn">
        <img :src="getAssetsImg(require('../assets/no-bpmn.svg'))">
      </div>
      <div class="legend">
        <ul class="legend-ul">
          <li>
            <i class="legend-icon legend-none"></i>
            <span>未执行</span>
          </li>
          <li>
            <i class="legend-icon legend-unExec"></i>
            <span>待审批</span>
          </li>
          <li>
            <i class="legend-icon legend-exec"></i>
            <span>已审批</span>
          </li>
          <li>
            <i class="legend-icon legend-back"></i>
            <span>被驳回</span>
          </li>
        </ul>
      </div>
    </Spin>
    <BTLayout>
      <template slot="head">
        <slot></slot>
      </template>
      <template slot="right">
        <BTZoom v-show="showZoom" :center="myOptions.center" :bpmnViewer="bpmnViewer" ref="cBTZoom"/>
        <slot name="timeLine" v-if="showTimeLine" v-bind:loading="timeLine_loading" v-bind:data="taskData">
          <BTimeLine :loading="timeLine_loading" :data="taskData"/>
        </slot>
      </template>
    </BTLayout>
  </div>
</template>

<script>
import VueBpmn from './bpmn/VueBpmn.vue';
import bpmnThemeBlue from './blue/index.js'
import {BTimeLine,utils,BTLayout,BTZoom} from './controls/index.js'
import {util} from '@dpark/s2-utils'
import {Spin} from "ant-design-vue";
import axios from 'axios'
import urljoin from 'url-join';
import {LogFv} from '@dpark/logfv-web-vue'
export default {
  name: "VueBpmnViewer",
  props:{
    baseApi:{type:String,required:false},
    instanceId:{type:String},
    xmlId:{type:String},
    type:{type:Number,required: false},
    static:{type:Boolean,default: false},
    source:{type:String},
    timeData:{type:Array},
    options:{type:Object},
    log:{type:Boolean,default:false},
    logReportUrl:{type:String,default:'http://58.210.9.133/iplatform/logfv-server/logfv/web/upload'}
  },
  components:{
    VueBpmn,
    BTLayout,
    BTZoom,
    BTimeLine,
    Spin
  },
  data(){
    return {
      logfv:null,
      loading:false,
      bpmnViewer:null,
      timeLine_loading:true,
      bpmnOptions:{
        additionalModules:[bpmnThemeBlue]
      },
      taskData:[],
      url:{
        xmlUrl:'/rest/model/loadXmlByModelId/',
        instanceUrl:'rest/formdetail/getprocessXml/',
        allExtensionTasks:'rest/extension/task/get-all-extension-tasks',
        exportUrl:'app/rest/models/[]/bpmn20?version=1617092632878',
        restModels:'app/rest/models/'
      },
      myOptions:null
    }
  },
  computed:{
    xml(){
      this.clearWatermark()
      utils.clearAllHighLight()
      if(this.source){
        return this.source
      }else if(this.baseApi){
        if(this.type===1 && this.xmlId){
          return urljoin(this.baseApi,this.url.xmlUrl+this.xmlId)
        }else if(this.type===2 && this.instanceId){
          return urljoin(this.baseApi,this.url.instanceUrl+this.instanceId)
        }
      }else{
        return null
      }
    },
    showSpining(){
      if(this.source){
        return false
      }else if(this.baseApi){
        return  this.loading
      }else{
        return false
      }
    },
    showBpmn(){
      if(this.source){
        return true
      }else if(this.baseApi){
        return (this.instanceId&&this.type===2)||(this.xmlId&&this.type===1)
      }else{
        return false
      }
    },
    showTimeLine(){
      if(this.myOptions.timeLine&&!this.loading){
        if(this.timeData){
          return true
        }else{
          return this.type===2&&this.instanceId
        }
      }else{
        return false
      }
    },
    showZoom(){
      if(this.myOptions.zoom&&!this.loading){
        if(this.source){
          return true
        } else {
          return  (this.type===2&&this.instanceId)||(this.type===1&&this.xmlId)
        }
      }else{
        return false
      }
    }
  },
  watch:{
    'taskData':{
      handler:function (nv){
        nv.forEach(c=>{
          switch (c.status){
            case '已办':{
              if(c.approveType === '驳回'){
                utils.setTaskHighlight([c.taskDefinitionKey],{color:'#ff0000',setline: false,shadow: false,type:3,stroke:true})
              } else{
                utils.setTaskHighlight([c.taskDefinitionKey],{color:'#5BC14B',setline: false,shadow: false,type:2,stroke:true})
              }
            }break;
            case '待办':{
              utils.setTaskHighlight([c.taskDefinitionKey],{color:'#f5842c',setline: this.myOptions.setline,shadow:false,type:1,stroke:true })
            }break;
          }
        })
      }
    },
    xml(){
      this.loading = true
    }
  },
  methods:{
    getTaskList(){
      this.taskData=[]
      if(this.timeData){
        this.dealWithTimeData(this.timeData)
        this.timeLine_loading=false
      }else{
        this.getTimeData()
      }
    },
    getTimeData(){
      if(this.instanceId){
        axios.get(urljoin(this.baseApi,this.url.allExtensionTasks),{
          params:{
            initPageIndex:1,
            pageIndex:1,
            pageNum:1,
            pageSize:99,
            processInstanceId:this.instanceId
          }
        }).then(res=>{
          this.logfv.info(JSON.stringify({
            title: '获取流程详细执行数据成功！',
            actionUrl:urljoin(this.baseApi,this.url.allExtensionTasks),
          }))
          this.dealWithTimeData(res.data.data)
        }).catch(err=>{
          this.logfv.info(JSON.stringify({
            title: '获取流程详细执行数据失败！',
            error:{
              message:err.message,
              stack:err.stack
            },
            props:{
              baseApi:this.baseApi,
              instanceId:this.instanceId,
              xmlId:this.xmlId,
              type:this.type,
              static:this.static,
              options:this.myOptions
            }
          }))
          console.error(err)
        }).finally(()=>{
          this.timeLine_loading=false
        })
      }
    },
    dealWithTimeData(timeRes){
      timeRes.sort((a,b)=>{
        return a.startTime - b.startTime
      }).forEach(f=>{
        utils.setTaskMaxDay(f.taskDefinitionKey,f.customTaskMaxDay+'天')
        if(f.realName){
          utils.setTaskRealName(f.taskDefinitionKey,f.realName)
        }
        this.taskData.push(f)
      })
      if(this.taskData.filter(c=>c.status==='待办').length===0){
        utils.setEndHighLight({stroke: '#5ac14a', fill: '#53D894'})
      }
    },
    bpmnLoadDone(){
      this.logfv.info(JSON.stringify({
        title:'流程图xml加载成功！',
        xmlUrl:this.xml,
        props:{
          baseApi:this.baseApi,
          instanceId:this.instanceId,
          xmlId:this.xmlId,
          type:this.type,
          static:this.static,
          options:this.myOptions
        }
      }))
      this.loading=false
      if(this.myOptions.timeLine){
        this.getTaskList()
      }
      this.bpmnViewer= this.$refs.bpmnObj.bpmnViewer
      window.bpmnViewer =  this.bpmnViewer
       setTimeout(()=>{
         this.$refs.cBTZoom.handleZoomReset()
       },10)
    },
    bpmnLoadError(err){
      this.logfv.info(JSON.stringify({
        title: '流程图加载失败！',
        error:{
          message:err.message,
          stack:err.stack
        },
        props:{
          baseApi:this.baseApi,
          instanceId:this.instanceId,
          xmlId:this.xmlId,
          type:this.type,
          static:this.static,
          options:this.myOptions
        }
      }))
      this.loading=false
    },
    clearWatermark(){
      setTimeout(()=>{
        if(document.querySelector('.bjs-powered-by')){
          document.querySelector('.bjs-powered-by').remove()
        }
      },300)
    },
    reload(){
      this.logfv.info(JSON.stringify({
        title:'执行了组件刷新方法！',
        xmlUrl:this.xml,
        props:{
          baseApi:this.baseApi,
          instanceId:this.instanceId,
          xmlId:this.xmlId,
          type:this.type,
          static:this.static,
          options:this.myOptions
        }
      }))
      this.$refs.bpmnObj.reload()
    },
    getAssetsImg:util.getAssetsImg
  },
  mounted() {
    this.clearWatermark()
    this.logfv.info(JSON.stringify({
      title:'工作流执行器挂载成功！',
      url:window.location.href,
      props:{
        baseApi:this.baseApi,
        instanceId:this.instanceId,
        xmlId:this.xmlId,
        type:this.type,
        static:this.static,
        options:this.myOptions
      }
    }))
  },
  created() {
    this.myOptions = Object.assign({
      zoom:true,
      timeLine:false,
      center:true,
      setline:false
    },this.options)
    this.logfv = new LogFv({
      reportUrl:this.logReportUrl,
      appId:'vue-bpmn-viewer',
      appName:'工作流执行器',
      objType:2,
      enable:this.log
    })
  }
}
</script>

<style scoped>
#bpmn-viewer{
  width: 100%;
  height: 100%;
  background: #F5F5F7;
  cursor: grab;
  position: relative;
}
#bpmn-viewer:active{
  cursor: grabbing;
}
.bpmn-viewer-canvas{
  /*width: 90%;*/
  /*height: 95%;*/
  /*margin: 5% auto 0 auto;*/
  width: 100%;
  height: 100%;
}
.no-bpmn{
  text-align: center;
}
.no-bpmn img{
  display: inline-block;
  margin-top: 100px;
  width: 700px;
}
.legend{
  position: absolute;
  bottom: 26px;
  left: 0;
}
.legend-ul{
  list-style: none;
  display: flex;
  justify-content: left;
  align-items: center;
  margin: 0 0 0 20px;
  padding: 0;
}
.legend-ul li{
  padding-right: 40px;
}
.legend-ul li span{
  display: inline-block;
  margin-left: 8px;
}
.legend-icon{
  display: inline-block;
  width: 18px;
  height: 18px;
  background: #333;
  vertical-align: sub;
}
.legend-none{
  background: #3296fa;
  border:1px solid #1a70c5;
  opacity: 0.8;
}
.legend-unExec{
  background: #f5842c;
  border:1px solid #d66106;
  opacity: 0.8;
}
.legend-exec{
  background:#5bc14b;
  border:1px solid #459838;
  opacity: 0.8;
}
.legend-back{
  background:#ff0000;
  border:1px solid #ad0000;
  opacity: 0.8;
}
</style>
<style>
.bpmn-viewer-canvas .ant-spin-container{
  width: 100% !important;
  height: 100% !important;
}
</style>
