<template>
  <div class="dpark-bpmn-viewer" :data-theme="myStyl.theme">
    <div class="bpmn-viewer-canvas">
      <vue-bpmn :viewer="myOptions.static" ref="bpmnObj" :options="bpmnOptions" :url="xml"
                @loading="bpmnLoading"
                @loaded="bpmnLoadDone"
                @error="bpmnLoadError"
                @click="handleClick"
                @viewChange="handleViewChange"></vue-bpmn>
    </div>
    <BTLayout :showBpmn="showBpmn" :myOptions="myOptions"
              :bpmnViewer="bpmnViewer" :selectKey="selectKey"
              :taskData="taskData" :bpmnOptions="bpmnOptions"
    >
      <slot></slot>
      <template v-slot:time="slotProps">
        <slot name="time" v-bind:item="slotProps.item">
        </slot>
      </template>
    </BTLayout>
  </div>
</template>

<script>
import VueBpmn from './bpmn/VueBpmn.vue';
import bpmnThemeDefault from './styl/default/index.js'
import bpmnThemeClassic from './styl/classic/index.js'
import bpmnThemeDark from './styl/dark/index'
import BTLayout from './controls/BTLayout.vue'
import urljoin from 'url-join';
import utils from './controls/lib/utils'
import zoomScroll from './controls/lib/zoomScroll'
import TouchModule from 'diagram-js/lib/navigation/touch'
import {append, create} from "tiny-svg";
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
    styl:{type:Object,default(){return {theme:null,stylMap: null}}}
  },
  components:{
    VueBpmn,
    BTLayout
  },
  data(){
    return {
      selectKey:null,
      showBpmn:false,
      bpmnViewer:null,
      bpmnOptions:{
        additionalModules:[]
      },
      taskData:[],
      httpData:[],
      url:{
        xmlUrl:'/rest/model/loadXmlByModelId/',
        instanceUrl:'rest/formdetail/getprocessXml/',
        allExtensionTasks:'rest/extension/task/get-all-extension-tasks',
        exportUrl:'app/rest/models/[]/bpmn20?version=1617092632878',
        restModels:'app/rest/models/',
        httpUrl:'rest/task/get-http-tasks'
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
          zoomScroll,
          TouchModule
        ]
      }else{
        this.bpmnOptions.additionalModules=[
          this.myStyl.stylMap[this.myStyl.theme],
          TouchModule
        ]
      }
      return _option;
    },
    myStyl(){
      let _styl =  {
        theme:'default',
        stylMap:{
          default:bpmnThemeDefault,
          classic:bpmnThemeClassic,
          dark:bpmnThemeDark,
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
  methods:{
    async getTaskList(){
      try {
        if(this.timeData){
          this.taskData = utils.dealWithTimeData(this.bpmnViewer._container,this.timeData)
        }else{
          if(this.type===2){
            this.taskData = await this.getTimeData()
            this.httpData = await this.getHttpData()
          }else{
            this.taskData = utils.dealWithTimeData(this.bpmnViewer._container,[])
          }
        }
      }catch (err){
        console.error(err)
      }
      this.bpmnOptions.additionalModules[0].utils.taskSyncHighLight(this.bpmnViewer._container,this.$refs.bpmnObj,[...this.taskData,...this.httpData],this.myOptions,this.bpmnOptions.additionalModules[0].colors)
    },
    getTimeData(){
      return new Promise((resolve,reject)=>{
        if(this.instanceId){
          utils.getTimeData(urljoin(this.baseApi,this.url.allExtensionTasks),this.instanceId).then(res=>{
            this.taskData = utils.dealWithTimeData(this.bpmnViewer._container,res.data.data)
            resolve(this.taskData)
          }).catch(err=>{
            reject(err)
          })
        }
      })
    },
    getHttpData(){
      return new Promise((resolve,reject)=>{
        if(this.instanceId){
          utils.getHttpData(urljoin(this.baseApi,this.url.httpUrl),this.instanceId).then(res=>{
            let data = res.data.map(c=>{
              return {
                status:'已办',
                taskDefinitionKey:c.activityId
              }
            })
            resolve(data)
          }).catch(err=>{
            reject(err)
          })
        }
      })
    },
    bpmnLoading(){
      this.$emit('loading')
    },
    async bpmnLoadDone(){
      utils.clearWatermark()
      this.showBpmn = true
      this.bpmnViewer= this.$refs.bpmnObj.bpmnViewer
      window.bpmnViewer =  this.bpmnViewer
      let g = this.bpmnViewer._container.getElementsByClassName('viewport')[0]
      append(g,this.myStyl.stylMap[this.myStyl.theme].style)
      utils.setView(this.bpmnViewer,this.myOptions)
      await this.getTaskList()
      if(this.taskData&&this.taskData.length>0){
        let lastData = this.taskData[this.taskData.length-1]
        if(lastData.status!=='已办'&&this.myOptions.focus){
          this.selectKey = lastData.taskDefinitionKey
          utils.setView(this.bpmnViewer,this.myOptions,this.selectKey)
        }
      }
      this.$emit('loaded')
    },
    bpmnLoadError(err){
      this.$emit('loadError',err)
    },
    reload(){
      this.$nextTick(()=>{
        if(this.$refs.bpmnObj){
          this.$refs.bpmnObj.reload()
        }
      })
    },
    setTaskHighlight(ids,options){
      this.bpmnOptions.additionalModules[0].utils.setTaskHighlight(this.bpmnViewer._container,ids,options)
    },
    clearHighLight(ids){
      this.bpmnOptions.additionalModules[0].utils.clearHighLight(this.bpmnViewer._container,ids)
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
  }
}
</script>

<style scoped>
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
