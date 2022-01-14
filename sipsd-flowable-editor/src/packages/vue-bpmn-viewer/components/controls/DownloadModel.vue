<template>
  <div class="mask-layer" v-if="showClose">
    <div class="content-layer">
      <div class="content-top">
        <div style="color: #8a8a8a">流程图下载</div>
        <div class="title-icon" @click="handleClose">
          <svg t="1642059629860" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2427" width="32" height="32"><path d="M511.333 127.333c51.868 0 102.15 10.144 149.451 30.15 45.719 19.337 86.792 47.034 122.078 82.321 35.287 35.286 62.983 76.359 82.321 122.078 20.006 47.3 30.15 97.583 30.15 149.451s-10.144 102.15-30.15 149.451c-19.337 45.719-47.034 86.792-82.321 122.078-35.286 35.287-76.359 62.983-122.078 82.321-47.3 20.006-97.583 30.15-149.451 30.15s-102.15-10.144-149.451-30.15c-45.719-19.337-86.792-47.034-122.078-82.321-35.287-35.286-62.983-76.359-82.321-122.078-20.006-47.3-30.15-97.583-30.15-149.451s10.144-102.15 30.15-149.451c19.337-45.719 47.034-86.792 82.321-122.078 35.286-35.287 76.359-62.983 122.078-82.321 47.301-20.006 97.583-30.15 149.451-30.15m0-64c-247.424 0-448 200.576-448 448s200.576 448 448 448 448-200.576 448-448-200.576-448-448-448z" fill="#8a8a8a" p-id="2428"></path><path d="M557.254 512l147.373-147.373c12.497-12.497 12.497-32.758 0-45.255-12.496-12.497-32.758-12.497-45.254 0L512 466.746 364.627 319.373c-12.497-12.497-32.758-12.497-45.255 0s-12.497 32.758 0 45.255L466.746 512 319.373 659.373c-12.497 12.496-12.497 32.758 0 45.254C325.621 710.876 333.811 714 342 714s16.379-3.124 22.627-9.373L512 557.254l147.373 147.373C665.621 710.876 673.811 714 682 714s16.379-3.124 22.627-9.373c12.497-12.496 12.497-32.758 0-45.254L557.254 512z" fill="#8a8a8a" p-id="2429"></path></svg>
        </div>
      </div>
      <div class="content-inside">
              <div class="download-model">
                <VueBpmnViewer
                    :type="type"
                    :baseApi="baseApi"
                    :xmlId="xmlId"
                    :source="source"
                    :timeData="timeData"
                    :options="options"
                    :styl="styl"
                    style="height: 100%">
                </VueBpmnViewer>
              </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex';
import {portal} from "@dpark/s2-utils"
import VueBpmnViewer from "../../index.js";
export default {
  name: "DownloadModel",
  components: {VueBpmnViewer},
  props: ["baseApi","xmlId","type","source","timeData","options","styl"],
  // computed:{
  //   ...mapState({
  //     form: state => state.biz.bpmn.form,
  //     timeData: state => state.biz.bpmn.timeData,
  //     options: state => state.biz.bpmn.options
  //   }),
  // },
  data() {
    return {
      showClose:false
    }
  },
  created() {

  },
  methods:{
    handleClose() {
      this.showClose = false
      // this.$emit('close',true)
    },
    init() {
      this.showClose = true
      console.log(this.xmlId)
    },
  }
}
</script>

<style scoped>
.mask-layer {
  position: fixed;
  display: flex;
  justify-content: center;
  align-items: center;
  left:0;
  top:0;
  width: 100%;
  height: 100%;
  background-color: rgba(108, 108, 108, 0.6);
  z-index: 1000;
}
.content-layer {
  width: 1200px;
  height: 800px;
  background-color: #fff;
}
.content-top {
  width: 100%;
  height: 50px;
  font-size: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
.content-top .title-icon:hover {
  cursor: pointer;
}
.content-inside {

}
.download-model{
  width: 1200px;
  height: 800px;
}
</style>
