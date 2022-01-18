<template>
  <div class="io-zoom-controls" v-if="myOptions.zoom">
    <ul class="io-zoom-reset io-control io-control-list">
      <li class="icon icon-download" @click="download">
      </li>
      <li>
        <hr/>
      </li>
      <li class="icon icon-aim" @click="handleZoomReset">
      </li>
      <li>
        <hr/>
      </li>
      <li class="icon icon-plus" @click="handleZoomIn">
      </li>
      <li>
        <hr/>
      </li>
      <li class="icon icon-minus" @click="handleZoomOut">
      </li>
    </ul>
  </div>
</template>

<script>
import utils from "./lib/utils.js";
export default {
  name: "BTZoom",
  props: ['bpmnViewer', 'myOptions','selectKey',"baseApi","xmlId","type","source","timeData","options","styl"],
  data() {
    return {
      bpmnObj: null,
      dialogVisible: false
    }
  },
  methods: {
    handleZoomReset() {
      utils.setView(this.bpmnViewer,this.myOptions,this.selectKey)
      this.$emit('zoomReset')
    },
    handleZoomIn() {
      if(this.bpmnViewer){
        this.canvas = this.bpmnViewer.get('canvas')
        if(this.canvas){
          let zoom = this.canvas.zoom()
          this.canvas.zoom(zoom + 0.2);
        }
      }
    },
    handleZoomOut() {
      if(this.bpmnViewer){
        this.canvas = this.bpmnViewer.get('canvas')
        if(this.canvas){
          let zoom = this.canvas.zoom()
          this.canvas.zoom(zoom - 0.2);
        }
      }
    },
    download() {
      if(this.bpmnViewer){
        this.$emit('download')
      }
    }
  }
}
</script>

<style scoped>
</style>
