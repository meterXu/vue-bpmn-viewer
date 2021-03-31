<template>
  <div class="bpmn-toolbar-container">
    <a-button-group>
      <a-tooltip class="item" placement="bottomLeft">
        <template slot="title">
          <span>修改模型属性</span>
        </template>
        <a-button type="primary" @click="handleEdit"><a-icon type="edit" /></a-button>
      </a-tooltip>
      <a-tooltip class="item" placement="bottom">
        <template slot="title">
          <span>复制这个模型</span>
        </template>
        <a-button type="primary" @click="handleCopy"><a-icon type="copy" /></a-button>
      </a-tooltip>
      <a-tooltip class="item" placement="bottomRight">
        <template slot="title">
          <span>删除这个模型</span>
        </template>
        <a-button type="primary" @click="handleDelete"><a-icon type="delete" /></a-button>
      </a-tooltip>
    </a-button-group>
    <a-button-group>
      <a-tooltip class="item" placement="bottom">
        <template slot="title">
          <span>导出到BPMN2</span>
        </template>
        <a-button type="primary" @click="handleExport"><a-icon type="download" /></a-button>
      </a-tooltip>
    </a-button-group>
    <a-button-group>
      <a-button type="primary" @click="handlePush"><a-icon type="check" />发布</a-button>
      <a-button type="primary" @click="handleViewEdit"><a-icon type="form" />可视化编辑</a-button>
    </a-button-group>
    <a-button-group>
      <a-tooltip class="item" placement="bottomRight">
        <template slot="title">
          <span>关闭</span>
        </template>
        <a-button type="primary" @click="handleClose"><a-icon type="close" /></a-button>
      </a-tooltip>
    </a-button-group>
  </div>
</template>

<script>
export default {
  name: "BToolBar",
  methods:{
    handleEdit(){
      this.$emit('edit')
    },
    handleCopy(){
      this.$emit('copy')
    },
    handleDelete(){
      this.$emit('delete')
    },
    handleExport(){
      this.$emit('export')
    },
    handlePush(){
      this.$emit('push')
    },
    handleViewEdit(){
      this.$emit('viewEdit')
    },
    handleClose(){
      this.$emit('close')
    }
  }
}
</script>

<style scoped>
.bpmn-toolbar-container{
  position: fixed;
  top: 10px;
  right: 20px;
  z-index: 10;
}
.ant-btn-group{
  margin-left: 10px;
}
</style>
