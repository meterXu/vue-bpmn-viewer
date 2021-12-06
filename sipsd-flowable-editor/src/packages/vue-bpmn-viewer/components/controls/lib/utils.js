import {attr, classes} from "tiny-svg";

function setSingleTaskHighLight(container,id,options) {
    if (id) {
        let completeTasks = container.querySelectorAll(`[data-element-type="bpmn:userTask"][data-element-id="${id}"]`)
        if(completeTasks.length>0){
            completeTasks.forEach(completeTask=>{
                let feColorMatrix = completeTask.querySelector('.djs-visual feColorMatrix')
                let rect = completeTask.querySelector('.djs-visual rect')
                let title1 = completeTask.querySelectorAll('.djs-visual rect')[1]
                let title2 = completeTask.querySelectorAll('.djs-visual rect')[2]
                let user = completeTask.querySelectorAll('.djs-visual text')[1]
                if(options.user){
                    user.innerHTML=options.user
                }else {
                    attr(user,{
                        fill:'#cdcdcd'
                    })
                }
                if(title1){
                    attr(title1, {
                        fill: options.color,
                    })
                }
                if(title2){
                    attr(title2, {
                        fill:options.color,
                    })
                }
                if (options.shadow&&feColorMatrix && rect) {
                    const rgb = hexToRgb(options.color)
                    attr(feColorMatrix, {
                        values: `${(rgb[0]/255).toFixed(3)} 0 0 0 0
                           0 ${(rgb[1]/255).toFixed(3)} 0 0 0
                           0 0 ${(rgb[2]/255).toFixed(3)} 0 0
                           0 0 0 0.8 0`
                    })
                    if(options.stroke){
                        attr(rect, {
                            stroke: `rgba(${rgb[0]},${rgb[1]},${rgb[2]},1)`,
                        })
                    }
                }
            })
        }

    }
}

function clearSingleTaskHighLight(container,id) {
    let completeTasks = container.querySelectorAll(`[data-element-type="bpmn:userTask"][data-element-id="${id}"]`)
    if(completeTasks.length>0){
        completeTasks.forEach(completeTask=>{
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
                    fill: "#3296fa",
                })
            }
            if (title2) {
                attr(title2, {
                    fill: "#3296fa",
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
    if(hex.indexOf('#')===0){
        let r = parseInt('0x' + hex.slice(1, 3))
        let g = parseInt('0x' + hex.slice(3, 5))
        let b = parseInt('0x' + hex.slice(5, 7))
        return [
            r,
            g,
            b,
        ]
    }else{
        let color = hex.match(/\d+/g)
        return [
            color[0],
            color[1],
            color[2]
        ]
    }
}

function utils(){
    let taskHighlightTimer = {}

    this.getTaskObj=function (container,id){
        let res = []
        let completeTasks = container.querySelectorAll(`[data-element-type="bpmn:userTask"][data-element-id="${id}"]`)
        if(completeTasks.length>0){
            completeTasks.forEach(completeTask=>{
                let title1 = completeTask.querySelectorAll('.djs-visual rect')[1]
                if(title1){
                    let businessObject = Object.assign({},{color:title1.style.fill})
                    res.push(businessObject)
                }
            })
        }
        return res
    }

    this.setTaskHighlight=function(container,ids,options={color:'#5BC14B',setline:false,user:undefined,shadow:false,stroke:true}) {
        ids.forEach(id => {
            this.clearHighLight(container,id)
            const xx =setSingleTaskHighLight(container,id,options)
            if(xx){
                taskHighlightTimer[id]=xx
            }
            if(options.setline){
                this.setFlowHighLight(container,id,options)
            }
        })
    }

    this.setFlowHighLight=function(container,id,options={color:'#5BC14B'}) {
        let paths = container.querySelectorAll(`[data-targetRef-id="${id}"]`)
        if (paths) {
            paths.forEach(c=>{
                let path = c.querySelector('path')
                attr(path,{
                    stroke:options.color
                })
                classes(path).add('highlight-custom-path')
            })

        }

    }

    this.clearAllFlowHighLight=function(container) {
        let paths = container.querySelectorAll('.djs-connection path')
        paths.forEach(c => {
            attr(c,{
                stroke:'#ccc'
            })
            classes(c).remove('highlight-custom-path')
        })
    }

    this.clearFlowHighLight=function(container,id){
        let paths = container.querySelectorAll(`[data-targetRef-id="${id}"]`)
        if(paths){
            paths.forEach(c => {
                attr(c,{
                    stroke:'#ccc'
                })
                classes(c).remove('highlight-custom-path')
            })
        }
    }

    this.setAllHighLight=function(container) {
        this.clearAllHighLight(container)
        let tasks = container.querySelectorAll('.djs-shape')
        tasks.forEach(c => {
            setSingleTaskHighLight(container,c.getAttribute('data-element-id'))
        })
        this.setEndHighLight(container)
    }

    this.setEndHighLight=function(container,color = {stroke: '#db4744', fill: '#FD706D'},setline=false) {
        let endEvents = container.querySelectorAll('[data-element-type="bpmn:EndEvent"]')
        if(endEvents.length>0){
            endEvents.forEach(c=>{
                let circle = c.querySelector('circle')
                attr(circle, {
                    stroke: color.stroke,
                    fill: color.fill,
                })
                if(setline){
                    this.setFlowHighLight(container,c.getAttribute('data-element-id'))
                }
            })
        }
    }

    this.clearAllHighLight=function(container) {
        if(taskHighlightTimer){
            Object.keys(taskHighlightTimer).forEach(key => {
                window.clearInterval(taskHighlightTimer[key])
            })
        }
        taskHighlightTimer = {}
        let tasks = container.querySelectorAll('[data-element-type="bpmn:userTask"]')
        tasks.forEach(c => {
            clearSingleTaskHighLight(container,c.getAttribute('data-element-id'))
        })
        this.setEndHighLight(container,{stroke: '#aaa', fill: '#d1d1d1'})
        this.clearAllFlowHighLight(container)
    }

    this.clearHighLight=function(container,id) {
        if(taskHighlightTimer){
            window.clearInterval(taskHighlightTimer[id])
        }
        clearSingleTaskHighLight(container,id)
        this.clearFlowHighLight(container,id)
    }

    this.setTaskMaxDay=function(container,id,day){
        let text = container.querySelector(`[data-element-type="bpmn:userTask"][data-element-id="${id}"] .custom-max-day text`)
        if(text){
            text.innerHTML = day
        }
    }

    this.setTaskRealName=function(container,id,day){
        let text = container.querySelector(`[data-element-type="bpmn:userTask"][data-element-id="${id}"] .custom-realName text`)
        if(text){
            text.innerHTML = day
        }
    }

    this.setCenter=function(canvas) {
        let vbox = canvas.viewbox(),
            outer = vbox.outer,
            inner = vbox.inner,
            newScale =1,
            newViewbox;
        newViewbox = {
            x: inner.x + (inner.width / 2 - (outer.width-306) / newScale / 2 ),
            y: inner.y + (inner.height / 2 - outer.height / newScale / 2),
            width: outer.width / newScale,
            height: outer.height / newScale
        };
        canvas.viewbox(newViewbox);
    }
}

export default new utils()

