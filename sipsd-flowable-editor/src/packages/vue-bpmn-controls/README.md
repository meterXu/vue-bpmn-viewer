# vue-bpmn控件库

在原有的视图上增加一些操作和效果。
## 封装的组件
* 放大、缩小、重置
* 时间轴
* 布局
* toolbar

## 封装的函数 utils
* setTaskHighlight
* setFlowHighLight
* setEndHighLight
* setAllHighLight  
* clearFlowHighLight
* clearHighLight
* clearAllFlowHighLight  
* clearAllHighLight
* setTaskMaxDay

### 如何使用
```bash
npm i vue-bpmn-controls --registry http://192.168.126.25/npm
```
```vue
<template>
  <BTLayout>
    <template slot="head">
    </template>
    <template slot="right">
      <BTZoom v-show="instanceId" :bpmnViewer="bpmnViewer" ref="BTZoom"/>
      <BTimeLine v-if="instanceId" :loading="timeLine_loading" :data="taskData.completeTask" :uData="taskData.upcomingTask"/>
    </template>
  </BTLayout>
</template>
<script>
import {BTZoom,BTimeLine,utils,BToolBar,BTLayout} from 'vue-bpmn-controls'
export default {
  components:{BTZoom,BTimeLine,BToolBar,BTLayout},
  methods:{
    handleAllHighLight(){
      utils.setAllHighLight()
    }
  }
}
</script>
```
