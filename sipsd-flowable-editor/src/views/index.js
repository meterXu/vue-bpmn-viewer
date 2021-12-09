import Viewer from './Viewer.vue'
import StaticViewer from "./StaticViewer";
import ProcessTestViewer from "./ProcessTestViewer";
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
    path: '/bpmn/staticViewer',
    name: 'bpmn-staticViewer',
    meta: {
      title: 'saticViewer'
    },
    component: StaticViewer
  }
  ,
  {
    path: '/bpmn/processTestViewer',
    name: 'bpmn-processTestViewer',
    meta: {
      title: 'processTestViewer'
    },
    component: ProcessTestViewer
  }
]
