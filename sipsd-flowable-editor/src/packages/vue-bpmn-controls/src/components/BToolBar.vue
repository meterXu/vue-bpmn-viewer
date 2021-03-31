<template>
  <div class="bpmn-toolbar-container">
    <el-button-group>
      <el-tooltip class="item" effect="dark" content="修改模型属性" placement="bottom-start">
        <el-button type="primary" icon="el-icon-edit" @click="handleEdit"></el-button>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" content="复制这个模型" placement="bottom">
        <el-button type="primary" icon="el-icon-document-copy" @click="handleCopy"></el-button>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" content="删除这个模型" placement="bottom-end">
        <el-button type="primary" icon="el-icon-delete" @click="handleDelete"></el-button>
      </el-tooltip>
    </el-button-group>
    <el-button-group>
      <el-tooltip class="item" effect="dark" content="导出到BPMN2" placement="bottom">
        <el-button type="primary" icon="el-icon-download" @click="handleExport"></el-button>
      </el-tooltip>
    </el-button-group>
    <el-button-group>
      <el-button type="primary" icon="el-icon-check" @click="handlePush">发布</el-button>
      <el-button type="primary" icon="el-icon-edit-outline" @click="handleViewEdit">可视化编辑</el-button>
    </el-button-group>
    <el-button-group>
      <el-tooltip class="item" effect="dark" content="关闭" placement="bottom-end">
        <el-button type="primary" icon="el-icon-close" @click="handleClose"></el-button>
      </el-tooltip>
    </el-button-group>
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
