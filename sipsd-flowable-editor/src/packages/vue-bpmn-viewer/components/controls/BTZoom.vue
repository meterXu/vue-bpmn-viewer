<template>
  <div class="io-zoom-controls" v-if="options.zoom">
    <ul class="io-zoom-reset io-control io-control-list">
      <li class="icon" @click="handleZoomReset">
        <i class="el-icon-aim"/>
      </li>
      <li>
        <hr/>
      </li>
      <li class="icon" @click="handleZoomIn">
        <i class="el-icon-plus"/>
      </li>
      <li>
        <hr/>
      </li>
      <li class="icon" @click="handleZoomOut">
        <i class="el-icon-minus"/>
      </li>
    </ul>
  </div>
</template>

<script>
import utils from "./lib/utils.js";
export default {
  name: "BTZoom",
  props: ['bpmnViewer', 'options','selectKey'],
  data() {
    return {
      bpmnObj: null,
    }
  },
  methods: {
    handleZoomReset() {
      utils.setView(this.bpmnViewer,this.options,this.selectKey)
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
    }
  },
  destroyed() {
    utils.clearAllHighLight(this.bpmnViewer._container)
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.io-control-list {
  list-style: none;
  padding: 5px;
  margin: 0;
}

.io-zoom-controls {
  width: 34px;
  margin-right: 10px;
}

.io-control {
  background: #FAFAFA;
  border-radius: 2px;
  border: solid 1px #E0E0E0;
  padding: 5px;
}

.io-control-list a, .io-control-list a:visited, .io-control-list button {
  padding: 0;
  outline: none;
  cursor: pointer;
  font-size: 22px;
  line-height: 26px;
  color: #555555;
  background: none;
  border: none;
}

.io-control hr {
  border: none;
  border-top: solid 1px #DDD;
  width: 15px;
}
.icon{
  display: flex;
  align-items:center;
  justify-content: center;
  font-size: 24px;
  color: #8f8f8f;
}
.icon:hover{
  color: #000;
}
</style>
