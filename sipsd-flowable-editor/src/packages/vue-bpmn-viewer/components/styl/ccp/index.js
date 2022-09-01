import './css/index.css'
import style from './css/style.js'
import custom from './custom'
import {setTaskHighlight,setEndHighLight,setFlowHighLight,
    clearHighLight,clearFlowHighLight,taskSyncHighLight,
    setStartTaskHighlight,clearAllFlowHighLight,clearAllHighLight} from './approach'
export default {
    __init__: ['bpmn-theme-ccp'],
    'bpmn-theme-ccp': ['type', custom],
    'utils':{
        setTaskHighlight:setTaskHighlight,
        setEndHighLight:setEndHighLight,
        setFlowHighLight:setFlowHighLight,
        clearAllHighLight:clearAllHighLight,
        clearAllFlowHighLight: clearAllFlowHighLight,
        clearHighLight:clearHighLight,
        clearFlowHighLight:clearFlowHighLight,
        taskSyncHighLight: taskSyncHighLight,
        setStartTaskHighlight: setStartTaskHighlight
    },
    'colors':["#8f8f8f","#f5842c","#5BC14B","#ff0000"],
    'style':style
}
