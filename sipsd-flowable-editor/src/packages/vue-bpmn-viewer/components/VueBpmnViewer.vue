<template>
  <div class="dpark-bpmn-viewer">
    <div class="bpmn-viewer-canvas">
      <vue-bpmn :viewer="myOptions.static" ref="bpmnObj" :options="bpmnOptions" :url="xml"
                @loading="bpmnLoading"
                @loaded="bpmnLoadDone"
                @error="bpmnLoadError"
                @click="handleClick"
                @viewChange="handleViewChange"></vue-bpmn>
    </div>
    <BTLayout :showBpmn="showBpmn" :myOptions="myOptions" :bpmnViewer="bpmnViewer" :selectKey="selectKey" :taskData="taskData"></BTLayout>
  </div>
</template>

<script>
import VueBpmn from './bpmn/VueBpmn.vue';
import bpmnThemeDefault from './styl/default/index.js'
import BTLayout from './controls/BTLayout'
import urljoin from 'url-join';
import {LogFv} from '@dpark/logfv-web-vue'
import utils from './controls/lib/utils'
import zoomScroll from './controls/lib/zoomScroll'
export default {
  name: "VueBpmnViewer",
  props:{
    baseApi:{type:String,required:false},
    instanceId:{type:String},
    xmlId:{type:String},
    type:{type:Number,required: false},
    source:{type:String},
    timeData:{type:Array},
    options:{type:Object},
    styl:{type:Object,default(){return {theme:null,stylMap:null}}},
    logReportUrl:{type:String,default:'http://58.210.9.133/iplatform/logfv-server/logfv/web/upload'}
  },
  components:{
    VueBpmn,
    BTLayout
  },
  data(){
    return {
      selectKey:null,
      logfv:null,
      showBpmn:false,
      bpmnViewer:null,
      bpmnOptions:{
        additionalModules:[]
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
    myOptions(){
      let _option =  Object.assign({
        zoom:true,
        timeLine:false,
        fit:false,
        setline:false,
        scrollZoom:false,
        static:false,
        log:false,
        track:false,
        trackFocus:false
      },this.options)
      if(_option.scrollZoom){
        this.bpmnOptions.additionalModules=[
          this.myStyl.stylMap[this.myStyl.theme],
          zoomScroll
        ]
      }else{
        this.bpmnOptions.additionalModules=[
          this.myStyl.stylMap[this.myStyl.theme],
        ]
      }
      return _option;
    },
    myStyl(){
      let _styl =  {
        theme:'default',
        stylMap:{
          default:bpmnThemeDefault
        }
      }
      _styl.theme = this.styl.theme||_styl.theme
      if(this.styl.stylMap){
        Object.keys(this.styl.stylMap).forEach(key=>{
          _styl.stylMap[key]=Object.assign(_styl.stylMap[key]||{},this.styl.stylMap[key])
        })
      }
      return _styl
    },
    xml(){
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
    }
  },
  watch:{
    taskData:{
      handler:function (nv){
        utils.taskSyncHighLight(this.bpmnViewer._container,this.$refs.bpmnObj,nv,this.myOptions)
      }
    }
  },
  methods:{
    async getTaskList(){
      if(this.timeData){
        this.taskData = utils.dealWithTimeData(this.bpmnViewer._container,this.timeData)
      }else{
        if(this.type===2){
          this.taskData = await this.getTimeData()
        }else{
          this.taskData = utils.dealWithTimeData(this.bpmnViewer._container,[])
        }
      }
    },
    getTimeData(){
      return new Promise((resolve,reject)=>{
        if(this.instanceId){
          utils.getTimeData(urljoin(this.baseApi,this.url.allExtensionTasks),this.instanceId).then(res=>{
            this.logfv.info(JSON.stringify({
              title: '获取流程详细执行数据成功！',
              actionUrl:urljoin(this.baseApi,this.url.allExtensionTasks),
            }))
            this.taskData = utils.dealWithTimeData(this.bpmnViewer._container,res.data.data)
            resolve(this.taskData)
          }).catch(err=>{
            utils.error({
              title: '获取流程详细执行数据失败！',
              error:{
                message:err.message,
                stack:err.stack
              },
            },this)
            console.error(err)
            reject(err)
          })
        }
      })
    },
    bpmnLoading(){
      this.$emit('loading')
    },
    async bpmnLoadDone(){
      utils.log({
        title:'流程图xml加载成功！',
        xmlUrl:this.xml,
      },this)
      utils.clearWatermark()
      this.showBpmn = true
      this.bpmnViewer= this.$refs.bpmnObj.bpmnViewer
      window.bpmnViewer =  this.bpmnViewer
      utils.setView(this.bpmnViewer,this.myOptions)
      await this.getTaskList()
      if(this.taskData&&this.taskData.length>0){
        let lastData = this.taskData[this.taskData.length-1]
        if(lastData.status!=='已办'&&this.myOptions.focus){
          this.selectKey = lastData.taskDefinitionKey
          utils.setView(this.bpmnViewer,this.options,this.selectKey)

        }

      }
      this.$emit('loaded')
    },
    bpmnLoadError(err){
      utils.error({ title: '流程图加载失败！',
        error:{
          message:err.message,
          stack:err.stack
        },},this)
      this.$emit('loadError',err)
    },
    reload(){
      utils.log({
        title:'执行了组件刷新方法！',
        xmlUrl:this.xml,
      },this)
      this.$nextTick(()=>{
        if(this.$refs.bpmnObj){
          this.$refs.bpmnObj.reload()
        }
      })
    },
    handleClick(obj){
      if(this.taskData){
        let _task=this.taskData.find(c=>c.taskDefinitionKey===obj.shape.id)
        if(_task){
          obj.taskData=_task
        }else{
          obj.taskData = null
        }
      }
      this.$emit('click',obj)
    },
    handleViewChange(event){
      this.$emit('viewChange',event)
    },
    itemClick(item){
      this.$emit('timeItemClick',item)
    }
  },
  mounted() {
    utils.log({
      title:'工作流执行器挂载成功！',
      url:window.location.href,
    },this)
  },
  created() {
    this.logfv = new LogFv({
      reportUrl:this.logReportUrl,
      appId:'vue-bpmn-viewer',
      appName:'工作流执行器',
      objType:2,
      enable:this.myOptions.log
    })
  }
}
</script>

<style scoped>
.dpark-bpmn-viewer{
  width: 100%;
  height: 100%;
  background: #F5F5F7;
  cursor: grab;
  position: relative;
}
.dpark-bbpmn-viewer:active{
  cursor: grabbing;
}
.bpmn-viewer-canvas{
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
<style>
.dpark-bbpmn-viewer .bpmn-viewer-canvas{
  width: 100% !important;
  height: 100% !important;
}
.dpark-bpmn-viewer .smooth .viewport{
  transition: transform 0.8s;
}
</style>
