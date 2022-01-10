import {getDi} from "bpmn-js/lib/draw/BpmnRenderUtil";
import {append, classes, create} from "tiny-svg";
export default (options)=>{
    const g = create('g',{
        'data-element-type':'bpmn:userTask'
    });
    const rect = create('rect',{
        x:0,
        y:0,
        width:options.width,
        height:options.height,
        rx:4,
        ry:4,
        stroke:'#ececec',
        collapsed:false,
        strokeWidth:1,
        transform:"translate(0.5,0.5)",
        fill:'#fff',
        filter:`url(#f1_${options.element.id})`
    })
    options.element.collapsed=false
    const filter = create('filter',{
        id:"f1_"+options.element.id,
        x:0,
        y:0,
        width:"200%",
        height:"200%"
    })
    append(filter,create('feOffset',{
        result:"offOut",
        in:"SourceGraphic",
        dx:1,
        dy:1
    }))
    let feColorMatrix = create('feColorMatrix',{
        result:"matrixOut",
        in:"offOut",
        type:"matrix",
        values:`0 0 0 0 0
              0 0 0 0 0
              0 0 0 0 0
              0 0 0 0.1 0`
    })
    append(filter,feColorMatrix)
    classes(feColorMatrix).add('highlight-custom-rect')
    append(filter,create('feGaussianBlur',{
        result:"blurOut",
        in:"matrixOut",
        stdDeviation:3
    }))
    append(filter,create('feBlend',{
        in:"SourceGraphic",
        in2:"blurOut",
        mode:"normal"
    }))
    append(g,append(create('defs'),filter))
    append(g,rect)
    // 创建标题
    append(g,create('rect',{
        x:1,
        y:1,
        width:options.width-1,
        height:24,
        fill: "#aaaaaa",
        rx:4,
        ry:4
    }))
    let titleRect = create('rect',{
        x:1,
        y:5,
        width:options.width-1,
        height:20,
        fill: "#aaaaaa",
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
