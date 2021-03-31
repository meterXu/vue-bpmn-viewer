import Viewer from './Viewer.vue'
export default [
  {
    path: '/bpmn/viewer/:xmlId/:instanceId',
    name: 'bpmn-viewer',
    meta: {
      title: 'viewer'
    },
    component: Viewer,
    props: true
  }
]
