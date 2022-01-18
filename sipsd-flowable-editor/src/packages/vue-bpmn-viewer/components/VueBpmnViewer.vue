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
              :bpmnViewer2="bpmnViewer2"
    >
      <slot></slot>
      <template v-slot:dialog>
        <vue-bpmn :viewer="myOptions.static" ref="bpmnObj2" :options="bpmnOptions" :url="xml"
                  @loading="bpmnLoading"
                  @loaded="bpmnLoadDone2"
                  @error="bpmnLoadError"
                  @click="handleClick"
                  @viewChange="handleViewChange"></vue-bpmn>
      </template>
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
import BTLayout from './controls/BTLayout.vue'
import urljoin from 'url-join';
import {LogFv} from '@dpark/logfv-web-vue'
import utils from './controls/lib/utils'
import zoomScroll from './controls/lib/zoomScroll'
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
    styl:{type:Object,default(){return {theme:null,stylMap: null}}},
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
      bpmnViewer2:null,
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
          default:bpmnThemeDefault,
          classic:bpmnThemeClassic
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
        this.bpmnOptions.additionalModules[0].utils.taskSyncHighLight(this.bpmnViewer._container,this.$refs.bpmnObj,nv,this.myOptions,this.bpmnOptions.additionalModules[0].colors)
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

      let g = document.getElementsByClassName('viewport')[0]
      let style = create('style',{
        type:"text/css"
      })
      style.innerHTML=`
     .clock-spin{
    animation: spin 1s infinite;
    animation-timing-function:linear;
}
    .d-userTask{
    cursor: pointer;
}
.highlight-custom-path{
    stroke-dasharray:6;
    animation: complete 2s infinite;
    animation-timing-function:linear;
}
.d-userTask-title{
    text-align: left;
    font-size: 12px;
    color: #fff;
    padding-left: 23px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    height: 100%;
}
.d-subProcess-title{
    text-align: left;
    font-size: 12px;
    color: #fff;
    padding-left: 4px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    height: 100%;
}
.d-userTask-content{
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
.d-userTask-content ul{
    padding: 0;
    margin: 0;
    display: inline-block;
    list-style: none;
    padding-right: 6px;
}
.d-userTask-content ul li{
    height: 24px;
    line-height: 24px;
    color: #cdcdcd;
}
.d-userTask-content li{
    display: flex;
    align-items: center;
    justify-content: left;
}
.d-userTask-content span{
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}
.d-con-user-icon,.d-con-clock-icon{
    display: inline-block;
    background: url("data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyB3aWR0aD0iNDgiIGhlaWdodD0iNDgiIHZpZXdCb3g9IjAgMCA0OCA0OCIgZmlsbD0ibm9uZSIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj4KICAgIDxwYXRoIGQ9Ik01MTMuNDU3MjMxIDQxLjM1Mzg0NmMtMTMzLjM3Nzk2OSAyLjgzNzY2Mi0yNDQuOTk5ODc3IDQ5LjE4OTQxNS0zMzIuOTczMjkyIDEzNy4xNjA4NjJDOTIuNTEyNDkyIDI2Ni40ODgxMjMgNDYuMTYwNzM4IDM3OC4xMTAwMzEgNDMuMzIzMDc3IDUxMS40ODhjMi44Mzc2NjIgMTMzLjM3Nzk2OSA0OS4xODk0MTUgMjQ0LjA1NDY0NiAxMzcuMTYwODYyIDMzMi4wMjYwOTJDMjY4LjQ1NzM1NCA5MzEuNDg3NTA4IDM4MC4wNzkyNjIgOTc3LjgzOTI2MiA1MTMuNDU3MjMxIDk4MC42NzY5MjNjMTMzLjM3Nzk2OS0yLjgzNzY2MiAyNDQuMDU0NjQ2LTQ5LjE4OTQxNSAzMzIuMDI2MDkyLTEzNy4xNjA4NjJTOTc5LjgwODQ5MiA2NDQuODY3OTM4IDk4Mi42NDYxNTQgNTExLjQ4OGMtMi44Mzc2NjItMTMzLjM3Nzk2OS00OS4xODk0MTUtMjQ0LjA1NDY0Ni0xMzcuMTYwODYyLTMzMi4wMjYwOTJDNzU3LjUxMTg3NyA5MC41NDMyNjIgNjQ2LjgzNzE2OSA0NC4xOTE1MDggNTEzLjQ1NzIzMSA0MS4zNTM4NDZMNTEzLjQ1NzIzMSA0MS4zNTM4NDZ6TTgxMS40MzEzODUgNzk5LjA1NjczOGMtMjMuNjUwNDYyLTExLjM1MjYxNS00OS4xODk0MTUtMjMuNjQ4NDkyLTc0LjczMDMzOC0yNi40ODYxNTQtNjcuMTYwNjE1LTUuNjc1MzIzLTEwOC43ODIyNzctMTYuMDgwNzM4LTEyNC44NjMwMTUtMzIuMTYxNDc3LTE1LjEzNzQ3Ny0xNi4wODI3MDgtMjEuNzU4MDMxLTM4Ljc4NC0xOC45MTg0LTY4LjEwOTc4NSAwLjk0NTIzMS0xNy45NzEyIDUuNjc1MzIzLTI4LjM3NjYxNSAxNi4wODA3MzgtMzMuMTA4Njc3IDEwLjQwNTQxNS00LjcyODEyMyAyMS43NTYwNjItMzEuMjE2MjQ2IDMzLjEwODY3Ny03OC41MTMyMzEgMy43ODQ4NjItMTQuMTg4MzA4IDcuNTY3NzU0LTI1LjU0MDkyMyAxMi4yOTc4NDYtMzQuMDUzOTA4IDQuNzI4MTIzLTguNTEyOTg1IDEyLjI5NTg3Ny0yNi40ODYxNTQgMjEuNzU2MDYyLTU1LjgwOTk2OSA0LjczMDA5Mi0yMC44MTA4MzEgNC43MzAwOTItMzMuMTA4Njc3LTAuOTQ1MjMxLTM1Ljk0NDM2OS01LjY3NTMyMy0yLjgzNzY2Mi05LjQ2MDE4NS0zLjc4NDg2Mi0xMC40MDM0NDYtMi44Mzc2NjJMNjY5LjUzODQ2MiA0MTEuMjE4NzA4YzIuODM3NjYyLTEzLjI0MzA3NyA0LjcyODEyMy0yOS4zMjM4MTUgNi42MjA1NTQtNDcuMjk2OTg1IDUuNjc3MjkyLTQ3LjI5Njk4NS0zLjc4Mjg5Mi04Ny45NzM0MTUtMjguMzc4NTg1LTEyMi4wMjczMjMtMjQuNTkzNzIzLTM0Ljk5OTEzOC02OS45OTgyNzctNTIuOTcyMzA4LTEzNy4xNjA4NjItNTQuODY0NzM4LTU5LjU5NDgzMSAwLjk0NTIzMS05MC44MTEwNzcgMjQuNTk1NjkyLTEyNS44MTAyMTUgNTMuOTE5NTA4LTM4Ljc4NCAzMC4yNzEwMTUtNTIuMDI3MDc3IDcwLjk0NTQ3Ny00MS42MjE2NjIgMTIyLjk3MjU1NCA3LjU2Nzc1NCAzOC43ODQgMTguOTE4NCA3MC45NDU0NzcgMTEuMzUwNjQ2IDY4LjEwNzgxNS0wLjk0NTIzMS0wLjk0NTIzMS00LjcyODEyMyAwLTkuNDU4MjE1IDIuODM3NjYyLTQuNzMwMDkyIDIuODM3NjYyLTUuNjc1MzIzIDE1LjEzNTUwOC0xLjg5MjQzMSAzNS45NDQzNjkgMTAuNDA1NDE1IDI1LjU0MDkyMyAxNy45NzMxNjkgNDMuNTE0MDkyIDIzLjY0ODQ5MiA1Mi45NzQyNzcgNS42NzUzMjMgOS40NTgyMTUgOS40NTgyMTUgMjEuNzU2MDYyIDEyLjI5Nzg0NiAzNi44ODk2IDkuNDYwMTg1IDQ2LjM1MTc1NCAxOC45MTg0IDcxLjg5MjY3NyAyOS4zMjM4MTUgNzYuNjIwOCAxMC40MDU0MTUgNC43MzAwOTIgMTcuMDI1OTY5IDE3LjAyNzkzOCAyMC44MTA4MzEgMzQuOTk5MTM4IDQuNzMwMDkyIDI5LjMyNTc4NS0wLjk0NTIzMSA1Mi4wMjcwNzctMTcuOTczMTY5IDY4LjEwOTc4NS0xNy4wMjU5NjkgMTYuMDgwNzM4LTU3LjcwMjQgMjYuNDg2MTU0LTEyMi4wMjczMjMgMzIuMTYxNDc3LTIzLjY0ODQ5MiAyLjgzNzY2Mi01Mi4wMjcwNzcgMTQuMTg4MzA4LTc0LjcyODM2OSAyNS41NDA5MjMtNzIuODM3OTA4LTc1LjY3NTU2OS0xMTUuNDA0OC0xNzUuOTQ2ODMxLTExNi4zNTItMjg2LjYyMTUzOCAyLjgzNzY2Mi0xMTcuMjk3MjMxIDQzLjUxNDA5Mi0yMTQuNzI4ODYyIDEyMi4wMjczMjMtMjkzLjI0NDA2MnMxNzUuOTQ2ODMxLTExOS4xODc2OTIgMjkzLjI0NDA2Mi0xMjIuMDI3MzIzYzExNy4yOTUyNjIgMi44Mzc2NjIgMjE0LjcyODg2MiA0My41MTQwOTIgMjkyLjI5Njg2MiAxMjIuMDI3MzIzIDc4LjUxMzIzMSA3OC41MTMyMzEgMTE5LjE4OTY2MiAxNzUuOTQ2ODMxIDEyMi4wMjczMjMgMjkzLjI0NDA2MkM5MjYuODM0MjE1IDYyNC4wNTUxMzggODg0LjI2NzMyMyA3MjQuMzI0NDMxIDgxMS40MzEzODUgNzk5LjA1NjczOHoiIHRyYW5zZm9ybT0ibWF0cml4KDAuMDUgMCAwIDAuMDUgLTIgLTEpIiBzdHlsZT0iZmlsbDogcmdiKDgzLCAxOTUsIDIxNik7Ij48L3BhdGg+Cjwvc3ZnPgo=") no-repeat center center;
    background-size: contain;
    width: 16px;
    height: 16px;
    margin: 0 4px;
    }
.d-con-clock-icon{
    background: url("data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyB3aWR0aD0iNDgiIGhlaWdodD0iNDgiIHZpZXdCb3g9IjAgMCA0OCA0OCIgZmlsbD0ibm9uZSIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj4KICAgIDxnIHRyYW5zZm9ybT0ibWF0cml4KDAuMDQ1IDAgMCAwLjA0NSAxIDEpIj4KICAgICAgICA8cGF0aCBkPSJNNTEyIDUxMm0tNTEyIDBhNTEyIDUxMiAwIDEgMCAxMDI0IDAgNTEyIDUxMiAwIDEgMC0xMDI0IDBaIiBzdHlsZT0iZmlsbDogcmdiKDI1NSwgMjE1LCAyMTUpOyI+PC9wYXRoPgogICAgICAgIDxwYXRoIGQ9Ik01MTIgNTEybS0zOTguMjIyMjIyIDBhMzk4LjIyMjIyMiAzOTguMjIyMjIyIDAgMSAwIDc5Ni40NDQ0NDQgMCAzOTguMjIyMjIyIDM5OC4yMjIyMjIgMCAxIDAtNzk2LjQ0NDQ0NCAwWiIgc3R5bGU9ImZpbGw6IHJnYigyNDgsIDEyOCwgOTgpOyI+PC9wYXRoPgogICAgICAgIDxwYXRoIGQ9Ik00MjYuNjY2NjY3IDI4NC40NDQ0NDR2MzEyLjg4ODg4OWgyNTZ2LTg1LjMzMzMzM2gtMTcwLjY2NjY2N3YtMjI3LjU1NTU1NnoiIHN0eWxlPSJmaWxsOiByZ2IoMjU1LCAyNTUsIDI1NSk7Ij48L3BhdGg+CiAgICA8L2c+Cjwvc3ZnPgo=") no-repeat center center;
    background-size: contain;
}
.d-con-user-icon:after,.d-con-clock-icon:after{
    content: '';
    width: 14px;
    height: 14px;
    display: inline-block;
}
     `
      append(g,style)

      this.showBpmn = true
      this.bpmnViewer= this.$refs.bpmnObj.bpmnViewer
      window.bpmnViewer =  this.bpmnViewer
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
    async bpmnLoadDone2(){
      utils.log({
        title:'流程图xml加载成功！',
        xmlUrl:this.xml,
      },this)
      let myOptions2 = Object.assign({},this.myOptions,{track:false,focus:false})
      // myOptions2.track = false
      // myOptions2.focus = false
      utils.clearWatermark()
      this.showBpmn = true
      this.bpmnViewer2= this.$refs.bpmnObj2.bpmnViewer
      window.bpmnViewer2 =  this.bpmnViewer2
      utils.setView(this.bpmnViewer2,myOptions2)
      //await this.getTaskList()
      if(this.taskData&&this.taskData.length>0){
        let lastData = this.taskData[this.taskData.length-1]
        if(lastData.status!=='已办'&&this.myOptions.focus){
          this.selectKey = lastData.taskDefinitionKey
          utils.setView(this.bpmnViewer2,myOptions2,this.selectKey)
        }
        this.bpmnOptions.additionalModules[0].utils.taskSyncHighLight(this.bpmnViewer2._container,this.$refs.bpmnObj2,this.taskData,myOptions2,this.bpmnOptions.additionalModules[0].colors)
      }
      //this.$emit('loaded')
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
