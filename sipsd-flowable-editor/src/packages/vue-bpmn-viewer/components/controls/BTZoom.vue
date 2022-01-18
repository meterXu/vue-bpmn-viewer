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
import saveSvgAsPng from "save-svg-as-png";
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
      let graphics = document.querySelector(".djs-container svg")
      let g = document.querySelector(".djs-container svg g")
      let _width= document.getElementsByClassName('viewport')[0].getBBox().width
      let _height= document.getElementsByClassName('viewport')[0].getBBox().height
      let width =  Math.ceil(_width)+Math.ceil(g.transform.animVal[0].matrix.e*3)
      let height = Math.ceil(_height)+Math.ceil(g.transform.animVal[0].matrix.f*3)
      saveSvgAsPng.saveSvgAsPng(graphics,"diagram.png",{height:height,encoderOptions:1})
    }
  }
}
</script>

<style scoped>
</style>
