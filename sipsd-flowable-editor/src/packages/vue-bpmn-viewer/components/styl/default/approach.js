import {attr, classes} from "tiny-svg";
import axios from "axios";
let colorsArr = []
function setSingleTaskHighLight(container, id, options) {
    if (id) {
        let completeTasks = container.querySelectorAll(`[data-element-type="bpmn:userTask"][data-element-id="${id}"]`)
        if (completeTasks.length > 0) {
            completeTasks.forEach(completeTask => {
                let feColorMatrix = completeTask.querySelector('.djs-visual feColorMatrix')
                let rect = completeTask.querySelector('.djs-visual rect')
                let title1 = completeTask.querySelectorAll('.djs-visual rect')[1]
                let title2 = completeTask.querySelectorAll('.djs-visual rect')[2]
                let user = completeTask.querySelectorAll('.djs-visual .d-userTask-content .d-con-user span')[0]
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
                    fill: colorsArr[0],
                })
            }
            if (title2) {
                attr(title2, {
                    fill: colorsArr[0],
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
export let setTaskHighlight = function (container, ids, options = {
    color: '#5BC14B',
    setline: false,
    user: undefined,
    shadow: false,
    stroke: true
}) {
    ids.forEach(id => {
        clearHighLight(container, id)
        setSingleTaskHighLight(container, id, options)
        if (options.setline) {
            setFlowHighLight(container, id, options)
        }
    })
}
export let setEndHighLight = function (container, color = {stroke: '#db4744', fill: '#FD706D'}, setline = false) {
    let endEvents = container.querySelectorAll('[data-element-type="bpmn:EndEvent"]')
    if (endEvents.length > 0) {
        endEvents.forEach(c => {
            let circle = c.querySelector('circle')
            attr(circle, {
                stroke: color.stroke,
                fill: color.fill,
            })
            if (setline) {
                setFlowHighLight(container, c.getAttribute('data-element-id'))
            }
        })
    }
}
export let setFlowHighLight = function (container, id, options = {color: '#5BC14B'}) {
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
export let clearHighLight = function (container, id) {
    clearSingleTaskHighLight(container, id)
    clearFlowHighLight(container, id)
}
export let clearFlowHighLight = function (container, id) {
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
export let taskSyncHighLight = function (container, bpmnObj, nv, options,colors) {
    colorsArr = colors
    clearAllHighLight(container)
    if(nv&&nv.length>0){
        nv.forEach(c => {
            switch (c.status) {
                case '已办': {
                    if (c.approveType === '驳回') {
                        setTaskHighlight(container, [c.taskDefinitionKey], {
                            color: colorsArr[3],
                            setline: false,
                            shadow: false,
                            type: 3,
                            stroke: true
                        })
                    } else {
                        setTaskHighlight(container, [c.taskDefinitionKey], {
                            color: colorsArr[2],
                            setline: false,
                            shadow: false,
                            type: 2,
                            stroke: true
                        })
                    }
                }
                    break;
                case '待办': {
                    setTaskHighlight(container, [c.taskDefinitionKey], {
                        color: colorsArr[1],
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
            setEndHighLight(container, {stroke: '#5ac14a', fill: '#53D894'})
        }
    }
}
export let setStartTaskHighlight = function (container, ids, options = {
    color: '#5BC14B',
    setline: false,
    user: undefined,
    shadow: false,
    stroke: true
}) {
    ids.forEach(id => {
        this.clearHighLight(container, id)
        const xx = setStaetSingleTaskHighLight(container, id, options)
        if (xx) {
            taskHighlightTimer[id] = xx
        }
        if (options.setline) {
            this.setFlowHighLight(container, id, options)
        }
    })
}
export let clearAllFlowHighLight = function (container) {
    let paths = container.querySelectorAll('.djs-connection path')
    paths.forEach(c => {
        attr(c, {
            stroke: '#ccc'
        })
        classes(c).remove('highlight-custom-path')
    })
}
export let clearAllHighLight = function (container) {
    let tasks = container.querySelectorAll('[data-element-type="bpmn:userTask"]')
    tasks.forEach(c => {
        clearSingleTaskHighLight(container, c.getAttribute('data-element-id'))
    })
    setEndHighLight(container, {stroke: '#8f8f8f', fill: '#aaa'})
    clearAllFlowHighLight(container)
}
