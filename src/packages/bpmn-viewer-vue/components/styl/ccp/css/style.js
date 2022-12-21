import {create} from "tiny-svg";
let flag = navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)
let styleCon=`
.clock-spin{
    animation: spin 1s infinite;
    animation-timing-function:linear;
}
    .d-userTask{
    cursor: pointer;
}
.highlight-custom-path{
    stroke-dasharray:6;
    animation: complete 2s infinite;
    animation-timing-function:linear;
}
.d-userTask-title{
    text-align: left;
    font-size: 12px;
    color: #fff;
    padding-left: 23px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    height: 100%;
}
.d-subProcess-title{
    text-align: left;
    font-size: 12px;
    color: #fff;
    padding-left: 4px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    height: 100%;
}
.d-userTask-content{
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
.d-userTask-content ul{
    padding: 0;
    margin: 0;
    display: inline-block;
    list-style: none;
    padding-right: 6px;
}
.d-userTask-content ul li{
    height: 24px;
    line-height: 24px;
    color: #cdcdcd;
}
.d-userTask-content li{
    display: flex;
    align-items: center;
    justify-content: left;
}
.d-userTask-content span{
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}
.d-con-user-icon,.d-con-clock-icon{
    display: inline-block;
    background: url("data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyB3aWR0aD0iNDgiIGhlaWdodD0iNDgiIHZpZXdCb3g9IjAgMCA0OCA0OCIgZmlsbD0ibm9uZSIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj4KICAgIDxwYXRoIGQ9Ik01MTMuNDU3MjMxIDQxLjM1Mzg0NmMtMTMzLjM3Nzk2OSAyLjgzNzY2Mi0yNDQuOTk5ODc3IDQ5LjE4OTQxNS0zMzIuOTczMjkyIDEzNy4xNjA4NjJDOTIuNTEyNDkyIDI2Ni40ODgxMjMgNDYuMTYwNzM4IDM3OC4xMTAwMzEgNDMuMzIzMDc3IDUxMS40ODhjMi44Mzc2NjIgMTMzLjM3Nzk2OSA0OS4xODk0MTUgMjQ0LjA1NDY0NiAxMzcuMTYwODYyIDMzMi4wMjYwOTJDMjY4LjQ1NzM1NCA5MzEuNDg3NTA4IDM4MC4wNzkyNjIgOTc3LjgzOTI2MiA1MTMuNDU3MjMxIDk4MC42NzY5MjNjMTMzLjM3Nzk2OS0yLjgzNzY2MiAyNDQuMDU0NjQ2LTQ5LjE4OTQxNSAzMzIuMDI2MDkyLTEzNy4xNjA4NjJTOTc5LjgwODQ5MiA2NDQuODY3OTM4IDk4Mi42NDYxNTQgNTExLjQ4OGMtMi44Mzc2NjItMTMzLjM3Nzk2OS00OS4xODk0MTUtMjQ0LjA1NDY0Ni0xMzcuMTYwODYyLTMzMi4wMjYwOTJDNzU3LjUxMTg3NyA5MC41NDMyNjIgNjQ2LjgzNzE2OSA0NC4xOTE1MDggNTEzLjQ1NzIzMSA0MS4zNTM4NDZMNTEzLjQ1NzIzMSA0MS4zNTM4NDZ6TTgxMS40MzEzODUgNzk5LjA1NjczOGMtMjMuNjUwNDYyLTExLjM1MjYxNS00OS4xODk0MTUtMjMuNjQ4NDkyLTc0LjczMDMzOC0yNi40ODYxNTQtNjcuMTYwNjE1LTUuNjc1MzIzLTEwOC43ODIyNzctMTYuMDgwNzM4LTEyNC44NjMwMTUtMzIuMTYxNDc3LTE1LjEzNzQ3Ny0xNi4wODI3MDgtMjEuNzU4MDMxLTM4Ljc4NC0xOC45MTg0LTY4LjEwOTc4NSAwLjk0NTIzMS0xNy45NzEyIDUuNjc1MzIzLTI4LjM3NjYxNSAxNi4wODA3MzgtMzMuMTA4Njc3IDEwLjQwNTQxNS00LjcyODEyMyAyMS43NTYwNjItMzEuMjE2MjQ2IDMzLjEwODY3Ny03OC41MTMyMzEgMy43ODQ4NjItMTQuMTg4MzA4IDcuNTY3NzU0LTI1LjU0MDkyMyAxMi4yOTc4NDYtMzQuMDUzOTA4IDQuNzI4MTIzLTguNTEyOTg1IDEyLjI5NTg3Ny0yNi40ODYxNTQgMjEuNzU2MDYyLTU1LjgwOTk2OSA0LjczMDA5Mi0yMC44MTA4MzEgNC43MzAwOTItMzMuMTA4Njc3LTAuOTQ1MjMxLTM1Ljk0NDM2OS01LjY3NTMyMy0yLjgzNzY2Mi05LjQ2MDE4NS0zLjc4NDg2Mi0xMC40MDM0NDYtMi44Mzc2NjJMNjY5LjUzODQ2MiA0MTEuMjE4NzA4YzIuODM3NjYyLTEzLjI0MzA3NyA0LjcyODEyMy0yOS4zMjM4MTUgNi42MjA1NTQtNDcuMjk2OTg1IDUuNjc3MjkyLTQ3LjI5Njk4NS0zLjc4Mjg5Mi04Ny45NzM0MTUtMjguMzc4NTg1LTEyMi4wMjczMjMtMjQuNTkzNzIzLTM0Ljk5OTEzOC02OS45OTgyNzctNTIuOTcyMzA4LTEzNy4xNjA4NjItNTQuODY0NzM4LTU5LjU5NDgzMSAwLjk0NTIzMS05MC44MTEwNzcgMjQuNTk1NjkyLTEyNS44MTAyMTUgNTMuOTE5NTA4LTM4Ljc4NCAzMC4yNzEwMTUtNTIuMDI3MDc3IDcwLjk0NTQ3Ny00MS42MjE2NjIgMTIyLjk3MjU1NCA3LjU2Nzc1NCAzOC43ODQgMTguOTE4NCA3MC45NDU0NzcgMTEuMzUwNjQ2IDY4LjEwNzgxNS0wLjk0NTIzMS0wLjk0NTIzMS00LjcyODEyMyAwLTkuNDU4MjE1IDIuODM3NjYyLTQuNzMwMDkyIDIuODM3NjYyLTUuNjc1MzIzIDE1LjEzNTUwOC0xLjg5MjQzMSAzNS45NDQzNjkgMTAuNDA1NDE1IDI1LjU0MDkyMyAxNy45NzMxNjkgNDMuNTE0MDkyIDIzLjY0ODQ5MiA1Mi45NzQyNzcgNS42NzUzMjMgOS40NTgyMTUgOS40NTgyMTUgMjEuNzU2MDYyIDEyLjI5Nzg0NiAzNi44ODk2IDkuNDYwMTg1IDQ2LjM1MTc1NCAxOC45MTg0IDcxLjg5MjY3NyAyOS4zMjM4MTUgNzYuNjIwOCAxMC40MDU0MTUgNC43MzAwOTIgMTcuMDI1OTY5IDE3LjAyNzkzOCAyMC44MTA4MzEgMzQuOTk5MTM4IDQuNzMwMDkyIDI5LjMyNTc4NS0wLjk0NTIzMSA1Mi4wMjcwNzctMTcuOTczMTY5IDY4LjEwOTc4NS0xNy4wMjU5NjkgMTYuMDgwNzM4LTU3LjcwMjQgMjYuNDg2MTU0LTEyMi4wMjczMjMgMzIuMTYxNDc3LTIzLjY0ODQ5MiAyLjgzNzY2Mi01Mi4wMjcwNzcgMTQuMTg4MzA4LTc0LjcyODM2OSAyNS41NDA5MjMtNzIuODM3OTA4LTc1LjY3NTU2OS0xMTUuNDA0OC0xNzUuOTQ2ODMxLTExNi4zNTItMjg2LjYyMTUzOCAyLjgzNzY2Mi0xMTcuMjk3MjMxIDQzLjUxNDA5Mi0yMTQuNzI4ODYyIDEyMi4wMjczMjMtMjkzLjI0NDA2MnMxNzUuOTQ2ODMxLTExOS4xODc2OTIgMjkzLjI0NDA2Mi0xMjIuMDI3MzIzYzExNy4yOTUyNjIgMi44Mzc2NjIgMjE0LjcyODg2MiA0My41MTQwOTIgMjkyLjI5Njg2MiAxMjIuMDI3MzIzIDc4LjUxMzIzMSA3OC41MTMyMzEgMTE5LjE4OTY2MiAxNzUuOTQ2ODMxIDEyMi4wMjczMjMgMjkzLjI0NDA2MkM5MjYuODM0MjE1IDYyNC4wNTUxMzggODg0LjI2NzMyMyA3MjQuMzI0NDMxIDgxMS40MzEzODUgNzk5LjA1NjczOHoiIHRyYW5zZm9ybT0ibWF0cml4KDAuMDUgMCAwIDAuMDUgLTIgLTEpIiBzdHlsZT0iZmlsbDogcmdiKDgzLCAxOTUsIDIxNik7Ij48L3BhdGg+Cjwvc3ZnPgo=") no-repeat center center;
    background-size: contain;
    width: 16px;
    height: 16px;
    margin: 0 4px;
    }
.d-con-clock-icon{
    background: url("data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyB3aWR0aD0iNDgiIGhlaWdodD0iNDgiIHZpZXdCb3g9IjAgMCA0OCA0OCIgZmlsbD0ibm9uZSIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj4KICAgIDxnIHRyYW5zZm9ybT0ibWF0cml4KDAuMDQ1IDAgMCAwLjA0NSAxIDEpIj4KICAgICAgICA8cGF0aCBkPSJNNTEyIDUxMm0tNTEyIDBhNTEyIDUxMiAwIDEgMCAxMDI0IDAgNTEyIDUxMiAwIDEgMC0xMDI0IDBaIiBzdHlsZT0iZmlsbDogcmdiKDI1NSwgMjE1LCAyMTUpOyI+PC9wYXRoPgogICAgICAgIDxwYXRoIGQ9Ik01MTIgNTEybS0zOTguMjIyMjIyIDBhMzk4LjIyMjIyMiAzOTguMjIyMjIyIDAgMSAwIDc5Ni40NDQ0NDQgMCAzOTguMjIyMjIyIDM5OC4yMjIyMjIgMCAxIDAtNzk2LjQ0NDQ0NCAwWiIgc3R5bGU9ImZpbGw6IHJnYigyNDgsIDEyOCwgOTgpOyI+PC9wYXRoPgogICAgICAgIDxwYXRoIGQ9Ik00MjYuNjY2NjY3IDI4NC40NDQ0NDR2MzEyLjg4ODg4OWgyNTZ2LTg1LjMzMzMzM2gtMTcwLjY2NjY2N3YtMjI3LjU1NTU1NnoiIHN0eWxlPSJmaWxsOiByZ2IoMjU1LCAyNTUsIDI1NSk7Ij48L3BhdGg+CiAgICA8L2c+Cjwvc3ZnPgo=") no-repeat center center;
    background-size: contain;
}
.d-con-user-icon:after,.d-con-clock-icon:after{
    content: '';
    width: 14px;
    height: 14px;
    display: inline-block;
}`
let styleConFlag=`
.highlight-custom-path{
    stroke-dasharray:6;
    animation: complete 2s infinite;
    animation-timing-function:linear;
}
.d-userTask-title{
    text-align: left;
    font-size: 12px;
    color: #fff;
    padding-left: 23px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    height: 100%;
}
.d-subProcess-title{
    text-align: left;
    font-size: 12px;
    color: #fff;
    padding-left: 4px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    height: 100%;
}
.d-userTask-content{
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
.d-userTask-content ul{
    padding: 0;
    margin: 0;
    display: inline-block;
    list-style: none;
    padding-right: 6px;
}
.d-userTask-content ul li{
    height: 24px;
    line-height: 24px;
    color: #cdcdcd;
}
.d-userTask-content li{
    display: flex;
    align-items: center;
    justify-content: left;
}
.d-userTask-content span{
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}
.d-con-user-icon,.d-con-clock-icon{
    display: inline-block;
    background: url("data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBzdGFuZGFsb25lPSJubyI/PjwhRE9DVFlQRSBzdmcgUFVCTElDICItLy9XM0MvL0RURCBTVkcgMS4xLy9FTiIgImh0dHA6Ly93d3cudzMub3JnL0dyYXBoaWNzL1NWRy8xLjEvRFREL3N2ZzExLmR0ZCI+PHN2ZyB0PSIxNjYyMDIyMDI5NzA5IiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHAtaWQ9IjQyNjYiIHdpZHRoPSI0OCIgaGVpZ2h0PSI0OCIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik05MjMuMiA0MjkuNkg2MDhsLTk3LjYtMzA0LTk3LjYgMzA0SDk3LjZsMjU2IDE4NS42TDI1NiA5MTcuNmwyNTYtMTg3LjIgMjU2IDE4Ny4yLTEwMC44LTMwMi40eiIgZmlsbD0iI0ZBRDk3RiIgcC1pZD0iNDI2NyI+PC9wYXRoPjxwYXRoIGQ9Ik0xMDI0IDM5Nkg2MzMuNkw1MTIgMjEuNiAzOTAuNCAzOTZIMGwzMTUuMiAyMzAuNC0xMjEuNiAzNzQuNEw1MTIgNzcwLjRsMzE2LjggMjMyTDcwNy4yIDYyOCAxMDI0IDM5NnpNNTEyIDczMC40bC0yNTYgMTg3LjIgOTcuNi0zMDIuNC0yNTYtMTg1LjZoMzE1LjJsOTcuNi0zMDQgOTcuNiAzMDRoMzE1LjJsLTI1NiAxODUuNkw3NjggOTE3LjZsLTI1Ni0xODcuMnoiIGZpbGw9IiIgcC1pZD0iNDI2OCI+PC9wYXRoPjwvc3ZnPg==") no-repeat center center;
    background-size: contain;
    width: 16px;
    height: 16px;
    margin: 0 4px;
    }
.d-con-clock-icon{
    background: url("data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHN2ZyB3aWR0aD0iNDgiIGhlaWdodD0iNDgiIHZpZXdCb3g9IjAgMCA0OCA0OCIgZmlsbD0ibm9uZSIgdmVyc2lvbj0iMS4xIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIj4KICAgIDxnIHRyYW5zZm9ybT0ibWF0cml4KDAuMDQ1IDAgMCAwLjA0NSAxIDEpIj4KICAgICAgICA8cGF0aCBkPSJNNTEyIDUxMm0tNTEyIDBhNTEyIDUxMiAwIDEgMCAxMDI0IDAgNTEyIDUxMiAwIDEgMC0xMDI0IDBaIiBzdHlsZT0iZmlsbDogcmdiKDI1NSwgMjE1LCAyMTUpOyI+PC9wYXRoPgogICAgICAgIDxwYXRoIGQ9Ik01MTIgNTEybS0zOTguMjIyMjIyIDBhMzk4LjIyMjIyMiAzOTguMjIyMjIyIDAgMSAwIDc5Ni40NDQ0NDQgMCAzOTguMjIyMjIyIDM5OC4yMjIyMjIgMCAxIDAtNzk2LjQ0NDQ0NCAwWiIgc3R5bGU9ImZpbGw6IHJnYigyNDgsIDEyOCwgOTgpOyI+PC9wYXRoPgogICAgICAgIDxwYXRoIGQ9Ik00MjYuNjY2NjY3IDI4NC40NDQ0NDR2MzEyLjg4ODg4OWgyNTZ2LTg1LjMzMzMzM2gtMTcwLjY2NjY2N3YtMjI3LjU1NTU1NnoiIHN0eWxlPSJmaWxsOiByZ2IoMjU1LCAyNTUsIDI1NSk7Ij48L3BhdGg+CiAgICA8L2c+Cjwvc3ZnPgo=") no-repeat center center;
    background-size: contain;
}
.d-con-user-icon:after,.d-con-clock-icon:after{
    content: '';
    width: 14px;
    height: 14px;
    display: inline-block;
}`
let style = create('style',{
    type:"text/css"
})
if(flag) {
    style.innerHTML=styleConFlag
} else {
    style.innerHTML=styleCon
}

export default style
