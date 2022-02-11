import Viewer from './Viewer.vue'
import StaticViewer from "./StaticViewer";
import test from "./test";
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
  },
  {
    path: '/bpmn/test',
    name: 'bpmn-test',
    meta: {
      title: 'test'
    },
    component: test
  }
]
