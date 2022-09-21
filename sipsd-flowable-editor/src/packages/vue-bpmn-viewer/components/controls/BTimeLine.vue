<template>
  <div v-show=this.menuShow ref="bpmn-time-line" class="bpmn-time-line" :class="{'spin-center':taskData&&taskData.length===0}" v-if="options.timeLine">
    <div class="timeline" v-if="taskData&&taskData.length>0">
      <div class="timeline-item" v-for="item in taskData" :key="item.id" :color="getTimeLineColor(item)">
        <div class="timeline-item__tail"></div>
        <div class="timeline-item__node" :style="{'background-color': getTimeLineColor(item)}"></div>
        <div class="timeline-item__wrapper">
          <div class="timeline-item__timestamp">
            {{fmtDate(item.startTime)}}
          </div>
          <div :class="highLightClass(item)" :style="{'--colorEd': colorEd,'--colorTurn':colorTurn,'--colorUned':colorUned,'--colorHang':colorHang}">
            <div @mouseover="handleItemOver(item)"
                 @mouseout="handleItemOut(item)"
                 @click="handleClick(item)">
              <slot v-bind:item="item">
                <p>{{item.taskName}}</p>
                <p>审批类型：{{item.approveType}}</p>
                <p>状态：{{item.status}}</p>
                <p v-if="item.status==='已办'">持续时间：{{timeFormat(item.duration)}}</p>
                <p v-else>剩余时间：{{timeFormat(item.restTime)}}</p>
              </slot>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="no-data"></div>
  </div>
</template>

<script>
import utils from "./lib/utils.js";
import ms from 'pretty-ms'
import moment from 'moment'
export default {
  name: "BTimeLine",
  props:['taskData','bpmnViewer','options','bpmnOptions',"menuShow"],
  data(){
    let setline = this.options.setline
    return {
      selectItem:null,
      loadingInstance:null,
      oldStyle:{color:null,setline,user:null,shadow:false,stroke:true},
      highLight:[
        {color:null,setline,user:null,shadow:true,stroke:true},
        {color:null,setline,user:null,shadow:true,stroke:true},
        {color:null,setline,user:null,shadow:true,stroke:true},
        {color:null,setline,user:null,shadow:true,stroke:true}
      ],
      colorEd: null,
      colorTurn: null,
      colorUned: null,
      colorHang: null,
    }
  },
  mounted() {
    this.highLight[0].color=this.bpmnOptions.additionalModules[0].colors[1]
    this.colorUned = this.highLight[0].color
    this.highLight[1].color=this.bpmnOptions.additionalModules[0].colors[2]
    this.colorEd = this.highLight[1].color
    this.highLight[2].color=this.bpmnOptions.additionalModules[0].colors[3]
    this.colorTurn = this.highLight[2].color
    this.highLight[3].color=this.bpmnOptions.additionalModules[0].colors[4]
  },
  watch:{
    taskData:{
      handler(){
        this.scrollMove()
      },
      immediate:true,
      deep:true
    }
  },
  methods:{
    highLightClass(item){
      let cls = ['timeLine-item-over']
      if(item){
        if(item.status==='已办'){
          if(item.approveType==='驳回'){
            cls.push('timeLine-item-over-turn')
          }else{
            cls.push('timeLine-item-over-ed')
          }
        }
        else if(item.status==='待办'){
          if(item.suspensionState==='挂起'){
            cls.push('timeLine-item-over-hang')
          }else{
            cls.push('timeLine-item-over-uned')
          }
        }
        if(this.selectItem&&this.selectItem.id === item.id){
          cls.push(cls[1].replace('over','active'))
        }
      }
      return cls
    },
    fmtDate(dt){
      if(dt){
        if(typeof(dt)==='string'){
          return dt
        }
        else if(typeof(dt)==="number"){
          return moment(dt).format('YYYY-MM-DD HH:mm:ss')
        }
      }
      else{
        return '-'
      }
    },
    timeFormat(s){
      if(s){
        return ms(s*1000)
            .replace(/ -/g,'')
            .replace('d','天')
            .replace('h','小时')
            .replace('m','分')
            .replace('s','秒')
      }else{
        return '-'
      }
    },
    handleItemOver(item){
      const type = item.status==='已办'?(item.approveType==='审批'?2:3):(item.suspensionState==='挂起'?4:1)
      const taskObj = utils.getTaskObj(this.bpmnViewer._container,item.taskDefinitionKey)
      if(taskObj){
        let xx = utils.rgbToHex(taskObj.color)
        item.oldStyle = Object.assign({},this.oldStyle,{color:xx})
      }
      this.bpmnOptions.additionalModules[0].utils.setTaskHighlight(this.bpmnViewer._container,[item.taskDefinitionKey],this.highLight[type-1])
    },
    handleItemOut(item){
      this.bpmnOptions.additionalModules[0].utils.clearHighLight(this.bpmnViewer._container,item.taskDefinitionKey)
      this.bpmnOptions.additionalModules[0].utils.setTaskHighlight(this.bpmnViewer._container,[item.taskDefinitionKey],item.oldStyle)
    },
    handleClick(item){
      if(this.options.track){
        this.selectItem = item
      }
      utils.setView(this.bpmnViewer,this.options,item.taskDefinitionKey)
      this.$emit('itemClick',item)
    },
    getTimeLineColor(obj){
      if(obj.status==='已办'){
        if(obj.approveType==='审批'){
          return this.highLight[1].color
        }else if(obj.approveType==='驳回'){
          return this.highLight[2].color
        }
      }else if(obj.status==='待办'){
        if(obj.suspensionState==='挂起'){
          return this.highLight[3].color
        }else{
          return this.highLight[0].color
        }
      }
    },
    scrollMove(){
      this.selectItem=null
      if(this.taskData&&this.taskData.length>0){
        let lastData=this.taskData[this.taskData.length-1]
        if(lastData.status!=='已办'&&this.options.focus&&this.$refs['bpmn-time-line']){
          this.$nextTick(()=>{
            this.$refs['bpmn-time-line'].scrollTop = this.$refs['bpmn-time-line'].scrollHeight
          })
          if(this.options.track){
            this.selectItem = lastData
            this.handleItemOver(lastData)
          }
        }
      }
    }
  }
}
</script>
<style scoped>
.dpark-bpmn-viewer .timeLine-item-over-ed:hover{
  background: var(--colorEd);
  opacity: 0.8;
}
.dpark-bpmn-viewer .timeLine-item-active-ed{
  background: var(--colorEd);
  opacity: 0.8;
}
.dpark-bpmn-viewer .timeLine-item-over-turn:hover{
  background: var(--colorTurn);
  opacity: 0.8;
}
.dpark-bpmn-viewer .timeLine-item-active-turn{
  background: var(--colorTurn);
  opacity: 0.8;
}
.dpark-bpmn-viewer .timeLine-item-over-uned:hover{
  background: var(--colorUned);
  opacity: 0.8;
}
.dpark-bpmn-viewer .timeLine-item-active-uned{
  background: var(--colorUned);
  opacity: 0.8;
}
.dpark-bpmn-viewer .timeLine-item-over-hang:hover{
  background: var(--colorHang);
  opacity: 0.8;
}
.dpark-bpmn-viewer .timeLine-item-active-hang{
  background: var(--colorHang);
  opacity: 0.8;
}
.dpark-bpmn-viewer .timeLine-item-active-ed,.timeLine-item-active-turn,.timeLine-item-active-uned,.timeLine-item-active-hang,
.dpark-bpmn-viewer .timeLine-item-over-uned:hover p,.timeLine-item-over-ed:hover p,.timeLine-item-over-turn:hover p,.timeLine-item-over-hang:hover p{
  color: #fff;
}
</style>
