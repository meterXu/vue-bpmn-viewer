<template>
  <div>
    <div v-if="!showBpmn" class="no-bpmn"></div>
    <div v-if="showBpmn" class="legend">
      <ul class="legend-ul">
        <li v-for="item in legend" :key="item.class">
          <i :class="['legend-icon',item.class]"></i>
          <span>{{item.text}}</span>
        </li>
      </ul>
    </div>
    <div class="bt-layout-header">
      <slot></slot>
    </div>
    <div class="bt-layout-right">
      <BTZoom ref="cBTZoom" :options="myOptions" :bpmnViewer="bpmnViewer" :selectKey="selectKey" @zoomReset="zoomReset"/>
      <BTimeLine ref="cBTimeLine" :options="myOptions" :taskData="taskData" :bpmnViewer="bpmnViewer"
                 @itemClick="itemClick"
      >
        <template v-slot="slotProps">
          <slot name="time" v-bind:item="slotProps.item"></slot>
        </template>
      </BTimeLine>
    </div>
  </div>

</template>

<script>
import BTimeLine from './BTimeLine'
import BTZoom from './BTZoom'
export default {
name: "BTLayout",
  props:["showBpmn","myOptions","bpmnViewer","selectKey","taskData"],
  data(){
    return{
      legend:[
        {text:"未执行",class:"legend-none"},
        {text:"待审批",class:"legend-unExec"},
        {text:"已审批",class:"legend-exec"},
        {text:"被驳回",class:"legend-back"}
      ]
    }
  },
  components:{
    BTimeLine,
    BTZoom
  },
  methods:{
    itemClick(item){
      this.$emit('timeItemClick',item)
    },
    zoomReset(){
      this.$refs.cBTimeLine.scrollMove()
    }
  }
}
</script>
