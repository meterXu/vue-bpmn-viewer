<template>
  <div class="bpmn-time-line">
    <el-timeline>
      <el-timeline-item v-for="item in data" :key="item.id" color="#5BC14B">
        <p>{{fmtDate(item.startTime)}}</p>
        <p>{{item.taskName}}</p>
        <p>已审批</p>
      </el-timeline-item>
      <el-timeline-item v-for="item in uData" :key="item.id" color="orange">
        <p>{{item.taskName}}</p>
        <p>待审批</p>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>

<script>
export default {
  name: "BTimeLine",
  props:['data','uData'],
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
    }
  },
  mounted() {
  }
}
</script>

<style scoped>
.bpmn-time-line{
  position: fixed;
  z-index: 10;
  right: 20px;
  top: 60px;
  bottom: 50px;
  padding: 30px 60px 30px 10px;
  background: #fff;
  border-radius: 5px;
  border: 1px solid #E0E0E0;
  /*box-shadow: 1px 1px 5px #cdcdcd;*/
  overflow-y: auto;
  transition: height .3s;
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
  background: rgba(51, 101, 189,0.6);
}
</style>
