<template>
  <div :class="controlsClass" v-if="myOptions.zoom">
    <ul class="io-zoom-reset io-control io-control-list">
      <li class="icon-download" :class="iconClass" @click="download">
      </li>
      <li>
        <hr :class="hrClass"/>
      </li>
      <li class="icon-aim" :class="iconClass" @click="handleZoomReset">
      </li>
      <li>
        <hr :class="hrClass"/>
      </li>
      <li class="icon-plus" :class="iconClass" @click="handleZoomIn">
      </li>
      <li>
        <hr :class="hrClass"/>
      </li>
      <li class="icon-minus" :class="iconClass" @click="handleZoomOut">
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
      dialogVisible: false,
      controlsClass: 'io-zoom-controls',
      iconClass: 'icon',
      hrClass: ''
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
  },
  created() {
    let flag = navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)
    if(flag) {
      this.controlsClass = 'io-zoom-controls controls-flag'
      this.iconClass = 'icon icon-flag'
      this.hrClass = 'hr-flag'
    }
  }
}
</script>

<style scoped>
</style>
