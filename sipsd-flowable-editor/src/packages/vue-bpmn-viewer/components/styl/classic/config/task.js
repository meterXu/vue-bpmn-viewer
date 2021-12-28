import {
    append, attr,
    classes,
    create,
} from 'tiny-svg';
const svgWH ={
    "width": '',
    "height": ''
}
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
}

export default (options)=>{
    svgWH.width = options.width
    svgWH.height = options.height
    const type = options.element.type
    const g = create('g',{
        'data-element-type':'bpmn:userTask'
    });
    const rect = create('rect',{
        x:-1,
        y:-1,
        width:options.width+1,
        height:options.height+1,
        rx:4,
        ry:4,
        stroke:'#000',
        strokeWidth:1,
        transform:"translate(0.5,0.5)",
        fill:'#F5F5F5',
    })
    const filter = create('filter',{
        id:"f1_"+options.element.id,
        x:0,
        y:0,
        width:"200%",
        height:"200%"
    })
    append(filter,create('feOffset',{
        result:"offOut",
        in:"SourceGraphic",
        dx:1,
        dy:1
    }))
    let feColorMatrix = create('feColorMatrix',{
        result:"matrixOut",
        in:"offOut",
        type:"matrix",
        values:`0 0 0 0 0
              0 0 0 0 0
              0 0 0 0 0
              0 0 0 0.1 0`
    })

    append(filter,feColorMatrix)
    classes(feColorMatrix).add('highlight-custom-rect')
    append(filter,create('feGaussianBlur',{
        result:"blurOut",
        in:"matrixOut",
        stdDeviation:3
    }))
    append(filter,create('feBlend',{
        in:"SourceGraphic",
        in2:"blurOut",
        mode:"normal"
    }))
    append(g,append(create('defs'),filter))
    append(g,rect)
    append(g,create('rect',{
        x:1,
        y:1,
        width:options.width-1,
        height:24,
        fill: "#F5F5F5",
        rx:4,
        ry:4
    }))
    append(g,create('rect',{
        x:1,
        y:5,
        width:options.width-1,
        height:20,
        fill: "#F5F5F5",
        rx:0,
        ry:0
    }))
    // let text = create('text',{
    //     x:24,
    //     y:18,
    //     fontSize:'12px',
    //     fontWeight:400,
    //     fill: "#333"
    // })
    // if(options.width<=100&&options.businessObject.name){
    //     text.innerHTML=options.businessObject.name.substring(0,5)||'-'
    // }
    // else {
    //     text.innerHTML=options.businessObject.name||'-'
    // }
    // append(g,text)
    classes(rect).add('d-userTask')

    // 设置标题图标和内容
    let titleIcon = taskIcon[type].icon()
    append(g,titleIcon)

    let content = taskIcon[type].content(options)
    append(g,content)
    return g
}

function userObj(options){
    let user = create('g')
    classes(user).add('custom-realName')
    // let icon = null
    let text = null
    if(options.businessObject&&options.businessObject.extensionElements){
        const userInfoLastName = options.businessObject.extensionElements.values.find(c=>c.$type.indexOf('info-lastname')>0)
        const assignee = options.businessObject.$attrs["flowable:assignee"]
        if(userInfoLastName){
            // text = createConText(userInfoLastName.$body)
            text = createConText(options.businessObject.name)
            // icon=createUserIcon()
        }else if(assignee){
            text = createConText(assignee)
            // icon=createUserIcon()

        }else{
            text = createUnConText()
        }
    }else{
         text = createUnConText()
    }
    append(user,text)
    // if(icon){
    //     append(user,icon)
    // }
    // let clock = clockObj(options)
    // if(clock){
    //     append(user,clock)
    // }
    return user
}

function serviceObj(options){
    let con = create('g')
    const text = createUnConText({x:20,y:50,text:'服务任务'})
    append(con,text)
    return con
}

function scriptObj(options){
    let con = create('g')
    const text = createUnConText({x:20,y:50,text:'脚本任务'})
    append(con,text)
    return con
}

function clockObj(options){
    let clock = create('g')
    classes(clock).add('custom-max-day')
    let icon = null
    let text = null
    if(options.businessObject&&options.businessObject.extensionElements){
        const taskMaxDay = options.businessObject.extensionElements.values.find(c=>c.$type.indexOf('task_max_day')>0)
        if(taskMaxDay&&taskMaxDay.$body){
            text = createConText(taskMaxDay.$body+'天',{x:38,y:76,fill:'#f88062'})
            icon=createClockIcon()
        }else{
            return  null
        }
    }else{
       return  null
    }
    append(clock,text)
    if(icon){
        append(clock,icon)
    }
    return clock
}

function createUserIcon(){
    let path = create('path',{
        d:'M513.457231 41.353846c-133.377969 2.837662-244.999877 49.189415-332.973292 137.160862C92.512492 266.488123 46.160738 378.110031 43.323077 511.488c2.837662 133.377969 49.189415 244.054646 137.160862 332.026092C268.457354 931.487508 380.079262 977.839262 513.457231 980.676923c133.377969-2.837662 244.054646-49.189415 332.026092-137.160862S979.808492 644.867938 982.646154 511.488c-2.837662-133.377969-49.189415-244.054646-137.160862-332.026092C757.511877 90.543262 646.837169 44.191508 513.457231 41.353846L513.457231 41.353846zM811.431385 799.056738c-23.650462-11.352615-49.189415-23.648492-74.730338-26.486154-67.160615-5.675323-108.782277-16.080738-124.863015-32.161477-15.137477-16.082708-21.758031-38.784-18.9184-68.109785 0.945231-17.9712 5.675323-28.376615 16.080738-33.108677 10.405415-4.728123 21.756062-31.216246 33.108677-78.513231 3.784862-14.188308 7.567754-25.540923 12.297846-34.053908 4.728123-8.512985 12.295877-26.486154 21.756062-55.809969 4.730092-20.810831 4.730092-33.108677-0.945231-35.944369-5.675323-2.837662-9.460185-3.784862-10.403446-2.837662L669.538462 411.218708c2.837662-13.243077 4.728123-29.323815 6.620554-47.296985 5.677292-47.296985-3.782892-87.973415-28.378585-122.027323-24.593723-34.999138-69.998277-52.972308-137.160862-54.864738-59.594831 0.945231-90.811077 24.595692-125.810215 53.919508-38.784 30.271015-52.027077 70.945477-41.621662 122.972554 7.567754 38.784 18.9184 70.945477 11.350646 68.107815-0.945231-0.945231-4.728123 0-9.458215 2.837662-4.730092 2.837662-5.675323 15.135508-1.892431 35.944369 10.405415 25.540923 17.973169 43.514092 23.648492 52.974277 5.675323 9.458215 9.458215 21.756062 12.297846 36.8896 9.460185 46.351754 18.9184 71.892677 29.323815 76.6208 10.405415 4.730092 17.025969 17.027938 20.810831 34.999138 4.730092 29.325785-0.945231 52.027077-17.973169 68.109785-17.025969 16.080738-57.7024 26.486154-122.027323 32.161477-23.648492 2.837662-52.027077 14.188308-74.728369 25.540923-72.837908-75.675569-115.4048-175.946831-116.352-286.621538 2.837662-117.297231 43.514092-214.728862 122.027323-293.244062s175.946831-119.187692 293.244062-122.027323c117.295262 2.837662 214.728862 43.514092 292.296862 122.027323 78.513231 78.513231 119.189662 175.946831 122.027323 293.244062C926.834215 624.055138 884.267323 724.324431 811.431385 799.056738z',
        transform:'matrix(0.015 0 0 0.015 20 37)',
        fill:'#53c3d8'
    })
    return path
}

function createClockIcon(){
    let path = create('path',{
        d:'M511.914 63.99c-247.012 0-447.925 200.912-447.925 447.924s200.913 447.925 447.925 447.925 447.925-200.913 447.925-447.925S758.926 63.989 511.914 63.989z m0 831.687c-211.577 0-383.763-172.186-383.763-383.763 0-211.577 172.014-383.763 383.763-383.763s383.763 172.014 383.763 383.763-172.186 383.763-383.763 383.763z m160.145-383.763H512.086v-223.79c0-17.718-14.277-32.167-31.995-32.167-17.717 0-31.994 14.45-31.994 32.167V544.08c0 17.717 14.277 31.994 31.994 31.994H672.06c17.718 0 32.167-14.277 32.167-31.994-0.172-17.89-14.621-32.167-32.167-32.167z',
        transform:'matrix(0.015 0 0 0.015 20 73)',
        fill:'#2c3e50'
    })

    let g = create('g')
    let path1 = create('path',{
        d:'M512 512m-512 0a512 512 0 1 0 1024 0 512 512 0 1 0-1024 0Z',
        fill:"#ffd7d7"
    })
    let path2 = create('path',{
        d:'M512 512m-398.222222 0a398.222222 398.222222 0 1 0 796.444444 0 398.222222 398.222222 0 1 0-796.444444 0Z',
        fill:'#f88062',
    })
    let path3 = create('path',{
        d:'M426.666667 284.444444v312.888889h256v-85.333333h-170.666667v-227.555556z',
        fill:"#ffffff"
    })
    append(g,path1)
    append(g,path2)
    append(g,path3)
    classes(path3).add('clock-spin')
    attr(g,{
        transform:'matrix(0.015 0 0 0.015 20 64)',
    })
    return g
}


function createUnConText(option={x:20,y:50,text:'未分配'}){
    let text  = create('text',{
        x:option.x,
        y:option.y,
        fontSize:'14px',
        fill:'#bfbfbf',
    })
    text.innerHTML=option.text
    return text
}
function createConText(name,option={x:38,y:50,fill:'#333'}){
    let foreignObject = create('foreignObject',{
        width:svgWH.width,
        height:svgWH.height
    })
    let div = document.createElement("div");
    div.style.margin = "10px"
    div.style.marginTop = "30px"
    div.style.textAlign = "center"
    div.style.whiteSpace = "normal"
    div.style.wordBreak = "break-all"
    div.innerHTML = name
    append(foreignObject,div)
    return foreignObject
}

function createUserTitleIcon(){
    let g = create('g',{
        transform:'matrix(0.012 0 0 0.012 8 7)'
    })
    let user_1 = create('path',{
        d:'M248.384 264.128C248.384 118.272 366.4 0 512 0s263.616 118.272 263.616 264.128S657.6 528.256 512 528.256 248.384 409.984 248.384 264.128z',
        fill:'#333'
    })
    let user_2 = create('path',{
        d:'M972.8 1024c-11.456-245.056-213.376-428.736-460.8-428.736S62.656 778.944 51.2 1024L972.8 1024z',
        fill:'#333'
    })
    classes(user_1).add('f_userColor1')
    classes(user_2).add('f_userColor2')
    append(g,user_1)
    append(g,user_2)
    return g
}
function createServiceTitleIcon(){
    let g = create('g',{
        transform:'matrix(1 0 0 1 5 4)'
    })
    let icon_1 = create('path',{
        d:'M 8,1 7.5,2.875 c 0,0 -0.02438,0.250763 -0.40625,0.4375 C 7.05724,3.330353 7.04387,3.358818 7,3.375 6.6676654,3.4929791 6.3336971,3.6092802 6.03125,3.78125 6.02349,3.78566 6.007733,3.77681 6,3.78125 5.8811373,3.761018 5.8125,3.71875 5.8125,3.71875 l -1.6875,-1 -1.40625,1.4375 0.96875,1.65625 c 0,0 0.065705,0.068637 0.09375,0.1875 0.002,0.00849 -0.00169,0.022138 0,0.03125 C 3.6092802,6.3336971 3.4929791,6.6676654 3.375,7 3.3629836,7.0338489 3.3239228,7.0596246 3.3125,7.09375 3.125763,7.4756184 2.875,7.5 2.875,7.5 L 1,8 l 0,2 1.875,0.5 c 0,0 0.250763,0.02438 0.4375,0.40625 0.017853,0.03651 0.046318,0.04988 0.0625,0.09375 0.1129372,0.318132 0.2124732,0.646641 0.375,0.9375 -0.00302,0.215512 -0.09375,0.34375 -0.09375,0.34375 L 2.6875,13.9375 4.09375,15.34375 5.78125,14.375 c 0,0 0.1229911,-0.09744 0.34375,-0.09375 0.2720511,0.147787 0.5795915,0.23888 0.875,0.34375 0.033849,0.01202 0.059625,0.05108 0.09375,0.0625 C 7.4756199,14.874237 7.5,15.125 7.5,15.125 L 8,17 l 2,0 0.5,-1.875 c 0,0 0.02438,-0.250763 0.40625,-0.4375 0.03651,-0.01785 0.04988,-0.04632 0.09375,-0.0625 0.332335,-0.117979 0.666303,-0.23428 0.96875,-0.40625 0.177303,0.0173 0.28125,0.09375 0.28125,0.09375 l 1.65625,0.96875 1.40625,-1.40625 -0.96875,-1.65625 c 0,0 -0.07645,-0.103947 -0.09375,-0.28125 0.162527,-0.290859 0.262063,-0.619368 0.375,-0.9375 0.01618,-0.04387 0.04465,-0.05724 0.0625,-0.09375 C 14.874237,10.52438 15.125,10.5 15.125,10.5 L 17,10 17,8 15.125,7.5 c 0,0 -0.250763,-0.024382 -0.4375,-0.40625 C 14.669647,7.0572406 14.641181,7.0438697 14.625,7 14.55912,6.8144282 14.520616,6.6141566 14.4375,6.4375 c -0.224363,-0.4866 0,-0.71875 0,-0.71875 L 15.40625,4.0625 14,2.625 l -1.65625,1 c 0,0 -0.253337,0.1695664 -0.71875,-0.03125 l -0.03125,0 C 11.405359,3.5035185 11.198648,3.4455201 11,3.375 10.95613,3.3588185 10.942759,3.3303534 10.90625,3.3125 10.524382,3.125763 10.5,2.875 10.5,2.875 L 10,1 8,1 z m 1,5 c 1.656854,0 3,1.3431458 3,3 0,1.656854 -1.343146,3 -3,3 C 7.3431458,12 6,10.656854 6,9 6,7.3431458 7.3431458,6 9,6 z',
        fill:'#aaa'
    })
    append(g,icon_1)
    return g
}
function createScriptTitleIcon(){
    let g = create('g',{
        transform:'matrix(1 0 0 1 5 4)'
    })
    let icon_1 = create('path',{
        d:'m 5,2 0,0.094 c 0.23706,0.064 0.53189,0.1645 0.8125,0.375 0.5582,0.4186 1.05109,1.228 1.15625,2.5312 l 8.03125,0 1,0 1,0 c 0,-3 -2,-3 -2,-3 l -10,0 z M 4,3 4,13 2,13 c 0,3 2,3 2,3 l 9,0 c 0,0 2,0 2,-3 L 15,6 6,6 6,5.5 C 6,4.1111 5.5595,3.529 5.1875,3.25 4.8155,2.971 4.5,3 4.5,3 L 4,3 z',
        fill:'#aaa'
    })
    append(g,icon_1)
    return g
}
