<template>
  <div ref="container" class="vue-bpmn-diagram-container"></div>
</template>

<script>
import Vue from 'vue'
import BpmnJS from 'bpmn-js/dist/bpmn-navigated-viewer.production.min.js';
import BpmnViewer from "bpmn-js/lib/Viewer"
import {Loading} from 'element-ui'
export default {
  name: 'VueBpmn',
  props: {
    url: {
      type: String,
    },
    options: {
      type: Object
    },
    viewer:{
      type:Boolean,
      default:false
    }
  },
  data: function() {
    return {
      diagramXML: null,
      bpmnReadOnly:null,
      bpmnFull:null,
      loadingInstance:null
    };
  },
  computed:{
    bpmnViewer(){
      Vue.prototype.$bpmnViewer =this.bpmnReadOnly||this.bpmnFull
      return Vue.prototype.$bpmnViewer
    }
  },
  methods: {
    init(){
      let container = this.$refs.container;
      let self = this;
      let _options = Object.assign({
        container: container
      }, this.options)
      if(this.viewer){
        if(!this.bpmnReadOnly){
          this.bpmnReadOnly = new BpmnViewer(_options);
          this.bpmnReadOnly.on('import.done', function(event) {
            let error = event.error;
            let warnings = event.warnings;
            if (error) {
              self.$emit('error', error);
            } else {
              self.$emit('loaded', warnings);
            }
          });
        }
      }else{
        if(!this.bpmnFull){
          this.bpmnFull = new BpmnJS(_options);
          this.bpmnFull.on('import.done', function(event) {
            let error = event.error;
            let warnings = event.warnings;
            if (error) {
              self.$emit('error', error);
            } else {
              self.$emit('loaded', warnings);
            }
          });
        }
      }
      this.fetchDiagram(this.url).then(xml=>{
        this.drawXml(xml)
      });
    },
    fetchDiagram: function(url) {
      return new Promise((resolve, reject)=>{
        let self = this;
        if(url){
          if(/^http:\/\/|^https:\/\//g.test(this.url)){
            fetch(url)
                .then(function(response) {
                  return response.text();
                })
                .then(function(text) {
                  self.diagramXML = text;
                  resolve(text)
                })
                .catch(function(err) {
                  reject(err)
                  self.$emit('error', err);
                })
          }else{
            self.diagramXML = url;
            resolve(url)
          }
        }
      })
    },
    drawXml(xml) {
      if(this.viewer){
        if(this.bpmnReadOnly){
          this.bpmnReadOnly.importXML(xml);
          this.bpmnReadOnly.get('canvas').zoom('fit-viewport');
        }
      }else{
        if(this.bpmnFull){
          this.bpmnFull.importXML(xml);
          this.bpmnFull.get('canvas').zoom('fit-viewport');
        }
      }
    },
    reload(){
      this.init()
    }
  },
  mounted: function () {
    this.init()
  },
  beforeDestroy: function() {
    if(this.bpmnReadOnly){
      this.bpmnReadOnly.destroy();
    }
    if(this.bpmnFull){
      this.bpmnFull.destroy();
    }
  }
};
</script>

<style scoped>
.vue-bpmn-diagram-container {
  height: 100%;
  width: 100%;
}
</style>
