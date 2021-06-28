(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory(require('diagram-js/lib/draw/BaseRenderer'), require('tiny-svg'), require('min-dash'), require('bpmn-js/lib/draw/BpmnRenderUtil')) :
  typeof define === 'function' && define.amd ? define(['diagram-js/lib/draw/BaseRenderer', 'tiny-svg', 'min-dash', 'bpmn-js/lib/draw/BpmnRenderUtil'], factory) :
  (global = typeof globalThis !== 'undefined' ? globalThis : global || self, global['bpmn-theme-blue'] = factory(global.BaseRenderer, global.tinySvg, global.minDash, global.BpmnRenderUtil));
}(this, (function (BaseRenderer, tinySvg, minDash, BpmnRenderUtil) { 'use strict';

  function _interopDefaultLegacy (e) { return e && typeof e === 'object' && 'default' in e ? e : { 'default': e }; }

  var BaseRenderer__default = /*#__PURE__*/_interopDefaultLegacy(BaseRenderer);

  function styleInject(css, ref) {
    if ( ref === void 0 ) ref = {};
    var insertAt = ref.insertAt;

    if (!css || typeof document === 'undefined') { return; }

    var head = document.head || document.getElementsByTagName('head')[0];
    var style = document.createElement('style');
    style.type = 'text/css';

    if (insertAt === 'top') {
      if (head.firstChild) {
        head.insertBefore(style, head.firstChild);
      } else {
        head.appendChild(style);
      }
    } else {
      head.appendChild(style);
    }

    if (style.styleSheet) {
      style.styleSheet.cssText = css;
    } else {
      style.appendChild(document.createTextNode(css));
    }
  }

  var css_248z = ".clock-spin{transform-origin:512px 512px;animation:spin 1s infinite;animation-timing-function:linear}@keyframes spin{0%{transform:rotate(0deg)}to{transform:rotate(1turn)}}";
  styleInject(css_248z);

  const taskIcon={
      "bpmn:UserTask":{
          icon:createUserTitleIcon,
          content:userObj
      },
      "bpmn:ScriptTask":{
          icon:createScriptTitleIcon,
          content:scriptObj
      },
      "bpmn:ServiceTask":{
          icon:createServiceTitleIcon,
          content:serviceObj
      }
  };

  var Task = (options)=>{
      const type = options.element.type;
      const g = tinySvg.create('g',{
          'data-element-type':'bpmn:userTask'
      });
      const rect = tinySvg.create('rect',{
          x:0,
          y:0,
          width:options.width,
          height:options.height,
          rx:4,
          ry:4,
          stroke:'#ececec',
          strokeWidth:1,
          transform:"translate(0.5,0.5)",
          fill:'#fff',
          filter:`url(#f1_${options.element.id})`
      });
      const filter = tinySvg.create('filter',{
          id:"f1_"+options.element.id,
          x:0,
          y:0,
          width:"200%",
          height:"200%"
      });
      tinySvg.append(filter,tinySvg.create('feOffset',{
          result:"offOut",
          in:"SourceGraphic",
          dx:1,
          dy:1
      }));
      let feColorMatrix = tinySvg.create('feColorMatrix',{
          result:"matrixOut",
          in:"offOut",
          type:"matrix",
          values:`0 0 0 0 0
              0 0 0 0 0
              0 0 0 0 0
              0 0 0 0.1 0`
      });

      tinySvg.append(filter,feColorMatrix);
      tinySvg.classes(feColorMatrix).add('highlight-custom-rect');
      tinySvg.append(filter,tinySvg.create('feGaussianBlur',{
          result:"blurOut",
          in:"matrixOut",
          stdDeviation:3
      }));
      tinySvg.append(filter,tinySvg.create('feBlend',{
          in:"SourceGraphic",
          in2:"blurOut",
          mode:"normal"
      }));
      tinySvg.append(g,tinySvg.append(tinySvg.create('defs'),filter));
      tinySvg.append(g,rect);
      tinySvg.append(g,tinySvg.create('rect',{
          x:1,
          y:1,
          width:options.width-1,
          height:24,
          fill: "#3296fa",
          rx:4,
          ry:4
      }));
      tinySvg.append(g,tinySvg.create('rect',{
          x:1,
          y:5,
          width:options.width-1,
          height:20,
          fill: "#3296fa",
          rx:0,
          ry:0
      }));
      let text = tinySvg.create('text',{
          x:24,
          y:18,
          fontSize:'12px',
          fontWeight:700,
          fill: "#fff"
      });
      if(options.width<=100&&options.businessObject.name){
          text.innerHTML=options.businessObject.name.substring(0,5)||'-';
      }
      else {
          text.innerHTML=options.businessObject.name||'-';
      }
      tinySvg.append(g,text);
      tinySvg.classes(rect).add('d-userTask');

      // 设置标题图标和内容
      let titleIcon = taskIcon[type].icon();
      tinySvg.append(g,titleIcon);

      let content = taskIcon[type].content(options);
      tinySvg.append(g,content);
      return g
  };

  function userObj(options){
      let user = tinySvg.create('g');
      tinySvg.classes(user).add('custom-realName');
      let icon = null;
      let text = null;
      if(options.businessObject&&options.businessObject.extensionElements){
          const userInfoLastName = options.businessObject.extensionElements.values.find(c=>c.$type.indexOf('info-lastname')>0);
          if(userInfoLastName){
              text = createConText(userInfoLastName.$body);
              icon=createUserIcon();
          }else {
              text = createUnConText();
          }
      }else {
           text = createUnConText();
      }
      tinySvg.append(user,text);
      if(icon){
          tinySvg.append(user,icon);
      }
      let clock = clockObj(options);
      if(clock){
          tinySvg.append(user,clock);
      }
      return user
  }

  function serviceObj(options){
      let con = tinySvg.create('g');
      const text = createUnConText({x:20,y:50,text:'服务任务'});
      tinySvg.append(con,text);
      return con
  }

  function scriptObj(options){
      let con = tinySvg.create('g');
      const text = createUnConText({x:20,y:50,text:'脚本任务'});
      tinySvg.append(con,text);
      return con
  }

  function clockObj(options){
      let clock = tinySvg.create('g');
      tinySvg.classes(clock).add('custom-max-day');
      let icon = null;
      let text = null;
      if(options.businessObject&&options.businessObject.extensionElements){
          const taskMaxDay = options.businessObject.extensionElements.values.find(c=>c.$type.indexOf('task_max_day')>0);
          if(taskMaxDay&&taskMaxDay.$body){
              text = createConText(taskMaxDay.$body+'天',{x:38,y:76,fill:'#f88062'});
              icon=createClockIcon();
          }else {
              return  null
          }
      }else {
         return  null
      }
      tinySvg.append(clock,text);
      if(icon){
          tinySvg.append(clock,icon);
      }
      return clock
  }

  function createUserIcon(){
      let path = tinySvg.create('path',{
          d:'M513.457231 41.353846c-133.377969 2.837662-244.999877 49.189415-332.973292 137.160862C92.512492 266.488123 46.160738 378.110031 43.323077 511.488c2.837662 133.377969 49.189415 244.054646 137.160862 332.026092C268.457354 931.487508 380.079262 977.839262 513.457231 980.676923c133.377969-2.837662 244.054646-49.189415 332.026092-137.160862S979.808492 644.867938 982.646154 511.488c-2.837662-133.377969-49.189415-244.054646-137.160862-332.026092C757.511877 90.543262 646.837169 44.191508 513.457231 41.353846L513.457231 41.353846zM811.431385 799.056738c-23.650462-11.352615-49.189415-23.648492-74.730338-26.486154-67.160615-5.675323-108.782277-16.080738-124.863015-32.161477-15.137477-16.082708-21.758031-38.784-18.9184-68.109785 0.945231-17.9712 5.675323-28.376615 16.080738-33.108677 10.405415-4.728123 21.756062-31.216246 33.108677-78.513231 3.784862-14.188308 7.567754-25.540923 12.297846-34.053908 4.728123-8.512985 12.295877-26.486154 21.756062-55.809969 4.730092-20.810831 4.730092-33.108677-0.945231-35.944369-5.675323-2.837662-9.460185-3.784862-10.403446-2.837662L669.538462 411.218708c2.837662-13.243077 4.728123-29.323815 6.620554-47.296985 5.677292-47.296985-3.782892-87.973415-28.378585-122.027323-24.593723-34.999138-69.998277-52.972308-137.160862-54.864738-59.594831 0.945231-90.811077 24.595692-125.810215 53.919508-38.784 30.271015-52.027077 70.945477-41.621662 122.972554 7.567754 38.784 18.9184 70.945477 11.350646 68.107815-0.945231-0.945231-4.728123 0-9.458215 2.837662-4.730092 2.837662-5.675323 15.135508-1.892431 35.944369 10.405415 25.540923 17.973169 43.514092 23.648492 52.974277 5.675323 9.458215 9.458215 21.756062 12.297846 36.8896 9.460185 46.351754 18.9184 71.892677 29.323815 76.6208 10.405415 4.730092 17.025969 17.027938 20.810831 34.999138 4.730092 29.325785-0.945231 52.027077-17.973169 68.109785-17.025969 16.080738-57.7024 26.486154-122.027323 32.161477-23.648492 2.837662-52.027077 14.188308-74.728369 25.540923-72.837908-75.675569-115.4048-175.946831-116.352-286.621538 2.837662-117.297231 43.514092-214.728862 122.027323-293.244062s175.946831-119.187692 293.244062-122.027323c117.295262 2.837662 214.728862 43.514092 292.296862 122.027323 78.513231 78.513231 119.189662 175.946831 122.027323 293.244062C926.834215 624.055138 884.267323 724.324431 811.431385 799.056738z',
          transform:'matrix(0.015 0 0 0.015 20 37)',
          fill:'#53c3d8'
      });
      return path
  }

  function createClockIcon(){
      tinySvg.create('path',{
          d:'M511.914 63.99c-247.012 0-447.925 200.912-447.925 447.924s200.913 447.925 447.925 447.925 447.925-200.913 447.925-447.925S758.926 63.989 511.914 63.989z m0 831.687c-211.577 0-383.763-172.186-383.763-383.763 0-211.577 172.014-383.763 383.763-383.763s383.763 172.014 383.763 383.763-172.186 383.763-383.763 383.763z m160.145-383.763H512.086v-223.79c0-17.718-14.277-32.167-31.995-32.167-17.717 0-31.994 14.45-31.994 32.167V544.08c0 17.717 14.277 31.994 31.994 31.994H672.06c17.718 0 32.167-14.277 32.167-31.994-0.172-17.89-14.621-32.167-32.167-32.167z',
          transform:'matrix(0.015 0 0 0.015 20 73)',
          fill:'#2c3e50'
      });

      let g = tinySvg.create('g');
      let path1 = tinySvg.create('path',{
          d:'M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z',
          fill:"#ffd7d7"
      });
      let path2 = tinySvg.create('path',{
          d:'M512 512m-398.222222 0a398.222222 398.222222 0 1 0 796.444444 0 398.222222 398.222222 0 1 0-796.444444 0Z',
          fill:'#f88062',
      });
      let path3 = tinySvg.create('path',{
          d:'M426.666667 284.444444v312.888889h256v-85.333333h-170.666667v-227.555556z',
          fill:"#ffffff"
      });
      tinySvg.append(g,path1);
      tinySvg.append(g,path2);
      tinySvg.append(g,path3);
      tinySvg.classes(path3).add('clock-spin');
      tinySvg.attr(g,{
          transform:'matrix(0.015 0 0 0.015 20 64)',
      });
      return g
  }


  function createUnConText(option={x:20,y:50,text:'未分配'}){
      let text  = tinySvg.create('text',{
          x:option.x,
          y:option.y,
          fontSize:'14px',
          fill:'#bfbfbf',
      });
      text.innerHTML=option.text;
      return text
  }
  function createConText(name,option={x:38,y:50,fill:'#2c3e50'}){
      let text  = tinySvg.create('text',{
          x:option.x,
          y:option.y,
          fontSize:'14px',
          fill:option.fill,
      });
      text.innerHTML=name;
      return text
  }

  function createUserTitleIcon(){
      let g = tinySvg.create('g',{
          transform:'matrix(0.012 0 0 0.012 8 7)'
      });
      let user_1 = tinySvg.create('path',{
          d:'M248.384 264.128C248.384 118.272 366.4 0 512 0s263.616 118.272 263.616 264.128S657.6 528.256 512 528.256 248.384 409.984 248.384 264.128z',
          fill:'#fff'
      });
      let user_2 = tinySvg.create('path',{
          d:'M972.8 1024c-11.456-245.056-213.376-428.736-460.8-428.736S62.656 778.944 51.2 1024L972.8 1024z',
          fill:'#fff'
      });
      tinySvg.append(g,user_1);
      tinySvg.append(g,user_2);
      return g
  }
  function createServiceTitleIcon(){
      let g = tinySvg.create('g',{
          transform:'matrix(1 0 0 1 5 4)'
      });
      let icon_1 = tinySvg.create('path',{
          d:'M 8,1 7.5,2.875 c 0,0 -0.02438,0.250763 -0.40625,0.4375 C 7.05724,3.330353 7.04387,3.358818 7,3.375 6.6676654,3.4929791 6.3336971,3.6092802 6.03125,3.78125 6.02349,3.78566 6.007733,3.77681 6,3.78125 5.8811373,3.761018 5.8125,3.71875 5.8125,3.71875 l -1.6875,-1 -1.40625,1.4375 0.96875,1.65625 c 0,0 0.065705,0.068637 0.09375,0.1875 0.002,0.00849 -0.00169,0.022138 0,0.03125 C 3.6092802,6.3336971 3.4929791,6.6676654 3.375,7 3.3629836,7.0338489 3.3239228,7.0596246 3.3125,7.09375 3.125763,7.4756184 2.875,7.5 2.875,7.5 L 1,8 l 0,2 1.875,0.5 c 0,0 0.250763,0.02438 0.4375,0.40625 0.017853,0.03651 0.046318,0.04988 0.0625,0.09375 0.1129372,0.318132 0.2124732,0.646641 0.375,0.9375 -0.00302,0.215512 -0.09375,0.34375 -0.09375,0.34375 L 2.6875,13.9375 4.09375,15.34375 5.78125,14.375 c 0,0 0.1229911,-0.09744 0.34375,-0.09375 0.2720511,0.147787 0.5795915,0.23888 0.875,0.34375 0.033849,0.01202 0.059625,0.05108 0.09375,0.0625 C 7.4756199,14.874237 7.5,15.125 7.5,15.125 L 8,17 l 2,0 0.5,-1.875 c 0,0 0.02438,-0.250763 0.40625,-0.4375 0.03651,-0.01785 0.04988,-0.04632 0.09375,-0.0625 0.332335,-0.117979 0.666303,-0.23428 0.96875,-0.40625 0.177303,0.0173 0.28125,0.09375 0.28125,0.09375 l 1.65625,0.96875 1.40625,-1.40625 -0.96875,-1.65625 c 0,0 -0.07645,-0.103947 -0.09375,-0.28125 0.162527,-0.290859 0.262063,-0.619368 0.375,-0.9375 0.01618,-0.04387 0.04465,-0.05724 0.0625,-0.09375 C 14.874237,10.52438 15.125,10.5 15.125,10.5 L 17,10 17,8 15.125,7.5 c 0,0 -0.250763,-0.024382 -0.4375,-0.40625 C 14.669647,7.0572406 14.641181,7.0438697 14.625,7 14.55912,6.8144282 14.520616,6.6141566 14.4375,6.4375 c -0.224363,-0.4866 0,-0.71875 0,-0.71875 L 15.40625,4.0625 14,2.625 l -1.65625,1 c 0,0 -0.253337,0.1695664 -0.71875,-0.03125 l -0.03125,0 C 11.405359,3.5035185 11.198648,3.4455201 11,3.375 10.95613,3.3588185 10.942759,3.3303534 10.90625,3.3125 10.524382,3.125763 10.5,2.875 10.5,2.875 L 10,1 8,1 z m 1,5 c 1.656854,0 3,1.3431458 3,3 0,1.656854 -1.343146,3 -3,3 C 7.3431458,12 6,10.656854 6,9 6,7.3431458 7.3431458,6 9,6 z',
          fill:'#fff'
      });
      tinySvg.append(g,icon_1);
      return g
  }
  function createScriptTitleIcon(){
      let g = tinySvg.create('g',{
          transform:'matrix(1 0 0 1 5 4)'
      });
      let icon_1 = tinySvg.create('path',{
          d:'m 5,2 0,0.094 c 0.23706,0.064 0.53189,0.1645 0.8125,0.375 0.5582,0.4186 1.05109,1.228 1.15625,2.5312 l 8.03125,0 1,0 1,0 c 0,-3 -2,-3 -2,-3 l -10,0 z M 4,3 4,13 2,13 c 0,3 2,3 2,3 l 9,0 c 0,0 2,0 2,-3 L 15,6 6,6 6,5.5 C 6,4.1111 5.5595,3.529 5.1875,3.25 4.8155,2.971 4.5,3 4.5,3 L 4,3 z',
          fill:'#fff'
      });
      tinySvg.append(g,icon_1);
      return g
  }

  var startEvent = (options)=>{
      const g = tinySvg.create('g');
      const circle = tinySvg.create('circle',{
          cx:options.width/2,
          cy:options.height/2,
          r:options.width/2,
          stroke:'#5ac14a',
          strokeWidth:1,
          transform:"translate(0.5,0.5)",
          fill:'#53D894',
          // filter:"url(#f1)"
      });
      let text = tinySvg.create('text',{
          x:(options.width/2)-12,
          y:options.height+20,
          'font-size':'12px',
          fill: "#444"
      });
      text.innerHTML=options.title||'开始';
      tinySvg.append(g,text);
      tinySvg.append(g,circle);

      let hand = tinySvg.create('path',{
         d:'M72,32.5c0,0.5-0.1,0.9-0.3,1.3c-0.2,0.4-0.4,0.8-0.7,1c-0.3,0.3-0.6,0.5-1,0.7c-0.4,0.2-0.8,0.3-1.3,0.3H54.6\n' +
             '\t\tc0.9,0,1.7,0.3,2.3,0.9c0.6,0.6,1,1.3,1,2.2c0,0.9-0.3,1.6-1,2.3c-0.6,0.6-1.4,1-2.3,1h-1.3c0.5,0,0.9,0.1,1.3,0.3s0.8,0.4,1.1,0.7\n' +
             '\t\tc0.3,0.3,0.6,0.6,0.7,1c0.2,0.4,0.3,0.8,0.3,1.2c0,0.5-0.1,0.9-0.4,1.3c-0.3,0.4-0.6,0.7-1,1c-0.4,0.3-0.9,0.5-1.5,0.7\n' +
             '\t\tc-0.6,0.2-1.1,0.3-1.7,0.3c0.9,0,1.7,0.3,2.3,0.9c0.6,0.6,0.8,1.4,0.8,2.3c0,0.6-0.2,1-0.5,1.4c-0.3,0.4-0.7,0.7-1.2,1\n' +
             '\t\tc-0.5,0.3-1,0.5-1.6,0.6C51.5,54.9,51,55,50.5,55h-2.2h-10c-0.9,0-1.7-0.1-2.5-0.4c-0.8-0.3-1.4-0.7-2-1.2c-0.6-0.5-1-1.2-1.3-1.9\n' +
             '\t\tc-0.3-0.8-0.5-1.6-0.5-2.5V36.2c0-0.5,0-1.1,0.1-1.6c0-0.5,0.1-1.1,0.3-1.5c0.2-0.5,0.4-0.9,0.7-1.3c0.3-0.4,0.8-0.7,1.4-1\n' +
             '\t\tc0.8-0.3,1.8-0.9,3-1.7c1.2-0.8,2.3-1.8,3.3-2.9c1.1-1.1,2-2.3,2.7-3.7c0.8-1.3,1.1-2.7,1.1-4.1c0-0.8,0.1-1.5,0.3-2\n' +
             '\t\tc0.2-0.6,0.5-1,0.8-1.4c0.3-0.3,0.7-0.6,1-0.8c0.4-0.2,0.8-0.2,1.1-0.2c1,0,1.8,0.3,2.5,0.9c0.7,0.6,1.2,1.6,1.5,3\n' +
             '\t\tc0.1,1.2,0,2.5-0.2,3.8c-0.2,1.1-0.5,2.4-0.9,3.7c-0.4,1.4-1.1,2.7-1.9,4h19.8c0.9,0,1.7,0.3,2.3,0.9C71.7,30.8,72,31.6,72,32.5\n' +
             '\t\tL72,32.5z',
      });
      tinySvg.attr(hand,{
          fill:'#fff',
          transform:'matrix(0.5 0 0 0.5 -8 -2)'
      });
      tinySvg.append(g,hand);
      return g
  };

  var endEvent = (options)=>{
      const g = tinySvg.create('g',{
          'data-element-type':'bpmn:EndEvent'
      });
      const circle = tinySvg.create('circle',{
          cx:options.width/2,
          cy:options.height/2,
          r:options.width/2,
          strokeWidth:1,
          transform:"translate(0.5,0.5)",
          fill:'#d1d1d1',
          stroke:'#aaa'
          // filter:"url(#f1)"
      });
      let text = tinySvg.create('text',{
          x:(options.width/2)-12,
          y:options.height+20,
          fontSize:'12px',
          fill: "#444"
      });
      text.innerHTML=options.title||'结束';
      tinySvg.append(g,text);
      tinySvg.append(g,circle);

      let ok = tinySvg.create('path');
      tinySvg.attr(ok,{d:'M45.6,55.2c2.7-4.8,12-13.3,18.7-17.7l2.6-1.6c1-0.5,1.9-0.9,2.7-1.1c-2.1-4.6-0.3-8.3,0-12.6l0,0\n' +
              '\tc-2.5,1.2-5,3-7.4,5l-2.8,2.5C51.4,37.1,45,46.3,45,46.3l-4.7-8.9l-10.7,5.1C34.1,44.1,41,49.7,45.6,55.2L45.6,55.2z',
          fill:'#fff',
          transform:'matrix(0.5 0 0 0.5 -11 -4)'
          });
      tinySvg.append(g,ok);
      return g
  };

  var sequenceFlow = (options)=>{
      const g = tinySvg.create('g',{
          'data-sourceRef-id':options.sourceRefId,
          'data-targetRef-id':options.targetRefId
      });
      const path = tinySvg.create('path',{
          d:options.d,
          stroke:'#ccc',
          strokeWidth:2,
          transform:"translate(0.5,0.5)",
          fill:'#000',
          fillOpacity:0,
          pointerEvents:'stroke',
          strokeMiterlimit:10,
          markerEnd: options.flowEnd,
          strokeLinejoin: 'butt',
      });
      if(options.complete){
          tinySvg.classes(path).add('highlight-custom-path');
      }
      tinySvg.append(g,path);
      return g
  };

  const iconMap={
      'bpmn:ParallelGateway':parallelGatewayIcon,
      'bpmn:ExclusiveGateway':exclusiveGatewayIcon,
      'bpmn:InclusiveGateway':inclusiveGatewayIcon,
      'bpmn:EventGateway':eventGatewayIcon
  };

  var gateway = (options)=>{
      const type = options.element.type;
      let g = tinySvg.create('g');
      let rect = tinySvg.create('rect',{
          x:0,
          y:0,
          width:options.width/Math.sin(Math.PI / 4)/2,
          height:options.height/Math.sin(Math.PI / 4)/2,
          fillOpacity:0,
          stroke:'#9399B2',
          strokeWidth:1,
          transform:`matrix(${Math.cos(Math.PI/4)},${Math.sin(Math.PI / 4)},-${Math.sin(Math.PI / 4)},${Math.cos(Math.PI/4)},20,0)`,
      });
      tinySvg.append(g,rect);

      let linearGradie = createLinearGradient();
      tinySvg.append(g,linearGradie);
      let icon = iconMap[type]();
      tinySvg.append(g,icon);
      return g
  };

  function parallelGatewayIcon(){
      let icon = tinySvg.create('path',{
          d:'M64,52H52v12c0,1.1-0.9,2-2,2s-2-0.9-2-2V52H36c-1.1,0-2-0.9-2-2s0.9-2,2-2h12V36c0-1.1,0.9-2,2-2s2,0.9,2,2v12  h12c1.1,0,2,0.9,2,2S65.1,52,64,52z',
          fill:'url(#SVGID_1_)',
          transform:'matrix(0.6,0,0,0.6,-10,-10)'
      });
      return icon
  }

  function exclusiveGatewayIcon(){
      let icon = tinySvg.create('path',{
          d:'M52.8,50l8.5,8.5c0.8,0.8,0.8,2,0,2.8c-0.8,0.8-2,0.8-2.8,0L50,52.8l-8.5,8.5c-0.8,0.8-2,0.8-2.8,0  c-0.8-0.8-0.8-2,0-2.8l8.5-8.5l-8.5-8.5c-0.8-0.8-0.8-2,0-2.8c0.8-0.8,2-0.8,2.8,0l8.5,8.5l8.5-8.5c0.8-0.8,2-0.8,2.8,0  c0.8,0.8,0.8,2,0,2.8L52.8,50z',
          fill:'url(#SVGID_1_)',
          transform:'matrix(0.6,0,0,0.6,-10,-10)'
      });
      return icon
  }

  function  inclusiveGatewayIcon(){
      let icon = tinySvg.create('path',{
          d:'M50,66c-8.8,0-16-7.2-16-16s7.2-16,16-16s16,7.2,16,16S58.8,66,50,66z M50,38c-6.6,0-12,5.4-12,12s5.4,12,12,12  s12-5.4,12-12S56.6,38,50,38z',
          fill:'url(#SVGID_1_)',
          transform:'matrix(0.6,0,0,0.6,-10,-10)'
      });
      return icon
  }

  function eventGatewayIcon(){
      let icon = tinySvg.create('path',{
          d:'M52.7,35.3L52.7,35.3L52.7,35.3 M50,40.6l2.1,4.2c0.7,1.5,2.2,2.5,3.9,2.7l4.8,0.7l-3.4,3.3  c-1.2,1.2-1.8,2.8-1.5,4.4l0.8,4.6l-4.3-2.2c-0.7-0.4-1.6-0.6-2.4-0.6c-0.8,0-1.7,0.2-2.4,0.6l-4.3,2.2l0.8-4.6  c0.3-1.6-0.3-3.3-1.5-4.4l-3.4-3.3l4.8-0.7c1.7-0.2,3.1-1.3,3.9-2.7L50,40.6 M50,33c-0.4,0-0.8,0.2-1,0.6l-4.8,9.5  c-0.2,0.3-0.5,0.5-0.8,0.6l-10.7,1.5c-0.9,0.1-1.2,1.2-0.6,1.8l7.7,7.4c0.3,0.2,0.4,0.6,0.3,0.9l-1.8,10.4c-0.1,0.7,0.4,1.2,1.1,1.2  c0.2,0,0.3,0,0.5-0.1l9.6-4.9c0.2-0.1,0.3-0.1,0.5-0.1c0.2,0,0.3,0,0.5,0.1l9.6,4.9c0.2,0.1,0.3,0.1,0.5,0.1c0.6,0,1.2-0.6,1.1-1.2  l-1.8-10.4c-0.1-0.3,0.1-0.7,0.3-0.9l7.7-7.4c0.6-0.6,0.3-1.7-0.6-1.8l-10.7-1.5c-0.4-0.1-0.7-0.3-0.8-0.6L51,33.6  C50.8,33.2,50.4,33,50,33L50,33z',
          fill:'url(#SVGID_1_)',
          transform:'matrix(0.6,0,0,0.6,-10,-10)'
      });
      return icon
  }

  function createLinearGradient(){
      if(!document.querySelector('SVGID_1_')){
          let linearGradient = tinySvg.create('linearGradient',{
              id:'SVGID_1_',
              gradientUnits:'userSpaceOnUse',
              x1:'9.2969',
              y1:'86.3438',
              x2:'9.2969',
              y2:'87.3438',
              gradientTransform:'matrix(32 0 0 -32 -247.5 2829)'
          });
          tinySvg.append(linearGradient,tinySvg.create('stop',{
              offset:0,
              'stop-color':'#B0B8D5'
          }));
          tinySvg.append(linearGradient,tinySvg.create('stop',{
              offset:1,
              'stop-color':'#81869D'
          }));
          return linearGradient
      }
  }

  const customConfig = {
      'bpmn:UserTask': {
          draw:Task
      },
      'bpmn:ScriptTask':{
          draw:Task
      },
      'bpmn:ServiceTask':{
          draw:Task
      },
      'bpmn:StartEvent':{
          draw:startEvent
      },
      'bpmn:EndEvent':{
          draw:endEvent
      },
      'bpmn:SequenceFlow':{
          draw:sequenceFlow
      },
      'bpmn:ParallelGateway':{
          draw:gateway
      },
      'bpmn:ExclusiveGateway':{
          draw:gateway
      },
      'bpmn:InclusiveGateway':{
          draw:gateway
      },
      'bpmn:EventGateway':{
          draw:gateway
      }
  };

  const HIGH_PRIORITY = 1500; // 最高优先级
  const customElements = Object.keys(customConfig);
  function createPathFromConnection(connection) {
      let waypoints = connection.waypoints;

      let pathData = 'm  ' + waypoints[0].x + ',' + waypoints[0].y;
      for (let i = 1; i < waypoints.length; i++) {
          pathData += 'L' + waypoints[i].x + ',' + waypoints[i].y + ' ';
      }
      return pathData;
  }
  let _marker = null;
  function addMarker(id,parentNode, options){
      let attrs = minDash.assign({
          fill: 'black',
          strokeWidth: 1,
          strokeLinejoin: 'butt',
          strokeDasharray: 'none'
      }, options.attrs);
      let ref = options.ref || { x: 0, y: 0 };
      let scale = options.scale || 1;
      if (attrs.strokeDasharray === 'none') {
          attrs.strokeDasharray = [10000, 1];
      }
      let marker = tinySvg.create('marker');
      tinySvg.attr(options.element, attrs);
      tinySvg.append(marker, options.element);
      tinySvg.attr(marker, {
          id: id,
          viewBox: '0 0 20 20',
          refX: ref.x,
          refY: ref.y,
          markerWidth: 20 * scale,
          markerHeight: 20 * scale,
          orient: 'auto'
      });
      tinySvg.append(parentNode, marker);
      return marker
  }

  function marker(parentNode, fill, stroke) {
      let id = "xxxlll";
      if(!_marker){
          let sequenceflowEnd = tinySvg.create('path');
          tinySvg.attr(sequenceflowEnd, { d: 'M 1 5 L 11 10 L 1 15 L 5.6 10 Z' });
          _marker = addMarker(id,parentNode, {
              element: sequenceflowEnd,
              ref: { x: 11, y: 10 },
              scale: 0.5,
              attrs: {
                  fill: fill,
                  stroke: stroke
              }
          });
          return 'url(#' + id + ')';
      }else {
          return 'url(#' + id + ')';
      }
  }

  class CustomRenderer extends BaseRenderer__default['default'] { // 继承BaseRendere
      constructor(eventBus, bpmnRenderer) {
          super(eventBus, HIGH_PRIORITY);
          this.bpmnRenderer = bpmnRenderer;
      }

      canRender(element) {
          // ignore labels
          return !element.labelTarget
      }
      drawConnection(parentNode, element){
          let pathData = createPathFromConnection(element),sourceRefId='',targetRefId='';
          if(element.businessObject){
              sourceRefId = element.businessObject.sourceRef.id;
              targetRefId = element.businessObject.targetRef.id;
          }
          const line  = customConfig[element.type].draw({
              d:pathData.toUpperCase(),
              flowEnd:marker(parentNode, '#ccc', '#ccc'),
              sourceRefId:sourceRefId,
              targetRefId:targetRefId,
              complete:false,
              businessObject:element.businessObject
          });
          tinySvg.append(parentNode, line);
          return line

      }
      drawShape(parentNode, element) {
          const type = element.type; // 获取到类型
          if (customElements.includes(type)) {
              const { draw} = customConfig[type];
              const customIcon = draw({
                  width:element.width,
                  height:element.height,
                  businessObject:element.businessObject,
                  parentNode,
                  element
              });
              tinySvg.attr(customIcon,{
                  'data-element-id':element.id
              });
              tinySvg.append(parentNode, customIcon);
              return customIcon
          }else {
              const di =BpmnRenderUtil.getDi(element);
              di.set('bioc:stroke','#999');
              const shape = this.bpmnRenderer.drawShape(parentNode, element);
              return shape
          }
      }

      getShapePath(shape) {
          return this.bpmnRenderer.getShapePath(shape)
      }
  }

  CustomRenderer.$inject = ['eventBus', 'bpmnRenderer'];

  var index = {
      __init__: ['bpmn-theme-blue'],
      'bpmn-theme-blue': ['type', CustomRenderer]
  };

  return index;

})));
