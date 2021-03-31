<template>
  <div class="bpmn-time-line" :class="{'spin-center':loading}">
    <a-spin :spinning="loading" tip="加载中...">
      <a-timeline>
        <a-timeline-item v-for="item in data" :key="item.id" color="#5BC14B">
          <p>{{fmtDate(item.startTime)}}</p>
          <p>{{item.taskName}}</p>
          <p>已审批</p>
        </a-timeline-item>
        <a-timeline-item v-for="item in uData" :key="item.id" color="orange">
          <p>{{item.taskName}}</p>
          <p>待审批</p>
        </a-timeline-item>
      </a-timeline>
    </a-spin>
  </div>
</template>

<script>
export default {
  name: "BTimeLine",
  props:['loading','data','uData'],
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
}
.spin-center .ant-spin-nested-loading{
  flex: 1;
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
