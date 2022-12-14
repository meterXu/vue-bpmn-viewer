const project_bpmn = {
  namespace: "bpmn",
  mainSys:true,
  variable: {
    baseApi: process.env.VUE_APP_baseApi,
    source:process.env.VUE_APP_source,
    accessTokenTime: 3600 * 1000,
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
      "title": "bpmn-viewer-vue",
      "desc": "！"
    },
    "head": {
      "title": {
        "desktop": "",
        "mobile": ""
      },
      "searchMenu": {
        "show": true
      },
      "helper": {
        "show": false,
        "href": "",
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

module.exports=project_bpmn

