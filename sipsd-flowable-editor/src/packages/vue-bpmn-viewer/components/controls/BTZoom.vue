<template>
  <div class="io-zoom-controls">
    <ul class="io-zoom-reset io-control io-control-list">
      <li @click="handleZoomReset">
        <div class="icon icon-refresh" />
      </li>
      <li>
        <hr/>
      </li>
      <li @click="handleZoomIn">
        <div class="icon icon-plus" />
      </li>
      <li>
        <hr/>
      </li>
      <li @click="handleZoomOut">
        <div class="icon icon-minus"/>
      </li>
    </ul>
  </div>
</template>

<script>
import utils from "./lib/utils.js";
export default {
  name: "BTZoom",
  props: ['bpmnViewer', 'fit'],
  data() {
    return {
      bpmnObj: null,
    }
  },
  methods: {
    handleZoomReset() {
      if(this.bpmnViewer){
        this.canvas = this.bpmnViewer.get('canvas')
        if (this.canvas) {
          if(this.fit){
            this.canvas.zoom('fit-viewport',true);
          }else {
            utils.setCenter(this.canvas)
          }
        }
      }
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
  width:20px ;
  height: 20px;
}
.icon:hover{

}
.icon-refresh{
  background-image: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMzIiIGhlaWdodD0iMzIiIHZpZXdCb3g9IjAgMCA0OCA0OCIgdmVyc2lvbj0iMS4xIj48ZyBzdHJva2U9Im5vbmUiIHN0cm9rZS13aWR0aD0iMiIgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgtMC41MDAwMDAsIDAuMDAwMDAwKSI+PHBhdGggZD0iTTQyLDI0IEM0MiwxNC4wNTg4NzQ1IDMzLjk0MTEyNTUsNiAyNCw2IEMyMS41NTk0NjcsNiAxOS4yMzIzNzY2LDYuNDg1NzA1MDcgMTcuMTEwMTE4Niw3LjM2NTcyNTI0IEMxNi4wMTQzNzM5LDcuODIwMDg5MTMgMTQuOTczMjMyOSw4LjM3OTU2ODI3IDEzLjk5OTI3NCw5LjAzMTU4NDA5IEMxMy4wMTc2NzYyLDkuNjg4NzEzNzUgMTIuMTA0MzE4NSwxMC40Mzk4MzczIDExLjI3MjA3NzksMTEuMjcyMDc3OSBDMTAuNDM5ODM3MywxMi4xMDQzMTg1IDkuNjg4NzEzNzUsMTMuMDE3Njc2MiA5LjAzMTU4NDA5LDEzLjk5OTI3NCBNNiwyNCBDNiwzMy45NDExMjU1IDE0LjA1ODg3NDUsNDIgMjQsNDIgTDI0LDQyIEMyNi40NDA1MzMsNDIgMjguNzY3NjIzNCw0MS41MTQyOTQ5IDMwLjg4OTg4MTQsNDAuNjM0Mjc0OCBDMzEuOTg1NjI2MSw0MC4xNzk5MTA5IDMzLjAyNjc2NzEsMzkuNjIwNDMxNyAzNC4wMDA3MjYsMzguOTY4NDE1OSBDMzQuOTgyMzIzOCwzOC4zMTEyODYyIDM1Ljg5NTY4MTUsMzcuNTYwMTYyNyAzNi43Mjc5MjIxLDM2LjcyNzkyMjEgQzM3LjU2MDE2MjcsMzUuODk1NjgxNSAzOC4zMTEyODYyLDM0Ljk4MjMyMzggMzguOTY4NDE1OSwzNC4wMDA3MjYiIHN0cm9rZT0iIzMzMyIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48cGF0aCBkPSJNMzQsMTYgTDUwLDE2IiBzdHJva2U9IiMzMzMiIHN0cm9rZS13aWR0aD0iMiIgc3Ryb2tlLWxpbmVjYXA9InJvdW5kIiBzdHJva2UtbGluZWpvaW49InJvdW5kIiB0cmFuc2Zvcm09InRyYW5zbGF0ZSg0Mi4wMDAwMDAsIDE2LjAwMDAwMCkgcm90YXRlKDkwLjAwMDAwMCkgdHJhbnNsYXRlKC00Mi4wMDAwMDAsIC0xNi4wMDAwMDApICIvPjxwYXRoIGQ9Ik0tMiwzMiBMMTQsMzIiIHN0cm9rZT0iIzMzMyIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiIHRyYW5zZm9ybT0idHJhbnNsYXRlKDYuMDAwMDAwLCAzMi4wMDAwMDApIHJvdGF0ZSg5MC4wMDAwMDApIHRyYW5zbGF0ZSgtNi4wMDAwMDAsIC0zMi4wMDAwMDApICIvPjwvZz48L2c+PC9zdmc+");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: contain;
}
.icon-plus{
  background-image: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMzIiIGhlaWdodD0iMzIiIHZpZXdCb3g9IjAgMCA0OCA0OCIgZmlsbD0ibm9uZSIgdmVyc2lvbj0iMS4xIj48cGF0aCBkPSJNMjEgMzhDMzAuMzg4OCAzOCAzOCAzMC4zODg4IDM4IDIxQzM4IDExLjYxMTIgMzAuMzg4OCA0IDIxIDRDMTEuNjExMiA0IDQgMTEuNjExMiA0IDIxQzQgMzAuMzg4OCAxMS42MTEyIDM4IDIxIDM4WiIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMzMzIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48cGF0aCBkPSJNMjEgMTVMMjEgMjciIHN0cm9rZT0iIzMzMyIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48cGF0aCBkPSJNMTUgMjFMMjcgMjEiIHN0cm9rZT0iIzMzMyIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48cGF0aCBkPSJNMzMuMjIxOCAzMy4yMjE4TDQxLjcwNzEgNDEuNzA3MSIgc3Ryb2tlPSIjMzMzIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIvPjwvc3ZnPg==");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: contain;
}
.icon-minus{
  background-image: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB3aWR0aD0iMzIiIGhlaWdodD0iMzIiIHZpZXdCb3g9IjAgMCA0OCA0OCIgZmlsbD0ibm9uZSIgdmVyc2lvbj0iMS4xIj48cGF0aCBkPSJNMjEgMzhDMzAuMzg4OCAzOCAzOCAzMC4zODg4IDM4IDIxQzM4IDExLjYxMTIgMzAuMzg4OCA0IDIxIDRDMTEuNjExMiA0IDQgMTEuNjExMiA0IDIxQzQgMzAuMzg4OCAxMS42MTEyIDM4IDIxIDM4WiIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjMzMzIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48cGF0aCBkPSJNMTUgMjFMMjcgMjEiIHN0cm9rZT0iIzMzMyIgc3Ryb2tlLXdpZHRoPSIyIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiLz48cGF0aCBkPSJNMzMuMjIxOCAzMy4yMjE4TDQxLjcwNzEgNDEuNzA3MSIgc3Ryb2tlPSIjMzMzIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIvPjwvc3ZnPg==");
  background-repeat: no-repeat;
  background-position: center center;
  background-size: contain;
}
</style>
