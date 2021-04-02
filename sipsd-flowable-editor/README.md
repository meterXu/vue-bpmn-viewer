# sipsd-flowable-editor
> flowable流程图在线预览

可以独立部署该工具，通过url传递不同的实例ID即可查看不同的工作流。同时，我们也提供单独的预览组件`vue-bpmn-viewer`，可以在项目中单独使用该组件。

下面是两种使用方式说明：

## 部署模式
该模式下，业务系统通过使用iframe嵌入具体的流程图预览地址。

假设部署后的预览工具地址为：`http://[domain]/flowable-viewer/#/bpmn/viewer`

在项目中显示流程图
```html
...
<iframe src="http://[domain]/flowable-viewer/#/bpmn/viewer?instanceId=[实例ID]"></iframe>
...
```

url中可使用的参数
* instanceId 实例ID
* zoom 显示缩放控件，默认true
* timeLine 显示时间轴，默认true

部署模式下，请确认好flowable后端地址是否配置正确，配置文件地址：`\biz\bpmn\js\project.[hash].js`，具体配置参考[s2框架](http://192.168.126.25/pldoc/deploy/)

## 组件模式
在项目中单独使用预览组件
```vue
    <VueBpmnViewer :baseApi="baseApi"
                   :instanceId="instanceId">
    </VueBpmnViewer>
```
其中，baseApi是flowable后端地址，instanceId为示例ID。

组件详细用法参考[vue-bpmn-viewer](http://192.168.126.25/git/flowable-dev/flowable_v3/-/blob/master/sipsd-flowable-editor/src/packages/vue-bpmn-viewer/README.md)

