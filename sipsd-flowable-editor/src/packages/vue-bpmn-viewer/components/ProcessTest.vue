<template>
  <Form v-loading="loading" element-loading-text="加载中" :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
    <FormItem label="发起人" prop="currentUserCode">
      <Input v-model="ruleForm.currentUserCode" placeholder="请输入发起人"></Input>
    </FormItem>
    <FormItem label="流程编码" prop="processDefinitionKey">
      <Input v-model="ruleForm.processDefinitionKey" placeholder="请输入流程编码"></Input>
    </FormItem>
    <FormItem label="流程变量">
      <Row style="margin-bottom: 20px" v-for="(item,index) in variableArr" :key="item.key">
        <Col :span="11">
          <FormItem>
            <Input v-model="item.VariableName" placeholder="变量名称" style="width: 94%;"></Input>
          </FormItem>
        </Col>
        <Col :span="13">
          <FormItem>
            <div class="variable">
              <Input v-model="item.variableNum" placeholder="变量值" style="width: 80%; margin-right: 20px"></Input>
              <DoAddFour v-if="index===variableArr.length-1" @click="addVariable" class="variableBtn" theme="filled" size="26" fill='#9A9A9A' />
              <DoDeleteOne v-else @click="removeVariable(item)" class="variableBtn" theme="filled" size="26" fill='#9A9A9A' />
            </div>
          </FormItem>
        </Col>
      </Row>

    </FormItem>
    <FormItem label="">
      <pre><code class="language-javascript" v-html="desc" style="min-height: 200px;max-height: 400px;"></code></pre>
    </FormItem>
    <FormItem class="testBtn">
      <Button type="primary" @click="submitForm('ruleForm')">测试</Button>
    </FormItem>
  </Form>
</template>

<script>
import 'highlight.js/lib/common';
import 'highlight.js/styles/atom-one-light.css';
import hljs from 'highlight.js/lib/core';
import javascript from 'highlight.js/lib/languages/javascript.js';
import axios from "axios";
import { Input,Button,Form,FormItem,Col,Row } from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css'



export default {
  name: "VueProcessTestTool",
  props:{
    baseApi:{type:String,required:false},
  },
  components: {Input,Button,Form,FormItem,Col,Row},
  data() {
    return {
      loading: false,
      desc: '',
      variableArr: [{VariableName:'', variableNum:''}],
      ruleForm: {
        currentUserCode: '',
        processDefinitionKey: '',
      },
      rules: {
        currentUserCode: [
          { required: true, message: '请输入发起人', trigger: 'blur' },
        ],
        processDefinitionKey: [
          { required: true, message: '请输入流程编码', trigger: 'blur' },
        ],
      }
    };
  },
  mounted() {
    hljs.highlightAll()
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true
          let variable = {}
          let isVariableName = false
          this.variableArr.filter(i=>{
            if (i.VariableName) {
              variable[i.VariableName]=i.variableNum.split(',')
            } else {
              this.$message.error('变量名称不能为空')
              isVariableName = true
              return false;

            }
          })
          if(isVariableName) {
            this.loading = false
            return false;
          }
          let parmas = Object.assign({
            systemSn: 'flow',
            variables: variable
          },this.ruleForm)
          axios.post(this.baseApi,parmas).then(res=>{
            if(res.data.data) {
              this.desc = JSON.stringify(res.data.data,null, 2)
            } else {
              this.$message.error(res.data.message)
            }
            this.loading = false
          }).catch(err=>{
            this.loading = false
            this.$message.error(err)
          })
        } else {
          return false;
        }
      });
    },
    addVariable() {
      this.variableArr.push({VariableName:'', variableNum:'',key: Date.now()})
    },
    removeVariable(item) {
      var index = this.variableArr.indexOf(item)
      if (index !== -1) {
        this.variableArr.splice(index, 1)
      }
    }
  }
}
</script>

<style scoped>
.demo-ruleForm >>> .testBtn{
  text-align: center;
}
.demo-ruleForm .variable {
  display: flex;
  align-items: center;
}
.demo-ruleForm .variable .variableBtn {
  cursor: pointer;
}
</style>
