<template>
  <div>
    <div v-if="!showBpmn" class="no-bpmn"></div>
    <div v-if="showBpmn" class="legend">
      <ul class="legend-ul">
        <li v-for="item in legend" :key="item.class">
          <i :class="['legend-icon',item.class]" :style="{'--colorNone': colorNone,'--colorUnExec':colorUnExec,'--colorExec':colorExec,'--colorBack':colorBack}"></i>
          <span>{{item.text}}</span>
        </li>
      </ul>
    </div>
    <div class="bt-layout-header">
      <slot></slot>
    </div>
    <div class="bt-layout-right">
      <BTZoom ref="cBTZoom" :myOptions="myOptions"
              :bpmnViewer="bpmnViewer" :selectKey="selectKey"
              :baseApi="baseApi" :xmlId="xmlId"
              :type="type" :source="source" :timeData="timeData"
              :options="options" :styl="styl"
              @zoomReset="zoomReset"/>
      <BTimeLine ref="cBTimeLine" :options="myOptions" :taskData="taskData" :bpmnViewer="bpmnViewer" :bpmnOptions="bpmnOptions"
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
import BTimeLine from './BTimeLine.vue'
import BTZoom from './BTZoom.vue'
export default {
name: "BTLayout",
  props:["showBpmn","myOptions","bpmnViewer","selectKey","taskData","bpmnOptions","baseApi","xmlId","type","source","timeData","options","styl"],
  data(){
    return{
      legend:[
        {text:"未执行",class:"legend-none"},
        {text:"待审批",class:"legend-unExec"},
        {text:"已审批",class:"legend-exec"},
        {text:"被驳回",class:"legend-back"}
      ],
      colorNone: '',
      colorUnExec: '',
      colorExec: '',
      colorBack: ''
    }
  },
  components:{
    BTimeLine,
    BTZoom
  },
  mounted() {
    this.colorNone = this.bpmnOptions.additionalModules[0].colors[0]
    this.colorUnExec = this.bpmnOptions.additionalModules[0].colors[1]
    this.colorExec = this.bpmnOptions.additionalModules[0].colors[2]
    this.colorBack = this.bpmnOptions.additionalModules[0].colors[3]
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
<style scoped>
.dpark-bpmn-viewer .legend-none{
  background: var(--colorNone);
  border:1px solid var(--colorNone);
  opacity: 0.8;
}
.dpark-bpmn-viewer .legend-unExec{
  background: var(--colorUnExec);
  border:1px solid var(--colorUnExec);
  opacity: 0.8;
}
.dpark-bpmn-viewer .legend-exec{
  background: var(--colorExec);
  border:1px solid var(--colorExec);
  opacity: 0.8;
}
.dpark-bpmn-viewer .legend-back{
  background: var(--colorBack);
  border:1px solid var(--colorBack);
  opacity: 0.8;
}
</style>
