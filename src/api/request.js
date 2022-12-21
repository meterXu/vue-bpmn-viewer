import axios from "axios";
import Vue from "vue";
const ACCESS_TOKEN = 'Access-Token';
function getService(project,withCredentials,baseApiKey='baseApi') {
    return createService(project,withCredentials,baseApiKey,true)
}
function getServiceSSO(project,withCredentials,baseApiKey='ssoApi') {
    return createService(project,withCredentials,baseApiKey,true)
}
function getServiceLogin(project,withCredentials,baseApiKey='baseApi') {
    return createService(project,withCredentials,baseApiKey,false)
}

function createService(project,withCredentials,baseApiKey,isToken){
    let _project = project;
    let baseUrl = _project.variable[baseApiKey];
    const service = axios.create({
        baseURL: baseUrl,
        timeout: 15000, // 请求超时时间
        withCredentials:withCredentials||false
    });
    service.interceptors.request.use(config => {
        const token = decodeURIComponent(Vue.ls.get(ACCESS_TOKEN));
        if (token&&isToken) {
            if(config.headers&&!config.headers[_project.variable.tokenKey || 'X-Access-Token']){
                if(isToken) {
                    config.headers[_project.variable.tokenKey || 'X-Access-Token'] = token; // 让每个请求携带自定义 token 请根据实际情况自行修改
                } else {
                    config.headers[_project.variable.tokenKey || 'X-Access-Token'] = null; // 让每个请求携带自定义 token 请根据实际情况自行修改
                }
            }
        }
        if (config.method == 'get') {
            if (config.url&&config.url.indexOf("sys/dict/getDictItems") < 0) {
                config.params = Object.assign({
                    _t: new Date().valueOf()
                },config.params);
            }
        }
        return config
    }, (error) => {
        return Promise.reject(error)
    });
    service.interceptors.response.use((response) => {
        return response.data
    }, (err) => {
        return error(err, _project)
    });
    return service
}
function error(error, project) {
    if (error.response) {
        let data = error.response.data;
        let message = "";
        if(typeof(data)==="string"&&data.indexOf('{')>-1){
            data = JSON.parse(data);
        }
        if(data){
            message = data.msg||data.message;
        }
        const token = decodeURIComponent(Vue.ls.get(ACCESS_TOKEN));
        console.log("------异常响应------", token);
        console.log("------异常响应------", error.response.status);
        switch (error.response.status) {
            case 403:
                break
            case 500:
                if (data.message == "Token失效，请重新登录") {
                } else if(data.message.indexOf("用户名不存在或者密码错误")>-1){
                } else {
                }
                break
            case 404:
                break
            case 504:
                break
            case 401:
                break
            case 426:
                break
            default:
                break
        }
    }else {
    }
    return Promise.reject(error)
}

const request ={
    getService:getService
}
export default request