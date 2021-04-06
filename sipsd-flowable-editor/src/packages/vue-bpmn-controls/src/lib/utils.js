import {attr, classes} from "tiny-svg";

function setSingleTaskHighLight(id,options) {
    if (id) {
        let completeTask = document.querySelector(`[data-element-id="${id}"]`)
        if(completeTask){
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
            if (feColorMatrix && rect) {
                const rgb = hexToRgb(options.color)
                let t = 0, x = 0.1
                let xx = setInterval(() => {
                    t += x
                    attr(feColorMatrix, {
                        values: `${(rgb[0]/255).toFixed(3)} 0 0 0 0
                                 0 ${(rgb[1]/255).toFixed(3)} 0 0 0
                                 0 0 ${(rgb[2]/255).toFixed(3)} 0 0
                                 0 0 0 ${t} 0`
                    }, 100)
                    attr(rect, {
                        stroke: `rgba(${rgb[0]},${rgb[1]},${rgb[2]},${t})`,
                    })
                    if (t >= 1) {
                        x = -0.1
                    }
                    if (t <= 0) {
                        x = 0.1
                    }
                }, 100)
                return xx
            }
        }

    }
}

function clearSingleTaskHighLight(id) {
    let completeTask = document.querySelector(`[data-element-id="${id}"]`)
    if(completeTask){
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
    }
}

function hexToRgb(hex) {
    return [
        parseInt('0x' + hex.slice(1, 3)),
        parseInt('0x' + hex.slice(3, 5)),
        parseInt('0x' + hex.slice(5, 7))
    ]
}

function utils(){
    let taskHighlightTimer = {}
    this.setTaskHighlight=function(ids,options={color:'#5BC14B',setline:false,user:undefined}) {
        ids.forEach(id => {
            this.clearHighLight(id)
            const xx =setSingleTaskHighLight(id,options)
            if(xx){
                this.taskHighlightTimer[id]=xx
            }
            if(options.setline){
                this.setFlowHighLight(id,options)
            }
        })
    }

    this.setFlowHighLight=function(id,options={color:'#5BC14B'}) {
        let paths = document.querySelectorAll(`[data-targetRef-id="${id}"]`)
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

    this.clearAllFlowHighLight=function() {
        let paths = document.querySelectorAll('.djs-connection path')
        paths.forEach(c => {
            attr(c,{
                stroke:'#ccc'
            })
            classes(c).remove('highlight-custom-path')
        })
    }
    this.clearFlowHighLight=function(id){
        let paths = document.querySelectorAll(`[data-targetRef-id="${id}"]`)
        if(paths){
            paths.forEach(c => {
                attr(c,{
                    stroke:'#ccc'
                })
                classes(c).remove('highlight-custom-path')
            })
        }
    }

    this.setAllHighLight=function() {
        this.clearAllHighLight()
        let tasks = document.querySelectorAll('.djs-shape')
        tasks.forEach(c => {
            setSingleTaskHighLight(c.getAttribute('data-element-id'))
        })
        this.setEndHighLight()
    }


    this.setEndHighLight=function(color = {stroke: '#db4744', fill: '#FD706D'}) {
        let endEvents = document.querySelectorAll('[data-element-type="bpmn:EndEvent"]')
        if(endEvents.length>0){
            endEvents.forEach(c=>{
                let circle = c.querySelector('circle')
                attr(circle, {
                    stroke: color.stroke,
                    fill: color.fill,
                })
                this.setFlowHighLight(c.getAttribute('data-element-id'))
            })
        }
    }


    this.clearAllHighLight=function() {
        if(this.taskHighlightTimer){
            Object.keys(this.taskHighlightTimer).forEach(key => {
                window.clearInterval(this.taskHighlightTimer[key])
            })
        }
        this.taskHighlightTimer = {}
        let tasks = document.querySelectorAll('[data-element-type="bpmn:userTask"]')
        tasks.forEach(c => {
            clearSingleTaskHighLight(c.getAttribute('data-element-id'))
        })
        this.setEndHighLight({stroke: '#aaa', fill: '#d1d1d1'})
        this.clearAllFlowHighLight()
    }

    this.clearHighLight=function(id) {
        window.clearInterval(this.taskHighlightTimer[id])
        clearSingleTaskHighLight(id)
        this.clearFlowHighLight(id)
    }

    this.setTaskMaxDay=function(id,day){
        let text = document.querySelector(`[data-element-id="${id}"] .custom-max-day text`)
        if(text){
            text.innerHTML = day
        }
    }
}

export default new utils()

