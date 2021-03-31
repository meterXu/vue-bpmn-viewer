<template>
<div id="bpmn">
  <vue-bpmn ref="bpmnObj" :options="options" :url="xmlUrl"></vue-bpmn>
  <BTZoom ref="BTZoom"/>
  <BTimeLine :loading="timeLine_loading" :data="taskData.completeTask" :uData="taskData.upcomingTask"/>
  <BToolBar
      @edit="handleEdit"
      @copy="handleCopy"
      @delete="handleDelete"
      @export="handleExport"
      @push="handlePush"
      @viewEdit="handleViewEdit"
      @close="handleClose"
  />
  <a-modal
      :title="taskTitle"
      v-model="dialogVisible"
      width="30%">
    <span>
      {{taskContent}}
    </span>
    <span slot="footer" class="dialog-footer">
    <a-button @click="dialogVisible = false">取 消</a-button>
    <a-button type="primary" @click="dialogVisible = false">确 定</a-button>
  </span>
  </a-modal>
</div>
</template>

<script>
import VueBpmn from 'vue-bpmn';
import bpmnThemeBlue from '../packages/bpmn-theme-blue'
import BTZoom from '../packages/vue-bpmn-controls'
import {BTimeLine,utils,BToolBar} from '../packages/vue-bpmn-controls'
import {getAction} from "@/api/manage";

export default {
  name: "Viewer",
  props:{
    xmlId:{
      type:String,
      default:'423739ec-8c80-11eb-a15e-f2326a570310'
    },
    processInstanceId:{
      type:String,
      default:'0837e105905d11eb86a576c39c853d34'
    }
  },
  components:{
    VueBpmn,
    BTZoom,
    BTimeLine,
    BToolBar
  },
  data(){
    return {
      dialogVisible:false,
      taskContent:null,
      taskTitle:null,
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
        exportUrl:'app/rest/models/[]/bpmn20?version=1617092632878'
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
    elementClick(element){
      if(element.type==='bpmn:UserTask'){
        this.dialogVisible = true
        this.taskTitle = element.businessObject.name + ' - 任务属性';
        this.taskContent=JSON.stringify(element.businessObject)
      }
    },
    getTaskList(){
      return new Promise(((resolve, reject) => {
        getAction(this.url.allExtensionTasks,{
          initPageIndex:1,
          pageIndex:1,
          pageNum:1,
          pageSize:99,
          processInstanceId:this.processInstanceId,
        }).then(res=>{
          resolve(res.data)
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

    }
  },
  mounted() {
    let that = this
    window.xx = this.$bpmnViewer
    this.$bpmnViewer.on('import.done', function() {
      that.$refs.BTZoom.handleZoomReset()
      document.querySelector('.bjs-powered-by').remove()
    });
    const eventBus = this.$bpmnViewer.get('eventBus');
    this.events.forEach(function(event) {
      eventBus.on(event, function(e) {
        switch (e.type){
          case 'element.click':{
            that.elementClick(e.element)
          }
        }
      });
    });
  },
  async created() {
    const tasks = await this.getTaskList()
    tasks.forEach(f=>{
      utils.setTaskMaxDay(f.taskDefinitionKey,f.customTaskMaxDay+'天')
      if(f.status==='已办'){
        this.taskData.completeTask.push(f)
      }else if(f.status==='待办'){
        this.taskData.upcomingTask.push(f)
      }
    })
    this.timeLine_loading=false
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
.demo-btn{
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 9999;
}
</style>
