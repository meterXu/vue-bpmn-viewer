package com.sipsd.flow.rest.api;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.bean.FlowElementVo;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.service.flowable.IFlowableProcessInstanceService;
import com.sipsd.flow.vo.flowable.EndProcessVo;
import com.sipsd.flow.vo.flowable.ProcessInstanceQueryVo;
import com.sipsd.flow.vo.flowable.ret.ProcessInstanceVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : chengtg
 * @title: : ApiTask
 * @projectName : flowable
 * @description: 流程实例API
 * @date : 2019/11/1321:21
 */
@RestController
@RequestMapping("/rest/processInstance")
public class ApiFlowableProcessInstanceResource extends BaseResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiFlowableProcessInstanceResource.class);
    @Autowired
    private IFlowableProcessInstanceService flowableProcessInstanceService;
    /**
     * 分页查询流程定义列表
     *
     * @param params 参数
     * @param query  分页
     * @return
     */
    @PostMapping(value = "/page-model")
    public PageModel<ProcessInstanceVo> pageModel(ProcessInstanceQueryVo params, Query query) {
        PageModel<ProcessInstanceVo> pm = flowableProcessInstanceService.getPagerModel(params, query);
        return pm;
    }

    /**
     * 删除流程实例haha
     *
     * @param processInstanceId 参数
     * @return
     */
    @GetMapping(value = "/deleteProcessInstanceById/{processInstanceId}")
    public Result<String> deleteProcessInstanceById(@PathVariable String processInstanceId) {
        Result<String> data = flowableProcessInstanceService.deleteProcessInstanceById(processInstanceId);
        return data;
    }

    /**
     * 激活或者挂起流程定义
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/saProcessInstanceById")
    public Result<String> saProcessInstanceById(String id, int suspensionState) {
        Result<String> Result = flowableProcessInstanceService.suspendOrActivateProcessInstanceById(id, suspensionState);
        return Result;
    }

    /**
     * 终止
     *
     * @param params 参数
     * @return
     */
    @PostMapping(value = "/stopProcess")
    public Result<String> stopProcess(EndProcessVo params) {
        boolean flag = this.isSuspended(params.getProcessInstanceId());
        Result<String> result = null;
        if (flag){
            params.setMessage("后台执行终止");
            params.setUserCode(this.getLoginUser().getId());
            result = flowableProcessInstanceService.stopProcessInstanceById(params);
        }else{
        	result = Result.failed("流程已挂起，请联系管理员激活!");
        }
        return result;
    }
    
    /**
     * 获取流程动态表单信息
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/formData")
    public Result formData(@RequestParam String processInstanceId) {
    	return flowableProcessInstanceService.formData(processInstanceId);
    }

    /**
     * 获取当前以及下一任务节点
     * @param node
     * @param taskId
     * @return
     */
    @GetMapping(value = "/nextFlowNode")
    public Result nextFlowNode(@RequestParam String node, @RequestParam String taskId) {
        Result<List<FlowElementVo>> result =new Result<>();
        List<FlowElementVo> flowElementVoList =  flowableProcessInstanceService.nextFlowNode(node,taskId);
        result.setData(flowElementVoList);
        return result;
    }


}
