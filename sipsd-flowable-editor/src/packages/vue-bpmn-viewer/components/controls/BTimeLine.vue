<template>
  <div class="bpmn-time-line" :class="{'spin-center':loading||(!loading&&data.length===0)}">
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
      <span v-else-if="!loading">无数据</span>
    </div>

  </div>
</template>

<script>
import utils from "./lib/utils.js";
import ms from 'pretty-ms'
import moment from 'moment'
import {Loading,Timeline,Card,TimelineItem} from "element-ui";
console.log(Card)
export default {
  name: "BTimeLine",
  props:['loading','data'],
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
  watch:{
    loading:{
      handler(nv){
        if(nv){
          this.loadingInstance=Loading.service({
            target:this.$refs['bpmn-time-line'],
            fullscreen:false
          })
        }else{
          this.loadingInstance&&this.loadingInstance.close()
        }
      },
      immediate:true
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
      const taskObj = utils.getTaskObj(taskId)
      if(taskObj){
        this.oldStyle.color=taskObj.color
      }
      utils.setTaskHighlight([taskId],this.highLight[type-1])
    },
    handleItemOut(taskId){
      utils.clearHighLight(taskId)
      utils.setTaskHighlight([taskId],this.oldStyle)
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
