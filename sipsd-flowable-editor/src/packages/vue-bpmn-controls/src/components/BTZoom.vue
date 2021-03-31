<template>
  <div class="io-zoom-controls">
    <ul class="io-zoom-reset io-control io-control-list">
      <li>
        <button title="reset zoom" @click="handleZoomReset">
          <a-icon type="redo" />
        </button>
      </li>
      <li>
        <hr/>
      </li>
      <li>
        <button title="zoom in" @click="handleZoomIn">
          <a-icon type="plus" />
        </button>
      </li>
      <li>
        <hr/>
      </li>
      <li>
        <button href title="zoom out" @click="handleZoomOut">
          <a-icon type="minus" />
        </button>
      </li>
    </ul>
  </div>
</template>

<script>
import utils from "../lib/utils";

export default {
  name: "BTZoom",
  props:['bpmnViewer'],
  data(){
    return {
      bpmnObj:null,
    }
  },
  methods:{
    handleZoomReset(){
      this.canvas = this.bpmnViewer.get('canvas')
      let viewbox = this.canvas.viewbox()
      this.canvas.zoom('fit-viewport');
      this.canvas.scroll({dx:(viewbox.outer.width-viewbox.inner.width)/2-150,dy:150});
      this.canvas.zoom(1)
      this.$emit('zoomResetCompleted')
    },
    handleZoomIn(){
      let zoom = this.bpmnViewer.get('canvas').zoom()
      this.bpmnViewer.get('canvas').zoom(zoom+0.2);
      this.$emit('zoomInCompleted')
    },
    handleZoomOut(){
      let zoom = this.bpmnViewer.get('canvas').zoom()
      this.bpmnViewer.get('canvas').zoom(zoom-0.2);
      this.$emit('zoomOutCompleted')
    }
  },
  mounted() {
    const that = this
    // document.getElementsByClassName('vue-bpmn-diagram-container')[0].addEventListener('mousewheel',(event)=>{
    //   let down = event.wheelDelta?event.wheelDelta<0:event.detail>0;
    //   if(down){
    //     that.handleZoomOut()
    //   }else{
    //     that.handleZoomIn()
    //   }
    //   return false;
    // })
  },
  destroyed() {
    utils.clearAllHighLight()
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
  width: auto;
  position: fixed;
  z-index: 10;
  right: 320px;
  bottom: 40px;
}
.io-zoom-reset {
  margin-bottom: 10px;
}
.io-zoom-reset {
  margin-bottom: 10px;
}
.io-zoom-reset {
  margin-bottom: 10px;
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
</style>
