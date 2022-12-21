import {append, attr, create} from "tiny-svg";

export default (options)=>{
    const g = create('g');
    const circle = create('circle',{
        cx:options.width/2,
        cy:options.height/2,
        r:options.width/2,
        stroke:'#c40227',
        strokeWidth:1,
        transform:"translate(0.5,0.5)",
        fill:'#c40227',
    })
    let text = create('text',{
        x:(options.width/2)-12,
        y:options.height+20,
        'font-size':'12px',
        fill: "#444"
    })
    if(options&&options.businessObject&&options.businessObject.name){
        text.innerHTML=''
    }else{
        text.innerHTML=options.title||'开始'
    }
    append(g,text)
    append(g,circle)

    let star = create('svg',{
        viewBox:"-128 -128 1024 1024",
        width:24,
        height:24,
    })

    let p1 = create('path',{
       d:'M923.2 429.6H608l-97.6-304-97.6 304H97.6l256 185.6L256 917.6l256-187.2 256 187.2-100.8-302.4z',
       fill:'#FAD97F'
    })
    star.append(p1)
    append(g,star)
    return g
}
