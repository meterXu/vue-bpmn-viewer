import {attr, classes} from "tiny-svg";
import axios from "axios";

function setSingleTaskHighLight(container, id, options) {
    if (id) {
        let completeTasks = container.querySelectorAll(`[data-element-type="bpmn:userTask"][data-element-id="${id}"]`)
        if (completeTasks.length > 0) {
            completeTasks.forEach(completeTask => {
                let feColorMatrix = completeTask.querySelector('.djs-visual feColorMatrix')
                let rect = completeTask.querySelector('.djs-visual rect')
                let title1 = completeTask.querySelectorAll('.djs-visual rect')[1]
                let title2 = completeTask.querySelectorAll('.djs-visual rect')[2]
                let user = completeTask.querySelectorAll('.djs-visual text')[1]
                if (options.user) {
                    user.innerHTML = options.user
                } else {
                    attr(user, {
                        fill: '#cdcdcd'
                    })
                }
                if (title1) {
                    attr(title1, {
                        fill: options.color,
                    })
                }
                if (title2) {
                    attr(title2, {
                        fill: options.color,
                    })
                }
                if (options.shadow && feColorMatrix && rect) {
                    const rgb = hexToRgb(options.color)
                    attr(feColorMatrix, {
                        values: `${(rgb[0] / 255).toFixed(3)} 0 0 0 0
                           0 ${(rgb[1] / 255).toFixed(3)} 0 0 0
                           0 0 ${(rgb[2] / 255).toFixed(3)} 0 0
                           0 0 0 0.8 0`
                    })
                    if (options.stroke) {
                        attr(rect, {
                            stroke: `rgba(${rgb[0]},${rgb[1]},${rgb[2]},1)`,
                        })
                    }
                }
            })
        }

    }
}

function clearSingleTaskHighLight(container, id) {
    let completeTasks = container.querySelectorAll(`[data-element-type="bpmn:userTask"][data-element-id="${id}"]`)
    if (completeTasks.length > 0) {
        completeTasks.forEach(completeTask => {
            let feColorMatrix = completeTask.querySelector('.djs-visual feColorMatrix')
            let rect = completeTask.querySelector('.djs-visual rect')
            let title1 = completeTask.querySelectorAll('.djs-visual rect')[1]
            let title2 = completeTask.querySelectorAll('.djs-visual rect')[2]
            if (rect) {
                attr(rect, {
                    stroke: '#ececec',
                })
            }
            if (title1) {
                attr(title1, {
                    fill: "#aaa",
                })
            }
            if (title2) {
                attr(title2, {
                    fill: "#aaa",
                })
            }
            if (feColorMatrix) {
                attr(feColorMatrix, {
                    values: `0 0 0 0 0
              0 0 0 0 0
              0 0 0 0 0
              0 0 0 0.1 0`
                })
            }
        })
    }
}

function hexToRgb(hex) {
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

function utils() {
    let taskHighlightTimer = {}
    this.getTaskObj = function (container, id) {
        let completeTask = container.querySelector(`[data-element-type="bpmn:userTask"][data-element-id="${id}"]`)
        let task_container = container.querySelector(`[class="djs-element djs-shape"][data-element-id="${id}"]`)
        if(completeTask){
            let title1 = completeTask.querySelectorAll('.djs-visual rect')[1]
            if (title1) {
                let businessObject = Object.assign({container:task_container}, {color: title1.style.fill})
                return businessObject
            }
        }
        return null
    }

    this.setTaskHighlight = function (container, ids, options = {
        color: '#5BC14B',
        setline: false,
        user: undefined,
        shadow: false,
        stroke: true
    }) {
        ids.forEach(id => {
            this.clearHighLight(container, id)
            const xx = setSingleTaskHighLight(container, id, options)
            if (xx) {
                taskHighlightTimer[id] = xx
            }
            if (options.setline) {
                this.setFlowHighLight(container, id, options)
            }
        })
    }

    this.setFlowHighLight = function (container, id, options = {color: '#5BC14B'}) {
        let paths = container.querySelectorAll(`[data-targetRef-id="${id}"]`)
        if (paths) {
            paths.forEach(c => {
                let path = c.querySelector('path')
                attr(path, {
                    stroke: options.color
                })
                classes(path).add('highlight-custom-path')
            })

        }

    }

    this.clearAllFlowHighLight = function (container) {
        let paths = container.querySelectorAll('.djs-connection path')
        paths.forEach(c => {
            attr(c, {
                stroke: '#ccc'
            })
            classes(c).remove('highlight-custom-path')
        })
    }

    this.clearFlowHighLight = function (container, id) {
        let paths = container.querySelectorAll(`[data-targetRef-id="${id}"]`)
        if (paths) {
            paths.forEach(c => {
                attr(c, {
                    stroke: '#ccc'
                })
                classes(c).remove('highlight-custom-path')
            })
        }
    }

    this.setAllHighLight = function (container) {
        this.clearAllHighLight(container)
        let tasks = container.querySelectorAll('.djs-shape')
        tasks.forEach(c => {
            setSingleTaskHighLight(container, c.getAttribute('data-element-id'))
        })
        this.setEndHighLight(container)
    }

    this.setEndHighLight = function (container, color = {stroke: '#db4744', fill: '#FD706D'}, setline = false) {
        let endEvents = container.querySelectorAll('[data-element-type="bpmn:EndEvent"]')
        if (endEvents.length > 0) {
            endEvents.forEach(c => {
                let circle = c.querySelector('circle')
                attr(circle, {
                    stroke: color.stroke,
                    fill: color.fill,
                })
                if (setline) {
                    this.setFlowHighLight(container, c.getAttribute('data-element-id'))
                }
            })
        }
    }

    this.clearAllHighLight = function (container) {
        if (taskHighlightTimer) {
            Object.keys(taskHighlightTimer).forEach(key => {
                window.clearInterval(taskHighlightTimer[key])
            })
        }
        taskHighlightTimer = {}
        let tasks = container.querySelectorAll('[data-element-type="bpmn:userTask"]')
        tasks.forEach(c => {
            clearSingleTaskHighLight(container, c.getAttribute('data-element-id'))
        })
        this.setEndHighLight(container, {stroke: '#8f8f8f', fill: '#aaa'})
        this.clearAllFlowHighLight(container)
    }

    this.clearHighLight = function (container, id) {
        if (taskHighlightTimer) {
            window.clearInterval(taskHighlightTimer[id])
        }
        clearSingleTaskHighLight(container, id)
        this.clearFlowHighLight(container, id)
    }

    this.setTaskMaxDay = function (container, id, day) {
        let text = container.querySelector(`[data-element-type="bpmn:userTask"][data-element-id="${id}"] .custom-max-day text`)
        if (text) {
            text.innerHTML = day
        }
    }

    this.setTaskRealName = function (container, id, day) {
        let text = container.querySelector(`[data-element-type="bpmn:userTask"][data-element-id="${id}"] .custom-realName text`)
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
        newViewbox = {
            x: (_x > 0 ? -20 : inner.x+_x)-offset.x,
            y: (_y > 0 ? -20 : inner.y+_y)-offset.y,
            width: outer.width / newScale,
            height: outer.height / newScale
        };
        canvas.viewbox(newViewbox);
    }
    this.taskSyncHighLight = function (container, bpmnObj, nv, options) {
        this.clearAllHighLight(container, bpmnObj)
        nv.forEach(c => {
            switch (c.status) {
                case '已办': {
                    if (c.approveType === '驳回') {
                        this.setTaskHighlight(container, [c.taskDefinitionKey], {
                            color: '#ff0000',
                            setline: false,
                            shadow: false,
                            type: 3,
                            stroke: true
                        })
                    } else {
                        this.setTaskHighlight(container, [c.taskDefinitionKey], {
                            color: '#5BC14B',
                            setline: false,
                            shadow: false,
                            type: 2,
                            stroke: true
                        })
                    }
                }
                    break;
                case '待办': {
                    this.setTaskHighlight(container, [c.taskDefinitionKey], {
                        color: '#f5842c',
                        setline: options.setline,
                        shadow: false,
                        type: 1,
                        stroke: true
                    })
                }
                    break;
            }
        })
        if (nv.filter(c => c.status === '待办').length === 0) {
            this.setEndHighLight(container, {stroke: '#5ac14a', fill: '#53D894'})
        }
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
            this.setTaskMaxDay(container, f.taskDefinitionKey, f.customTaskMaxDay + '天')
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
    this.focus=function(bpmnViewer,canvas,options,key){
        const _container =bpmnViewer._container
        const taskObj = this.getTaskObj(_container,key)
        if(taskObj){
            let matrix =taskObj.container.transform.baseVal[0].matrix
            let x = canvas.viewbox().inner.width/2 - matrix.e
            let y = canvas.viewbox().inner.height/2 - matrix.f
            this.setCenter(canvas,options,{x,y})
        }else{
            this.setCenter(canvas,options)
        }
    }
    this.log = function (data, obj) {
        obj.logfv.info(JSON.stringify(Object.assign({
                props: {
                    baseApi: obj.baseApi,
                    instanceId: obj.instanceId,
                    xmlId: obj.xmlId,
                    type: obj.type,
                    static: obj.static,
                    options: obj.myOptions
                }
            }, data)
        ))
    }
    this.error = function (data, obj) {
        obj.logfv.error(JSON.stringify(Object.assign({
                props: {
                    baseApi: obj.baseApi,
                    instanceId: obj.instanceId,
                    xmlId: obj.xmlId,
                    type: obj.type,
                    static: obj.static,
                    options: obj.myOptions
                }
            }, data)
        ))
    }
}

export default new utils()

