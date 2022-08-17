import Vue from 'vue'
import App from './App.vue'
import Vuels from 'vue-ls'
import store from './store'
import {portal} from '@dpark/s2-utils'
import "./assets/less/common.less";
import 'element-ui/lib/theme-chalk/index.css'
import elementUI from 'element-ui'
import project from './project'
import router from './routes'
import LogFv from "@dpark/logfv-web-vue";
Vue.use(Vuels,{
    namespace: 'pro__',
    name: 'ls',
    storage: 'local',
})
Vue.use(elementUI)
let _project = portal.getProject(project);
Vue.use(LogFv,_project.variable.logfv)
Vue.prototype.$project_bpmn = _project
// 启动应用
new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')
