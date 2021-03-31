
import Vue from 'vue'
import {project,globalStore} from '@/utils'
import axios from 'axios'
import { ACCESS_TOKEN } from "./mutation-types"
import {Modal, notification} from 'ant-design-vue'
let baseDomain = project.variable.baseApi
// 创建 axios 实例
const service = axios.create({
    baseURL: baseDomain,
    timeout: 15000 // 请求超时时间
  })

  const err = (error) => {
    if (error.response) {
      let data = error.response.data
      const token = Vue.ls.get(ACCESS_TOKEN)
      console.log("------异常响应------",token)
      console.log("------异常响应------",error.response.status)
      switch (error.response.status) {
        case 403:
          notification.error({ message: '系统提示', description: '拒绝访问',duration: 4})
          break
        case 500:
          if(data.message=="Token失效，请重新登录"){
            Modal.error({
              title: '登录已过期',
              content: '很抱歉，登录已过期，请重新登录',
              okText: '重新登录',
              mask: false,
              onOk: () => {
                globalStore.dispatch('Logout').then(() => {
                  Vue.ls.remove(ACCESS_TOKEN)
                  var sevice ="http://"+window.location.host+"/"+project.variable.name;
                  var serviceUrl = project.variable.ssoAuth+"/login?redirect_url="+encodeURIComponent(sevice);
                  serviceUrl+=encodeURIComponent(`&action=logout`)
                  window.location.href=serviceUrl
                })
              }
            })
          }
          break
        case 404:
            notification.error({ message: '系统提示', description:'很抱歉，资源未找到!',duration: 4})
          break
        case 504:
          notification.error({ message: '系统提示', description: '网络超时'})
          break
        case 401:
          notification.error({ message: '系统提示', description:'未授权，请重新登录',duration: 4})
          if (token) {
            globalStore.dispatch('Logout').then(() => {
              Vue.ls.remove(ACCESS_TOKEN)
              var sevice ="http://"+window.location.host+"/"+project.variable.name;
              var serviceUrl = project.variable.ssoAuth+"/login?redirect_url="+encodeURIComponent(sevice);
              serviceUrl+=encodeURIComponent(`&action=logout`)
              window.location.href=serviceUrl
            })
          }
          break
        default:
          notification.error({
            message: '系统提示',
            description: data.message,
            duration: 4
          })
          break
      }
    }
    return Promise.reject(error)
  };
  
  // request interceptor
  service.interceptors.request.use(config => {
    const token = Vue.ls.get(ACCESS_TOKEN)
  
    if (token) {
      config.headers[ 'X-Access-Token' ] = token // 让每个请求携带自定义 token 请根据实际情况自行修改
    }
    if(config.method=='get'){
      if(config.url.indexOf("sys/dict/getDictItems")<0){
        config.params = {
          _t: Date.parse(new Date())/1000,
          ...config.params
        }
      }
    }
    return config
  },(error) => {
    return Promise.reject(error)
  })
  
  // response interceptor
  service.interceptors.response.use((response) => {
      return response.data
    }, err)
  
  const installer = {
    vm: {},
    install (Vue, router = {}) {
      Vue.use(VueAxios, router, service)
    }
  }
  
  export {
    installer as VueAxios,
    service as axios
  }
