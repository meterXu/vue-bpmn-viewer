import Vue from 'vue'
import Viewer from '../views/Viewer.vue'
import StaticViewer from "../views/StaticViewer";
import Router from 'vue-router'
Vue.use(Router)

export default new Router({
    routes:[
        {
            path: '/',
            redirect:'/bpmn/staticViewer'
        },
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
    ]
})
