import './css/index.css'
import custom from './custom'
export default {
    __init__: ['bpmn-theme-default'],
    'bpmn-theme-default': ['type', custom],
    'utils':{
        setTaskHighlight:null,
        setStartHighLight:null,
        setEndHighLight:null,
        setFlowHighLight:null,
        clearStartHighLight:null,
        clearEndHighLight:null,
        clearHighLight:null,
        clearFlowHighLight:null
    },
    'colors':["#8f8f8f","#f5842c","#5BC14B","#ff0000"]
}
