<template>
  <div class="io-zoom-controls">
    <ul class="io-zoom-reset io-control io-control-list">
      <li>
        <button title="重置" @click="handleZoomReset">
          <Icon class="el-icon-refresh"/>
        </button>
      </li>
      <li>
        <hr/>
      </li>
      <li>
        <button title="放大" @click="handleZoomIn">
          <Icon class="el-icon-plus"/>
        </button>
      </li>
      <li>
        <hr/>
      </li>
      <li>
        <button href title="缩小" @click="handleZoomOut">
          <Icon class="el-icon-minus"/>
        </button>
      </li>
    </ul>
  </div>
</template>

<script>
import utils from "./lib/utils.js";
import {Icon} from 'element-ui'
export default {
  name: "BTZoom",
  props: ['bpmnViewer', 'center'],
  data() {
    return {
      bpmnObj: null,
    }
  },
  components:{Icon},
  methods: {
    handleZoomReset() {
      if(this.bpmnViewer){
        this.canvas = this.bpmnViewer.get('canvas')
        if (this.canvas) {
          if(this.center){
            this.canvas.zoom('fit-viewport');
          }
        }
      }
      this.$emit('zoomResetCompleted')
    },
    handleZoomIn() {
      if(this.bpmnViewer){
        this.canvas = this.bpmnViewer.get('canvas')
        if(this.canvas){
          let zoom = this.canvas.zoom()
          this.canvas.zoom(zoom + 0.2);
        }
      }
      this.$emit('zoomInCompleted')
    },
    handleZoomOut() {
      if(this.bpmnViewer){
        this.canvas = this.bpmnViewer.get('canvas')
        if(this.canvas){
          let zoom = this.canvas.zoom()
          this.canvas.zoom(zoom - 0.2);
        }
      }
      this.$emit('zoomOutCompleted')
    }
  },
  mounted() {
    const that = this
    let container = document.getElementsByClassName('vue-bpmn-diagram-container')[0]
    if (container) {
      addEventListener('mousewheel', (event) => {
        // let down = event.wheelDelta?event.wheelDelta<0:event.detail>0;
        // if(down){
        //   that.handleZoomOut()
        // }else{
        //   that.handleZoomIn()
        // }
        return false;
      })
    }

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
</style>
