import bl_project from '@/project.js'

export async function registerApp (app,callback){
    if(window.majesty && window.majesty.util.registerApp){
       await window.majesty.util.registerApp({
          project:getProject(),
          routes:app.routes,
          store:app.store,
          permission:app.permission,
          frameRoutes:app.frameRoutes
        },(Vue,globalRouter, globalStore)=>{
            if(callback){
                callback(Vue,globalRouter, globalStore)
            }
        })
    }
}

export function getAssets(path){
    if(process.env.NODE_ENV==="development"){
        const script=document.querySelector("script[src*='"+bl_project.namespace+".js']")
        let src =script.getAttribute('src')
        src = src.replace(`/${bl_project.namespace}.js`,"")
        return `${src+path}`
    }
    if(process.env.NODE_ENV==="production"){
        return `./${path}`
    }
}


function getProject(){
    const w_project = window.project[bl_project.namespace]
    let _project = Object.assign({},w_project)
    _project.variable = _project.variable[process.env.NODE_ENV]
    return _project
}

export const globalStore = window.majesty && window.majesty.store
export const globalRouter = window.majesty && window.majesty.router
export const project = getProject()
