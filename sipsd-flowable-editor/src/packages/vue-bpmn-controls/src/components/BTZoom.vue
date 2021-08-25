<template>
  <div class="io-zoom-controls">
    <ul class="io-zoom-reset io-control io-control-list">
      <li>
        <button title="reset zoom" @click="handleZoomReset">
          <a-icon type="redo"/>
        </button>
      </li>
      <li>
        <hr/>
      </li>
      <li>
        <button title="zoom in" @click="handleZoomIn">
          <a-icon type="plus"/>
        </button>
      </li>
      <li>
        <hr/>
      </li>
      <li>
        <button href title="zoom out" @click="handleZoomOut">
          <a-icon type="minus"/>
        </button>
      </li>
    </ul>
  </div>
</template>

<script>
import utils from "../lib/utils";

export default {
  name: "BTZoom",
  props: ['bpmnViewer', 'center'],
  data() {
    return {
      bpmnObj: null,
    }
  },
  methods: {
    handleZoomReset() {
      this.canvas = this.bpmnViewer.get('canvas')
      if (this.center) {
        let viewbox = this.canvas.viewbox()
        this.canvas.zoom('fit-viewport');
        let dx = 0
        let dy = 0
        if (viewbox.outer.width > viewbox.inner.width) {
          dx = (viewbox.outer.width - viewbox.inner.width) / 2 - 150
          dx = dx < 0 ? 0 : dx
        }
        if (viewbox.inner.height < viewbox.outer.height) {
          dy = (viewbox.outer.height - viewbox.inner.height) / 2
          dy = dy < 50 ? 0 : dy
          dy = dy > 150 ? 150 : dy
        }
        this.canvas.scroll({dx, dy});
      }
      this.$emit('zoomResetCompleted')
    },
    handleZoomIn() {
      let zoom = this.bpmnViewer.get('canvas').zoom()
      this.bpmnViewer.get('canvas').zoom(zoom + 0.2);
      this.$emit('zoomInCompleted')
    },
    handleZoomOut() {
      let zoom = this.bpmnViewer.get('canvas').zoom()
      this.bpmnViewer.get('canvas').zoom(zoom - 0.2);
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
