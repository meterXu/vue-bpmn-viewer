import Vue from 'vue'
import Vuels from 'vue-ls'
import store from './store'
import routes,{frameRoutes} from './routes'
import {registerApp,project} from '@/utils'
import "@/assets/less/common.less";
import elementUI from 'element-ui'
import {staticPermission} from "@/api";
Vue.use(Vuels,{
    namespace: 'pro__',
    name: 'ls',
    storage: 'local',
})
let _staticPer = staticPermission()
registerApp({routes,permission:_staticPer,store,frameRoutes},function(globaVue,globalRouter, globalStore){
    if(globaVue.config.errorHandler){
        let _handler  = globaVue.config.errorHandler
        globaVue.config.errorHandler = function (err){
            console.error(err)
            _handler(err)
        }
    }else{
        globaVue.config.errorHandler=function (err){
            console.error(err)
        }
    }
    globaVue.prototype.$project_bpmn=project
    globaVue.use(elementUI)
})
