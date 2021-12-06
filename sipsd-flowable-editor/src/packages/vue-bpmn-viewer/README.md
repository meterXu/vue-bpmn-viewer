# @dpark/vue-bpmn-viewer
> 沃壤平台工作流引擎执行器组件

![sipsd-flowable-editor](http://58.210.9.133/iplatform/codimd/uploads/upload_e649630e2412b862d9c9314def7635b2.gif)


## 安装
1. 在工程目录下增加.npmrc文件，内容为：
```yaml=
registry  = "https://registry.npm.taobao.org"
@dpark:registry="http://58.210.9.133/iplatform/npm/"
```

2. 安装组件
``` bash
npm i @dpark/vue-bpmn-viewer
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
                 :instanceId="instanceId"
                 :options="options">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from '@dpark/vue-bpmn-viewer'
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      baseApi:'http://58.210.9.133/iplatform/sipsd-flow-modeler/',
      instanceId:'e6c573bcc99211eba5465e2c421612f0',
      options:{
        timeLine:true
      }
    }
  }
}
</script>
```

## 属性

|名称|说明|默认值|
|:---|:---|:---|
|source|流程图xml地址或者xml字符串|null|
|baseApi|工作流引擎后端地址，如果配置了source，则该属性不生效|null|
|xmlId|流程图ID，配合baseApi使用|null|
|instanceId|flowable实例ID，配合baseApi使用|null|
|type|流程图显示模式|1：流程图，2：流程实例，配合baseApi使用|
|static|是否静态不可拖动|false|
|timeData|时间轴数据，此数据会替换接口获得的数据|null|
|options|控件配置|{zoom:true,timeLine:false,center:true,setline:false}|
|log|是否记录使用日志|false|
|logReportUrl|日志上报地址|http://58.210.9.133/iplatform/logfv-server/logfv/web/upload|

### options
|名称|说明|默认值|
|:---|:---|:---|
|zoom|是否启用缩放控件|true|
|timeLine|是否启用时间轴|false|
|fit|流程图是否全部缩放在画布上|false|
|setline|是否显示动态线条(实验性功能，待完善)|false|
|zoomScroll|是否滚动缩放|false|
|track|是否追踪显示|false|

**说明**
1. 组件可以纯前端使用，也可以配合沃壤平台工作流引擎进行使用
2. 纯前端使用不需要配置baseApi，xmlId，instanceId，type属性
3. 如果同时配置的source和baseApi，则忽略baseApi属性


## 方法
|名称|说明|示例|
|:---|:---|:---|
|reload|重新加载流程图|`this.$refs.bpmnView.reload()`|

## 事件
|名称|说明|参数|
|:---|:---|:---|
|click|元素点击事件|	Function(event,shape,taskData)|
|viewChange|画布变化事件|	Function(event)|
|loading|流程图加载中|	Function()|
|loaded|流程图加载完成|	Function()|
|loadError|流程图加载失败|	Function(err)|

## 插槽
1. 自定义时间轴

```vue
// slotProps.item 为时间轴每项的数据对象
<VueBpmnViewer :type="2"
               :baseApi="baseApi"
               :instanceId="instanceId">
  <template v-slot:time="slotProps">
      <p>{{slotProps.item.taskName}}</p>
      <p>审批类型：{{slotProps.item.approveType}}</p>
      <p>状态：{{slotProps.item.status}}</p>
      <p v-if="slotProps.item.status==='已办'">持续时间：{{timeFormat(slotProps.item.duration)}}</p>
      <p v-else>剩余时间：{{timeFormat(slotProps.item.restTime)}}</p>
      <p>下载：<a target="_blank" href="http://www.baidu.com">baidu</a></p>
  </template>
</VueBpmnViewer>
<script>
import ms from 'pretty-ms'
export default {
  methods:{
    timeFormat(s){
      if(s){
        return ms(s*1000)
            .replace(/ -/g,'')
            .replace('d','天')
            .replace('h','小时')
            .replace('m','分')
            .replace('s','秒')
      }else{
        return '-'
      }
    }
  }
}
</script>
```
2. 自定义顶部工具栏

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
