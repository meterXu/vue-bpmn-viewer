const project_bpmn = {
  namespace: "bpmn",
  mainSys:true,
  variable: {
    baseApi: process.env.VUE_APP_baseApi,
    source:process.env.VUE_APP_source,
    accessTokenTime: 3600 * 1000,
    logfv:{
      enable:false,
      console:false,
      reportUrl:'http://58.210.9.133/iplatform/logfv-server/logfv/web/upload'
    }
  },
  redirect: {
    login: '/bpmn/login',
    index: '/bpmn/staticViewer'
  },
  style: {
    theme: 'dark',
    color: '#1890FF',
    layout: 'sidemenu',
    multipage: true,
    colorWeak: false,
    fixedHeader: false,
    fixSiderbar: false,
    autoHideHeader: false,
  },
  config: {
    "logo": "./static/dpark/logo_sipsd.png",
    "darkLogo": "./static/dpark/logo_sipsd_white.png",
    "favicon": "./static/dpark/logo_sipsd_favicon.png",
    "title": "sipsd-flowable-editor",
    "login": {
      "title": "bpmn-viewer",
      "desc": "项目由s2驱动！"
    },
    "head": {
      "title": {
        "desktop": "欢迎使用S2前端开发框架",
        "mobile": "S2开发框架"
      },
      "searchMenu": {
        "show": true
      },
      "helper": {
        "show": true,
        "href": "http://192.168.126.25/pldoc/",
        "target": "_blank"
      }
    },
    "sideMenu": {
      "title": "bpmn-viewer"
    },
    "footer": {
      "links": [{
        "name": "帮助",
        "href": "javascript:;",
        "target": "_self"
      }, {
        "name": "隐私",
        "href": "javascript:;",
        "target": "_self"
      },
      {
        "name": "条款",
        "href": "javascript:;",
        "target": "_self"
      }],
      "copyright": {
        "content": "苏州工业园园区测绘地理信息有限公司",
        "year": "2020",
        "href": "http://www.dpark.com.cn",
        "target": "_blank"
      }
    },
    "plugins": {
      "changeSystem": {
        "enable": false
      }
    },
    "browserFilter": {
      "chrome": 65,
      "firefox": 53
    }
  }
}

if (!window.project) {
  window.project = {}
  window.project[project_bpmn.namespace] = project_bpmn
} else if (!window.project[project_bpmn.namespace]) {
  window.project[project_bpmn.namespace] = project_bpmn
}


export default project_bpmn
