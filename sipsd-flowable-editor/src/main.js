import Vue from 'vue'
import App from './App.vue'
import Vuels from 'vue-ls'
import store from './store'
import "./assets/less/common.less";
import 'element-ui/lib/theme-chalk/index.css'
import elementUI from 'element-ui'
import router from './routes'
import LogFv from "@dpark/logfv-web-vue";
Vue.use(Vuels,{
    namespace: 'pro__',
    name: 'ls',
    storage: 'local',
})
Vue.use(elementUI)
Vue.prototype.$project_bpmn = window.project['bpmn']
console.log(Vue.prototype.$project_bpmn)
Vue.use(LogFv,Vue.prototype.$project_bpmn.variable.logfv)
// 启动应用
new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')
