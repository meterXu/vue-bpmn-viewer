<template>
  <div class="bpmn-toolbar-container">
    <a-button-group>
      <a-tooltip class="item" effect="dark" content="修改模型属性" placement="bottomLeft">
        <a-button type="primary" icon="el-icon-edit" @click="handleEdit"></a-button>
      </a-tooltip>
      <a-tooltip class="item" effect="dark" content="复制这个模型" placement="bottom">
        <a-button type="primary" icon="el-icon-document-copy" @click="handleCopy"></a-button>
      </a-tooltip>
      <a-tooltip class="item" effect="dark" content="删除这个模型" placement="bottomRight">
        <a-button type="primary" icon="el-icon-delete" @click="handleDelete"></a-button>
      </a-tooltip>
    </a-button-group>
    <a-button-group>
      <a-tooltip class="item" effect="dark" content="导出到BPMN2" placement="bottom">
        <a-button type="primary" icon="el-icon-download" @click="handleExport"></a-button>
      </a-tooltip>
    </a-button-group>
    <a-button-group>
      <a-button type="primary" icon="el-icon-check" @click="handlePush">发布</a-button>
      <a-button type="primary" icon="el-icon-edit-outline" @click="handleViewEdit">可视化编辑</a-button>
    </a-button-group>
    <a-button-group>
      <a-tooltip class="item" effect="dark" content="关闭" placement="bottomRight">
        <a-button type="primary" icon="el-icon-close" @click="handleClose"></a-button>
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
.el-button-group{
  margin-left: 10px;
}
</style>
