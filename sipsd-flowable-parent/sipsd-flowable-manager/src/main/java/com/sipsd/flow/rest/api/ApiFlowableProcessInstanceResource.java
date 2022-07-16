package com.sipsd.flow.rest.api;


import com.sipsd.flow.vo.flowable.AssigneeVo;
import com.sipsd.flow.bean.FlowElementVo;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.service.flowable.IFlowableProcessInstanceService;
import com.sipsd.flow.service.flowable.IFlowableUserService;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.EndProcessVo;
import com.sipsd.flow.vo.flowable.ProcessInstanceQueryVo;
import com.sipsd.flow.vo.flowable.ret.ProcessInstanceVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author : chengtg/gaoqiang
 * @title: : ApiTask
 * @projectName : flowable
 * @description: 流程实例API
 * @date : 2019/11/1321:21
 */
@Api(tags={"流程实例"})
@RestController
@RequestMapping("/rest/processInstance")
public class ApiFlowableProcessInstanceResource extends BaseResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiFlowableProcessInstanceResource.class);
    @Autowired
    private IFlowableProcessInstanceService flowableProcessInstanceService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IFlowableUserService flowableUserService;
    @Autowired
    private HistoryService historyService;
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
    @ApiOperation("获取当前以及下一任务节点信息")
    @GetMapping(value = "/nextFlowNode")
    public Result nextFlowNode(@RequestParam String node, @RequestParam String taskId) {
        Result<List<FlowElementVo>> result =new Result<>();
        List<FlowElementVo> flowElementVoList =  flowableProcessInstanceService.nextFlowNode(node,taskId);
        result.setData(flowElementVoList);
        return result;
    }

    /**
     * 根据流程实例ID获取当前流程所有节点信息
     * @param processInstanceId
     * @return
     */
    @ApiOperation("根据流程实例ID获取当前流程所有节点信息")
    @GetMapping(value = "/getAllNodeListByProcessInstanceId")
    public Result<List<FlowElementVo>> getAllNodeListByProcessInstanceId(@RequestParam String processInstanceId) {
        Result<List<FlowElementVo>> result = new Result<>();
        result.setMessage("查询返回节点成功");
        String definitionId = null;
        //查看是否是已结束的流程
        Object o = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if(o==null)
        {
            //流程已经结束
            definitionId = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
        }
        else {
            //流程未结束
            definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult().getProcessDefinitionId();
        }
        //获取所有节点信息
        List<org.flowable.bpmn.model.Process> processes = repositoryService.getBpmnModel(definitionId).getProcesses();
        for(org.flowable.bpmn.model.Process process:processes)
        {
            // 获取全部的FlowElement（流元素）信息
            Collection<FlowElement> flowElements = process.getFlowElements();
            //return flowElements;

            List<String> assigneeList = null;
            List<FlowElementVo> flowNodeVos=null;
            //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
            if(CollectionUtils.isEmpty(flowElements)){
                return null;
            }
            flowNodeVos=new ArrayList<>();
            for(FlowElement flowElement:flowElements){
                if(flowElement instanceof  UserTask){
                    FlowElementVo flowNodeVo=new FlowElementVo();
                    flowNodeVo.setFlowNodeId(flowElement.getId());
                    flowNodeVo.setFlowNodeName(flowElement.getName());
                    List<String> groupIdList = ((UserTask) flowElement).getCandidateGroups();
                    if(CollectionUtils.isNotEmpty(groupIdList))
                    {
                        List<AssigneeVo> groupList = new ArrayList<>();
                        for(String groupId:groupIdList)
                        {
                            AssigneeVo  assigneeVo = new AssigneeVo();
                            assigneeVo.setGroupId(groupId);
                            List<com.sipsd.flow.vo.flowable.User> userList = flowableUserService.getUserListByGroupIds(Arrays.asList(groupId));
                            assigneeVo.setUserList(userList);
                            groupList.add(assigneeVo);
                        }
                        flowNodeVo.setGroupList(groupList);
                    }


                    if (StringUtils.isEmpty(((UserTask) flowElement).getAssignee()))
                    {
                        flowNodeVo.setAssigneeList(((UserTask) flowElement).getCandidateUsers());
                    }
                    else
                    {
                        assigneeList = new ArrayList<>();
                        assigneeList.add(((UserTask) flowElement).getAssignee());
                        flowNodeVo.setAssigneeList(assigneeList);
                    }
                    flowNodeVos.add(flowNodeVo);
                }
            }
            Collections.sort(flowNodeVos, new Comparator<FlowElementVo>() {
                @Override
                public int compare(FlowElementVo o1, FlowElementVo o2) {
                    //升序
                    return o1.getFlowNodeId().compareTo(o2.getFlowNodeId());
                }
            });
            result.setData(flowNodeVos);
            break;
        }
        return result;
    }
}
