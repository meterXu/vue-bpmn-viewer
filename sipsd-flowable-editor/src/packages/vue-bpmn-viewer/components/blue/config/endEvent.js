import {append, attr, create} from "tiny-svg";

export default (options)=>{
    const g = create('g',{
        'data-element-type':'bpmn:EndEvent'
    });
    const circle = create('circle',{
        cx:options.width/2,
        cy:options.height/2,
        r:options.width/2,
        strokeWidth:1,
        transform:"translate(0.5,0.5)",
        fill:'#d1d1d1',
        stroke:'#aaa'
        // filter:"url(#f1)"
    })
    let text = create('text',{
        x:(options.width/2)-12,
        y:options.height+20,
        fontSize:'12px',
        fill: "#444"
    })
    if(options&&options.businessObject&&options.businessObject.name){
        text.innerHTML=''
    }else{
        text.innerHTML=options.title||'结束'
    }
    append(g,text)
    append(g,circle)

    let ok = create('path')
    attr(ok,{d:'M45.6,55.2c2.7-4.8,12-13.3,18.7-17.7l2.6-1.6c1-0.5,1.9-0.9,2.7-1.1c-2.1-4.6-0.3-8.3,0-12.6l0,0\n' +
            '\tc-2.5,1.2-5,3-7.4,5l-2.8,2.5C51.4,37.1,45,46.3,45,46.3l-4.7-8.9l-10.7,5.1C34.1,44.1,41,49.7,45.6,55.2L45.6,55.2z',
        fill:'#fff',
        transform:'matrix(0.5 0 0 0.5 -11 -4)'
        })
    append(g,ok)
    return g
}
