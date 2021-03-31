<template>
    <div>
        <IconView :msg="msg"/>
    </div>
</template>
<script>
import IconView from '@/components/IconView.vue'
export default {
    name:'page-b',
    components:{
      IconView
    },
     data(){
      return {
          msg:"这是页面B"
      }
  },
}
</script>
