import Viewer from './Viewer.vue'
export default [
  {
    path: '/bpmn/Viewer/:xmlId/:instanceId"',
    name: '@getRoutesName(path)',
    meta: {
      title: 'viewer'
    },
    component: Viewer,
    props: true
  }
]
