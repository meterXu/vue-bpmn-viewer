import {append, classes, create} from "tiny-svg";

export default (options)=>{
    const g = create('g',{
        'data-sourceRef-id':options.sourceRefId,
        'data-targetRef-id':options.targetRefId
    });
    const path = create('path',{
        d:options.d,
        stroke:'#ccc',
        strokeWidth:2,
        transform:"translate(0.5,0.5)",
        fill:'#000',
        fillOpacity:0,
        pointerEvents:'stroke',
        strokeMiterlimit:10,
        markerEnd: options.flowEnd,
        strokeLinejoin: 'butt',
    })
    if(options.complete){
        classes(path).add('highlight-custom-path')
    }
    append(g,path)
    g.setAttribute('businessObject',options.businessObject)
    return g
}
