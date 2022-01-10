import BaseRenderer from 'diagram-js/lib/draw/BaseRenderer'
import {
    append,
    append as svgAppend, attr, create,remove
} from 'tiny-svg';
import {
    assign,
} from 'min-dash';
import customConfig from './config'
import {getDi} from "bpmn-js/lib/draw/BpmnRenderUtil";

const HIGH_PRIORITY = 1500 // 最高优先级
const customElements = Object.keys(customConfig)
function createPathFromConnection(connection) {
    let waypoints = connection.waypoints;

    let pathData = 'm  ' + waypoints[0].x + ',' + waypoints[0].y;
    for (let i = 1; i < waypoints.length; i++) {
        pathData += 'L' + waypoints[i].x + ',' + waypoints[i].y + ' ';
    }
    return pathData;
}
let _marker = null
function addMarker(id,parentNode, options){
    let attrs = assign({
        fill: 'black',
        strokeWidth: 1,
        strokeLinejoin: 'butt',
        strokeDasharray: 'none'
    }, options.attrs);
    let ref = options.ref || { x: 0, y: 0 };
    let scale = options.scale || 1;
    if (attrs.strokeDasharray === 'none') {
        attrs.strokeDasharray = [10000, 1];
    }
    let marker = create('marker');
    attr(options.element, attrs);
    append(marker, options.element);
    attr(marker, {
        id: id,
        viewBox: '0 0 20 20',
        refX: ref.x,
        refY: ref.y,
        markerWidth: 20 * scale,
        markerHeight: 20 * scale,
        orient: 'auto'
    });
    append(parentNode, marker);
    return marker
}

function marker(parentNode, fill, stroke) {
    let id = "xxxlll";
    if(!_marker){
        let sequenceflowEnd = create('path');
        attr(sequenceflowEnd, { d: 'M 1 5 L 11 10 L 1 15 L 5.6 10 Z' });
        _marker = addMarker(id,parentNode, {
            element: sequenceflowEnd,
            ref: { x: 11, y: 10 },
            scale: 0.5,
            attrs: {
                fill: fill,
                stroke: stroke
            }
        });
        return 'url(#' + id + ')';
    }else {
        return 'url(#' + id + ')';
    }
}

export default class CustomRenderer extends BaseRenderer { // 继承BaseRendere
    constructor(eventBus, bpmnRenderer) {
        super(eventBus, HIGH_PRIORITY)
        this.bpmnRenderer = bpmnRenderer
        this.eventBus = eventBus
    }

    canRender(element) {
        // ignore labels
        return !element.labelTarget
    }
    drawConnection(parentNode, element){
        let pathData = createPathFromConnection(element),sourceRefId='',targetRefId='';
        if(element.businessObject){
            sourceRefId = element.businessObject.sourceRef.id
            targetRefId = element.businessObject.targetRef.id
        }
        const line  = customConfig[element.type].draw({
            d:pathData.toUpperCase(),
            flowEnd:marker(parentNode, '#ccc', '#ccc'),
            sourceRefId:sourceRefId,
            targetRefId:targetRefId,
            complete:false,
            businessObject:element.businessObject
        })
        svgAppend(parentNode, line)
        return line

    }
    drawShape(parentNode, element) {
        console.log(element)
        const type = element.type // 获取到类型
        if (customElements.includes(type)) {
            if(type==='bpmn:SubProcess') {
                const shape = this.bpmnRenderer.drawShape(parentNode, element)
                const rect = drawRect(parentNode, element.width, element.height, 2, '#52B415');
                prependTo(rect, parentNode);
                remove(shape);
                return shape
            }
            const { draw} = customConfig[type]
            const customIcon = draw({
                width:element.width,
                height:element.height,
                businessObject:element.businessObject,
                parentNode,
                element
            })

            attr(customIcon,{
                'data-element-id':element.id
            })
            svgAppend(parentNode, customIcon)
            return customIcon
        }else{
            const di =getDi(element)
            di.set('bioc:stroke','#999')
            const shape = this.bpmnRenderer.drawShape(parentNode, element)
            // let shape
            // debugger
            // if(element.type==='bpmn:SubProcess') {
            //     // shape= this.bpmnRenderer.drawShape(parentNode, element)
            // } else {
            //     shape = this.bpmnRenderer.drawShape(parentNode, element)
            // }
            return shape
        }
    }

    getShapePath(shape) {
        return this.bpmnRenderer.getShapePath(shape)
    }
}

CustomRenderer.$inject = ['eventBus', 'bpmnRenderer']
// copied from https://github.com/bpmn-io/bpmn-js/blob/master/lib/draw/BpmnRenderer.js
function drawRect(parentNode, width, height, borderRadius, strokeColor) {
    const rect = create('rect');

    attr(rect, {
        width: width,
        height: height,
        rx: borderRadius,
        ry: borderRadius,
        stroke: strokeColor || '#000',
        strokeWidth: 2,
        fill: '#fff',
    });

    svgAppend(parentNode, rect);

    return rect;
}

// copied from https://github.com/bpmn-io/diagram-js/blob/master/lib/core/GraphicsFactory.js
function prependTo(newNode, parentNode, siblingNode) {
    parentNode.insertBefore(newNode, siblingNode || parentNode.firstChild);
}
