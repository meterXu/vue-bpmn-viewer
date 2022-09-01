import {getDi} from "bpmn-js/lib/draw/BpmnRenderUtil";
import {append, classes, create} from "tiny-svg";
export default (options)=>{
    const g = create('g',{
        'data-element-type':'bpmn:UserTask'
    });
    const rect = create('rect',{
        x:0,
        y:0,
        width:options.width,
        height:options.height,
        rx:4,
        ry:4,
        stroke:'#407080',
        collapsed:false,
        strokeWidth:1,
        transform:"translate(0.5,0.5)",
        fill:'#00344C',

    })
    options.element.collapsed=false
    append(g,rect)
    // 创建标题
    append(g,create('rect',{
        x:1,
        y:1,
        width:options.width-1,
        height:24,
        fill: "#00344C",
        rx:4,
        ry:4
    }))
    let titleRect = create('rect',{
        x:1,
        y:5,
        width:options.width-1,
        height:20,
        fill: "#00344C",
        rx:0,
        ry:0
    })
    append(g,titleRect)
    let foreignObject = create('foreignObject',{
        x:1,
        y:5,
        width:options.width-1,
        height:20,
    })
    let div = document.createElement("div");
    div.classList.add('d-subProcess-title')
    div.innerText='子流程'
    div.title=div.innerText
    append(foreignObject,div)
    append(g,foreignObject)
    return g
}
