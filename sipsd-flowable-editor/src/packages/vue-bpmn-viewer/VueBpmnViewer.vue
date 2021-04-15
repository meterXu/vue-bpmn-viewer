<template>
  <div id="bpmn-viewer">
    <a-spin :spinning="loading" tip="加载中..."  wrapperClassName="bpmn-viewer-canvas">
      <vue-bpmn v-if="(instanceId&&type===2)||(xmlId&&type===1)" ref="bpmnObj" :options="bpmnOptions" :url="xml" @shown="bpmnLoadDone" @loading="bpmnLoadDone" @error="bpmnLoadError"></vue-bpmn>
      <div v-else class="no-bpmn">
        <img :src="require('./assets/no-bpmn.svg')">
      </div>
    </a-spin>
    <BTLayout>
      <template slot="head">
        <slot></slot>
      </template>
      <template slot="right">
        <BTZoom v-show="(type===2&&instanceId||type===1&&xmlId)&&options.zoom&&!loading" :bpmnViewer="bpmnViewer" ref="cBTZoom"/>
        <slot name="timeLine" v-if="type===2&&instanceId&&options.timeLine&&!loading" v-bind:loading="timeLine_loading" v-bind:data="taskData.completeTask" v-bind:uData="taskData.upcomingTask" >
          <BTimeLine :loading="timeLine_loading" :data="taskData.completeTask" :uData="taskData.upcomingTask"/>
        </slot>
      </template>
    </BTLayout>
  </div>
</template>

<script>
import VueBpmn from '@dpark/vue-bpmn';
import bpmnThemeBlue from '@dpark/bpmn-theme-blue'
import {BTimeLine,utils,BTLayout,BTZoom} from '@dpark/vue-bpmn-controls'
import axios from 'axios'
export default {
  name: "VueBpmnViewer",
  props:{
    baseApi:{type:String,required:true},
    instanceId:{type:String},
    xmlId:{type:String},
    type:{type:Number,required: true},
    options:{type:Object,default:{
      zoom:true,
      timeLine:true
    }}
  },
  components:{
    VueBpmn,
    BTLayout,
    BTZoom,
    BTimeLine,
  },
  data(){
    return {
      loading:false,
      bpmnViewer:null,
      timeLine_loading:true,
      bpmnOptions:{
        additionalModules:[bpmnThemeBlue]
      },
      taskData:{
        completeTask:[],
        upcomingTask:[]
      },
      url:{
        xmlUrl:'rest/model/loadXmlByModelId/',
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
      if(this.type===1 && this.xmlId){
        this.instanceId = null
        this.options = Object.assign(this.options,{timeLine:false})
        utils.clearAllHighLight()
        return `${this.baseApi}${this.url.xmlUrl}${this.xmlId}`
      }else if(this.type===2 && this.instanceId){
        return `${this.baseApi}${this.url.instanceUrl}${this.instanceId}`
      }
    }
  },
  watch:{
    'taskData.completeTask':{
      handler:function (nv){
        utils.setTaskHighlight(nv.map(c=>c.taskDefinitionKey),{color:'#5BC14B',setline: false,shadow: false})
      }
    },
    'taskData.upcomingTask':{
      handler:function (nv){
        if(nv.length>0){
          utils.setTaskHighlight(nv.map(c=>c.taskDefinitionKey),{color:'#f5842c',setline: false,shadow: false})
        }
      }
    },
    xml(){
      this.loading = true
    }
  },
  methods:{
    getTaskList(){
      if(this.instanceId){
        this.taskData={
          completeTask:[],
          upcomingTask:[]
        }
        axios.get(this.baseApi+this.url.allExtensionTasks,{
          params:{
            initPageIndex:1,
            pageIndex:1,
            pageNum:1,
            pageSize:99,
            processInstanceId:this.instanceId
          }
        }).then(res=>{
          res.data.data.forEach(f=>{
            utils.setTaskMaxDay(f.taskDefinitionKey,f.customTaskMaxDay+'天')
            if(f.status==='已办'){
              this.taskData.completeTask.push(f)
            }else if(f.status==='待办'){
              this.taskData.upcomingTask.push(f)
            }
          })
          if(this.taskData.upcomingTask.length===0){
            utils.setEndHighLight()
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
      this.instanceId=null
      this.loading=false
    },
    clearWatermark(){
      setTimeout(()=>{
        if(document.querySelector('.bjs-powered-by')){
          document.querySelector('.bjs-powered-by').remove()
        }
      },300)
    }
  },
  mounted() {
    this.clearWatermark()
  }
}
</script>

<style scoped>
#bpmn-viewer{
  width: 100%;
  height: 100%;
  background: #F5F5F7;
  cursor: grab;
}
#bpmn-viewer:active{
  cursor: grabbing;
}
.bpmn-viewer-canvas{
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
</style>
<style>
.bpmn-viewer-canvas .ant-spin-container{
  width: 100% !important;
  height: 100% !important;
}
</style>
