package com.sipsd.flow.service.flowable.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.DateUtil;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.constant.FlowConstant;
import com.sipsd.flow.dao.flowable.IFlowableExtensionTaskDao;
import com.sipsd.flow.service.flowable.IFlowableExtensionTaskService;
import com.sipsd.flow.vo.flowable.ExtensionTaskQueryVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author : 高强
 * @title: : FlowableExtensionTaskServiceImpl
 * @projectName : flowable
 * @description: task service
 * @date : 2021/03/24 15:05
 */
@Service
public class FlowableExtensionTaskServiceImpl extends BaseProcessService implements IFlowableExtensionTaskService
{

	@Autowired
	private IFlowableExtensionTaskDao flowableExtensionTaskDao;

	@Override
	public void saveExtensionTask(String processDefinitionId,String fromKey)
	{
		String elementText = null;
		List<Task> waitedTaskList = taskService.createTaskQuery().processInstanceId(processDefinitionId).active().list();
		if (!CollectionUtils.isEmpty(waitedTaskList))
		{
			for(Task task:waitedTaskList)
			{

				String assignee = "";
				String groupId = "";
				List<ExtensionElement> elementList = getCustomProperty(task.getTaskDefinitionKey(), task.getProcessDefinitionId(), FlowConstant.PROPERTY_USERTASK_TASKMAXDAY);
				if (!CollectionUtils.isEmpty(elementList))
				{
					for (ExtensionElement extensionElement : elementList)
					{
						if (extensionElement.getName().equals(FlowConstant.PROPERTY_USERTASK_TASKMAXDAY))
						{
							//插入自定义属性表
							elementText = extensionElement.getElementText();
							//TODO xml中有两个相同的节点需要处理下不过不影响审批
							break;
						}
					}
				}
				List<IdentityLink> identityLinks=taskService.getIdentityLinksForTask(task.getId());
				//只可能是一条
				if(!CollectionUtils.isEmpty(identityLinks)){
					assignee = identityLinks.get(0).getUserId();
					groupId = identityLinks.get(0).getGroupId();
				}

				//如果是并联请求需要判断是否需要插入-通过流程实例id和taskId来判断
				//TODO 如果是null 应该删掉这条并联中的记录，不过不影响查询
				TaskExtensionVo vo = flowableExtensionTaskDao.getExtensionTaskByProcessInstanceIdAndTaskId(task.getProcessInstanceId(),task.getId());
				if(vo == null)
				{
					TaskExtensionVo taskExtensionVo = new TaskExtensionVo();
					taskExtensionVo.setTaskId(task.getId());
					taskExtensionVo.setAssignee(assignee);
					taskExtensionVo.setGroupId(groupId);
					taskExtensionVo.setExecutionId(task.getExecutionId());
					taskExtensionVo.setProcessDefinitionId(task.getProcessDefinitionId());
					taskExtensionVo.setProcessInstanceId(task.getProcessInstanceId());
					taskExtensionVo.setFromKey(fromKey);
					if(StringUtils.isNotEmpty(elementText))
					{
						//算出结束日期
						taskExtensionVo.setTaskMaxDay(elementText);
						taskExtensionVo.setCustomTaskMaxDay(elementText);
						taskExtensionVo.setEndTime(DateUtil.addDate(new Date(),Integer.parseInt(elementText)));
						//算出剩余处理时间
						Long restTime = DateUtil.diffDateTime(taskExtensionVo.getEndTime(),new Date());
						taskExtensionVo.setRestTime(restTime);
					}
					taskExtensionVo.setTaskDefinitionKey(task.getTaskDefinitionKey());
					taskExtensionVo.setTenantId(task.getTenantId());
					taskExtensionVo.setTaskName(task.getName());
					flowableExtensionTaskDao.insertExtensionTask(taskExtensionVo);
				}
			}
		}
	}


	@Override
	public void saveBackExtensionTask(String processDefinitionId)
	{
		List<org.flowable.task.api.Task> waitedTaskList = taskService.createTaskQuery().processInstanceId(processDefinitionId).active().list();
		if (!CollectionUtils.isEmpty(waitedTaskList))
		{
			Task task = waitedTaskList.get(0);
			//查询驳回之后的代办节点的前审批节点
			TaskExtensionVo taskExtensionVo =  flowableExtensionTaskDao.getExtensionTaskByTaskDefinitionKey(processDefinitionId,task.getTaskDefinitionKey());
			TaskExtensionVo vo = new TaskExtensionVo();
			vo.setTaskId(task.getId());
			vo.setAssignee(taskExtensionVo.getAssignee());
			vo.setGroupId(taskExtensionVo.getGroupId());
			vo.setExecutionId(task.getExecutionId());
			vo.setProcessDefinitionId(task.getProcessDefinitionId());
			vo.setProcessInstanceId(task.getProcessInstanceId());
			vo.setFromKey(taskExtensionVo.getFromKey());
			vo.setTaskMaxDay(taskExtensionVo.getTaskMaxDay());
			vo.setCustomTaskMaxDay(taskExtensionVo.getCustomTaskMaxDay());
			String taskMaxDay = taskExtensionVo.getCustomTaskMaxDay()==null?"":taskExtensionVo.getTaskMaxDay();
			if(StringUtils.isNotEmpty(taskMaxDay))
			{
				vo.setEndTime(DateUtil.addDate(new Date(),Integer.parseInt(taskExtensionVo.getCustomTaskMaxDay())));
				//算出剩余处理时间
				Long restTime = DateUtil.diffDateTime(vo.getEndTime(),new Date());
				vo.setRestTime(restTime);
			}
			vo.setTaskDefinitionKey(task.getTaskDefinitionKey());
			vo.setTenantId(task.getTenantId());
			vo.setTaskName(task.getName());
			flowableExtensionTaskDao.insertExtensionTask(vo);
		}
	}

	@Override
	public PageModel<TaskExtensionVo> getExtensionTaskByProcessInstanceId(String processInstanceId, Query query)
	{
		PageHelper.startPage(query.getPageNum(), query.getPageSize());
		Page<TaskExtensionVo> taskExtensionList = flowableExtensionTaskDao.getExtensionTaskByProcessInstanceId(processInstanceId);
		return new PageModel<>(taskExtensionList);
	}

	@Override
	public Result<String> updateExtensionCustomTaskById(ExtensionTaskQueryVo params)
	{
		TaskExtensionVo taskExtensionVo = flowableExtensionTaskDao.getExtensionTaskByProcessInstanceIdAndTaskId(params.getProcessInstanceId(),params.getTaskId());
		if(taskExtensionVo == null)
		{
			return Result.failed("无法找到该流程实例对应的自定义属性记录!");
		}
		//计算出代办的截止日期
		Date endDate = DateUtil.addDate(taskExtensionVo.getStartTime(),Integer.parseInt(params.getCustomTaskMaxDay()));
		taskExtensionVo.setEndTime(endDate);
		taskExtensionVo.setUpdateTime(new Date());
		taskExtensionVo.setCustomTaskMaxDay(params.getCustomTaskMaxDay());
		flowableExtensionTaskDao.updateExtensionCustomTaskById(taskExtensionVo);
		return Result.failed("更新成功!");
	}

	public FlowElement getFlowElementByActivityIdAndProcessDefinitionId(String taskDefinedKey, String processDefinitionId) {
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		List<Process> processes = bpmnModel.getProcesses();
		if (CollectionUtils.isNotEmpty(processes)) {
			for (Process process : processes) {
				FlowElement flowElement = process.getFlowElement(taskDefinedKey, true);
				if (Objects.nonNull(flowElement)) {
					return flowElement;
				}
			}
		}
		return null;
	}

	public List<ExtensionElement> getCustomProperty(String taskDefinedKey, String processDefinitionId, String customPropertyName) {
		FlowElement flowElement = this.getFlowElementByActivityIdAndProcessDefinitionId(taskDefinedKey, processDefinitionId);
		if (flowElement != null && flowElement instanceof UserTask) {
			UserTask userTask = (UserTask) flowElement;
			Map<String, List<ExtensionElement>> extensionElements = userTask.getExtensionElements();
			if (MapUtils.isNotEmpty(extensionElements)) {
				List<ExtensionElement> values = extensionElements.get(customPropertyName);
				if (CollectionUtils.isNotEmpty(values)) {
					return values;
				}
			}
		}
		return null;
	}
}
