# vue-bpmn-viewer 
> bpmn2.0 流程图预览组件

## 如何使用
``` bash
npm i vue-bpmn-viewer --registry http://192.168.126.25/npm
```

```vue
<template>
  <VueBpmnViewer :baseApi="baseApi"
                 :instanceId="instanceId">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from 'vue-bpmn-viewer'
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      baseApi:'http://192.168.172.11:9001/sipsd-flow-modeler/',
      instanceId:'457e2449929111ebad80b6a919c0963e',
    }
  }
}
</script>
```
## 属性
|名称|说明|默认值|
|:---:|:---:|:---:|
|baseApi|后端地址|null|
|instanceId|flowable实例ID|null|
|options|控件配置|{zoom:true,timeLine:true}|

## options
|名称|说明|默认值|
|:---:|:---:|:---:|
|zoom|是否启用缩放控件|true|
|timeLine|是否启用时间轴|true|

## toolbar
该预览组件支持自定义顶部toolbar
```vue
<template>
  <VueBpmnViewer :baseApi="baseApi"
                 :instanceId="instanceId">
    <a-button-group>
      <a-button></a-button>
      <a-button></a-button>
      <a-button></a-button>
      ...
    </a-button-group>
  </VueBpmnViewer>
</template>
```
