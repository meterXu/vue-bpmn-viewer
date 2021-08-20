# vue-bpmn-viewer 
> bpmn2.0 流程图预览组件

![sipsd-flowable-editor](http://192.168.126.25/iplatform/codimd/uploads/upload_e649630e2412b862d9c9314def7635b2.gif)

## 如何使用
``` bash
npm i @dpark/vue-bpmn-viewer --registry http://192.168.126.25/npm
```

```vue
<template>
  <VueBpmnViewer :type="2"
                 :baseApi="baseApi"
                 :instanceId="instanceId">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from '@dpark/vue-bpmn-viewer'
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

### type
> 显示模式，1：流程图，2：流程实例，默认为1

* 模式1，流程图显示模式

|名称|说明|默认值|
|:---:|:---:|:---:|
|baseApi|后端地址|null|
|xmlId|流程图ID|null|
|type|流程图显示模式|1|
|static|是否静态不可拖动|false|
|options|控件配置|{zoom:true,timeLine:true}|

* 模式2，流程实例显示模式

|名称|说明|默认值|
|:---:|:---:|:---:|
|baseApi|后端地址|null|
|instanceId|flowable实例ID|null|
|type|流程实例显示模式|2|
|static|是否静态不可拖动|false|
|options|控件配置|{zoom:true,timeLine:false,center:true}|

### options
|名称|说明|默认值|
|:---:|:---:|:---:|
|zoom|是否启用缩放控件|true|
|timeLine|是否启用时间轴，如果模式为1则强制为false|true|
|center|是否流程图居中显示|true|
## toolbar 插槽
该预览组件支持自定义顶部toolbar
```vue
<template>
  <VueBpmnViewer :type="1"
                 :baseApi="baseApi"
                 :xmlId="xmlId">
    <a-button-group>
      <a-button></a-button>
      <a-button></a-button>
      <a-button></a-button>
      ...
    </a-button-group>
  </VueBpmnViewer>
</template>
```
## 方法
|名称|说明|示例|
|:---:|:---:|:---:|
|reload|重新加载流程图|`this.$refs.bpmnView.reload()`|

## timeLine 插槽
自定义右侧时间轴内容
```vue
<VueBpmnViewer :type="2"
               :baseApi="baseApi"
               :instanceId="instanceId">
    <template slot="timeLine" slot-scope="{loading,data}">
      {{loading}}
      {{data.length}}
    </template>
</VueBpmnViewer>
```
