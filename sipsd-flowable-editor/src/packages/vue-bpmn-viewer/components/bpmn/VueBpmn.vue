<template>
  <div ref="container" class="vue-bpmn-diagram-container"></div>
</template>

<script>
import Vue from 'vue'
import BpmnJS from 'bpmn-js/dist/bpmn-navigated-viewer.production.min.js';
import BpmnViewer from "bpmn-js/lib/Viewer"
export default {
  name: 'VueBpmn',
  props: {
    url: {
      type: String,
      required: true
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
      diagramXML: null
    };
  },
  mounted: function () {
    this.init()
  },
  beforeDestroy: function() {
    this.bpmnViewer.destroy();
  },
  watch: {
    url: function(val) {
      this.$emit('loading');
      this.fetchDiagram(val);
    },
    diagramXML: function(val) {
      this.bpmnViewer.importXML(val);
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
        this.bpmnViewer = new BpmnViewer(_options);
      }else{
        this.bpmnViewer = new BpmnJS(_options);
      }
      Vue.prototype.$bpmnViewer=this.bpmnViewer
      this.bpmnViewer.on('import.done', function(event) {
        let error = event.error;
        let warnings = event.warnings;
        if (error) {
          self.$emit('error', error);
        } else {
          self.$emit('shown', warnings);
        }
        self.bpmnViewer.get('canvas').zoom('fit-viewport');
      });
      this.fetchDiagram(this.url);
    },
    fetchDiagram: function(url) {
      let self = this;
      if(url){
        if(/^http:\/\/|^https:\/\//g.test(this.url)){
          fetch(url)
              .then(function(response) {
                return response.text();
              })
              .then(function(text) {
                self.diagramXML = text;
              })
              .catch(function(err) {
                self.$emit('error', err);
              });
        }else{
          self.diagramXML = url;
        }
      }
    },
    reload(){
      this.init()
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
