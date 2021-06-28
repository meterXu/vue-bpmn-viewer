<template>
  <div id="bpmn-viewer">
    <a-spin :spinning="(type===1&&loading)||(type===2&&timeLine_loading)" tip="加载中..."  wrapperClassName="bpmn-viewer-canvas">
      <vue-bpmn v-if="(instanceId&&type===2)||(xmlId&&type===1)" ref="bpmnObj" :options="bpmnOptions" :url="xml" @shown="bpmnLoadDone" @loading="bpmnLoadDone" @error="bpmnLoadError"></vue-bpmn>
      <div v-else class="no-bpmn">
        <img :src="getAssetsImg(require('./assets/no-bpmn.svg'))">
      </div>
    </a-spin>
    <BTLayout>
      <template slot="head">
        <slot></slot>
      </template>
      <template slot="right">
        <BTZoom v-show="(type===2&&instanceId||type===1&&xmlId)&&options.zoom&&!loading" :bpmnViewer="bpmnViewer" ref="cBTZoom"/>
        <slot name="timeLine" v-if="type===2&&instanceId&&options.timeLine&&!loading" v-bind:loading="timeLine_loading" v-bind:data="taskData">
          <BTimeLine :loading="timeLine_loading" :data="taskData"/>
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
      taskData:[],
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
      utils.clearAllHighLight()
      if(this.type===1 && this.xmlId){
        this.instanceId = null
        this.options = Object.assign(this.options,{timeLine:false})
        return `${this.baseApi}${this.url.xmlUrl}${this.xmlId}`
      }else if(this.type===2 && this.instanceId){
        return `${this.baseApi}${this.url.instanceUrl}${this.instanceId}`
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
                utils.setTaskHighlight([c.taskDefinitionKey],{color:'#ff0000',setline: false,shadow: true})
              } else{
                utils.setTaskHighlight([c.taskDefinitionKey],{color:'#5BC14B',setline: false,shadow: false})
              }
            }break;
            case '待办':{
              utils.setTaskHighlight([c.taskDefinitionKey],{color:'#f5842c',setline: true,shadow:false })
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
        axios.get(this.baseApi+this.url.allExtensionTasks,{
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
  position: relative;
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
