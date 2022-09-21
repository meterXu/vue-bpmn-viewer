import Task from './task'
import startEvent from './startEvent'
import endEvent from "./endEvent";
import sequenceFlow from "./sequenceFlow";
import gateway from './gateway'
import subProcess from './subProcess'
const customConfig = {
    'bpmn:UserTask': {
        draw:Task
    },
    'bpmn:ScriptTask':{
        draw:Task
    },
    'bpmn:ServiceTask':{
        draw:Task
    },
    'bpmn:StartEvent':{
        draw:startEvent
    },
    'bpmn:EndEvent':{
        draw:endEvent
    },
    'bpmn:SequenceFlow':{
        draw:sequenceFlow
    },
    'bpmn:ParallelGateway':{
        draw:gateway
    },
    'bpmn:ExclusiveGateway':{
        draw:gateway
    },
    'bpmn:InclusiveGateway':{
        draw:gateway
    },
    'bpmn:EventGateway':{
        draw:gateway
    },
    'bpmn:SubProcess':{
        draw:subProcess
    },
}

export default customConfig
