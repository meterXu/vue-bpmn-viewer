import {append, attr, create} from "tiny-svg";

export default (options)=>{
    const g = create('g');
    const circle = create('circle',{
        cx:options.width/2,
        cy:options.height/2,
        r:options.width/2,
        stroke:'#53d894',
        strokeWidth:2,
        transform:"translate(0.5,0.5)",
        fill:'#042f43'
    })
    let text = create('text',{
        x:(options.width/2)-12,
        y:options.height+20,
        'font-size':'12px',
        fill: "#b6b6b6"
    })
    if(options&&options.businessObject&&options.businessObject.name){
        text.innerHTML=''
    }else{
        text.innerHTML=options.title||'开始'
    }
    append(g,text)
    append(g,circle)

    const gg = create('svg');
    attr(gg,{
        viewBox:"-128 -128 1024 1024",
        width:'24',
        height:'24',

    })
    let p1 =  create('path',{
        d:'M281.9 536.2L294 816.7l72.4-164z',
        fill:'#53d894'
    })
    let p2 =  create('path',{
        d:'M294 834.1c-1.1 0-2.1-0.1-3.2-0.3-7.9-1.5-13.8-8.3-14.2-16.4l-12.1-280.5c-0.3-7.7 4.4-14.7 11.7-17.2 7.2-2.5 15.3 0 19.8 6.2l84.4 116.5c3.6 5 4.3 11.6 1.8 17.2l-72.4 164c-2.7 6.5-9 10.5-15.8 10.5z m7.8-240.8l6.4 148.1 38.2-86.6-44.6-61.5z',
        fill:'#4C4848'
    })
    let p3 =  create('path',{
        d:'M910.3 180.3L505.2 844.2 113.9 304.4z',
        fill:'#53d894'
    })
    let p4 =  create('path',{
        d:'M505.2 861.6c-5.6 0-10.8-2.7-14.1-7.2L99.8 314.6c-3.6-4.9-4.3-11.3-2-16.9 2.3-5.6 7.4-9.6 13.4-10.5l796.3-124.1c6.8-1 13.4 1.9 17.2 7.5 3.8 5.6 3.9 12.9 0.4 18.7L520 853.3c-3 5-8.3 8.1-14.2 8.3h-0.6zM144.7 317.2l359.2 495.6 371.9-609.5-731.1 113.9z',
        fill:'#4C4848'
    })
    let p5 =  create('path',{
        d:'M910.3 180.3L366.4 652.7l-84.5-116.5z',
        fill:'#53d894'
    })
    let p6 =  create('path',{
        d:'M366.4 670.1c-0.7 0-1.3 0-2-0.1-4.8-0.6-9.2-3.1-12.1-7.1l-84.4-116.5c-2.9-4-4-9.1-2.9-13.9 1.1-4.8 4.1-9 8.4-11.5l628.3-355.9c7.8-4.4 17.6-2.2 22.8 5.1 5.2 7.3 4 17.3-2.8 23.2L377.8 665.8c-3.2 2.8-7.3 4.3-11.4 4.3z m-59-128.4l61.9 85.3 398.4-346-460.3 260.7z',
        fill:'#4C4848'
    })
    let p7 =  create('path',{
        d:'M408.6 711L297.1 811.4l69.3-158.7z',
        fill:'#53d894'
    })
    let p8 =  create('path',{
        d:'M297.1 828.8c-3.6 0-7.1-1.1-10.2-3.3-6.6-4.8-9-13.6-5.8-21.1l69.2-158.7c2.5-5.7 7.9-9.7 14.1-10.3 6.3-0.7 12.3 2 16 7.1l42.2 58.3c5.2 7.2 4.2 17.2-2.4 23.1L308.8 824.3c-3.3 3-7.5 4.5-11.7 4.5z m73-141.2l-24.9 57 40-36.1-15.1-20.9z',
        fill:'#4C4848'
    })
    append(gg,p1)
    append(gg,p2)
    append(gg,p3)
    append(gg,p4)
    append(gg,p5)
    append(gg,p6)
    append(gg,p7)
    append(gg,p8)
    append(g,gg)
    return g
}
