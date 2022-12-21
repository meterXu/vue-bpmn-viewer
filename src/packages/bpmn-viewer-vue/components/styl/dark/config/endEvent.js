import {append, attr, create} from "tiny-svg";

export default (options)=>{
    const g = create('g',{
        'data-element-type':'bpmn:EndEvent'
    });
    const circle = create('circle',{
        cx:options.width/2,
        cy:options.height/2,
        r:options.width/2,
        strokeWidth:2,
        transform:"translate(0.5,0.5)",
        fill:'#042f43',
        stroke:'#6f6f6f',
    })
    let text = create('text',{
        x:(options.width/2)-12,
        y:options.height+20,
        fontSize:'12px',
        fill: "#b6b6b6"
    })
    if(options&&options.businessObject&&options.businessObject.name){
        text.innerHTML=''
    }else{
        text.innerHTML=options.title||'结束'
    }
    append(g,text)
    append(g,circle)

    let ok = create('svg',{
        viewBox:"0 0 1024 1024",
        width:'30',
        height:'30'
    })
    let p1 = create('path',{
        d:'M512 640l-104.5632 54.976a25.6 25.6 0 0 1-37.1456-26.9952l19.968-116.4288-84.5824-82.4576a25.6 25.6 0 0 1 14.1824-43.6608l116.9024-16.9856 52.288-105.9328a25.6 25.6 0 0 1 45.9008 0l52.288 105.9328 116.9024 16.9856a25.6 25.6 0 0 1 14.1824 43.6608l-84.5824 82.4576 19.968 116.4288a25.6 25.6 0 0 1-37.1456 26.9952L512 640z',
        fill:'#b6b6b6'
    })
    ok.append(p1)
    append(g,ok)
    return g
}
