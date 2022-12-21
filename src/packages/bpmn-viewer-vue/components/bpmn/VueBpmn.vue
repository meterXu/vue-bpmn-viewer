<template>
  <div ref="graphics" class="vue-bpmn-graphics"></div>
</template>

<script>
import BpmnJS from 'bpmn-js/dist/bpmn-navigated-viewer.development.js';
import BpmnViewer from "bpmn-js/lib/Viewer"
import utils from "../controls/lib/utils";
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
      bpmnViewer:null,
      loadingInstance:null
    };
  },
  methods: {
    init(){
      let self = this;
      let _options = Object.assign({
        container: this.$refs.graphics,
      },this.options)
      if(!this.bpmnViewer) {
        if(this.viewer){
          this.bpmnViewer = new BpmnViewer(_options);
        }else{
          this.bpmnViewer  = new BpmnJS(_options);
        }
        this.bpmnViewer.on('import.done', function(event) {
          let error = event.error;
          let warnings = event.warnings;
          if (error) {
            self.$emit('error', error);
          } else {
            self.$emit('loaded', warnings);
            self.addEventBusListener()
          }
        });
      }
      utils.clearWatermark()
      this.fetchDiagram(this    .url).then(xml=>{
        this.drawXml(xml)
      })
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
    addEventBusListener(){
      let that = this
      let eventBus = null
      const eventTypes = ['element.click','canvas.viewbox.changed']
      if(this.bpmnViewer) {
        eventBus = this.bpmnViewer.get('eventBus')
        eventTypes.forEach(function(eventType) {
          eventBus.on(eventType, function(e) {
            if(eventType==='element.click'){
              let elementRegistry = that.bpmnViewer.get('elementRegistry')
              if (!e || e.element.type == 'bpmn:Process') return
              let shape = elementRegistry.get(e.element.id)
              that.$emit('click',{event:e.originalEvent,shape})
            }else{
              that.$emit('viewChange',{event:e})
            }

          })
        })
      }
    },
    drawXml(xml) {
      if(this.bpmnViewer){
        this.bpmnViewer.importXML(xml)
      }
    },
    reload(){
      if(this.bpmnViewer){
        this.$emit('loaded', null);
      }else{
        this.init()
      }
    },
    destroy(){
      if(this.bpmnViewer){
        this.bpmnViewer.destroy();
        this.bpmnViewer = null
      }
    }
  },
  mounted: function () {
    this.init()
  },
  beforeDestroy() {
   this.destroy()
  }
};
</script>

<style scoped>
.vue-bpmn-graphics {
  height: 100%;
  width: 100%;
}
</style>
