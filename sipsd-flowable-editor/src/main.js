import Vue from 'vue'
import Vuels from 'vue-ls'
import store from './store'
import routes,{frameRoutes} from './routes'
import {portal} from '@dpark/s2-utils'
import "./assets/less/common.less";
import 'element-ui/lib/theme-chalk/index.css'
import elementUI from 'element-ui'
import project from './project'
import {staticPermission} from "./api";
import LogFv from "@dpark/logfv-web-vue";
Vue.use(Vuels,{
    namespace: 'pro__',
    name: 'ls',
    storage: 'local',
})
let _staticPer = staticPermission()
let _project = portal.getProject(project);
portal.registerApp({routes,permission:_staticPer,store,frameRoutes,project},function(globaVue,globalRouter, globalStore){
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

    globaVue.use(elementUI)
    globaVue.use(LogFv,_project.variable.logfv)
})

