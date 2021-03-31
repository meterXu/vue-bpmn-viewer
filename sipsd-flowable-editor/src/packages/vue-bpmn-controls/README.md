# vue-bpmn控件库

![](http://192.168.126.25/codimd/uploads/upload_82548a84a158658af0bb468809a32a64.png)

![](http://192.168.126.25/codimd/uploads/upload_5021f25211cc3f4dd0cf831a42cadd94.png)

在原有的视图上增加“放大”，“缩小”，“重置”操作，增加元素的高亮效果。

### 如何使用
```bash
npm i vue-bpmn-controls --registry http://192.168.126.25/npm
```
```vue
<template>
  ...
  <ZoomControls/>
</template>
<script>
import ZoomControls from 'vue-bpmn-controls'
export default {
  components:{ZoomControls}
}
</script>
```
