window.project={
  "namespace": "bpmn",
  "mainSys": true,
  "variable": {
    "baseApi": "http://192.168.75.106:9001/sipsd-flow-modeler/",
    "source": "http://192.168.75.106:9001/sipsd-flow-modeler/rest/model/loadXmlByModelId/2cf19d8a-2742-11ed-ad80-acde48001122",
    "accessTokenTime": 3600000,
  },
  "redirect": {
    "login": "/bpmn/login",
    "index": "/bpmn/staticViewer"
  },
  "style": {
    "theme": "dark",
    "color": "#1890FF",
    "layout": "sidemenu",
    "multipage": true,
    "colorWeak": false,
    "fixedHeader": false,
    "fixSiderbar": false,
    "autoHideHeader": false
  },
  "config": {
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
      "links": [
        {
          "name": "帮助",
          "href": "javascript:;",
          "target": "_self"
        },
        {
          "name": "隐私",
          "href": "javascript:;",
          "target": "_self"
        },
        {
          "name": "条款",
          "href": "javascript:;",
          "target": "_self"
        }
      ],
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