import Viewer from './Viewer.vue'
import FlowableViewer from "./FlowableViewer";
import StaticViewer from "./StaticViewer";
export default [
  {
    path: '/bpmn/viewer',
    name: 'bpmn-viewer',
    meta: {
      title: 'viewer'
    },
    component: Viewer
  },
  {
    path: '/bpmn/flowableViewer',
    name: 'bpmn-flowableViewer',
    meta: {
      title: 'flowableViewer'
    },
    component: FlowableViewer
  },
  {
    path: '/bpmn/staticViewer',
    name: 'bpmn-staticViewer',
    meta: {
      title: 'saticViewer'
    },
    component: StaticViewer
  }
]
