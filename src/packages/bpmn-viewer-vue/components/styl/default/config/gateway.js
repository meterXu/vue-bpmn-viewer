import {
    append,
    create,
} from 'tiny-svg';

const iconMap={
    'bpmn:ParallelGateway':parallelGatewayIcon,
    'bpmn:ExclusiveGateway':exclusiveGatewayIcon,
    'bpmn:InclusiveGateway':inclusiveGatewayIcon,
    'bpmn:EventGateway':eventGatewayIcon
}

export default (options)=>{
    const type = options.element.type
    let g = create('g')
    let rect = create('rect',{
        x:0,
        y:0,
        width:options.width/Math.sin(Math.PI / 4)/2,
        height:options.height/Math.sin(Math.PI / 4)/2,
        fillOpacity:0,
        stroke:'#9399B2',
        strokeWidth:1,
        transform:`matrix(${Math.cos(Math.PI/4)},${Math.sin(Math.PI / 4)},-${Math.sin(Math.PI / 4)},${Math.cos(Math.PI/4)},20,0)`,
    })
    append(g,rect)

    let linearGradie = createLinearGradient()
    append(g,linearGradie)
    let icon = iconMap[type]()
    append(g,icon)
    return g
}

function parallelGatewayIcon(){
    let icon = create('path',{
        d:'M64,52H52v12c0,1.1-0.9,2-2,2s-2-0.9-2-2V52H36c-1.1,0-2-0.9-2-2s0.9-2,2-2h12V36c0-1.1,0.9-2,2-2s2,0.9,2,2v12  h12c1.1,0,2,0.9,2,2S65.1,52,64,52z',
        fill:'url(#SVGID_1_)',
        transform:'matrix(0.6,0,0,0.6,-10,-10)'
    })
    return icon
}

function exclusiveGatewayIcon(){
    let icon = create('path',{
        d:'M52.8,50l8.5,8.5c0.8,0.8,0.8,2,0,2.8c-0.8,0.8-2,0.8-2.8,0L50,52.8l-8.5,8.5c-0.8,0.8-2,0.8-2.8,0  c-0.8-0.8-0.8-2,0-2.8l8.5-8.5l-8.5-8.5c-0.8-0.8-0.8-2,0-2.8c0.8-0.8,2-0.8,2.8,0l8.5,8.5l8.5-8.5c0.8-0.8,2-0.8,2.8,0  c0.8,0.8,0.8,2,0,2.8L52.8,50z',
        fill:'url(#SVGID_1_)',
        transform:'matrix(0.6,0,0,0.6,-10,-10)'
    })
    return icon
}

function  inclusiveGatewayIcon(){
    let icon = create('path',{
        d:'M50,66c-8.8,0-16-7.2-16-16s7.2-16,16-16s16,7.2,16,16S58.8,66,50,66z M50,38c-6.6,0-12,5.4-12,12s5.4,12,12,12  s12-5.4,12-12S56.6,38,50,38z',
        fill:'url(#SVGID_1_)',
        transform:'matrix(0.6,0,0,0.6,-10,-10)'
    })
    return icon
}

function eventGatewayIcon(){
    let icon = create('path',{
        d:'M52.7,35.3L52.7,35.3L52.7,35.3 M50,40.6l2.1,4.2c0.7,1.5,2.2,2.5,3.9,2.7l4.8,0.7l-3.4,3.3  c-1.2,1.2-1.8,2.8-1.5,4.4l0.8,4.6l-4.3-2.2c-0.7-0.4-1.6-0.6-2.4-0.6c-0.8,0-1.7,0.2-2.4,0.6l-4.3,2.2l0.8-4.6  c0.3-1.6-0.3-3.3-1.5-4.4l-3.4-3.3l4.8-0.7c1.7-0.2,3.1-1.3,3.9-2.7L50,40.6 M50,33c-0.4,0-0.8,0.2-1,0.6l-4.8,9.5  c-0.2,0.3-0.5,0.5-0.8,0.6l-10.7,1.5c-0.9,0.1-1.2,1.2-0.6,1.8l7.7,7.4c0.3,0.2,0.4,0.6,0.3,0.9l-1.8,10.4c-0.1,0.7,0.4,1.2,1.1,1.2  c0.2,0,0.3,0,0.5-0.1l9.6-4.9c0.2-0.1,0.3-0.1,0.5-0.1c0.2,0,0.3,0,0.5,0.1l9.6,4.9c0.2,0.1,0.3,0.1,0.5,0.1c0.6,0,1.2-0.6,1.1-1.2  l-1.8-10.4c-0.1-0.3,0.1-0.7,0.3-0.9l7.7-7.4c0.6-0.6,0.3-1.7-0.6-1.8l-10.7-1.5c-0.4-0.1-0.7-0.3-0.8-0.6L51,33.6  C50.8,33.2,50.4,33,50,33L50,33z',
        fill:'url(#SVGID_1_)',
        transform:'matrix(0.6,0,0,0.6,-10,-10)'
    })
    return icon
}

function createLinearGradient(){
    if(!document.querySelector('SVGID_1_')){
        let linearGradient = create('linearGradient',{
            id:'SVGID_1_',
            gradientUnits:'userSpaceOnUse',
            x1:'9.2969',
            y1:'86.3438',
            x2:'9.2969',
            y2:'87.3438',
            gradientTransform:'matrix(32 0 0 -32 -247.5 2829)'
        })
        append(linearGradient,create('stop',{
            offset:0,
            'stop-color':'#B0B8D5'
        }))
        append(linearGradient,create('stop',{
            offset:1,
            'stop-color':'#81869D'
        }))
        return linearGradient
    }
}
