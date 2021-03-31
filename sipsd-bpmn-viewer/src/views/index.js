import ReadeMe from '@/views/m1/ReadMe.vue'
import PageA from '@/views/m2/PageA.vue'
import PageB from '@/views/m2/PageB.vue'

export default [
  {
    path: '/bpmn/m1/readme',
    name: '@getRoutesName(path)',
    meta: {
      title: 'readme'
    },
    component: ReadeMe
  },
  {
    path: '/bpmn/m2/page_a',
    name: '@getRoutesName(path)',
    meta: {
      title: 'page-a'
    },
    component: PageA
  },
  {
    path: '/bpmn/m2/page_b',
    name: '@getRoutesName(path)',
    meta: {
      title: 'page-b'
    },
    component: PageB
  }
]
