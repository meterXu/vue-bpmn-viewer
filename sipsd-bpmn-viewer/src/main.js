import Vue from 'vue'
import Vuels from 'vue-ls'
import store from './store'
import routes,{frameRoutes} from './routes'
import {registerApp,project} from '@/utils'
import "@/assets/less/common.less";
import {staticPermission} from "@/api";
Vue.use(Vuels,{
    namespace: 'pro__',
    name: 'ls',
    storage: 'local',
})
let _staticPer = staticPermission()
registerApp({routes,permission:_staticPer,store,frameRoutes},function(globaVue,globalRouter, globalStore){
    globaVue.prototype.$project_bpmn=project
})
