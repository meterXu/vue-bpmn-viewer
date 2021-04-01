# bpmn-js 蓝色主题

### 原始主题
![](http://192.168.126.25/codimd/uploads/upload_79ad802f162756c08f9d277393647df5.png)

### 蓝色主题
![](http://192.168.126.25/codimd/uploads/upload_f26b802e9c8e71ba2616dd72d0fb6e40.png)

### 如何使用
``` bash
npm i bpmn-theme-blue --registry http://192.168.126.25/npm
```

```js
import 'bpmn-theme-blue/dist/bundle.css'
import bpmnThemeBlue from 'bpmn-theme-blue'
this.bpmnViewer = new BpmnJS({
    container: document.getElementById('container'),
    additionalModules: [bpmnThemeBlue]
})
this.bpmnViewer.importXML('xxx.xml');
```
