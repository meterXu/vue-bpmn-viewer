<template>
  <div class="bpmn-toolbar-container">
    <a-button-group>
      <Tooltip class="item" placement="bottom-start">
        <template slot="title">
          <span>修改模型属性</span>
        </template>
        <Button type="primary" @click="handleEdit"><Icon class="el-icon-edit" /></Button>
      </Tooltip>
      <Tooltip class="item" placement="bottom">
        <template slot="title">
          <span>复制这个模型</span>
        </template>
        <Button type="primary" @click="handleCopy"><Icon class="el-icon-copy-document" /></Button>
      </Tooltip>
      <Tooltip class="item" placement="bottom-end">
        <template slot="title">
          <span>删除这个模型</span>
        </template>
        <Button type="primary" @click="handleDelete"><Icon class="el-icon-delete" /></Button>
      </Tooltip>
    </a-button-group>
    <a-button-group>
      <Tooltip class="item" placement="bottom">
        <template slot="title">
          <span>导出到BPMN2</span>
        </template>
        <Button type="primary" @click="handleExport"><Icon class="el-icon-download" /></Button>
      </Tooltip>
    </a-button-group>
    <a-button-group>
      <Button type="primary" @click="handlePush"><Icon class="el-icon-check" />发布</Button>
      <Button type="primary" @click="handleViewEdit"><Icon class="el-icon-edit-outline" />可视化编辑</Button>
    </a-button-group>
    <a-button-group>
      <Tooltip class="item" placement="bottom-end">
        <template slot="title">
          <span>关闭</span>
        </template>
        <Button type="primary" @click="handleClose"><Icon class="el-icon-close" /></Button>
      </Tooltip>
    </a-button-group>
  </div>
</template>

<script>
import {Tooltip,Button,Icon} from 'element-ui'
export default {
  name: "BToolBar",
  components:{
    Tooltip,
    Button,
    Icon,
    [Button.Group.name]:Button.Group
  },
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
