import {append, attr, create} from "tiny-svg";

export default (options)=>{
    const g = create('g');
    const circle = create('circle',{
        cx:options.width/2,
        cy:options.height/2,
        r:options.width/2,
        stroke:'#5ac14a',
        strokeWidth:1,
        transform:"translate(0.5,0.5)",
        fill:'#fff',
        // filter:"url(#f1)"
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
    return g
}
