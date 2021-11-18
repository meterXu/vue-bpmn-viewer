package com.sipsd.flow.flowable.listener.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sipsd.flow.bean.NoticeTask;
import com.sipsd.flow.dao.flowable.IFlowableExtensionTaskDao;
import com.sipsd.flow.service.flowable.IFlowableNoticeTaskService;
import com.sipsd.flow.util.SpringUtil;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import lombok.extern.slf4j.Slf4j;

import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.BpmnOverrideContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: suxy
 * @Description: 抄送-事件监听器
 * @DateTime:: 2021/11/11 13:24
 */
@Scope
@Component
@Slf4j
public class NoticeExecutionListener implements ExecutionListener {

    /**
     * 抄送用户
     */
    private Expression noticeUsers;

    @Override
    public void notify(DelegateExecution delegateExecution) {

        String processInstanceId = delegateExecution.getProcessInstanceId();
        String executionId = delegateExecution.getId();
        Object value = noticeUsers.getValue(delegateExecution);
        List<String> userList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(value) && value instanceof List) {
            userList = (List<String>) value;
        } else {
            userList.add(StrUtil.toString(value));
        }
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        Task task = taskService.createTaskQuery().executionId(executionId).active().singleResult();
        if (task != null) {
            List<Task> list = taskService.createTaskQuery().taskDefinitionKey(delegateExecution.getCurrentActivityId()).processInstanceId(processInstanceId).active().list();

            //当执行最后一个任务时在进行抄送
            if (list.size() == 1) {
                log.info("开始为: {},抄送任务ID:{}", userList.toString(), task.getId());
                this.addNoticeTask(processInstanceId, executionId, userList);
            } else if (list.size() > 1) {
               //会签任务满足完成条件后,添加抄送任务
                int nrOfInstances = this.getLoopVariable(delegateExecution, "nrOfInstances");
                int nrOfCompletedInstances = this.getLoopVariable(delegateExecution, "nrOfCompletedInstances");
                if(delegateExecution.getParent() != null){
                    boolean isCompletionConditionSatisfied = this.completionConditionSatisfied(delegateExecution.getParent());
                    if (nrOfCompletedInstances >= nrOfInstances || isCompletionConditionSatisfied) {
                        log.info("会签多实例任务： 开始为: {},抄送任务ID:{}", userList.toString(), task.getId());
                        this.addNoticeTask(processInstanceId, executionId, userList);
                    }
                }

            }
        }
    }

    /**
     * 新增抄送任务
     *
     * @param processInstanceId
     * @param executionId
     * @param userList
     */
    private void addNoticeTask(String processInstanceId, String executionId, List<String> userList) {
        TaskService taskService = SpringUtil.getBean(TaskService.class);
        Task task = taskService.createTaskQuery().executionId(executionId).active().singleResult();
        IFlowableNoticeTaskService noticeTaskService = SpringUtil.getBean(IFlowableNoticeTaskService.class);
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        IFlowableExtensionTaskDao flowableExtensionTaskDao = SpringUtil.getBean(IFlowableExtensionTaskDao.class);
        TaskExtensionVo taskExtensionVo = flowableExtensionTaskDao.getExtensionTaskByProcessInstanceIdAndTaskId(processInstanceId, task.getId());
        for (String assignee : userList) {
            NoticeTask noticeTask = new NoticeTask();
            noticeTask.setAssignee(assignee);
            noticeTask.setProcInstId(processInstanceId);
            noticeTask.setBusinessInfo(taskExtensionVo.getBusinessInfo());
            noticeTask.setCategory(task.getCategory());
            noticeTask.setBusinessKey(processInstance.getBusinessKey());
            noticeTask.setCreateTime(new Date());
            noticeTask.setDescription(task.getDescription());
            noticeTask.setFormKey(task.getFormKey());
            noticeTask.setName(task.getName());
            noticeTask.setOwner(task.getOwner());
            noticeTask.setParentTaskId(task.getId());
            noticeTask.setProcDefId(task.getProcessDefinitionId());
            noticeTask.setPriority(task.getPriority());
            noticeTask.setTaskDefId(task.getTaskDefinitionId());
            noticeTask.setTaskDefKey(task.getTaskDefinitionKey());
            noticeTask.setTenantId(processInstance.getTenantId());
            noticeTask.setFormName(processInstance.getName());
            noticeTaskService.insertNoticeTask(noticeTask);
        }

    }


    public boolean completionConditionSatisfied(DelegateExecution execution) {
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(execution.getProcessDefinitionId());
        Activity miActivityElement = (Activity) bpmnModel.getFlowElement(execution.getCurrentActivityId());
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = miActivityElement.getLoopCharacteristics();
        String completionCondition = multiInstanceLoopCharacteristics.getCompletionCondition();
        if (completionCondition != null) {
            ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
            ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();
            String activeCompletionCondition = null;
            if (CommandContextUtil.getProcessEngineConfiguration().isEnableProcessDefinitionInfoCache()) {
                ObjectNode taskElementProperties = BpmnOverrideContext.getBpmnOverrideElementProperties(miActivityElement.getId(), execution.getProcessDefinitionId());
                activeCompletionCondition = this.getActiveValue(completionCondition, "multiInstanceCompletionCondition", taskElementProperties);
            } else {
                activeCompletionCondition = completionCondition;
            }

            Object value = expressionManager.createExpression(activeCompletionCondition).getValue(execution);
            if (!(value instanceof Boolean)) {
                throw new FlowableIllegalArgumentException("completionCondition '" + activeCompletionCondition + "' does not evaluate to a boolean value");
            } else {
                Boolean booleanValue = (Boolean) value;
                log.debug("Completion condition of multi-instance satisfied: {}", booleanValue);
                return booleanValue;
            }
        } else {
            return false;
        }

    }

    protected String getActiveValue(String originalValue, String propertyName, ObjectNode taskElementProperties) {
        String activeValue = originalValue;
        if (taskElementProperties != null) {
            JsonNode overrideValueNode = taskElementProperties.get(propertyName);
            if (overrideValueNode != null) {
                if (overrideValueNode.isNull()) {
                    activeValue = null;
                } else {
                    activeValue = overrideValueNode.asText();
                }
            }
        }

        return activeValue;
    }

    public Integer getLoopVariable(DelegateExecution execution, String variableName) {
        Object value = execution.getVariableLocal(variableName);

        for(DelegateExecution parent = execution.getParent(); value == null && parent != null; parent = parent.getParent()) {
            value = parent.getVariableLocal(variableName);
        }

        return (Integer)((Integer)(value != null ? value : 0));
    }

}
