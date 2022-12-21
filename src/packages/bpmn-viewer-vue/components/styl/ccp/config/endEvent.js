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
        fill:'#aaa',
        stroke:'#8f8f8f'
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

    let svg = create('svg',{
        viewBox:'-120 -120 1024 1024',
        width:24,
        height:24,
    })
    let p1 = create('path',{
        d:'M426.3 181.4l-70.7 40.8c-6.5 3.8-11.2 10.1-12.7 17.5-3 14.5-11.8 27.7-25.5 35.7s-29.6 9-43.7 4.3c-7.1-2.4-14.9-1.5-21.5 2.3l-56.4 33.6c-6.4 3.7-8.6 12-4.9 18.4l67.3 116.6c3.7 6.4 12 8.6 18.4 4.9l56.4-33.6c6.5-3.8 11.2-10.1 12.7-17.5 3-14.5 11.8-27.7 25.5-35.7 46.6-26.9 84.2 24.7 92.8 39.7l53.8 93.3c10.6 74.8 22.5 107 71.3 176.4 88.1 125.3 79.1 139.1 93.8 165.2 14.8 26.1 40.7 36.9 58.6 26.5l77.2-44.6c18-10.4 21.6-38.2 6.4-64-15.3-25.8-31.7-25-96.2-163.9C693 520 671 494.8 611 447.8l-53.8-93.3c-23.7-41-16.2-83.9 30-110.5 46.6-26.9 78.2-30.5 117.8-9l16.3-22.5c-51-82.9-224.7-71.6-295-31.1z m278.4 427.2c65.6 141.3 83.7 143 97.4 166.2 3.8 6.4 9 23.8 3.4 27l-77.2 44.6c-5.7 3.3-18.1-10-21.7-16.4-13.2-23.4-5.8-40.1-95.3-167.5-42.9-61-54.5-90-63.6-147l61.5-35.5c44.8 36.4 64.1 61 95.5 128.6z m-130.9-388c-56.9 32.8-72.5 90.7-39.8 147.3l53.8 93.3-46.6 26.9-53.8-93.3c-25.9-44.9-77.5-79.6-129.6-49.5-19.8 11.4-33.8 30.9-38.2 53.1l-45.1 27.1-53.9-93.3 44.6-27.1c21.9 7.3 45.8 4.9 65.6-6.5 19.8-11.4 33.8-30.9 38.2-53.1l70.7-40.8c32-18.5 90.8-29 143-25.6 19.7 1.3 48.1 5.1 73.9 16.2-26.1-0.4-52.8 8-82.8 25.3z',
        fill:'#ddd'
    })
    append(svg,p1)
    append(g,svg)
    return g
}
