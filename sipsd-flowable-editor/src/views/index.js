import Viewer from './Viewer.vue'
import Drawio from './Drawio.vue'
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
    path: '/bpmn/drawio',
    name: 'bpmn-drawio',
    meta: {
      title: 'drawio'
    },
    component: Drawio
  }
]
