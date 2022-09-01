import axios from "axios";
import {clearHighLight, setFlowHighLight} from "../../styl/classic/approach";

function utils() {
    this.getTaskObj = function (container, id,index) {
        let completeTask = container.querySelector(`[data-element-type^="bpmn"][data-element-id="${id}"]`)
        let task_container = container.querySelector(`[class="djs-element djs-shape"][data-element-id="${id}"]`)
        if(completeTask){
            let rect = completeTask.querySelectorAll('.djs-visual rect')[0]
            if (rect) {
                let businessObject = Object.assign({container:task_container}, {color: rect.style.stroke})
                return businessObject
            }
        }
        return null
    }
    this.setAllHighLight = function (container) {
        this.clearAllHighLight(container)
        let tasks = container.querySelectorAll('.djs-shape')
        tasks.forEach(c => {
            setSingleTaskHighLight(container, c.getAttribute('data-element-id'))
        })
        this.setEndHighLight(container)
    }
    this.setTaskMaxDay = function (container, id, day) {
        let text = container.querySelector(`[data-element-type^="bpmn"][data-element-id="${id}"] .d-con-clock span`)
        if (text) {
            text.innerText = day
        }
    }
    this.hideTaskMaxDay = function (container, id) {
        let clock = container.querySelector(`[data-element-type^="bpmn"][data-element-id="${id}"] .d-con-clock`)
        if(clock){
            clock.style.display='none'
        }
    }
    this.setTaskRealName = function (container, id, day) {
        let text = container.querySelector(`[data-element-type^="bpmn"][data-element-id="${id}"] .custom-realName text`)
        if (text) {
            text.innerHTML = day
        }
    }
    this.setCenter = function (canvas,options,offset={x:0,y:0}) {
        let vbox = canvas.viewbox(),
            outer = vbox.outer,
            inner = vbox.inner,
            newScale = 1,
            newViewbox;
        let _y = (inner.height / 2 - outer.height / newScale / 2)
        let _x = (inner.width / 2 - (outer.width - (options.timeLine?265:0)-(options.zoom?44:0)) / newScale / 2)
        if(options.focus){
            newViewbox = {
                x: inner.x+_x-offset.x,
                y:  inner.y+_y-offset.y,
                width: outer.width / newScale,
                height: outer.height / newScale
            };
        }else{
            newViewbox = {
                x: (_x > 0 ? -20 : inner.x+_x)-offset.x,
                y: (_y > 0 ? -20 : inner.y+_y)-offset.y,
                width: outer.width / newScale,
                height: outer.height / newScale
            };
        }
        canvas.viewbox(newViewbox);
    }
    this.clearWatermark = function () {
        if (document.querySelector('.bjs-powered-by')) {
            document.querySelector('.bjs-powered-by').remove()
        }
    }
    this.dealWithTimeData = function (container, timeRes) {
        let _taskData = []
        let _timeRes = JSON.parse(JSON.stringify(timeRes))
        _timeRes.sort((a, b) => {
            return a.startTime - b.startTime
        }).forEach(f => {
            if(f.customTaskMaxDay){
                this.setTaskMaxDay(container, f.taskDefinitionKey, f.customTaskMaxDay + '天')
            }else{
                this.hideTaskMaxDay(container, f.taskDefinitionKey)
            }
            if (f.realName) {
                this.setTaskRealName(container, f.taskDefinitionKey, f.realName)
            }
            _taskData.push(f)
        })
        return _taskData
    }
    this.getTimeData = function (url, instanceId) {
        return axios.get(url, {
            params: {
                initPageIndex: 1,
                pageIndex: 1,
                pageNum: 1,
                pageSize: 99,
                processInstanceId: instanceId
            }
        })

    }
    this.getHttpData=function (url, instanceId){
        return axios.get(url,{
            params:{
                initPageIndex: 1,
                pageIndex: 1,
                pageNum: 1,
                pageSize: 99,
                processInstanceId: instanceId
            }
        })
    }
    this.setView = function (bpmnViewer, options,key) {
        if (bpmnViewer) {
            bpmnViewer._container.classList.add('smooth')
            let canvas = bpmnViewer.get('canvas')
            if(options.focus&&key){
                this.focus(bpmnViewer,canvas,options,key)
            }
            // 居中
            else if (options.fit) {
                // 完全显示
                canvas.zoom('fit-viewport', true);
            } else {
                //缩放比例为1
                if (canvas) {
                    this.setCenter(canvas,options)
                }
            }
            setTimeout(()=>{
                bpmnViewer._container.classList.remove('smooth')
            },1000)

        }
    }
    this.focus = function(bpmnViewer,canvas,options,key){
        const _container =bpmnViewer._container
        const taskObj = this.getTaskObj(_container,key)
        if(taskObj){
            let matrix =taskObj.container.transform.baseVal[0].matrix
            let width = taskObj.container.getBBox().width
            let height = taskObj.container.getBBox().height
            let x = canvas.viewbox().inner.width/2 - matrix.e
            let y = canvas.viewbox().inner.height/2 - matrix.f
            this.setCenter(canvas,options,{x,y})
        }else{
            this.setCenter(canvas,options)
        }
    }
    this.componentToHex = function(c){
        let hex = c.toString(16);
        return hex.length == 1 ? "0" + hex : hex;
    }

    this.rgbToHex = function (rgb){
        try{
            let re = /\d+/gi;
            let ms = rgb.match(re)
            if(ms.length>0){
                return "#" + this.componentToHex(parseInt(ms[0])) + this.componentToHex(parseInt(ms[1])) + this.componentToHex(parseInt(ms[2]));
            }
            else{
                return "#000"
            }
        }catch (err){
            return '#000'
        }
    }


    this.hexToRgb = function (hex){
        if (hex.indexOf('#') === 0) {
            let r = parseInt('0x' + hex.slice(1, 3))
            let g = parseInt('0x' + hex.slice(3, 5))
            let b = parseInt('0x' + hex.slice(5, 7))
            return [
                r,
                g,
                b,
            ]
        } else {
            let color = hex.match(/\d+/g)
            return [
                color[0],
                color[1],
                color[2]
            ]
        }
    }
}

export default new utils()

