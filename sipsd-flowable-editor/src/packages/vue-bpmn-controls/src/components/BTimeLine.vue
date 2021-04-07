<template>
  <div class="bpmn-time-line" :class="{'spin-center':loading||(!loading&&data.length===0&&uData.length===0)}">
    <a-spin :spinning="loading" tip="加载中...">
      <a-timeline v-if="data.length>0||uData.length>0">
        <a-timeline-item v-for="item in data" :key="item.id" color="#5BC14B">
          <div class="timeLine-item-over timeLine-item-over-ed" @mouseover="handleItemOver(1,item.taskDefinitionKey)" @mouseout="handleItemOut(item.taskDefinitionKey)">
            <p>{{fmtDate(item.startTime)}}</p>
            <p>{{item.taskName}}</p>
            <p>已审批</p>
          </div>
        </a-timeline-item>
        <a-timeline-item v-for="item in uData" :key="item.id" color="orange">
          <div class="timeLine-item-over timeLine-item-over-uned" @mouseover="handleItemOver(2,item.taskDefinitionKey)" @mouseout="handleItemOut(item.taskDefinitionKey)">
            <p>{{item.taskName}}</p>
            <p>待审批</p>
          </div>
        </a-timeline-item>
      </a-timeline>
      <span v-else-if="!loading">无数据</span>
    </a-spin>

  </div>
</template>

<script>
import utils from "../lib/utils";
export default {
  name: "BTimeLine",
  props:['loading','data','uData'],
  data(){
    return {
      oldStyle:{color:'#3296fa',setline:false,user:undefined,shadow:false},
      highLight:[
        {color:'#5BC14B',setline:false,user:undefined,shadow:true},
        {color:'#f5842c',setline:false,user:undefined,shadow:true}
      ]
    }
  },
  methods:{
    fmtDate(dt){
      const t = new Date(dt)
      let m = t.getMonth()+1
      if(m<10){m=`0${m}`}
      let d  = t.getDate()
      if(d<10){d=`0${d}`}
      let hh = t.getHours()
      if(hh<10){hh=`0${hh}`}
      let mm=t.getMinutes()
      if(mm<10){mm=`0${mm}`}
      let ss=t.getSeconds()
      if(ss<10){ss=`0${ss}`}
      return `${t.getFullYear()}-${m}-${d} ${hh}:${mm}:${ss}`
    },
    handleItemOver(type,taskId){
      const taskObj = utils.getTaskObj(taskId)
      if(taskObj){
        this.oldStyle.color=taskObj.color
      }
      utils.setTaskHighlight([taskId],this.highLight[type-1])
    },
    handleItemOut(taskId){
      utils.clearHighLight(taskId)
      utils.setTaskHighlight([taskId],this.oldStyle)
    }
  }
}
</script>

<style scoped>
.bpmn-time-line{
  z-index: 10;
  height: 100%;
  margin-right: 20px;
  padding: 30px 30px 30px 30px;
  width: 245px;
  background: #fff;
  border-radius: 5px;
  border: 1px solid #E0E0E0;
  box-shadow: 0 2px 2px #0000000d;
  overflow-y: auto;
  transition: height .3s;
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
.timeLine-item-over{
  padding: 5px 8px;
  transition: background .1s;
}
.timeLine-item-over-ed:hover{
  background: rgba(91,193,75,0.8);
  color: #fff;
  border-radius: 5px;
}
.timeLine-item-over-uned:hover{
  background: rgba(245,132,44,0.8);
  color: #fff;
  border-radius: 5px;
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
