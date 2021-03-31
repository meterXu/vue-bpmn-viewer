<template>
  <div>
    自定义登录
  </div>
</template>
<script>
import Vue from 'vue'
import { ACCESS_TOKEN,USER_NAME,USER_INFO} from "@/utils/mutation-types"
import {globalStore} from '@/utils'
import { welcome } from '@/utils/util'
export default {
  name:'login',
  mounted(){
    const token = 'xxxx'
    const userInfo = {
      'username': 'admin',
      'realname': '管理员',
      'avatar': null,
    }
    globalStore.commit('SET_TOKEN', token)
    globalStore.commit('SET_INFO', userInfo)
    globalStore.commit('SET_AVATAR', userInfo.avatar)
    globalStore.commit('SET_NAME', { username: userInfo.username, realname: userInfo.realname, welcome: welcome() })
    Vue.ls.set(ACCESS_TOKEN,token)
    Vue.ls.set(USER_NAME, userInfo.username)
    Vue.ls.set(USER_INFO, userInfo)
    this.$router.push({path:this.$project_bpmn.redirect.index})
  }
}
</script>

<style>

</style>
