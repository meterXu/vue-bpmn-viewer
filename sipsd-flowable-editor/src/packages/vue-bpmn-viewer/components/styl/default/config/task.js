import {
    append, attr,
    classes,
    create,
} from 'tiny-svg';

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
    const type = options.element.type
    const g = create('g',{
        'data-element-type':'bpmn:userTask'
    });
    const rect = create('rect',{
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
        fill: "#aaaaaa",
        rx:4,
        ry:4
    }))
    let titleRect = create('rect',{
        x:1,
        y:5,
        width:options.width-1,
        height:20,
        fill: "#aaaaaa",
        rx:0,
        ry:0
    })
    append(g,titleRect)

    let foreignObject = create('foreignObject',{
        x:1,
        y:5,
        width:options.width-1,
        height:20,
    })
    let div = document.createElement("div");
    div.classList.add('d-userTask-title')
    div.innerText=options.businessObject.name||'-'
    div.title=div.innerText
    append(foreignObject,div)
    append(g,foreignObject)

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
    let icon = null
    let content = null
    let text = null
    let clockText = null
    if(options.businessObject&&options.businessObject.extensionElements){
        const userInfoLastName = options.businessObject.extensionElements.values.find(c=>c.$type.indexOf('info-lastname')>0)
        const assignee = options.businessObject.$attrs["flowable:assignee"]

        if(userInfoLastName){
            text = userInfoLastName.$body
        }else if(assignee){
            text = assignee
        }
        const taskMaxDay = options.businessObject.extensionElements.values.find(c=>c.$type.indexOf('task_max_day')>0)
        if(taskMaxDay&&taskMaxDay.$body){
            clockText = taskMaxDay.$body+'天'
        }
        content = createCon(options,text,clockText)
    }else{
        content = createCon(options,text,clockText)
    }
    append(user,content)
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
function createConText(name,option={x:38,y:50,fill:'#2c3e50'}){
    let text  = create('text',{
        x:option.x,
        y:option.y,
        fontSize:'14px',
        fill:option.fill,
    })
    text.innerHTML=name
    return text
}

function createCon(options,text,clock){
    let foreignObject = create('foreignObject',{
        x:1,
        y:25,
        width:options.width-1,
        height:options.height-25,
    })
    let div = document.createElement("div");
    div.classList.add('d-userTask-content')
    div.innerHTML=`
    <ul style="max-width: ${options.width}px">
        <li class="d-con-user">
            <i class="d-con-user-icon"></i>
            <span style="color:${text?'#6c6c6c':''}">${text||'未分配'}</span>
        </li>
       <li class="d-con-clock">
            <i class="${clock?'d-con-clock-icon clock-spin':'d-con-clock-icon'}"></i>
            <span style="color:${clock?'#f88062':''}">${clock||'-'}</span>
       </li>
    </ul>`
    append(foreignObject,div)
    return foreignObject
}

function createUserTitleIcon(){
    let g = create('g',{
        transform:'matrix(0.012 0 0 0.012 8 7)'
    })
    let user_1 = create('path',{
        d:'M248.384 264.128C248.384 118.272 366.4 0 512 0s263.616 118.272 263.616 264.128S657.6 528.256 512 528.256 248.384 409.984 248.384 264.128z',
        fill:'#fff'
    })
    let user_2 = create('path',{
        d:'M972.8 1024c-11.456-245.056-213.376-428.736-460.8-428.736S62.656 778.944 51.2 1024L972.8 1024z',
        fill:'#fff'
    })
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
        fill:'#fff'
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
        fill:'#fff'
    })
    append(g,icon_1)
    return g
}
