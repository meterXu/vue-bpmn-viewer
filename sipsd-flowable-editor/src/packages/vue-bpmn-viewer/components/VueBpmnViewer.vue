<template>
  <div class="dpark-bpmn-viewer">
    <div class="bpmn-viewer-canvas">
      <vue-bpmn :viewer="myOptions.static" ref="bpmnObj" :options="bpmnOptions" :url="xml"
                @loading="bpmnLoading"
                @loaded="bpmnLoadDone"
                @error="bpmnLoadError"
                @click="handleClick"
                @viewChange="handleViewChange"></vue-bpmn>
      <div v-if="!showBpmn" class="no-bpmn"></div>
      <div v-if="showBpmn" class="legend">
        <ul class="legend-ul">
          <li v-for="item in legend" :key="item.class">
            <i :class="['legend-icon',item.class]"></i>
            <span>{{item.text}}</span>
          </li>
        </ul>
      </div>
    </div>
    <BTLayout>
      <template v-slot:head>
        <slot>
        </slot>
      </template>
      <template v-slot:right>
        <BTZoom ref="cBTZoom" :options="myOptions" :bpmnViewer="bpmnViewer" :selectKey="selectKey" @zoomReset="zoomReset"/>
        <BTimeLine ref="cBTimeLine" :options="myOptions" :timeData="taskData" :bpmnViewer="bpmnViewer"
                   @itemClick="itemClick"
        >
          <template v-slot="slotProps">
            <slot name="time" v-bind:item="slotProps.item"></slot>
          </template>
        </BTimeLine>
      </template>
    </BTLayout>
  </div>
</template>

<script>
import VueBpmn from './bpmn/VueBpmn.vue';
import bpmnThemeBlue from './blue/index.js'
import {BTimeLine,utils,BTLayout,BTZoom} from './controls/index.js'
import urljoin from 'url-join';
import {LogFv} from '@dpark/logfv-web-vue'
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
    logReportUrl:{type:String,default:'http://58.210.9.133/iplatform/logfv-server/logfv/web/upload'}
  },
  components:{
    VueBpmn,
    BTLayout,
    BTZoom,
    BTimeLine
  },
  data(){
    return {
      selectKey:null,
      logfv:null,
      showBpmn:false,
      bpmnViewer:null,
      bpmnOptions:{
        additionalModules:[
            bpmnThemeBlue
        ]
      },
      colors:{
        blue:['#aaaaaa','#53c3d8','#ffd7d7','#f88062','#2c3e50','#bfbfbf','#2c3e50','#5BC14B','#53D894','#f5842c','#ff0000','#ececec','#fff','#ccc','#000','#9399B2','#B0B8D5','#81869D','#8f8f8f','#aaa','#444'],
        classic:['#aaaaaa','#53c3d8','#ffd7d7','#f88062','#2c3e50','#bfbfbf','#2c3e50','#5BC14B','#53D894','#f5842c','#ff0000','#ececec','#fff','#ccc','#000','#9399B2','#B0B8D5','#81869D','#8f8f8f','#aaa','#444']
      },
      taskData:[],
      url:{
        xmlUrl:'/rest/model/loadXmlByModelId/',
        instanceUrl:'rest/formdetail/getprocessXml/',
        allExtensionTasks:'rest/extension/task/get-all-extension-tasks',
        exportUrl:'app/rest/models/[]/bpmn20?version=1617092632878',
        restModels:'app/rest/models/'
      },
      legend:[
        {text:"未执行",class:"legend-none"},
        {text:"待审批",class:"legend-unExec"},
        {text:"已审批",class:"legend-exec"},
        {text:"被驳回",class:"legend-back"}
      ]
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
      if(!_option.colors){
        _option.colors = this.colors[_option.theme]
      }
      if(_option.scrollZoom){
        this.bpmnOptions.additionalModules=[
          bpmnThemeBlue,
          zoomScroll
        ]
      }else{
        this.bpmnOptions.additionalModules=[
          bpmnThemeBlue
        ]
      }
      return _option;
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
      if(this.taskData&&this.taskData){
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
    },
    zoomReset(){
      this.$refs.cBTimeLine.scrollMove()
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
.no-bpmn{
  height: 100%;
  width: 100%;
  text-align: center;
  position: absolute;
  top: 0;
  left: 0;
  background-color: #fff;
  background-image: url("data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyB3aWR0aD0iMzcxcHgiIGhlaWdodD0iMjAwcHgiIHZpZXdCb3g9IjAgMCAzNzEgMjAwIiB2ZXJzaW9uPSIxLjEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPgogICAgPHRpdGxlPue8lue7hCA0PC90aXRsZT4KICAgIDxkZWZzPgogICAgICAgIDxsaW5lYXJHcmFkaWVudCB4MT0iLTQ3LjAxOTAxMjQlIiB5MT0iLTE4NS45NjM4ODglIiB4Mj0iNjguODY4ODQ2NSUiIHkyPSIxMDAlIiBpZD0ibGluZWFyR3JhZGllbnQtMSI+CiAgICAgICAgICAgIDxzdG9wIHN0b3AtY29sb3I9IiNEMkQyRDIiIG9mZnNldD0iMCUiPjwvc3RvcD4KICAgICAgICAgICAgPHN0b3Agc3RvcC1jb2xvcj0iI0QyRDJEMiIgc3RvcC1vcGFjaXR5PSIwIiBvZmZzZXQ9IjEwMCUiPjwvc3RvcD4KICAgICAgICA8L2xpbmVhckdyYWRpZW50PgogICAgICAgIDxsaW5lYXJHcmFkaWVudCB4MT0iMTE4Ljk5OTk3OSUiIHkxPSItMTEwLjg2NDExMiUiIHgyPSI1NC41MDE4NjY1JSIgeTI9IjU1LjU4NzE3NjglIiBpZD0ibGluZWFyR3JhZGllbnQtMiI+CiAgICAgICAgICAgIDxzdG9wIHN0b3AtY29sb3I9IiNEMkQyRDIiIG9mZnNldD0iMCUiPjwvc3RvcD4KICAgICAgICAgICAgPHN0b3Agc3RvcC1jb2xvcj0iI0QyRDJEMiIgc3RvcC1vcGFjaXR5PSIwIiBvZmZzZXQ9IjEwMCUiPjwvc3RvcD4KICAgICAgICA8L2xpbmVhckdyYWRpZW50PgogICAgICAgIDxsaW5lYXJHcmFkaWVudCB4MT0iNTAlIiB5MT0iLTIyMS4xNTY5MTIlIiB4Mj0iNTAlIiB5Mj0iMTAwJSIgaWQ9ImxpbmVhckdyYWRpZW50LTMiPgogICAgICAgICAgICA8c3RvcCBzdG9wLWNvbG9yPSIjRDJEMkQyIiBvZmZzZXQ9IjAlIj48L3N0b3A+CiAgICAgICAgICAgIDxzdG9wIHN0b3AtY29sb3I9IiNEMkQyRDIiIHN0b3Atb3BhY2l0eT0iMCIgb2Zmc2V0PSIxMDAlIj48L3N0b3A+CiAgICAgICAgPC9saW5lYXJHcmFkaWVudD4KICAgICAgICA8bGluZWFyR3JhZGllbnQgeDE9IjEwMC4wODA5NTklIiB5MT0iLTk3Ljc4MDQ2MSUiIHgyPSIzOC44MjYzMDYlIiB5Mj0iMTA0LjczNjA3OCUiIGlkPSJsaW5lYXJHcmFkaWVudC00Ij4KICAgICAgICAgICAgPHN0b3Agc3RvcC1jb2xvcj0iI0QyRDJEMiIgb2Zmc2V0PSIwJSI+PC9zdG9wPgogICAgICAgICAgICA8c3RvcCBzdG9wLWNvbG9yPSIjRDJEMkQyIiBzdG9wLW9wYWNpdHk9IjAiIG9mZnNldD0iMTAwJSI+PC9zdG9wPgogICAgICAgIDwvbGluZWFyR3JhZGllbnQ+CiAgICAgICAgPGxpbmVhckdyYWRpZW50IHgxPSI1MCUiIHkxPSItMjMwLjExNjMwMyUiIHgyPSI1MCUiIHkyPSIxMDAlIiBpZD0ibGluZWFyR3JhZGllbnQtNSI+CiAgICAgICAgICAgIDxzdG9wIHN0b3AtY29sb3I9IiNEMkQyRDIiIG9mZnNldD0iMCUiPjwvc3RvcD4KICAgICAgICAgICAgPHN0b3Agc3RvcC1jb2xvcj0iI0QyRDJEMiIgc3RvcC1vcGFjaXR5PSIwIiBvZmZzZXQ9IjEwMCUiPjwvc3RvcD4KICAgICAgICA8L2xpbmVhckdyYWRpZW50PgogICAgICAgIDxsaW5lYXJHcmFkaWVudCB4MT0iNTAlIiB5MT0iLTE2OS4yMDMyODklIiB4Mj0iNTAlIiB5Mj0iMTAwJSIgaWQ9ImxpbmVhckdyYWRpZW50LTYiPgogICAgICAgICAgICA8c3RvcCBzdG9wLWNvbG9yPSIjRDJEMkQyIiBvZmZzZXQ9IjAlIj48L3N0b3A+CiAgICAgICAgICAgIDxzdG9wIHN0b3AtY29sb3I9IiNEMkQyRDIiIHN0b3Atb3BhY2l0eT0iMCIgb2Zmc2V0PSIxMDAlIj48L3N0b3A+CiAgICAgICAgPC9saW5lYXJHcmFkaWVudD4KICAgICAgICA8bGluZWFyR3JhZGllbnQgeDE9Ijk0LjAxOTgwOTYlIiB5MT0iNTYuNTUzODc3JSIgeDI9IjI2LjE4Mjc3MzMlIiB5Mj0iNDYuODM1Mjk1NCUiIGlkPSJsaW5lYXJHcmFkaWVudC03Ij4KICAgICAgICAgICAgPHN0b3Agc3RvcC1jb2xvcj0iI0M1QzVDNSIgb2Zmc2V0PSIwJSI+PC9zdG9wPgogICAgICAgICAgICA8c3RvcCBzdG9wLWNvbG9yPSIjRjZGNkY2IiBvZmZzZXQ9IjEwMCUiPjwvc3RvcD4KICAgICAgICA8L2xpbmVhckdyYWRpZW50PgogICAgICAgIDxsaW5lYXJHcmFkaWVudCB4MT0iMy43MDk3NTE1NiUiIHkxPSIxMy40MjYxODk2JSIgeDI9IjE3MC43MzQ3MDUlIiB5Mj0iMTM3LjcxMjM1MSUiIGlkPSJsaW5lYXJHcmFkaWVudC04Ij4KICAgICAgICAgICAgPHN0b3Agc3RvcC1jb2xvcj0iI0ZGRkZGRiIgb2Zmc2V0PSIwJSI+PC9zdG9wPgogICAgICAgICAgICA8c3RvcCBzdG9wLWNvbG9yPSIjQzVDNUM1IiBvZmZzZXQ9Ijc4Ljc2JSI+PC9zdG9wPgogICAgICAgIDwvbGluZWFyR3JhZGllbnQ+CiAgICAgICAgPGxpbmVhckdyYWRpZW50IHgxPSIxMC4zMzQzOTMzJSIgeTE9IjMyLjQ5OTExODIlIiB4Mj0iODYuNDIzMDk2MiUiIHkyPSI5NC4wMzM4ODI3JSIgaWQ9ImxpbmVhckdyYWRpZW50LTkiPgogICAgICAgICAgICA8c3RvcCBzdG9wLWNvbG9yPSIjREZGNkY0IiBvZmZzZXQ9IjAlIj48L3N0b3A+CiAgICAgICAgICAgIDxzdG9wIHN0b3AtY29sb3I9IiMxQkQxRDIiIG9mZnNldD0iMTAwJSI+PC9zdG9wPgogICAgICAgIDwvbGluZWFyR3JhZGllbnQ+CiAgICA8L2RlZnM+CiAgICA8ZyBpZD0i6aG16Z2iLTEiIHN0cm9rZT0ibm9uZSIgc3Ryb2tlLXdpZHRoPSIxIiBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPgogICAgICAgIDxnIGlkPSLnqbrnirbmgIHlm77moIflpIfku70iIHRyYW5zZm9ybT0idHJhbnNsYXRlKC05NTguMDAwMDAwLCAtMTQ0MS4wMDAwMDApIiBmaWxsLXJ1bGU9Im5vbnplcm8iPgogICAgICAgICAgICA8ZyBpZD0i57yW57uELTQiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDk1OC4wMDAwMDAsIDE0NDEuMDAwMDAwKSI+CiAgICAgICAgICAgICAgICA8ZyBpZD0i57yW57uELTLlpIfku70iPgogICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik03My4yNTkyNTkzLDE1OS41NTQwODIgQzY1LjM0NTY3OSwxNTUuOTczODM1IDU2Ljc2NTQzMjEsMTQ2LjM2ODg5NyA1OS4xOTc1MzA5LDEzNy4zMzE4NTkgQzYwLjQxMzk4NTksMTM3LjAyMDUxNSA2MS42OTM4NTIsMTM3LjUxNjU4NyA2Mi4zODI3MTYsMTM4LjU2NjQyNyBDNjIuMzgyNzE2LDEzOC41NjY0MjcgNzYuMTExMTExMSwxNjAuMDEwODcyIDcyLjI1OTI1OTMsMTQwLjI4MjQ3NyBDNzAuNDQ0NDQ0NCwxMzEuMDEwODcyIDY3LjMyMDk4NzcsMTIyLjA0NzkwOSA2Ny4wNzQwNzQxLDExMi40Njc2NjIgQzY2LjY2NjY2NjcsOTUuMDYwMjU0NSA4Mi42NDE5NzUzLDkwLjIzMzA5NCA4Ny4yMzQ1Njc5LDEwOS40Njc2NjIgQzg3LjgzOTUwNjIsMTEyLjAxMDg3MiA5MC4xNjA0OTM4LDEzMi42ODk4ODQgOTUuNDQ0NDQ0NCwxMzAuMzE5NTE0IEM5Ni4xMjUwMzM2LDEyOS45NjE1NTcgOTYuNjc3MzI4MiwxMjkuNDAwNjMyIDk3LjAyNDY5MTQsMTI4LjcxNDU3NiBDOTkuNzAzNzAzNywxMjQuMzE5NTE0IDEwMC41MDYxNzMsMTE3Ljg2MjcyNCAxMDUuNDE5NzUzLDExNi4zNjg4OTcgQzEwOS43MjgzOTUsMTE1LjAxMDg3MiAxMTMuOTc1MzA5LDExOC43NTE2MTMgMTE2LjI5NjI5NiwxMjIuNjE1ODEgQzEyMSwxMzAuNDY3NjYyIDEyNy4yOTYyOTYsMTQ5Ljk4NjE4IDExNy42NjY2NjcsMTU2LjYwMzQ2NCBDMTA4LjAzNzAzNywxNjMuMjIwNzQ4IDgzLjc0MDc0MDcsMTY0LjI3MDEzMSA3My4zNDU2NzksMTU5LjY0MDUwMSBMNzMuMjU5MjU5MywxNTkuNTU0MDgyIFoiIGlkPSLot6/lvoQiIGZpbGw9InVybCgjbGluZWFyR3JhZGllbnQtMSkiPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNMjczLjE4NTE4NSwxODAuNDMwNjI1IEMyNjkuNzAzNzA0LDE3Mi40Njc2NjIgMjY5Ljc5MDEyMywxNTkuNTkxMTE5IDI3Ny42OTEzNTgsMTU0LjUwNDY5OSBDMjc4LjgwODA0NywxNTUuMTA0OTExIDI3OS40MTExNTksMTU2LjM1NTk5MyAyNzkuMTg1MTg1LDE1Ny42MDM0NjQgQzI3OS4xODUxODUsMTU3LjYwMzQ2NCAyNzQuOTc1MzA5LDE4Mi43MjY5MjEgMjg1LjM1ODAyNSwxNjUuNTA0Njk5IEMyOTAuMjk2Mjk2LDE1Ny4zOTM1ODggMjk0LDE0OC42NjUxOTMgMzAwLjE3Mjg0LDE0MS4zOTM1ODggQzMxMS41MTg1MTksMTI4LjEzNDMyOSAzMjYuNjQxOTc1LDEzNS4zMzE4NTkgMzE3LjE0ODE0OCwxNTIuNjc3NTM4IEMzMTUuOTEzNTgsMTU0Ljk3MzgzNSAzMDMuNzQwNzQxLDE3MS44NjI3MjQgMzA5LjI0NjkxNCwxNzMuNjY1MTkzIEMzMDkuOTkyMzU1LDE3My44NDg1MTUgMzEwLjc3NTg5MSwxNzMuODAxMTU4IDMxMS40OTM4MjcsMTczLjUyOTM5IEMzMTYuNDMyMDk5LDE3Mi4wNzI2IDMyMS4zNzAzNywxNjcuODI1Njg3IDMyNi4wMzcwMzcsMTY5Ljk3MzgzNSBDMzMwLjEzNTgwMiwxNzEuODYyNzI0IDMzMC43Nzc3NzgsMTc3LjQ2NzY2MiAzMjkuOTEzNTgsMTgxLjg5OTc2MSBDMzI4LjEzNTgwMiwxOTAuODc1MDY5IDMxOS40MjE3MjEsMjAwLjkxMjEyIDMwNy44NDE0NzQsMTk5LjM1NjU2NCBDMjk2LjAxNDMxNCwxOTcuNzUxNjI2IDI3Ny43Nzc3NzgsMTkwLjkyNDQ1MiAyNzMuMjIyMjIyLDE4MC41MTcwNDUgTDI3My4xODUxODUsMTgwLjQzMDYyNSBaIiBpZD0i6Lev5b6EIiBmaWxsPSJ1cmwoI2xpbmVhckdyYWRpZW50LTIpIj48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTM3MC41MTg1MTksMTk5LjM1NjU1MSBDMzIzLjk2Mjk2MywxNjcuNDE4Mjc5IDI1OC4xNzI4NCwxNDcuNTA0Njk5IDE4NS4yNTkyNTksMTQ3LjUwNDY5OSBDMTEyLjM0NTY3OSwxNDcuNTA0Njk5IDQ2LjU1NTU1NTYsMTY3LjQxODI3OSAwLDE5OS4zNTY1NTEgTDM3MC41MTg1MTksMTk5LjM1NjU1MSBaIiBpZD0i6Lev5b6EIiBmaWxsPSJ1cmwoI2xpbmVhckdyYWRpZW50LTMpIj48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTI5My40NjkxMzYsMTAzLjA2MDI1NSBDMjkwLjUxODUxOSwxMDYuNzYzOTU4IDI4OC42Mjk2MywxMTIuMDEwODcyIDI5MC4zOTUwNjIsMTE2LjcwMjIzIEMyOTEuNjE3Mjg0LDExOS45MzY3OTggMjk1LjI5NjI5NiwxMTkuOTYxNDg5IDI5NS4wMzcwMzcsMTI0LjQ1NTMxNiBDMjk0Ljc4MzE4NSwxMjYuNjk5MTY5IDI5NC4wMTQyMDYsMTI4Ljg1NCAyOTIuNzkwMTIzLDEzMC43NTE2MTMgQzI5MC43MDQyNTEsMTM0LjQwMDAyNCAyODguMTQ3MzIsMTM3Ljc1ODA3MSAyODUuMTg1MTg1LDE0MC43MzkyNjcgQzI4Ny42NTQzMDUsMTQyLjY2NTI2MiAyODguOTEwMzg5LDE0NS43NjA3ODEgMjg4LjQ4MTQ4MSwxNDguODYyNzI0IEMyODcuOTk3MjMxLDE1MS45NDE2MTYgMjg2LjUzOTEwNywxNTQuNzg0MzA5IDI4NC4zMjA5ODgsMTU2Ljk3MzgzNSBDMjgzLjY2Nzg3OSwxNTcuNzcyNDg0IDI4Mi44MTY0MTUsMTU4LjM4NTUzOSAyODEuODUxODUyLDE1OC43NTE2MTMgQzI4MC45MTc2NDgsMTU4Ljk2NzM4OSAyNzkuOTQ2NTUsMTU4Ljk2NzM4OSAyNzkuMDEyMzQ2LDE1OC43NTE2MTMgQzI3NC40MTkwNjIsMTU3Ljk3MjA0MSAyNjkuOTU5MjA5LDE1Ni41NDYzODQgMjY1Ljc2NTQzMiwxNTQuNTE3MDQ1IEMyNjMuMTQ4MTQ4LDE1My4yNzAxMzEgMjYwLjQ4MTQ4MSwxNTEuNTY2NDI3IDI1OS41OTI1OTMsMTQ4Ljg1MDM3OCBDMjU4LjU3NDA3NCwxNDUuNzMzMDk0IDI2MC4xOTM0OCwxNDQuNzQyNTQ2IDI2MS45NjExNzksMTQzLjc2NTg2MyBMMjYyLjQxNzQzLDE0My41MTM2OTEgQzI2My4zMzAyLDE0My4wMDQ2MzYgMjY0LjIyNTc1LDE0Mi40NTEzNDggMjY0Ljc2NTQzMiwxNDEuNTY2NDI3IEMyNjYuMDYxNzI4LDEzOS40NDI5NzEgMjY2LjA5ODc2NSwxMzYuNjAzNDY0IDI2Ni41NTU1NTYsMTM0LjIzMzA5NCBDMjY3LjIyMjIyMiwxMzAuNzM5MjY3IDI2Ni4yMzQ1NjgsMTI0LjM2ODg5NyAyNzAuNjA0OTM4LDEyMi45OTg1MjYgQzI3Mi4yOTYyOTYsMTIyLjQ2NzY2MiAyNzQuMTM1ODAyLDEyMi41OTExMTkgMjc1LjgyNzE2LDEyMi4wNDc5MDkgQzI3OS41MDEzNzIsMTIwLjg3ODQ5OSAyODEuMDIyNDQzLDExNy40MjIyNDIgMjgyLjQ5NTEwNCwxMTMuOTI3NTY0IEwyODIuNzYyNzk1LDExMy4yOTIyNDUgQzI4My42MTE3NjMsMTExLjI4MjIzMSAyODQuNDg2OTY4LDEwOS4zMDQ2NTMgMjg1Ljc5MDEyMywxMDcuNzg4NjUgQzI4Ny42NDE5NzUsMTA1LjYyODE1NiAyOTIuMjM0NTY4LDEwNC41NjY0MjcgMjkzLjQ2OTEzNiwxMDMuMDYwMjU1IFogTTI4Mi44MDI0NjksMTIzLjc4ODY1IEMyODIuODAyNDY5LDEyMy43ODg2NSAyNjkuNTA2MTczLDEzOS4yOTQ4MjIgMjY5LjkzODI3MiwxNTMuNjI4MTU2IEwyNzQuMjM0NTY4LDE1My42MjgxNTYgQzI3NC4yMzQ1NjgsMTUzLjYyODE1NiAyNzUuNzkwMTIzLDEzNi4zNDQyMDUgMjgyLjgwMjQ2OSwxMjMuNzg4NjUgWiIgaWQ9IuW9oueKtue7k+WQiCIgZmlsbD0idXJsKCNsaW5lYXJHcmFkaWVudC00KSI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0xMDQuMTM1ODAyLDIyLjAxMDg3MTggTDEwNC4xMzU4MDIsMjIuMDEwODcxOCBDMTA0LjIwNDQ4MSwxNy4yMjUyMDc1IDEwMC43ODYyMjIsMTMuMDk4Njg2MyA5Ni4wNzEzODU3LDEyLjI3NTUwOTcgQzkxLjM1NjU0OTYsMTEuNDUyMzMzMSA4Ni43NDIyMjMzLDE0LjE3NjQyMjYgODUuMTg1MTg1MiwxOC43MDIyMjk4IEM4Mi4yMTQ1OTY1LDE4LjY0Mzk5OTkgNzkuNDQ5MjAxMSwyMC4yMTIxNzU4IDc3Ljk3NDIxNTIsMjIuNzkxMzYyNiBDNzYuNDk5MjI5NCwyNS4zNzA1NDk0IDc2LjU0OTk5MTYsMjguNTQ5MjMgNzguMTA2NTgxNCwzMS4wODAwMDM4IEM3OS42NjMxNzEyLDMzLjYxMDc3NzYgODIuNDc3MjI5OSwzNS4wODk4NTE5IDg1LjQ0NDQ0NDQsMzQuOTM2Nzk3NyBMMTAzLjMzMzMzMywzNC45MzY3OTc3IEMxMDYuNzI5ODY2LDM0Ljg5MTQ5MiAxMDkuNTE3NTkzLDMyLjIzNjIzMjEgMTA5LjcyODA3LDI4Ljg0NTkyNDIgQzEwOS45Mzg1NDgsMjUuNDU1NjE2MyAxMDcuNTAwNjUsMjIuNDc1OTM4NSAxMDQuMTM1ODAyLDIyLjAxMDg3MTggTDEwNC4xMzU4MDIsMjIuMDEwODcxOCBaIiBpZD0i6Lev5b6EIiBmaWxsPSJ1cmwoI2xpbmVhckdyYWRpZW50LTUpIj48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTE3OC4wNzQwNzQsMTIuMjA4NDAyNyBMMTc4LjA3NDA3NCwxMi4xNDY2NzQzIEMxNzguMDY2Nzk5LDYuMjMxNTQ4NTQgMTczLjgwNDI1MywxLjE3OTY1NDM4IDE2Ny45NzQ3MDYsMC4xNzcwODIwNTQgQzE2Mi4xNDUxNiwtMC44MjU0OTAyNjggMTU2LjQzOTc3OSwyLjUxMjEwODA3IDE1NC40NTY3OSw4LjA4NDk0NTg5IEMxNDguODQ4NzEzLDguMTczNTg0MTkgMTQ0LjM3NDMyNSwxMi43OTE2ODQxIDE0NC40NjI5NjMsMTguMzk5NzYwNyBDMTQ0LjU1MTYwMSwyNC4wMDc4MzczIDE0OS4xNjk3MDEsMjguNDgyMjI2MiAxNTQuNzc3Nzc4LDI4LjM5MzU4NzkgTDE3NywyOC4zOTM1ODc5IEMxODEuMjgxNDc2LDI4LjQwMTIxNzggMTg0LjgyOTIxNywyNS4wNzUwMjkgMTg1LjA5NzMwNiwyMC44MDE5NDc1IEMxODUuMzY1Mzk1LDE2LjUyODg2NiAxODIuMjYxMjAyLDEyLjc4NTM0MDkgMTc4LjAxMjM0NiwxMi4yNTc3ODU0IEwxNzguMDc0MDc0LDEyLjIwODQwMjcgWiIgaWQ9Iui3r+W+hCIgZmlsbD0idXJsKCNsaW5lYXJHcmFkaWVudC02KSI+PC9wYXRoPgogICAgICAgICAgICAgICAgPC9nPgogICAgICAgICAgICAgICAgPGcgaWQ9Imljb27lsYDpg6gtMDQiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDExNi4wMDAwMDAsIDYwLjAwMDAwMCkiPgogICAgICAgICAgICAgICAgICAgIDxnIGlkPSLnvJbnu4QiPgogICAgICAgICAgICAgICAgICAgICAgICA8Zz4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0xMTkuNDU4MTAxLDE3LjExNDAwMzkgTDAsMTcuMTE0MDAzOSBMMCw3LjA0Njk0MjggQzAsMy4xNTA4NTk0IDMuMTUwODU5NCwwIDcuMDQ2OTQyOCwwIEwxMTIuMzk4MDg0LDAgQzExNi4yOTQxNjcsMCAxMTkuNDQ1MDI3LDMuMTUwODU5NCAxMTkuNDQ1MDI3LDcuMDQ2OTQyOCBMMTE5LjQ0NTAyNywxNy4xMTQwMDM5IEwxMTkuNDU4MTAxLDE3LjExNDAwMzkgWiIgaWQ9Iui3r+W+hCIgZmlsbD0idXJsKCNsaW5lYXJHcmFkaWVudC03KSI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTEwNy44MDkwNzMsMTA2LjAwNDg0NiBMMTEuNjQ5MDI3OSwxMDYuMDA0ODQ2IEM1LjIxNjU2ODA1LDEwNi4wMDQ4NDYgMCwxMDAuNzg4Mjc4IDAsOTQuMzQyNzQ0NCBMMCwxNy4xMTQwMDM5IEwxMTkuNDU4MTAxLDE3LjExNDAwMzkgTDExOS40NTgxMDEsOTQuMzU1ODE4NSBDMTE5LjQ1ODEwMSwxMDAuNzg4Mjc4IDExNC4yNDE1MzMsMTA2LjAwNDg0NiAxMDcuODA5MDczLDEwNi4wMDQ4NDYgWiIgaWQ9Iui3r+W+hCIgZmlsbD0idXJsKCNsaW5lYXJHcmFkaWVudC04KSI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPGNpcmNsZSBpZD0i5qSt5ZyG5b2iIiBmaWxsPSIjRkZGRkZGIiBjeD0iOS41MTc5NDg3MiIgY3k9IjkuMTM4Nzk5NjYiIHI9IjIuMDEzNDEyMjMiPjwvY2lyY2xlPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPGNpcmNsZSBpZD0i5qSt5ZyG5b2iIiBmaWxsPSIjRkZGRkZGIiBjeD0iMTYuNTI1NjY5MiIgY3k9IjkuMTM4Nzk5NjYiIHI9IjIuMDEzNDEyMjMiPjwvY2lyY2xlPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPGNpcmNsZSBpZD0i5qSt5ZyG5b2iIiBmaWxsPSIjRkZGRkZGIiBjeD0iMjMuNTMzMzg5NyIgY3k9IjkuMTM4Nzk5NjYiIHI9IjIuMDEzNDEyMjMiPjwvY2lyY2xlPgogICAgICAgICAgICAgICAgICAgICAgICA8L2c+CiAgICAgICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik00Ny45ODAzNzQ5LDY4LjM0MDI4MDEgTDM3LjY1MDA1NTEsNTguMDA4ODIxMyBMNDguMDM2ODI0Nyw0Ny42MjA5MDY0IEM0OS4zMjEwNTg0LDQ2LjMzNjUzMSA0OS4zMjEwNTg0LDQ0LjI0NzY1NjkgNDguMDM2ODI0Nyw0Mi45NjMyODE1IEM0Ni43NTI1OTEsNDEuNjc4OTA2MiA0NC42NjM5NDcxLDQxLjY3ODkwNjIgNDMuMzc5NzEzMyw0Mi45NjMyODE1IEwzMi45OTI5NDM4LDUzLjM1MTE5NjQgTDIyLjYyMDI4NjcsNDIuOTYzMjgxNSBDMjEuOTk5MzM4NSw0Mi4zNDIyNjQ5IDIxLjE2NjcwMzQsNDIuMDAzNTI4NSAyMC4yOTE3MzEsNDIuMDAzNTI4NSBDMTkuNDE2NzU4NSw0Mi4wMDM1Mjg1IDE4LjU4NDEyMzUsNDIuMzQyMjY0OSAxNy45NjMxNzUzLDQyLjk2MzI4MTUgQzE2LjY3ODk0MTYsNDQuMjQ3NjU2OSAxNi42Nzg5NDE2LDQ2LjMzNjUzMSAxNy45NjMxNzUzLDQ3LjYyMDkwNjQgTDI4LjM0OTk0NDksNTcuOTk0NzA3MiBMMTcuOTYzMTc1Myw2OC4zODI2MjIxIEMxNi42OTMwNTQsNjkuNjY2OTk3NSAxNi42OTMwNTQsNzEuNzQxNzU3NiAxNy45NjMxNzUzLDczLjAyNjEzMyBDMTguNTg0MTIzNSw3My42NDcxNDk2IDE5LjQxNjc1ODUsNzQgMjAuMjkxNzMxLDc0IEMyMC4yOTE3MzEsNzQgMjAuMjkxNzMxLDc0IDIwLjI5MTczMSw3NCBDMjEuMTY2NzAzNCw3NCAyMS45OTkzMzg1LDczLjY2MTI2MzYgMjIuNjIwMjg2Nyw3My4wMjYxMzMgTDMyLjk5Mjk0MzgsNjIuNjUyMzMyMSBMNDMuMzY1NjAwOSw3My4wMjYxMzMgQzQ0LjU2NTE1OTksNzQuMDU2NDU2MSA0Ni4zNzE1NTQ2LDc0LjA1NjQ1NjEgNDcuNjEzNDUwOSw3Mi45ODM3OTA5IEM0OS4wMTA1ODQzLDcxLjc5ODIxMzcgNDkuMTY1ODIxNCw2OS43MjM0NTM1IDQ3Ljk4MDM3NDksNjguMzQwMjgwMSBaIiBpZD0i6Lev5b6EIiBmaWxsPSIjRkZGRkZGIj48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgICAgIDxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKDYwLjE0MDg4NSwgMzguOTc3MTc3KSIgaWQ9Iui3r+W+hCI+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8ZyBmaWxsPSIjRDBEMEQwIj4KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNNDEuNzg0ODQwOCwwLjA0OTAyNzg5NTIgTDQ5LjQ4NTQ4ODksMC4wNDkwMjc4OTUyIEM1MC41NzA2Mzk2LDAuMDQ5MDI3ODk1MiA1MS40NDY2MDQ3LDAuOTI0OTkyOTU2IDUxLjQ0NjYwNDcsMi4wMTAxNDM3IEw1MS40NDY2MDQ3LDIuMDEwMTQzNyBDNTEuNDQ2NjA0NywzLjA5NTI5NDQ1IDUwLjU3MDYzOTYsMy45NzEyNTk1MSA0OS40ODU0ODg5LDMuOTcxMjU5NTEgTDQxLjc4NDg0MDgsMy45NzEyNTk1MSBDNDAuNjk5NjkwMSwzLjk3MTI1OTUxIDM5LjgyMzcyNSwzLjA5NTI5NDQ1IDM5LjgyMzcyNSwyLjAxMDE0MzcgQzM5LjgyMzcyNSwwLjkyNDk5Mjk1NiA0MC42OTk2OTAxLDAuMDQ5MDI3ODk1MiA0MS43ODQ4NDA4LDAuMDQ5MDI3ODk1MiBaIj48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTIuNTQ5NDUwNTUsMC4wNDkwMjc4OTUyIEwzMy43MTgxMTc4LDAuMDQ5MDI3ODk1MiBDMzQuODAzMjY4NSwwLjA0OTAyNzg5NTIgMzUuNjc5MjMzNiwwLjkyNDk5Mjk1NiAzNS42NzkyMzM2LDIuMDEwMTQzNyBDMzUuNjc5MjMzNiwzLjA5NTI5NDQ1IDM0LjgwMzI2ODUsMy45NzEyNTk1MSAzMy43MTgxMTc4LDMuOTcxMjU5NTEgTDIuNTQ5NDUwNTUsMy45NzEyNTk1MSBDMS40NjQyOTk4LDMuOTcxMjU5NTEgMC41ODgzMzQ3NDIsMy4wOTUyOTQ0NSAwLjU4ODMzNDc0MiwyLjAxMDE0MzcgQzAuNTg4MzM0NzQyLDAuOTI0OTkyOTU2IDEuNDY0Mjk5OCwwLjA0OTAyNzg5NTIgMi41NDk0NTA1NSwwLjA0OTAyNzg5NTIgWiI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPC9nPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgPGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoMC4wMDAwMDAsIDE2Ljk5NjMzNykiIGZpbGw9IiNGRkZGRkYiPgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDxwYXRoIGQ9Ik0yMi42MzEyNzY0LDAuMjA1OTE3MTYgTDQ5LjQ4NTQ4ODksMC4yMDU5MTcxNiBDNTAuNTcwNjM5NiwwLjIwNTkxNzE2IDUxLjQ0NjYwNDcsMS4wODE4ODIyMiA1MS40NDY2MDQ3LDIuMTY3MDMyOTcgTDUxLjQ0NjYwNDcsMi4xNjcwMzI5NyBDNTEuNDQ2NjA0NywzLjI1MjE4MzcxIDUwLjU3MDYzOTYsNC4xMjgxNDg3NyA0OS40ODU0ODg5LDQuMTI4MTQ4NzcgTDIyLjYzMTI3NjQsNC4xMjgxNDg3NyBDMjEuNTQ2MTI1Nyw0LjEyODE0ODc3IDIwLjY3MDE2MDYsMy4yNTIxODM3MSAyMC42NzAxNjA2LDIuMTY3MDMyOTcgQzIwLjY3MDE2MDYsMS4wOTQ5NTYzMyAyMS41NDYxMjU3LDAuMjA1OTE3MTYgMjIuNjMxMjc2NCwwLjIwNTkxNzE2IFoiPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNMi41NDk0NTA1NSwwLjIwNTkxNzE2IEwxNC43NjA2NjUsMC4yMDU5MTcxNiBDMTUuODQ1ODE1NywwLjIwNTkxNzE2IDE2LjcyMTc4MDgsMS4wODE4ODIyMiAxNi43MjE3ODA4LDIuMTY3MDMyOTcgQzE2LjcyMTc4MDgsMy4yNTIxODM3MSAxNS44NDU4MTU3LDQuMTI4MTQ4NzcgMTQuNzYwNjY1LDQuMTI4MTQ4NzcgTDIuNTQ5NDUwNTUsNC4xMjgxNDg3NyBDMS40NjQyOTk4LDQuMTI4MTQ4NzcgMC41ODgxOTIzNjMsMy4yNTIxODM3MSAwLjU4ODE5MjM2MywyLjE2NzAzMjk3IEMwLjU3NTI2MDYzNywxLjA5NDk1NjMzIDEuNDY0Mjk5OCwwLjIwNTkxNzE2IDIuNTQ5NDUwNTUsMC4yMDU5MTcxNiBaIj48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8L2c+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICA8ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgwLjAwMDAwMCwgMzQuMjM3ODEzKSIgZmlsbD0iI0ZGRkZGRiI+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTMyLjM5NzYzMzEsMC4xMTc2NjY5NDggTDQ0LjIyOTY5ODUsMC4xMTc2NjY5NDggQzQ1LjAwMTA3MDcsMC4xMTc2NjY5NDggNDUuNjQxNzAxOSwwLjk5MzYzMjAwOSA0NS42NDE3MDE5LDIuMDc4NzgyNzYgTDQ1LjY0MTcwMTksMi4wNzg3ODI3NiBDNDUuNjQxNzAxOSwzLjE2MzkzMzUgNDUuMDE0MTQ0OCw0LjAzOTg5ODU2IDQ0LjIyOTY5ODUsNC4wMzk4OTg1NiBMMzIuMzk3NjMzMSw0LjAzOTg5ODU2IEMzMS42MjYyNjA5LDQuMDM5ODk4NTYgMzAuOTg1NjI5OCwzLjE2MzkzMzUgMzAuOTg1NjI5OCwyLjA3ODc4Mjc2IEMzMC45ODU2Mjk4LDAuOTkzNjMyMDA5IDMxLjYxMzE4NjgsMC4xMTc2NjY5NDggMzIuMzk3NjMzMSwwLjExNzY2Njk0OCBaIj48L3BhdGg+CiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgPHBhdGggZD0iTTIuNTQ5NDUwNTUsMC4xMTc2NjY5NDggTDIxLjE2Njk3NjYsMC4xMTc2NjY5NDggQzIyLjI1MjEyNzQsMC4xMTc2NjY5NDggMjMuMTI4MDkyNCwwLjk5MzYzMjAwOSAyMy4xMjgwOTI0LDIuMDc4NzgyNzYgQzIzLjEyODA5MjQsMy4xNjM5MzM1IDIyLjI1MjEyNzQsNC4wMzk4OTg1NiAyMS4xNjY5NzY2LDQuMDM5ODk4NTYgTDIuNTQ5NDUwNTUsNC4wMzk4OTg1NiBDMS40NjQyOTk4LDQuMDM5ODk4NTYgMC41ODgxOTIzNjMsMy4xNjM5MzM1IDAuNTg4MTkyMzYzLDIuMDc4NzgyNzYgQzAuNTc1MjYwNjM3LDAuOTkzNjMyMDA5IDEuNDY0Mjk5OCwwLjExNzY2Njk0OCAyLjU0OTQ1MDU1LDAuMTE3NjY2OTQ4IFoiPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgICAgICAgICAgICAgPC9nPgogICAgICAgICAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgICAgICAgICA8ZyBpZD0i57yW57uEIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSg3OS45MTU0NjksIDQzLjE0NDU0OCkiPgogICAgICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNNjguNDE2NzkzNSw1NC4yNDQ0NjMyIEM2NC45NTIxNTU1LDU0LjI0NDQ2MzIgNjEuODkyODE0OSw1NS45NTcxNzEgNjAuMDEwMTQzNyw1OC41NzE5OTIxIEw0OS4wMTQ4MjExLDUyLjkxMDkwNDUgQzQ5LjkwMzg2MDIsNTAuOTg5MDExIDUwLjQzOTg5ODYsNDguODcxMDA1OSA1MC40Mzk4OTg2LDQ2LjYyMjI1OTggQzUwLjQzOTg5ODYsNDQuMDU5NzM1MSA0OS43NDY5NzEsNDEuNjgwMjQ4IDQ4LjYwOTUyMzgsMzkuNTc1MzE3IEw1NS4wMjg5MDk2LDM2LjkyMTI3MzYgQzU1LjYwNDE3MDIsMzcuODg4NzU3NCA1Ni4yOTcwOTc4LDM4LjgwMzk0NDggNTcuMjEyMjg1MiwzOS41NjIyNDI5IEM2MS41OTIxMTA1LDQzLjI3NTI4ODggNjguMTQyMjM3Miw0Mi43MzkyNTA1IDcxLjg1NTI4MzIsMzguMzU5NDI1MiBDNzUuNTY4MzI5MSwzMy45Nzk1OTk5IDc1LjAzMjI5MDgsMjcuNDI5NDczMSA3MC42NTI0NjU1LDIzLjcxNjQyNzIgQzY2LjI3MjY0MDIsMjAuMDAzMzgxMiA1OS43MjI1MTM0LDIwLjUzOTQxOTYgNTYuMDA5NDY3NSwyNC45MTkyNDQ5IEM1My43NjA3MjEzLDI3LjU2MDIxNDEgNTMuMDkzOTQyLDMwLjk5ODcwMzkgNTMuODc4Mzg4MywzNC4xMzY0ODkyIEw0Ni44ODM3NDE5LDM3LjAyNTg2NjQgQzQ0LjQ1MTk1ODMsMzQuMTM2NDg5MiA0MC45NjExNzIyLDMyLjIwMTUyMTYgMzYuOTg2NjQ0MSwzMS43OTYyMjQzIEwzNi45ODY2NDQxLDIxLjU5ODQyMjEgQzQyLjAwNzEwMDYsMjAuODUzMTk4MSA0NS44NjM5NjE3LDE2LjU3Nzk2NTYgNDUuODYzOTYxNywxMS4zNDgzMjM1IEM0NS44NjM5NjE3LDUuNjA4NzkxMjEgNDEuMjA5NTgwMiwwLjk1NDQwOTY5MyAzNS40NzAwNDc5LDAuOTU0NDA5NjkzIEMyOS43MzA1MTU2LDAuOTU0NDA5NjkzIDI1LjA3NjEzNDEsNS42MDg3OTEyMSAyNS4wNzYxMzQxLDExLjM0ODMyMzUgQzI1LjA3NjEzNDEsMTYuNTY0ODkxNSAyOC45NDYwNjkzLDIwLjg1MzE5ODEgMzMuOTUzNDUxNywyMS41OTg0MjIxIEwzMy45NTM0NTE3LDMxLjc5NjIyNDMgQzI2LjQwOTY5MjksMzIuNTY3NTk2NSAyMC40ODcxMjMxLDM4Ljg2OTMxNTMgMjAuNDg3MTIzMSw0Ni42MjIyNTk4IEMyMC40ODcxMjMxLDUwLjM0ODM3OTggMjEuODk5MTI2NSw1My43MjE0OTkgMjQuMTQ3ODcyNiw1Ni4zMzYzMjAxIEwxOS42MTExNTgxLDU4LjY2MzUxMDggQzE3LjcyODQ4NjksNTUuOTQ0MDk2OSAxNC42MDM3NzU3LDU0LjE2NjAxODYgMTEuMDYwNjkzMiw1NC4xNjYwMTg2IEM1LjMyMTE2MDg5LDU0LjE2NjAxODYgMC42NjY3NzkzNzQsNTguODIwNDAwMSAwLjY2Njc3OTM3NCw2NC41NTk5MzI0IEMwLjY2Njc3OTM3NCw3MC4yOTk0NjQ2IDUuMzIxMTYwODksNzQuOTUzODQ2MiAxMS4wNjA2OTMyLDc0Ljk1Mzg0NjIgQzE2LjgwMDIyNTQsNzQuOTUzODQ2MiAyMS40Mjg0NTg3LDcwLjI5OTQ2NDYgMjEuNDI4NDU4Nyw2NC41NTk5MzI0IEMyMS40Mjg0NTg3LDYzLjQ0ODYzMzQgMjEuMjE5MjczLDYyLjQwMjcwNSAyMC44OTI0MjA0LDYxLjQwOTA3MyBMMjYuNDQ4OTE1Miw1OC41NTg5MTggQzI4Ljk1OTE0MzQsNjAuNDU0NjYzMyAzMi4wNTc3MDY0LDYxLjYxODI1ODcgMzUuNDMwODI1Niw2MS42MTgyNTg3IEM0MC4zNTk3NjMzLDYxLjYxODI1ODcgNDQuNzAwMzY2Myw1OS4xOTk1NDkyIDQ3LjQxOTc4MDIsNTUuNTI1NzI1NiBMNTguNjExMjE0NCw2MS4yNzgzMzE5IEM1OC4yNDUxMzk1LDYyLjMzNzMzNDUgNTcuOTk2NzMxNSw2My40NjE3MDc1IDU3Ljk5NjczMTUsNjQuNjUxNDUxMSBDNTcuOTk2NzMxNSw3MC4zOTA5ODM0IDYyLjY1MTExMyw3NS4wNDUzNjQ5IDY4LjM5MDY0NTMsNzUuMDQ1MzY0OSBDNzQuMTMwMTc3NSw3NS4wNDUzNjQ5IDc4Ljc4NDU1OSw3MC4zOTA5ODM0IDc4Ljc4NDU1OSw2NC42NTE0NTExIEM3OC43ODQ1NTksNTguOTExOTE4OSA3NC4xNTYzMjU3LDU0LjI0NDQ2MzIgNjguNDE2NzkzNSw1NC4yNDQ0NjMyIEw2OC40MTY3OTM1LDU0LjI0NDQ2MzIgWiIgaWQ9Iui3r+W+hCIgZmlsbD0idXJsKCNsaW5lYXJHcmFkaWVudC05KSI+PC9wYXRoPgogICAgICAgICAgICAgICAgICAgICAgICA8cGF0aCBkPSJNMjQuODUzODc0Myw0OS4wOTMyNjU3IEMyMy43Njg3MjM2LDQ5LjA5MzI2NTcgMjIuODkyNzU4NSw0OC4yMTczMDA2IDIyLjg5Mjc1ODUsNDcuMTMyMTQ5OSBDMjIuODkyNzU4NSw0MC4xNjM2NTE3IDI4LjU1Mzg0NjIsMzQuNTAyNTY0MSAzNS41MjIzNDQzLDM0LjUwMjU2NDEgQzM2LjYwNzQ5NTEsMzQuNTAyNTY0MSAzNy40ODM0NjAxLDM1LjM3ODUyOTIgMzcuNDgzNDYwMSwzNi40NjM2Nzk5IEMzNy40ODM0NjAxLDM3LjU0ODgzMDcgMzYuNjA3NDk1MSwzOC40MjQ3OTU3IDM1LjUyMjM0NDMsMzguNDI0Nzk1NyBDMzAuNzI0MTQ3NiwzOC40MjQ3OTU3IDI2LjgxNDk5MDEsNDIuMzMzOTUzMiAyNi44MTQ5OTAxLDQ3LjEzMjE0OTkgQzI2LjgxNDk5MDEsNDguMjA0MjI2NSAyNS45MzkwMjUxLDQ5LjA5MzI2NTcgMjQuODUzODc0Myw0OS4wOTMyNjU3IFoiIGlkPSLot6/lvoQiIGZpbGw9IiNGRkZGRkYiPjwvcGF0aD4KICAgICAgICAgICAgICAgICAgICA8L2c+CiAgICAgICAgICAgICAgICA8L2c+CiAgICAgICAgICAgIDwvZz4KICAgICAgICA8L2c+CiAgICA8L2c+Cjwvc3ZnPg==");
  background-position: center center;
  background-repeat: no-repeat;
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
  background: #aaa;
  border:1px solid #8f8f8f;
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
.dpark-bbpmn-viewer .bpmn-viewer-canvas{
  width: 100% !important;
  height: 100% !important;
}
.dpark-bpmn-viewer .smooth .viewport{
  transition: transform 0.8s;
}
</style>
