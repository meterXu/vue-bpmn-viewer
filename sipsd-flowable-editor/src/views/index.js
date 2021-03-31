import Viewer from './Viewer.vue'
export default [
  {
    path: '/bpmn/viewer',
    name: 'bpmn-viewer',
    meta: {
      title: 'viewer'
    },
    component: Viewer,
    props: (route) => ({ xmlId: route.query.xmlId,instanceId: route.query.instanceId })
  }
]
