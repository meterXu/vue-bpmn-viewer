<template>
  <div>
    <div v-if="!showBpmn" class="no-bpmn"></div>
    <div v-if="showBpmn" class="legend">
      <ul :class="[this.screenWidth>1024 ? 'legend-ul':'legend-ul-small']"  >
        <li v-for="item in legend" :key="item.class" >
          <i :class="['legend-icon',item.class]" :style="{'--colorNone': colorNone,'--colorUnExec':colorUnExec,'--colorExec':colorExec,'--colorBack':colorBack,'--colorHang':colorHang}"></i>
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
              @zoomReset="zoomReset" @changeMenu="changeMenu"
      >
      </BTZoom>
      <BTimeLine ref="cBTimeLine" :options="myOptions" :taskData="taskData" :bpmnViewer="bpmnViewer" :bpmnOptions="bpmnOptions"
                 @itemClick="itemClick" :menuShow="isMenuShow" v-show=this.screen
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
  props:["showBpmn","myOptions","bpmnViewer","selectKey","taskData","bpmnOptions"],
  data(){
    return{
      legend:[
        {text:"未执行",class:"legend-none"},
        {text:"待审批",class:"legend-unExec"},
        {text:"已审批",class:"legend-exec"},
        {text:"被驳回",class:"legend-back"},
        {text:"已挂起",class:"legend-hang"}
      ],
      colorNone: '',
      colorUnExec: '',
      colorExec: '',
      colorBack: '',
      colorHang: '',
      dialogVisible:false,
      isMenuShow:true,
      screenWidth: null,
      screen:true
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
    this.colorHang = this.bpmnOptions.additionalModules[0].colors[4]
    this.screenWidth = document.body.clientWidth
      window.onresize = () => {
        return (() => {
          this.screenWidth = document.body.clientWidth
        })()
      }
  },
  watch: {
    screenWidth: {
      handler: function (val) {
        if (val < 1024) {
          this.screen = false
        } else {
          this.screen = true
        }
      },
    },
  },
  methods:{
    itemClick(item){
      this.$emit('timeItemClick',item)
    },
    zoomReset(){
      this.$refs.cBTimeLine.scrollMove()
    },
    changeMenu(item){
      this.isMenuShow = item
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
.dpark-bpmn-viewer .legend-hang{
  background: var(--colorHang);
  border:1px solid var(--colorHang);
  opacity: 0.8;
}
</style>
