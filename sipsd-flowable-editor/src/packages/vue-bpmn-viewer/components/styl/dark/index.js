import './css/index.css'
import style from './css/style.js'
import custom from './custom'
import {
    clearFlowHighLight,
    clearHighLight,
    setEndHighLight,
    setFlowHighLight, setStartTaskHighlight,
    setTaskHighlight, taskSyncHighLight
} from "../dark/approach.js";
export default {
    __init__: ['bpmn-theme-blue'],
    'bpmn-theme-blue': ['type', custom],
    'utils':{
        setTaskHighlight:setTaskHighlight,
        setStartHighLight:null,
        setEndHighLight:setEndHighLight,
        setFlowHighLight:setFlowHighLight,
        clearStartHighLight:null,
        clearEndHighLight:null,
        setStartTaskHighlight: setStartTaskHighlight,
        clearHighLight:clearHighLight,
        clearFlowHighLight:clearFlowHighLight,
        taskSyncHighLight: taskSyncHighLight
    },
    'colors':["#8f8f8f","#f5842c","#5BC14B","#ff0000"],
    'style':style
}
