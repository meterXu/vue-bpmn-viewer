<template>
  <div class="bpmn-time-line" :class="{'spin-center':data.length===0}">
    <div ref="bpmn-time-line" >
      <el-timeline v-if="data.length>0">
        <el-timeline-item v-for="item in data" :key="item.id" :color="getTimeLineColor(item)" :timestamp="fmtDate(item.startTime)" placement="top">
          <div :class="['timeLine-item-over',
              item.status==='已办'?
            (item.approveType==='驳回'?'timeLine-item-over-turn':'timeLine-item-over-ed')
            :'timeLine-item-over-uned']">
            <div @mouseover="handleItemOver(item,item.taskDefinitionKey)" @mouseout="handleItemOut(item.taskDefinitionKey)">
              <p>{{item.taskName}}</p>
              <p>审批类型：{{item.approveType}}</p>
              <p>状态：{{item.status}}</p>
              <p v-if="item.status==='已办'">持续时间：{{timeFormat(item.duration)}}</p>
              <p v-else>剩余时间：{{timeFormat(item.restTime)}}</p>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
      <div v-else class="no-data"></div>
    </div>

  </div>
</template>

<script>
import utils from "./lib/utils.js";
import ms from 'pretty-ms'
import moment from 'moment'
import {Timeline,Card,TimelineItem} from "element-ui";
export default {
  name: "BTimeLine",
  props:['data','bpmnViewer'],
  components:{
    [Card.name]:Card,
    [Timeline.name]:Timeline,
    [TimelineItem.name]: TimelineItem
  },
  data(){
    return {
      loadingInstance:null,
      oldStyle:{color:'#3296fa',setline:false,user:undefined,shadow:false,stroke:false},
      highLight:[
        {color:'#5BC14B',setline:false,user:undefined,shadow:true,stroke:true},
        {color:'#f5842c',setline:false,user:undefined,shadow:true,stroke:true},
        {color:'#ff0000',setline:false,user:undefined,shadow:true,stroke:true}
      ]
    }
  },
  methods:{
    fmtDate(dt){
      if(dt){
        return moment(dt).format('YYYY-MM-DD HH:mm:ss')
      }else{
        return '-'
      }
    },
    timeFormat(s){
      if(s){
        return ms(s*1000)
            .replace(/ -/g,'')
            .replace('d','天')
            .replace('h','小时')
            .replace('m','分')
            .replace('s','秒')
      }else{
        return '-'
      }
    },
    handleItemOver(item,taskId){
      const type = item.status==='已办'?(item.approveType==='审批'?1:3):2
      const taskObj = utils.getTaskObj(this.bpmnViewer._container,taskId)
      if(taskObj.length>0){
        this.oldStyle.color=taskObj[taskObj.length-1].color
      }
      utils.setTaskHighlight(this.bpmnViewer._container,[taskId],this.highLight[type-1])
    },
    handleItemOut(taskId){
      utils.clearHighLight(this.bpmnViewer._container,taskId)
      utils.setTaskHighlight(this.bpmnViewer._container,[taskId],this.oldStyle)
    },
    getTimeLineColor(data){
      if(data.status==='已办'){
        if(data.approveType==='审批'){
          return this.highLight[0].color
        }else if(data.approveType==='驳回'){
          return this.highLight[2].color
        }
      }else if(data.status==='待办'){
        return this.highLight[1].color
      }
    }
  }
}
</script>

<style scoped>
.bpmn-time-line{
  z-index: 10;
  height: 100%;
  margin-right: 20px;
  padding: 30px 10px 30px 0px;
  width: 245px;
  background: #fff;
  border-radius: 5px;
  border: 1px solid #E0E0E0;
  box-shadow: 0 2px 2px #0000000d;
  overflow-y: auto;
  transition: height .3s;
  text-align: left;
}
.bpmn-time-line .el-timeline{
  padding-left: 20px;
}
.spin-center{
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}
.spin-center .ant-spin-nested-loading{
  flex: 1;
}
.no-data{
  width: 50px;
  height: 50px;
  background-size: contain;
  background-image: url("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hsaW5rIiB0PSIxNjM3NzE4ODg2MzUyIiBjbGFzcz0iaWNvbiIgdmlld0JveD0iMCAwIDEwMjQgMTAyNCIgdmVyc2lvbj0iMS4xIiBwLWlkPSI0MzM1IiB3aWR0aD0iNjQwIiBoZWlnaHQ9IjY0MCI+PGRlZnM+PHN0eWxlIHR5cGU9InRleHQvY3NzIi8+PC9kZWZzPjxwYXRoIGQ9Ik0wIDc1OC4zNTczMzNjMCA4Mi4zNjggMjI5LjIyNjY2NyAxNDkuMTQxMzMzIDUxMiAxNDkuMTQxMzM0czUxMi02Ni43NzMzMzMgNTEyLTE0OS4xNDEzMzRjMC04Mi4zNjgtMjI5LjIyNjY2Ny0xNDkuMTQxMzMzLTUxMi0xNDkuMTQxMzMzcy01MTIgNjYuNzczMzMzLTUxMiAxNDkuMTJ6IiBmaWxsPSIjRTZFNkU2IiBwLWlkPSI0MzM2Ii8+PHBhdGggZD0iTTY3NC40OTYgMzc0LjM1NzMzM3YxMDUuNDUwNjY3YzAgNy42OC02LjIyOTMzMyAxMy45MDkzMzMtMTMuOTA5MzMzIDEzLjkwOTMzM0gzNjMuMTM2YTEzLjkwOTMzMyAxMy45MDkzMzMgMCAwIDEtMTMuOTA5MzMzLTEzLjkwOTMzM3YtMTA1LjQ1MDY2N2ExMy45MDkzMzMgMTMuOTA5MzMzIDAgMCAwLTEzLjkzMDY2Ny0xMy45MDkzMzNIMTI3LjE2OGExMy45MDkzMzMgMTMuOTA5MzMzIDAgMCAwLTEzLjkwOTMzMyAxMy45MDkzMzN2NDQ3LjQ0NTMzNGMwIDcuNjggNi4yMjkzMzMgMTMuOTA5MzMzIDEzLjkwOTMzMyAxMy45MDkzMzNoNzYyLjk4NjY2N2M3LjY4IDAgMTMuOTA5MzMzLTYuMjI5MzMzIDEzLjkwOTMzMy0xMy45MDkzMzNWMzc0LjM1NzMzM2ExMy45MDkzMzMgMTMuOTA5MzMzIDAgMCAwLTEzLjkwOTMzMy0xMy45MDkzMzNINjg4LjQyNjY2N2ExMy45MDkzMzMgMTMuOTA5MzMzIDAgMCAwLTEzLjkzMDY2NyAxMy45MDkzMzN6IiBmaWxsPSIjRjJGMkYyIiBwLWlkPSI0MzM3Ii8+PHBhdGggZD0iTTg5MC4xNTQ2NjcgODQ2Ljg0OEgxMjcuMTY4YTI1LjA0NTMzMyAyNS4wNDUzMzMgMCAwIDEtMjUuMDQ1MzMzLTI1LjA0NTMzM1YzNzQuMzU3MzMzYzAtMTMuODQ1MzMzIDExLjIyMTMzMy0yNS4wNDUzMzMgMjUuMDQ1MzMzLTI1LjA0NTMzM2gyMDguMTI4YzEzLjg0NTMzMyAwIDI1LjA0NTMzMyAxMS4yIDI1LjA0NTMzMyAyNS4wNDUzMzN2MTA1LjQ1MDY2N2MwIDEuNTM2IDEuMjU4NjY3IDIuNzczMzMzIDIuNzczMzM0IDIuNzczMzMzaDI5Ny40NzJhMi40OTYgMi40OTYgMCAwIDAgMi43NzMzMzMtMi43NzMzMzN2LTEwNS40NTA2NjdjMC0xMy44NDUzMzMgMTEuMjIxMzMzLTI1LjA0NTMzMyAyNS4wNjY2NjctMjUuMDQ1MzMzaDIwMS43MjhjMTMuODI0IDAgMjUuMDQ1MzMzIDExLjIgMjUuMDQ1MzMzIDI1LjA0NTMzM3Y0NDcuNDQ1MzM0YzAgMTMuODI0LTExLjIyMTMzMyAyNS4wNDUzMzMtMjUuMDQ1MzMzIDI1LjA0NTMzM3pNMTI3LjE2OCAzNzEuNTYyNjY3YTIuNDk2IDIuNDk2IDAgMCAwLTIuNzczMzMzIDIuNzczMzMzdjQ0Ny40NjY2NjdjMCAxLjUzNiAxLjIzNzMzMyAyLjc3MzMzMyAyLjc3MzMzMyAyLjc3MzMzM2g3NjIuOTg2NjY3YTMuMDUwNjY3IDMuMDUwNjY3IDAgMCAwIDIuNzczMzMzLTIuNzczMzMzVjM3NC4zNTczMzNhMi43NzMzMzMgMi43NzMzMzMgMCAwIDAtMi43NzMzMzMtMi43NzMzMzNINjg4LjQyNjY2N2EyLjc3MzMzMyAyLjc3MzMzMyAwIDAgMC0yLjc3MzMzNCAyLjc3MzMzM3YxMDUuNDUwNjY3YzAgMTMuODQ1MzMzLTExLjIyMTMzMyAyNS4wNDUzMzMtMjUuMDY2NjY2IDI1LjA0NTMzM0gzNjMuMTM2YTI1LjA0NTMzMyAyNS4wNDUzMzMgMCAwIDEtMjUuMDQ1MzMzLTI1LjA0NTMzM3YtMTA1LjQ1MDY2N2EyLjQ5NiAyLjQ5NiAwIDAgMC0yLjc3MzMzNC0yLjc3MzMzM0gxMjcuMTQ2NjY3eiIgZmlsbD0iI0IzQjNCMyIgcC1pZD0iNDMzOCIvPjxwYXRoIGQ9Ik0xMjEuNiAzNzcuMTMwNjY3TDEwNC42MTg2NjcgMzYyLjY2NjY2N2wyMDcuODcyLTI0Ny4xMDRjNC44NjQtNS41NDY2NjcgMTEuODE4NjY3LTguNzQ2NjY3IDE5LjItOC44OTZsMzY2Ljc0MTMzMyAxLjY2NGM3LjU5NDY2NyAwLjA0MjY2NyAxNC43NjI2NjcgMy41MiAxOS40NzczMzMgOS40NzJsMTk0Ljc3MzMzNCAyNDUuMTQxMzMzLTE3LjIzNzMzNCAxMy45MDkzMzMtMTk0Ljc3MzMzMy0yNDUuMTQxMzMzLTM2OC43MDQtMi43NzMzMzNMMTIxLjYgMzc3LjEwOTMzM3oiIGZpbGw9IiNCM0IzQjMiIHAtaWQ9IjQzMzkiLz48L3N2Zz4=");
}
.timeLine-item-over{
  padding: 5px 8px;
  transition: background .1s;
  text-align: left;
}
.timeLine-item-over-ed:hover{
  background: rgba(91,193,75,0.8);
  color: #fff;
  border-radius: 5px;
}
.timeLine-item-over-turn:hover{
  background: rgba(255,0,0,0.8);
  border-radius: 5px;
}
.timeLine-item-over-uned:hover{
  background: rgba(245,132,44,0.8);
  color: #fff;
  border-radius: 5px;
}
.timeLine-item-over-uned:hover p,.timeLine-item-over-ed:hover p,.timeLine-item-over-turn:hover p{
  color: #fff;
}

/* 设置滚动条的样式 */
::-webkit-scrollbar {
  width:8px;
}
/* 滚动槽 */
::-webkit-scrollbar-track {
  -webkit-box-shadow:inset006pxrgba(0,0,0,0.3);
  background: rgba(217, 217, 217, 0.3);

}
/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius:5px;
  background: rgba(94, 94, 94, 0.3);
  -webkit-box-shadow:inset006pxrgba(0,0,0,0.5);
}
::-webkit-scrollbar-thumb:window-inactive {
  background: rgba(94, 94, 94, 0.3);
}
</style>
