<template>
  <div>
    自定义登录
  </div>
</template>
<script>
import Vue from 'vue'
import { portal,util,type} from "@dpark/s2-utils"
export default {
  name:'login',
  mounted(){
    const token = 'xxxx'
    const userInfo = {
      'username': 'admin',
      'realname': '管理员',
      'avatar': null,
    }
    portal.globalStore.commit('SET_TOKEN', token)
    portal.globalStore.commit('SET_INFO', userInfo)
    portal.globalStore.commit('SET_AVATAR', userInfo.avatar)
    portal.globalStore.commit('SET_NAME', { username: userInfo.username, realname: userInfo.realname, welcome: util.welcome() })
    Vue.ls.set(type.ACCESS_TOKEN,token)
    Vue.ls.set(type.USER_NAME, userInfo.username)
    Vue.ls.set(type.USER_INFO, userInfo)
    this.$router.push({path:this.$project_bpmn.redirect.index})
  }
}
</script>

<style>

</style>
