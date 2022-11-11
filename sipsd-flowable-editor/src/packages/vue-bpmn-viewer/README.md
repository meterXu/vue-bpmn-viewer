# meter-vue-bpmn-viewer
> 流程图渲染器

![sipsd-flowable-editor](https://dev.dpark.com.cn/iplatform/codimd/uploads/upload_e649630e2412b862d9c9314def7635b2.gif)


## 安装
1. 在工程目录下增加.npmrc文件，内容为：
```yaml=
registry  = "https://registry.npm.taobao.org"
```

2. 安装组件
``` bash
npm i meter-vue-bpmn-viewer
```

## 快速上手

```vue
<template>
  <VueBpmnViewer :source="source" :timeData="timeData" :options="options">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from 'meter-vue-bpmn-viewer'
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      source:"https://dev.dpark.com.cn/iplatform/sipsd-flow-modeler/rest/formdetail/getprocessXml/e6c573bcc99211eba5465e2c421612f0",
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


## 配合flowable使用

```vue
<template>
  <VueBpmnViewer :type="2"
                 :baseApi="baseApi"
                 :instanceId="instanceId"
                 :options="options"
                 :styl="styl">
  </VueBpmnViewer>
</template>
<script>
import VueBpmnViewer from 'meter-vue-bpmn-viewer'
export default {
  components:{VueBpmnViewer},
  data(){
    return {
      baseApi:'http://host/sipsd-flow-modeler/',
      instanceId:'e6c573bcc99211eba5465e2c421612f0',
      options:{
        timeLine:true
      },
      styl: {
        theme:"classic",
        stylMap: {
          classic: {
            color: ["#8f8f8f","#f5eb2c","#63ee6a","#ff0000"] //重写classic主题中任务的四种状态颜色
          }
        }
      }
    }
  }
}
</script>
```

## 属性

|名称| 说明                                                          |默认值|
|:---|:------------------------------------------------------------|:---|
|source| 流程图xml地址或者xml字符串                                            |null|
|baseApi| 工作流引擎后端地址，如果配置了source，则该属性不生效                               |null|
|xmlId| 流程图ID，配合baseApi使用                                           |null|
|styl| 流程图主题，组件自带四种主题default、classic、dark、ccp，可以根据需要重写主题，也可以自己开发主题 |{theme:"default"}}|
|instanceId| flowable实例ID，配合baseApi使用                                    |null|
|type| 流程图显示模式                                                     |1：流程图，2：流程实例，配合baseApi使用|
|timeData| 时间轴数据，此数据会替换接口获得的数据                                         |null|
|options| 控件配置                                                        |{zoom:true,timeLine:false,center:true,setline:false}|

### options
|名称|说明|默认值|
|:---|:---|:---|
|zoom|是否启用缩放控件|true|
|timeLine|是否启用时间轴|false|
|fit|流程图是否全部缩放在画布上|false|
|static|是否静态不可拖动|false|
|setline|是否显示动态线条(实验性功能，待完善)|false|
|scrollZoom|是否滚动缩放|false|
|track|时间轴和流程图是否追踪高亮|false|
|focus|流程图是否聚焦居中|false|

**说明**
1. 组件可以纯前端使用，基于bpmn2.0标准，也可以配合flowable引擎进行使用
2. 纯前端使用不需要配置baseApi，xmlId，instanceId，type属性
3. 如果同时配置的source和baseApi，则忽略baseApi属性


## 方法
|名称|说明|示例|
|:---|:---|:---|
|reload|重新加载流程图|`this.$refs.bpmnView.reload()`|
|setTaskHighlight|设置任务高亮|`this.$refs.bpmnObj.setTaskHighlight(ids, options)`|
|clearTaskHighLight|清除任务高亮|`this.$refs.bpmnObj.clearTaskHighLight(ids)`|
## 事件
|名称|说明|参数|
|:---|:---|:---|
|click|元素点击事件|	Function(event,shape,taskData)|
|timeItemClick|时间轴数据项点击事件|Function(taskData)|
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
      <p>下载：<a target="_blank" href="http://www.baidu.com">baidu</a></p>
  </template>
</VueBpmnViewer>
<script>
export default {
  methods:{
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
