<template>
  <div id="bpmn">
    <vue-bpmn v-if="xmlId" ref="bpmnObj" :options="options" :url="xmlUrl"></vue-bpmn>
    <div v-else class="no-bpmn">
      <img :src="getAssetsImg(require('../assets/no-bpmn.svg'))">
    </div>
    <BTLayout>
      <template slot="head">
        <BToolBar
            v-if="xmlId"
            @edit="handleEdit"
            @copy="handleCopy"
            @delete="handleDelete"
            @export="handleExport"
            @push="handlePush"
            @viewEdit="handleViewEdit"
            @close="handleClose"
        />
      </template>
      <template slot="right">
        <BTZoom v-show="xmlId" :bpmnViewer="bpmnViewer" ref="BTZoom"/>
        <BTimeLine v-if="instanceId" :loading="timeLine_loading" :data="taskData.completeTask" :uData="taskData.upcomingTask"/>
      </template>
    </BTLayout>
  </div>
</template>

<script>
import VueBpmn from 'vue-bpmn';
import bpmnThemeBlue from '../packages/bpmn-theme-blue'
import BTZoom from '../packages/vue-bpmn-controls'
import {BTimeLine,utils,BToolBar,BTLayout} from '../packages/vue-bpmn-controls'
import axios from 'axios'
export default {
  name: "Viewer",
  props:{
    baseApi:{type:String},
    xmlId:{type:String},
    instanceId:{type:String}
  },
  components:{
    VueBpmn,
    BTLayout,
    BTZoom,
    BTimeLine,
    BToolBar
  },
  data(){
    return {
      bpmnViewer:null,
      timeLine_loading:true,
      options:{
        additionalModules:[bpmnThemeBlue]
      },
      events:[
        'element.click',
      ],
      taskData:{
        completeTask:[],
        upcomingTask:[]
      },
      url:{
        xmlUrl:'rest/model/loadXmlByModelId/',
        allExtensionTasks:'rest/extension/task/get-all-extension-tasks',
        exportUrl:'app/rest/models/[]/bpmn20?version=1617092632878',
        restModels:'app/rest/models/'
      }
    }
  },
  computed:{
    xmlUrl(){
      return `${this.$project_bpmn.variable.baseApi}${this.url.xmlUrl}${this.xmlId}`
    }
  },
  watch:{
    'taskData.completeTask':{
      handler:function (nv){
        utils.setTaskHighlight(nv.map(c=>c.taskDefinitionKey),{color:'#5BC14B',setline: false})
      }
    },
    'taskData.upcomingTask':{
      handler:function (nv){
        if(nv.length===0){
          utils.setEndHighLight()
        }else{
          utils.setTaskHighlight(nv.map(c=>c.taskDefinitionKey),{color:'#f5842c',setline: true})
        }
      }
    }
  },
  methods:{
    getTaskList(){
      return new Promise(((resolve, reject) => {
        axios.get(this.baseApi+this.url.allExtensionTasks,{
          params:{
            initPageIndex:1,
            pageIndex:1,
            pageNum:1,
            pageSize:99,
            processInstanceId:this.instanceId
          }
        }).then(res=>{
          resolve(res.data.data)
        }).catch(err=>{
          reject(err)
        })
      }))

    },
    handleEdit(){

    },
    handleCopy(){

    },
    handleExport(){
      let a = document.createElement('a')
      a.href=this.baseUrl+this.url.exportUrl.replace('[]',this.xmlId)
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
    },
    handleDelete(){

    },
    handlePush(){

    },
    handleViewEdit(){

    },
    handleClose(){
      window.history.back()
    }
  },
  mounted() {
    let that = this
    this.bpmnViewer= this.$refs.bpmnObj.bpmnViewer
    window.xx =  this.bpmnViewer
    this.bpmnViewer.on('import.done', function() {
      if(document.querySelector('.bjs-powered-by')){
        document.querySelector('.bjs-powered-by').remove()
      }
      that.$refs.BTZoom.handleZoomReset()
    });
  },
  async created() {
    if(this.$route&&(this.$route.xmlId||this.$route.query.instanceId)){
      this.xmlId = this.$route.query.xmlId
      this.instanceId = this.$route.query.instanceId
    }
    if(this.$project_bpmn&&this.$project_bpmn.variable.baseApi){
      this.baseApi = this.$project_bpmn.variable.baseApi
    }
    if(this.instanceId){
      try{
        const tasks = await this.getTaskList()
        tasks.forEach(f=>{
          utils.setTaskMaxDay(f.taskDefinitionKey,f.customTaskMaxDay+'天')
          if(f.status==='已办'){
            this.taskData.completeTask.push(f)
          }else if(f.status==='待办'){
            this.taskData.upcomingTask.push(f)
          }
        })
      }catch (err){
        console.error(err)
      }finally {
        this.timeLine_loading=false
      }
    }
  }
}
</script>

<style scoped>
#bpmn{
  width: 100%;
  height: 100%;
  background: #F5F5F7;
  cursor: grab;
}
#bpmn:active{
  cursor: grabbing;
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
