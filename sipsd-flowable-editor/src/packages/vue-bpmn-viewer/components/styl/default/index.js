import './css/index.css'
import custom from './custom'
import {setTaskHighlight,setEndHighLight,setFlowHighLight,clearHighLight,clearFlowHighLight,taskSyncHighLight} from './approach'
export default {
    __init__: ['bpmn-theme-default'],
    'bpmn-theme-default': ['type', custom],
    'utils':{
        setTaskHighlight:setTaskHighlight,
        // setStartHighLight:null,
        setEndHighLight:setEndHighLight,
        setFlowHighLight:setFlowHighLight,
        // clearStartHighLight:null,
        // clearEndHighLight:null,
        clearHighLight:clearHighLight,
        clearFlowHighLight:clearFlowHighLight,
        taskSyncHighLight: taskSyncHighLight
    },
    'colors':["#8f8f8f","#f5842c","#5BC14B","#ff0000"]
}
