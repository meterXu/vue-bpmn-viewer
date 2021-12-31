import Vue from 'vue'
import Vuels from 'vue-ls'
import store from './store'
import routes,{frameRoutes} from './routes'
import {portal} from '@dpark/s2-utils'
import "./assets/less/common.less";
import 'element-ui/lib/theme-chalk/index.css'
import project from './project'
import {staticPermission} from "./api";
Vue.use(Vuels,{
    namespace: 'pro__',
    name: 'ls',
    storage: 'local',
})
let _staticPer = staticPermission()
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
})
