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

import java.util.*;
import java.util.stream.Collectors;

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
	public void saveExtensionTask(String processDefinitionId,String fromKey,String businessInfo)
	{
		//并联任务的时候保证起始时间相同利于后续驳回查询节点相关的其他并联所有节点
		Date startTime = new Date();
		String elementText = null;
		List<Task> waitedTaskList = taskService.createTaskQuery().processInstanceId(processDefinitionId).active().list();
		//TODO 对于加签的情况驳回的时候无法判断
		String flowType = FlowConstant.FLOW_SEQUENTIAL;
		if(waitedTaskList.size()>1)
		{
			flowType = FlowConstant.FLOW_PARALLEL;
		}
		//设置
		if (!CollectionUtils.isEmpty(waitedTaskList))
		{
			for(Task task:waitedTaskList)
			{
				List<String> assigneeList = new ArrayList<>();
				List<String> groupIdList = new ArrayList<>();
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
				//只可能是一条(子流程除外)
				//此处assignee和groupId都有可能是多个
				if(!CollectionUtils.isEmpty(identityLinks)){
					for(IdentityLink identityLink:identityLinks)
					{
						if(StringUtils.isNotBlank(identityLink.getUserId()))
						{
							assigneeList.add(identityLink.getUserId());
						}
						if(StringUtils.isNotBlank(identityLink.getGroupId()))
						{
							groupIdList.add(identityLink.getGroupId());
						}


					}
				}

				//如果是并联请求需要判断是否需要插入-通过流程实例id和taskId来判断
				//TODO 如果是null 应该删掉这条并联中的记录，不过不影响查询
				TaskExtensionVo vo = flowableExtensionTaskDao.getExtensionTaskByProcessInstanceIdAndTaskId(task.getProcessInstanceId(),task.getId());
				if(vo == null)
				{
					TaskExtensionVo taskExtensionVo = new TaskExtensionVo();
					taskExtensionVo.setTaskId(task.getId());
					taskExtensionVo.setAssignee(assigneeList.stream().collect(Collectors.joining(",")));
					taskExtensionVo.setGroupId(groupIdList.stream().collect(Collectors.joining(",")));
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
					taskExtensionVo.setFlowType(flowType);
					taskExtensionVo.setStartTime(startTime);
					taskExtensionVo.setBusinessInfo(businessInfo);
					flowableExtensionTaskDao.insertExtensionTask(taskExtensionVo);
				}
			}
		}
	}


	@Override
	public void saveBackExtensionTask(String processDefinitionId)
	{
		//并联任务的时候保证起始时间相同利于后续驳回查询节点相关的其他并联所有节点
		Date startTime = new Date();
		String flowType = FlowConstant.FLOW_SEQUENTIAL;
		List<org.flowable.task.api.Task> waitedTaskList = taskService.createTaskQuery().processInstanceId(processDefinitionId).active().list();
		//TODO 对于加签的情况驳回的时候无法判断
		if(waitedTaskList.size()>1)
		{
			flowType = FlowConstant.FLOW_PARALLEL;
		}
		if (!CollectionUtils.isEmpty(waitedTaskList))
		{
			for(Task task:waitedTaskList)
			{
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
				vo.setFlowType(flowType);
				vo.setStartTime(startTime);
				flowableExtensionTaskDao.insertExtensionTask(vo);
			}
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
	public PageModel<TaskExtensionVo> getAllExtensionTaskByProcessInstanceId(String processInstanceId, Query query)
	{
		PageHelper.startPage(query.getPageNum(), query.getPageSize());
		Page<TaskExtensionVo> taskExtensionList = flowableExtensionTaskDao.getAllExtensionTaskByProcessInstanceId(processInstanceId);
		return new PageModel<>(taskExtensionList);
	}

	@Override
	public PageModel<TaskExtensionVo> getFinishExtensionTaskByProcessInstanceId(String processInstanceId, Query query)
	{
		PageHelper.startPage(query.getPageNum(), query.getPageSize());
		Page<TaskExtensionVo> taskExtensionList = flowableExtensionTaskDao.getFinishExtensionTaskByProcessInstanceId(processInstanceId);
		return new PageModel<>(taskExtensionList);
	}

	@Override
	public List<String> getParallelNodesByProcessInstanceIdAndTaskId(String processInstanceId, String taskId)
	{
		return flowableExtensionTaskDao.getParallelNodesByProcessInstanceIdAndTaskId(processInstanceId,taskId);
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
		Long restTime = DateUtil.diffDateTime(endDate,new Date());
		taskExtensionVo.setRestTime(restTime);
		flowableExtensionTaskDao.updateExtensionCustomTaskById(taskExtensionVo);
		return Result.sucess("更新成功!");
	}

	@Override
	public void updateAssigneeByProcessInstanceIdAndTaskID(String processInstanceId,String taskId,String assignee)
	{
		flowableExtensionTaskDao.updateAssigneeByProcessInstanceIdAndTaskID(processInstanceId,taskId,assignee);
	}

	@Override
	public Result<String> updateDbInfoById(TaskExtensionVo params)
	{
		flowableExtensionTaskDao.updateDbInfoById(params);
		return Result.sucess("更新成功!");
	}

	@Override
	public List<TaskExtensionVo> getExtensionTaskByStartTime(String processInstanceId, String startTime)
	{
		return flowableExtensionTaskDao.getExtensionTaskByStartTime(processInstanceId,startTime);
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
