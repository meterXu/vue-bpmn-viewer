package com.sipsd.flow.service.flowable.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.bean.AssigneeVo;
import com.sipsd.flow.bean.FlowElementVo;
import com.sipsd.flow.cmd.processinstance.DeleteFlowableProcessInstanceCmd;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.constant.FlowConstant;
import com.sipsd.flow.dao.flowable.IFlowableProcessInstanceDao;
import com.sipsd.flow.enm.flowable.CommentTypeEnum;
import com.sipsd.flow.service.flowable.*;
import com.sipsd.flow.vo.flowable.EndProcessVo;
import com.sipsd.flow.vo.flowable.ProcessInstanceQueryVo;
import com.sipsd.flow.vo.flowable.RevokeProcessVo;
import com.sipsd.flow.vo.flowable.StartProcessInstanceVo;
import com.sipsd.flow.vo.flowable.ret.ProcessInstanceVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 * @author : chengtg
 * @title: : FlowableProcessInstanceServiceImpl
 * @projectName : flowable
 * @description: 流程实例service
 * @date : 2019/11/1314:56
 */
@Slf4j
@Service
public class FlowableProcessInstanceServiceImpl extends BaseProcessService implements IFlowableProcessInstanceService
{

    @Autowired
    private IFlowableBpmnModelService flowableBpmnModelService;
    @Autowired
    private IFlowableProcessInstanceDao flowableProcessInstanceDao;
    @Autowired
    private FlowProcessDiagramGenerator flowProcessDiagramGenerator;
    @Autowired
    private IFlowableTaskService flowableTaskService;
    @Autowired
    private IFlowableExtensionTaskService flowableExtensionTaskService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private IFlowableUserService flowableUserService;

    @Override
    public PageModel<ProcessInstanceVo> getPagerModel(ProcessInstanceQueryVo params, Query query)
    {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Page<ProcessInstanceVo> page = flowableProcessInstanceDao.getPagerModel(params);
        page.forEach(processInstanceVo ->
        {
            this.setStateApprover(processInstanceVo);
        });
        return new PageModel<>(page);
    }

    /**
     * 设置状态和审批人
     *
     * @param processInstanceVo 参数
     */
    private void setStateApprover(ProcessInstanceVo processInstanceVo)
    {
        if (processInstanceVo.getEndTime() == null)
        {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceVo.getProcessInstanceId()).singleResult();
            if (processInstance.isSuspended())
            {
                processInstanceVo.setSuspensionState(FlowConstant.SUSPENSION_STATE);
            }
            else
            {
                processInstanceVo.setSuspensionState(FlowConstant.ACTIVATE_STATE);
            }
        }
        List<User> approvers = flowableTaskService.getApprovers(processInstanceVo.getProcessInstanceId());
        String userNames = this.createApprovers(approvers);
        processInstanceVo.setApprover(userNames);
    }

    /**
     * 组合审批人显示名称
     *
     * @param approvers 审批人列表
     * @return
     */
    private String createApprovers(List<User> approvers)
    {
        if (CollectionUtils.isNotEmpty(approvers))
        {
            StringBuffer approverstr = new StringBuffer();
            StringBuffer finalApproverstr = approverstr;
            approvers.forEach(user ->
            {
                finalApproverstr.append(user.getDisplayName()).append(";");
            });
            if (approverstr.length() > 0)
            {
                approverstr = approverstr.deleteCharAt(approverstr.length() - 1);
            }
            return approverstr.toString();
        }
        return null;
    }

    @Override
    public Result<ProcessInstance> startProcessInstanceByKey(StartProcessInstanceVo params)
    {
        Result<ProcessInstance> result = Result.sucess("启动成功");
        if (StringUtils.isNotBlank(params.getProcessDefinitionKey()) && StringUtils.isNotBlank(params.getBusinessKey())
                && StringUtils.isNotBlank(params.getSystemSn()))
        {
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(params.getProcessDefinitionKey()).latestVersion().singleResult();
            if (processDefinition != null && processDefinition.isSuspended())
            {
                result = Result.failed("此流程已经挂起,请联系系统管理员!");
                return result;
            }
            // 1.1、设置提交人字段为空字符串让其自动跳过
            params.getVariables().put(FlowConstant.SKIP, true);
            // 1.2、设置可以自动跳过
            params.getVariables().put(FlowConstant.FLOWABLE_SKIP_EXPRESSION_ENABLED, true);
            // 2、当我们流程创建人和发起人
            String creator = params.getCurrentUserCode();
            // 3.启动流程
            identityService.setAuthenticatedUserId(creator);
            ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
                    .processDefinitionKey(params.getProcessDefinitionKey().trim()).name(params.getFormName().trim())
                    .businessKey(params.getBusinessKey().trim()).variables(params.getVariables())
                    .tenantId(params.getSystemSn().trim()).start();
            result.setData(processInstance);
            // 4.添加审批记录
            this.addComment(creator, processInstance.getProcessInstanceId(), CommentTypeEnum.TJ.toString(),
                    params.getFormName() + "提交");
            //保存流程的自定义属性-最大审批天数
            flowableExtensionTaskService.saveExtensionTask(processInstance.getProcessInstanceId(), processInstance.getActivityId(), params.getBusinessInfo(),false);
            // 5.TODO 推送消息数据

        }
        else
        {
            result = Result.failed("请填写 这三个字段 ProcessDefinitionKey,BusinessKey,SystemSn");
        }
        return result;
    }

    @Override
    public PageModel<ProcessInstanceVo> getMyProcessInstances(ProcessInstanceQueryVo params, Query query)
    {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Page<ProcessInstanceVo> myProcesses = flowableProcessInstanceDao.getPagerModel(params);
        myProcesses.forEach(processInstanceVo ->
        {
            this.setStateApprover(processInstanceVo);
        });
        return new PageModel<>(myProcesses);
    }

    @Override
    public byte[] createImage(String processInstanceId)
    {
        // 1.获取当前的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = null;
        List<String> activeActivityIds = new ArrayList<>();
        List<String> highLightedFlows = new ArrayList<>();
        // 2.获取所有的历史轨迹线对象
        List<HistoricActivityInstance> historicSquenceFlows = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).activityType(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW).list();
        historicSquenceFlows
                .forEach(historicActivityInstance -> highLightedFlows.add(historicActivityInstance.getActivityId()));
        // 3. 获取流程定义id和高亮的节点id
        if (processInstance != null)
        {
            // 3.1. 正在运行的流程实例
            processDefinitionId = processInstance.getProcessDefinitionId();
            activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
        }
        else
        {
            // 3.2. 已经结束的流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            // 3.3. 获取结束节点列表
            List<HistoricActivityInstance> historicEnds = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId).activityType(BpmnXMLConstants.ELEMENT_EVENT_END).list();
            List<String> finalActiveActivityIds = activeActivityIds;
            historicEnds.forEach(
                    historicActivityInstance -> finalActiveActivityIds.add(historicActivityInstance.getActivityId()));
        }
        // 4. 获取bpmnModel对象
        BpmnModel bpmnModel = flowableBpmnModelService.getBpmnModelByProcessDefId(processDefinitionId);
        // 5. 生成图片流
        InputStream inputStream = flowProcessDiagramGenerator.generateDiagram(bpmnModel, activeActivityIds,
                highLightedFlows);
        // 6. 转化成byte便于网络传输
        byte[] datas = IoUtil.readInputStream(inputStream, "image inputStream name");
        return datas;
    }

    @Override
    public byte[] createXml(String processInstanceId)
    {
        // 1.获取当前的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = null;
        List<String> activeActivityIds = new ArrayList<>();
        List<String> highLightedFlows = new ArrayList<>();
        // 2.获取所有的历史轨迹线对象
        List<HistoricActivityInstance> historicSquenceFlows = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).activityType(BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW).list();
        historicSquenceFlows
                .forEach(historicActivityInstance -> highLightedFlows.add(historicActivityInstance.getActivityId()));
        // 3. 获取流程定义id和高亮的节点id
        if (processInstance != null)
        {
            // 3.1. 正在运行的流程实例
            processDefinitionId = processInstance.getProcessDefinitionId();
            activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
        }
        else
        {
            // 3.2. 已经结束的流程实例
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            // 3.3. 获取结束节点列表
            List<HistoricActivityInstance> historicEnds = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId).activityType(BpmnXMLConstants.ELEMENT_EVENT_END).list();
            List<String> finalActiveActivityIds = activeActivityIds;
            historicEnds.forEach(
                    historicActivityInstance -> finalActiveActivityIds.add(historicActivityInstance.getActivityId()));
        }
        // 4. 获取bpmnModel对象
        BpmnModel bpmnModel = flowableBpmnModelService.getBpmnModelByProcessDefId(processDefinitionId);
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);// 转xml
        return bpmnBytes;
    }

    @Override
    public Result<String> deleteProcessInstanceById(String processInstanceId)
    {
        Result<String> Result = null;
        long count = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).count();
        if (count > 0)
        {
            DeleteFlowableProcessInstanceCmd cmd = new DeleteFlowableProcessInstanceCmd(processInstanceId, "删除流程实例",
                    true);
            managementService.executeCommand(cmd);
            Result = Result.sucess("删除成功");
        }
        else
        {
            historyService.deleteHistoricProcessInstance(processInstanceId);
            Result = Result.sucess("删除成功");
        }
        return Result;
    }

    @Override
    public Result<String> suspendOrActivateProcessInstanceById(String processInstanceId, Integer suspensionState)
    {
        Result<String> Result = null;
        if (suspensionState == 1)
        {
            runtimeService.suspendProcessInstanceById(processInstanceId);
            Result = Result.sucess("挂起成功");
        }
        else
        {
            runtimeService.activateProcessInstanceById(processInstanceId);
            Result = Result.sucess("激活成功");
        }
        return Result;
    }

    @Override
    public Result<String> stopProcessInstanceById(EndProcessVo endVo)
    {
        Result<String> Result = null;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(endVo.getProcessInstanceId()).singleResult();
        if (processInstance != null)
        {
            // 1、添加审批记录
            this.addComment(endVo.getUserCode(), endVo.getProcessInstanceId(), CommentTypeEnum.LCZZ.toString(),
                    endVo.getMessage());
            List<EndEvent> endNodes = flowableBpmnModelService
                    .findEndFlowElement(processInstance.getProcessDefinitionId());
            String endId = endNodes.get(0).getId();
            String processInstanceId = endVo.getProcessInstanceId();
            // 2、执行终止
            List<Execution> executions = runtimeService.createExecutionQuery().parentId(processInstanceId).list();
            List<String> executionIds = new ArrayList<>();
            executions.forEach(execution -> executionIds.add(execution.getId()));
            this.moveExecutionsToSingleActivityId(executionIds, endId);
            Result = Result.sucess("终止成功");
        }
        else
        {
            Result = Result.failed("不存在运行的流程实例,请确认!");
        }
        return Result;
    }

    @Override
    public Result<String> revokeProcess(RevokeProcessVo revokeVo)
    {
        Result<String> result = Result.failed("撤回失败!");
        if (StringUtils.isNotBlank(revokeVo.getProcessInstanceId()))
        {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(revokeVo.getProcessInstanceId()).singleResult();
            if (processInstance != null)
            {
                // 1.添加撤回意见
                this.addComment(revokeVo.getUserCode(), revokeVo.getProcessInstanceId(), CommentTypeEnum.CH.toString(),
                        revokeVo.getMessage());
                // 2.设置提交人
                runtimeService.setVariable(revokeVo.getProcessInstanceId(), FlowConstant.FLOW_SUBMITTER_VAR,
                        processInstance.getStartUserId());
                // 3.执行撤回
                Activity disActivity = flowableBpmnModelService
                        .findActivityByName(processInstance.getProcessDefinitionId(), FlowConstant.FLOW_SUBMITTER);
                // 4.删除运行和历史的节点信息
                this.deleteActivity(disActivity.getId(), revokeVo.getProcessInstanceId());
                // 5.执行跳转
                List<Execution> executions = runtimeService.createExecutionQuery()
                        .parentId(revokeVo.getProcessInstanceId()).list();
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
                this.moveExecutionsToSingleActivityId(executionIds, disActivity.getId());
                result = Result.sucess("撤回成功!");
            }
        }
        else
        {
            result = Result.failed("流程实例id不能为空!");
        }
        return result;
    }

    @Override
    public Result<Map<String, Object>> formData(String processInstanceId)
    {
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        Object renderedStartForm = formService.getRenderedStartForm(processInstance.getProcessDefinitionId());

        Map<String, Object> variables = null;
        if (processInstance.getEndTime() == null)
        {
            variables = runtimeService.getVariables(processInstanceId);
        }
        else
        {
            List<HistoricVariableInstance> hisVals = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstanceId).list();
            variables = new HashMap<>(16);
            for (HistoricVariableInstance variableInstance : hisVals)
            {
                variables.put(variableInstance.getVariableName(), variableInstance.getValue());
            }
        }
        Map<String, Object> ret = new HashMap<String, Object>(4);
//        boolean showBusinessKey = isShowBusinessKey(processInstance.getProcessDefinitionId());
//        ret.put("showBusinessKey", showBusinessKey);
        ret.put(FlowConstant.BUSINESS_KEY, processInstance.getBusinessKey());
        ret.put("renderedStartForm", renderedStartForm);
        ret.put("variables", variables);
        return Result.ok(ret);
    }

    @Override
    public Result<Map<String, Object>> taskFormData(String taskId)
    {
        Object renderedStartForm = formService.getRenderedTaskForm(taskId);

        //获得局部变量
        Map<String, Object> localMap = taskService.getVariables(taskId);

        Map<String, Object> ret = new HashMap<String, Object>(4);
        ret.put("renderedStartForm", renderedStartForm);
        ret.put("variables", localMap);
        return Result.ok(ret);
    }


    /**
     * 获取任务节点
     *
     * @param node   查询节点选择
     * @param taskId 任务id
     */
    @Override
    public List<FlowElementVo> nextFlowNode(String node, String taskId)
    {
        Task task = processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntity ee = (ExecutionEntity) processEngine.getRuntimeService().createExecutionQuery()
                .executionId(task.getExecutionId()).singleResult();
        // 当前审批节点
        String crruentActivityId = ee.getActivityId();
        FlowElement flowElement = null;

        List<FlowElementVo> flowNodeVos = null;

        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId());
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(crruentActivityId);
        // 输出连线
        List<SequenceFlow> outFlows = flowNode.getOutgoingFlows();
        if (org.springframework.util.CollectionUtils.isEmpty(outFlows))
        {
            return flowNodeVos;
        }
        flowNodeVos = new ArrayList<>();
        List<String> assigneeList = new ArrayList<>();
        for (SequenceFlow sequenceFlow : outFlows)
        {
            FlowElementVo flowElementVo = new FlowElementVo();
            //当前审批节点
            if ("now".equals(node))
            {
                flowElement = sequenceFlow.getSourceFlowElement();
                log.info("当前节点: id=" + flowElement.getId() + ",name=" + flowElement.getName());
            }
            else if ("next".equals(node))
            {
                // 下一个审批节点
                flowElement = sequenceFlow.getTargetFlowElement();
                log.info("下一节点: id=" + flowElement.getId() + ",name=" + flowElement.getName());
            }

            List<FlowElement> flowElementList =  getTargetFlowElement(flowElement);
            for(FlowElement element:flowElementList)
            {
                flowElementVo = new FlowElementVo();
                assigneeList = new ArrayList<>();
                flowElementVo.setFlowNodeName(element.getName());
                flowElementVo.setFlowNodeId(element.getId());
                if (StringUtils.isEmpty(((UserTask) element).getAssignee()))
                {
                    flowElementVo.setAssigneeList(((UserTask) element).getCandidateUsers());
                }
                else
                {
                    assigneeList.add(((UserTask) element).getAssignee());
                    flowElementVo.setAssigneeList(assigneeList);
                }
                List<String> groupIdList = ((UserTask) element).getCandidateGroups();
                if(CollectionUtils.isNotEmpty(groupIdList))
                {
                    List<AssigneeVo> groupList = new ArrayList<>();
                    for(String groupId:groupIdList)
                    {
                        AssigneeVo  assigneeVo = new AssigneeVo();
                        assigneeVo.setGroupId(groupId);
                        List<com.sipsd.flow.bean.User> userList = flowableUserService.getUserListByGroupIds(Arrays.asList(groupId));
                        assigneeVo.setUserList(userList);
                        groupList.add(assigneeVo);
                    }
                    flowElementVo.setGroupList(groupList);
                }

                flowNodeVos.add(flowElementVo);
            }
            // 如果下个审批节点为结束节点
            if (flowElement instanceof EndEvent)
            {
                log.info("下一节点为结束节点：id=" + flowElement.getId() + ",name=" + flowElement.getName());
                flowElementVo.setFlowNodeName(flowElement.getName());
                flowElementVo.setFlowNodeId(flowElement.getId());
            }


        }
        return flowNodeVos;
    }


    public List<FlowElement> getTargetFlowElement(FlowElement flowElement)
    {
        List<FlowElement> flowElementList= new ArrayList<>();
        if (flowElement instanceof UserTask)
        {
            flowElementList.add(flowElement);
        }
        else if (flowElement instanceof ParallelGateway)
        {
            for (SequenceFlow flow : ((ParallelGateway) flowElement).getOutgoingFlows())
            {
                flowElement = flow.getTargetFlowElement();
                if (flowElement instanceof UserTask)
                {
                    flowElementList.add(flowElement);
                }
                else
                {
                    getTargetFlowElement(flowElement);
                }
            }
        }
        return flowElementList;
    }

}
