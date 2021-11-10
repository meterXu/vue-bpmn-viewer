# @dpark/vue-bpmn-viewer
> 沃壤平台工作流引擎执行器

![sipsd-flowable-editor](http://58.210.9.133/iplatform/codimd/uploads/upload_e649630e2412b862d9c9314def7635b2.gif)

## 安装
``` bash
npm i @dpark/vue-bpmn-viewer --registry http://58.210.9.133/iplatform/npm/
```

## 快速上手

```vue
<template>
  <VueBpmnViewer :source="source" :timeData="timeData" :options="options">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from '@dpark/vue-bpmn-viewer'
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      source:"http://58.210.9.133/iplatform/sipsd-flow-modeler/rest/formdetail/getprocessXml/e6c573bcc99211eba5465e2c421612f0",
      timeData:[
        {
          "id": 1,
          "taskMaxDay": "4",
          "realName": null,
          "startTime": 1623292212527,
          "restTime": -12913873,
          "customTaskMaxDay": "4",
          "taskDefinitionKey": "jsqr",
          "taskName": "建设局人员现场确认",
          "duration": 0,
          "approveType": "审批",
          "status": "待办"
        },
        {
          "id": 2,
          "taskMaxDay": "1",
          "realName": null,
          "startTime": 1623291833921,
          "restTime": -13173452,
          "customTaskMaxDay": "1",
          "taskDefinitionKey": "widowsA",
          "taskName": "窗口",
          "duration": 282,
          "approveType": "审批",
          "status": "已办"
        }
      ],
      options:{
        timeLine:true
      }
    }
  }
}
</script>
```


## 基于沃壤平台工作流引擎

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

|名称|说明|默认值|
|:---|:---:|:---|
|source|流程图xml地址或者xml字符串|null|
|baseApi|工作流引擎后端地址，如果配置了source，则该属性不生效|null|
|xmlId|流程图ID，配合baseApi使用|null|
|instanceId|flowable实例ID，配合baseApi使用|null|
|type|流程图显示模式|1：流程图，2：流程实例，配合baseApi使用|
|static|是否静态不可拖动|false|
|timeData|时间轴数据|false|
|options|控件配置|{zoom:true,timeLine:false,center:true,setline:false}|
|log|是否记录使用日志|false|
|logReportUrl|日志上报地址|http://58.210.9.133/iplatform/logfv-server/logfv/web/upload|

### options
|名称|说明|默认值|
|:---|:---:|---:|
|zoom|是否启用缩放控件|true|
|timeLine|是否启用时间轴，如果模式为1则强制为false|true|
|center|是否流程图居中显示|true|
|setline|是否显示动态线条(实验性功能，待完善)|false|

**说明**
1. 组件可以纯前端使用，也可以配合沃壤平台工作流引擎进行使用
2. 纯前端使用不需要配置baseApi，xmlId，instanceId，type属性
3. 如果同时配置的source和baseApi，则忽略baseApi属性


## 方法
|名称|说明|示例|
|:---:|:---:|:---:|
|reload|重新加载流程图|`this.$refs.bpmnView.reload()`|

## 插槽
自定义顶部工具栏
```vue
<template>
  <VueBpmnViewer :type="1"
                 :baseApi="baseApi"
                 :xmlId="xmlId">
    <a-button-group>
      <a-button>btn1</a-button>
      <a-button>btn2</a-button>
      <a-button>btn3</a-button>
    </a-button-group>
  </VueBpmnViewer>
</template>
```

自定义右侧时间轴
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
