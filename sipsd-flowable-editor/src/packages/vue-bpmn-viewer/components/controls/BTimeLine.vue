<template>
  <div ref="bpmn-time-line" class="bpmn-time-line" :class="{'spin-center':taskData&&taskData.length===0}" v-if="options.timeLine">
    <div class="timeline" v-if="taskData&&taskData.length>0">
      <div class="timeline-item" v-for="item in taskData" :key="item.id" :color="getTimeLineColor(item)">
        <div class="timeline-item__tail"></div>
        <div class="timeline-item__node" :style="{'background-color': getTimeLineColor(item)}"></div>
        <div class="timeline-item__wrapper">
          <div class="timeline-item__timestamp">
            {{fmtDate(item.startTime)}}
          </div>
          <div :class="highLightClass(item)">
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
  props:['taskData','bpmnViewer','options'],
  data(){
    return {
      selectItem:null,
      loadingInstance:null,
      oldStyle:{color:null,setline:false,user:null,shadow:false,stroke:false},
      highLight:[
        {color:'#5BC14B',setline:false,user:null,shadow:true,stroke:true},
        {color:'#f5842c',setline:false,user:null,shadow:true,stroke:true},
        {color:'#ff0000',setline:false,user:null,shadow:true,stroke:true}
      ]
    }
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
        else{
          cls.push('timeLine-item-over-uned')
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
      const type = item.status==='已办'?(item.approveType==='审批'?1:3):2
      const taskObj = utils.getTaskObj(this.bpmnViewer._container,item.taskDefinitionKey)
      if(taskObj){
        item.oldStyle = Object.assign({},this.oldStyle,{color:taskObj.color})
      }
      utils.setTaskHighlight(this.bpmnViewer._container,[item.taskDefinitionKey],this.highLight[type-1])
    },
    handleItemOut(item){
      utils.clearHighLight(this.bpmnViewer._container,item.taskDefinitionKey)
      utils.setTaskHighlight(this.bpmnViewer._container,[item.taskDefinitionKey],item.oldStyle)
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
          return this.highLight[0].color
        }else if(obj.approveType==='驳回'){
          return this.highLight[2].color
        }
      }else if(obj.status==='待办'){
        return this.highLight[1].color
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
</style>
