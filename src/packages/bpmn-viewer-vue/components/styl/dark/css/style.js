import {create} from "tiny-svg";

let styleCon=`
.clock-spin{
    transform-origin: 512px 512px;
    animation: spin 1s infinite;
    animation-timing-function:linear;
}
.d-userTask{
    cursor: pointer;
}
.d-userTask:hover{
    stroke: #aaa !important;
}
.highlight-custom-path{
    stroke-dasharray:6;
    animation: complete 2s infinite;
    animation-timing-function:linear;
}
.d-subProcess-title{
    text-align: left;
    font-size: 12px;
    color: #9FB5BF;
    padding-left: 4px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    height: 100%;
}
`

let style = create('style',{
    type:"text/css"
})
style.innerHTML=styleCon

export default style
