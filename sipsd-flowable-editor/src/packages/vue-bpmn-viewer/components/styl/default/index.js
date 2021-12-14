import '../../../assets/theme/blue/css/custom.css'
import custom from './custom'
export default {
    __init__: ['bpmn-theme-blue'],
    'bpmn-theme-blue': ['type', custom],
    'utils':{
        setTaskHighlight:null,
        setStartHighLight:null,
        setEndHighLight:null,
        setFlowHighLight:null,
        clearStartHighLight:null,
        clearEndHighLight:null,
        clearHighLight:null,
        clearFlowHighLight:null
    }
}
