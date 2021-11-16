<template>
  <div ref="container" class="vue-bpmn-diagram-container"></div>
</template>

<script>
import Vue from 'vue'
import BpmnJS from 'bpmn-js/dist/bpmn-navigated-viewer.production.min.js';

export default {
  name: 'VueBpmn',
  props: {
    url: {
      type: String,
      required: true
    },
    options: {
      type: Object
    }
  },
  data: function() {
    return {
      diagramXML: null
    };
  },
  mounted: function () {
    var container = this.$refs.container;

    var self = this;
    var _options = Object.assign({
      container: container
    }, this.options)
    this.bpmnViewer = new BpmnJS(_options);
    Vue.prototype.$bpmnViewer=this.bpmnViewer
    this.bpmnViewer.on('import.done', function(event) {

      var error = event.error;
      var warnings = event.warnings;

      if (error) {
        self.$emit('error', error);
      } else {
        self.$emit('shown', warnings);
      }

      self.bpmnViewer.get('canvas').zoom('fit-viewport');
    });

    if (this.url) {
      if(/^http:\/\/|^https:\/\//g.test(this.url)){
        this.fetchDiagram(this.url);
      }else{
        this.diagramXML=this.url
      }
    }
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
    fetchDiagram: function(url) {
      var self = this;
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
    },
    reload(){
      this.fetchDiagram(this.url)
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
