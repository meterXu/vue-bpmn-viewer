<template>
  <div class="con">
    <ul class="con-ul">
      <li><HelloWorld :msg="msg"/> </li>
      <li><HelloWorld :msg="msg" :size="'100px'"/> </li>
      <li><HelloWorld :msg="msg" :size="'80px'"/> </li>
    </ul>
    <p class="con-p">
      <span>该项目由@park/s2驱动</span>
    </p>
    <p class="con-p">
      <a href="http://58.210.9.131:5088/pldoc/" target="_blank">在线帮助文档</a>
    </p>
  </div>
</template>
<script>
import HelloWorld from '@/components/IconView.vue'
export default {
  name: 'hello',
  components:{
      HelloWorld
  },
  data(){
      return {
          msg:""
      }
  }
}
</script>
<style scoped>
.con{
  padding: 20px 0;
}
.con-p{
  text-align: center;
  font-size: 16px;
  padding: 10px 0;
}
.con-ul{
  list-style: none;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
  margin: 0;
}
.con-ul li{
  padding: 0 10px;
}
</style>
