<template>
  <div id="bpmn-viewer">
    <Spin :spinning="(type===1&&loading)||(type===2&&timeLine_loading)" tip="加载中..."  wrapperClassName="bpmn-viewer-canvas">
      <vue-bpmn :viewer="static" v-if="(instanceId&&type===2)||(xmlId&&type===1)" ref="bpmnObj" :options="bpmnOptions" :url="xml" @shown="bpmnLoadDone" @loading="bpmnLoadDone" @error="bpmnLoadError"></vue-bpmn>
      <div v-else class="no-bpmn">
        <img :src="getAssetsImg(require('./assets/no-bpmn.svg'))">
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
        <BTZoom v-show="(type===2&&instanceId||type===1&&xmlId)&&options.zoom&&!loading" :center="options.center" :bpmnViewer="bpmnViewer" ref="cBTZoom"/>
        <slot name="timeLine" v-if="type===2&&instanceId&&options.timeLine&&!loading" v-bind:loading="timeLine_loading" v-bind:data="taskData">
          <BTimeLine :loading="timeLine_loading" :data="taskData"/>
        </slot>
      </template>
    </BTLayout>
  </div>
</template>

<script>
import 'ant-design-vue/dist/antd.css'
import VueBpmn from '@dpark/vue-bpmn';
import bpmnThemeBlue from '@dpark/bpmn-theme-blue'
import {BTimeLine,utils,BTLayout,BTZoom} from '@dpark/vue-bpmn-controls'
// import bpmnThemeBlue from '../bpmn-theme-blue'
// import {BTimeLine,utils,BTLayout,BTZoom} from '../vue-bpmn-controls'
import {util} from '@dpark/s2-utils'
import {Spin} from "ant-design-vue";
import axios from 'axios'
import urljoin from 'url-join';
import {LogFv} from '@dpark/logfv-web-vue'
export default {
  name: "VueBpmnViewer",
  props:{
    baseApi:{type:String,required:true},
    instanceId:{type:String},
    xmlId:{type:String},
    type:{type:Number,required: true},
    static:{type:Boolean,default: false},
    options:{type:Object,default(){
      return {
      zoom:true,
      timeLine:true,
      center:true,
      setline:false
    }
    }},
    log:{type:Boolean,default:null},
    logReportUrl:{type:String,default:'http://192.168.126.25/logfv-server/logfv/web/upload'}
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
      }
    }
  },
  computed:{
    xml(){
      this.clearWatermark()
      utils.clearAllHighLight()
      if(this.baseApi){
        if(this.type===1 && this.xmlId){
        this.options = Object.assign(this.options,{timeLine:false})
        return urljoin(this.baseApi,this.url.xmlUrl+this.xmlId)
      }else if(this.type===2 && this.instanceId){
        return urljoin(this.baseApi,this.url.instanceUrl+this.instanceId)
      }
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
                utils.setTaskHighlight([c.taskDefinitionKey],{color:'#ff0000',setline: false,shadow: true,type:3})
              } else{
                utils.setTaskHighlight([c.taskDefinitionKey],{color:'#5BC14B',setline: false,shadow: false,type:2})
              }
            }break;
            case '待办':{
              utils.setTaskHighlight([c.taskDefinitionKey],{color:'#f5842c',setline: this.options.setline,shadow:true,type:1 })
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
      if(this.instanceId){
        this.taskData=[]
        axios.get(
          urljoin(this.baseApi,this.url.allExtensionTasks)
        ,{
          params:{
            initPageIndex:1,
            pageIndex:1,
            pageNum:1,
            pageSize:99,
            processInstanceId:this.instanceId
          }
        }).then(res=>{
          res.data.data.sort((a,b)=>{
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
        }).catch(err=>{
          console.error(err)
        }).finally(()=>{
          this.timeLine_loading=false
        })
      }
    },
    bpmnLoadDone(){
      this.loading=false
      if(this.options.timeLine){
        this.getTaskList()
      }
      this.bpmnViewer= this.$refs.bpmnObj.bpmnViewer
      window.bpmnViewer =  this.bpmnViewer
       setTimeout(()=>{
         this.$refs.cBTZoom.handleZoomReset()
       },10)
    },
    bpmnLoadError(){
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
      this.$refs.bpmnObj.reload()
    },
    getAssetsImg:util.getAssetsImg
  },
  mounted() {
    this.clearWatermark()
    LogFv.info(JSON.stringify({
      title:'工作流执行器被使用',
      url:window.location.href,
      props:{
        baseApi:this.baseApi,
        instanceId:this.instanceId,
        xmlId:this.xmlId,
        type:this.type,
        static:this.static,
        options:this.options
      }
    }))
  },
  created() {
    this.log = (this.log!==null?this.log:(process.env.NODE_ENV==='production'))
    LogFv.initConfig({
      reportUrl:this.logReportUrl,
      appId:'vue-bpmn-viewer',
      appName:'工作流执行器',
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
