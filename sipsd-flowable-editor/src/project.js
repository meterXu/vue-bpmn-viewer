const project = {
  namespace: "bpmn",
  mainSys:true,
  variable: {
    development: {
      baseApi: "http://192.168.126.25/sipsd-flow-modeler/",
      accessTokenTime: 3600 * 1000
    },
    production: {
      baseApi: "http://192.168.126.25/sipsd-flow-modeler/",
      accessTokenTime: 3600 * 1000
    },
    proxy:{
      baseApi: "http://192.168.126.25/covenant/mocky/sipsd-flow-modeler/",
      accessTokenTime: 3600 * 1000
    }
  },
  redirect: {
    login: '/bpmn/login',
    index: '/bpmn/viewer'
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
    "logo": require("./assets/logo_sipsd.png"),
    "darkLogo": require("./assets/logo_sipsd_white.png"),
    "favicon": require("./assets/logo_sipsd_favicon.png"),
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
  window.project[project.namespace] = project
} else if (!window.project[project.namespace]) {
  window.project[project.namespace] = project
}


export default project
