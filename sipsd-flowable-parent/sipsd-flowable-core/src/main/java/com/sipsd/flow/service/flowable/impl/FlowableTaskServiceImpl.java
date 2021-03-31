package com.sipsd.flow.service.flowable.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.DateUtil;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.constant.FlowConstant;
import com.sipsd.flow.dao.flowable.IFlowableExtensionTaskDao;
import com.sipsd.flow.dao.flowable.IFlowableTaskDao;
import com.sipsd.flow.enm.flowable.CommentTypeEnum;
import com.sipsd.flow.service.flowable.IFlowableBpmnModelService;
import com.sipsd.flow.service.flowable.IFlowableExtensionTaskService;
import com.sipsd.flow.service.flowable.IFlowableTaskService;
import com.sipsd.flow.vo.flowable.*;
import com.sipsd.flow.vo.flowable.ret.FlowNodeVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import com.sipsd.flow.vo.flowable.ret.TaskVo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.idm.api.User;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : chengtg
 * @title: : FlowableTaskServiceImpl
 * @projectName : flowable
 * @description: task service
 * @date : 2019/11/1315:15
 */
@Service
public class FlowableTaskServiceImpl extends BaseProcessService implements IFlowableTaskService {
	
	@Autowired
	private IFlowableTaskDao flowableTaskDao;
	@Autowired
	private IFlowableBpmnModelService flowableBpmnModelService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private IFlowableExtensionTaskService flowableExtensionTaskService;
	@Autowired
	private IFlowableExtensionTaskDao flowableExtensionTaskDao;

	@Override
	public boolean checkParallelgatewayNode(String taskId) {
		boolean flag = false;
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String executionId = task.getExecutionId();
		Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
		String pExecutionId = execution.getParentId();
		Execution pExecution = runtimeService.createExecutionQuery().executionId(pExecutionId).singleResult();
		if (pExecution != null) {
			String ppExecutionId = pExecution.getParentId();
			long count = runtimeService.createExecutionQuery().executionId(ppExecutionId).count();
			if (count == 0) {
				flag = true;
			}
		}
		return flag;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<String> jumpToStepTask(BackTaskVo backTaskVo) {

		//通过fromkey找到当前最新的那一条审批节点信息(通过时间排序)
		TaskExtensionVo taskExtensionVo = flowableExtensionTaskDao.getExtensionTaskByTaskDefinitionKey(backTaskVo.getProcessInstanceId(),backTaskVo.getDistFlowElementId());
		if(taskExtensionVo == null)
		{
			return Result.failed("无法找到上个审批节点的信息");
		}
		if(taskExtensionVo.getFlowType().equals(FlowConstant.FLOW_PARALLEL))
		{
			return  Result.failed("无法驳回到并行节点，请选择其他节点!");
		}

		Result<String> result = null;
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(backTaskVo.getTaskId())
				.singleResult();
		// 1.把当前的节点设置为空
		if (taskEntity != null) {
			// 2.设置审批人
			taskEntity.setAssignee(backTaskVo.getUserCode());
			taskService.saveTask(taskEntity);
			// 3.添加驳回意见
			this.addComment(backTaskVo.getTaskId(), backTaskVo.getUserCode(), backTaskVo.getProcessInstanceId(),
					CommentTypeEnum.BH.toString(), backTaskVo.getMessage());
			// 4.处理提交人节点
			FlowNode distActivity = flowableBpmnModelService
					.findFlowNodeByActivityId(taskEntity.getProcessDefinitionId(), backTaskVo.getDistFlowElementId());
			if (distActivity != null) {
				if (FlowConstant.FLOW_SUBMITTER.equals(distActivity.getName())) {
					ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
							.processInstanceId(taskEntity.getProcessInstanceId()).singleResult();
					runtimeService.setVariable(backTaskVo.getProcessInstanceId(), FlowConstant.FLOW_SUBMITTER_VAR,
							processInstance.getStartUserId());
				}
			}
			// 5.删除节点
			this.deleteActivity(backTaskVo.getDistFlowElementId(), taskEntity.getProcessInstanceId());
			List<String> executionIds = new ArrayList<>();
			// 6.判断节点是不是子流程内部的节点
			if (flowableBpmnModelService.checkActivitySubprocessByActivityId(taskEntity.getProcessDefinitionId(),
					backTaskVo.getDistFlowElementId())
					&& flowableBpmnModelService.checkActivitySubprocessByActivityId(taskEntity.getProcessDefinitionId(),
							taskEntity.getTaskDefinitionKey())) {
				// 6.1 子流程内部驳回
				Execution executionTask = runtimeService.createExecutionQuery().executionId(taskEntity.getExecutionId())
						.singleResult();
				String parentId = executionTask.getParentId();
				List<Execution> executions = runtimeService.createExecutionQuery().parentId(parentId).list();
				executions.forEach(execution -> executionIds.add(execution.getId()));
				this.moveExecutionsToSingleActivityId(executionIds, backTaskVo.getDistFlowElementId());
			} else {
				// 6.2 普通驳回
				List<Execution> executions = runtimeService.createExecutionQuery()
						.parentId(taskEntity.getProcessInstanceId()).list();
				executions.forEach(execution -> executionIds.add(execution.getId()));
				this.moveExecutionsToSingleActivityId(executionIds, backTaskVo.getDistFlowElementId());
			}
			result = Result.sucess("跳转成功!");
			//保存流程的自定义属性-最大审批天数
			//TODO 跳转只能跳转到以前的节点，如果跳转到后面的节点无法知道当前节点是审批还是未审批(需要讨论)
			flowableExtensionTaskService.saveBackExtensionTask(backTaskVo.getProcessInstanceId());
		} else {
			result = Result.failed("不存在任务实例,请确认!");
		}
		return result;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<String> backToStepTask(PreBackTaskVo preBackTaskVo) {
		//找到当前节点的上个审批节点-fromKey
		TaskExtensionVo taskExtensionVo = flowableExtensionTaskDao.getExtensionTaskByProcessInstanceIdAndTaskId(preBackTaskVo.getProcessInstanceId(),preBackTaskVo.getTaskId());
		String currentTaskDefKey = taskExtensionVo.getTaskDefinitionKey();
		if(taskExtensionVo == null || StringUtils.isEmpty(taskExtensionVo.getFromKey()))
		{
			return Result.failed("无法找到上个审批节点的信息");
		}
		//通过fromkey找到当前最新的那一条审批节点信息(通过时间排序)
		taskExtensionVo = flowableExtensionTaskDao.getExtensionTaskByTaskDefinitionKey(preBackTaskVo.getProcessInstanceId(),taskExtensionVo.getFromKey());
		if(taskExtensionVo == null)
		{
			return Result.failed("无法找到上个审批节点的信息");
		}

		Result<String> result = null;
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(preBackTaskVo.getTaskId())
				.singleResult();
		// 1.把当前的节点设置为空
		if (taskEntity != null) {
			// 2.设置审批人
			taskEntity.setAssignee(taskExtensionVo.getAssignee());
			taskService.saveTask(taskEntity);
			// 3.添加驳回意见
			this.addComment(preBackTaskVo.getTaskId(), taskExtensionVo.getAssignee(), preBackTaskVo.getProcessInstanceId(),
					CommentTypeEnum.BH.toString(), preBackTaskVo.getMessage());
			// 4.处理提交人节点
			FlowNode distActivity = flowableBpmnModelService
					.findFlowNodeByActivityId(taskEntity.getProcessDefinitionId(), taskExtensionVo.getTaskDefinitionKey());
			if (distActivity != null) {
				if (FlowConstant.FLOW_SUBMITTER.equals(distActivity.getName())) {
					ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
							.processInstanceId(taskEntity.getProcessInstanceId()).singleResult();
					runtimeService.setVariable(preBackTaskVo.getProcessInstanceId(), FlowConstant.FLOW_SUBMITTER_VAR,
							processInstance.getStartUserId());
				}
			}
			// 5.删除节点
			this.deleteActivity(taskExtensionVo.getTaskDefinitionKey(), taskEntity.getProcessInstanceId());
			List<String> executionIds = new ArrayList<>();
			// 6.判断节点是不是子流程内部的节点
			if (flowableBpmnModelService.checkActivitySubprocessByActivityId(taskEntity.getProcessDefinitionId(),
					taskExtensionVo.getTaskDefinitionKey())
					&& flowableBpmnModelService.checkActivitySubprocessByActivityId(taskEntity.getProcessDefinitionId(),
					taskEntity.getTaskDefinitionKey())) {
				// 6.1 子流程内部驳回
				Execution executionTask = runtimeService.createExecutionQuery().executionId(taskEntity.getExecutionId())
						.singleResult();
				String parentId = executionTask.getParentId();
				List<Execution> executions = runtimeService.createExecutionQuery().parentId(parentId).list();
				executions.forEach(execution -> executionIds.add(execution.getId()));
				this.moveExecutionsToSingleActivityId(executionIds, taskExtensionVo.getTaskDefinitionKey());
			} else if(taskExtensionVo.getFlowType().equals(FlowConstant.FLOW_PARALLEL)){
				List<String> distActivityIds = new ArrayList<>();
				//查询该节点的其他并联节点
				List<TaskExtensionVo> parallelTask =  flowableExtensionTaskService.getExtensionTaskByStartTime(preBackTaskVo.getProcessInstanceId(),
						DateUtil.getDateTime(taskExtensionVo.getStartTime()));
				for(TaskExtensionVo extensionVo:parallelTask)
				{
					distActivityIds.add(extensionVo.getTaskDefinitionKey());
				}

				//5.执行驳回操作
				runtimeService.createChangeActivityStateBuilder()
						.processInstanceId(preBackTaskVo.getProcessInstanceId())
						.moveSingleActivityIdToActivityIds(currentTaskDefKey,distActivityIds)
						.changeState();
			}
			else {
				// 6.2 普通驳回
				List<Execution> executions = runtimeService.createExecutionQuery()
						.parentId(taskEntity.getProcessInstanceId()).list();
				executions.forEach(execution -> executionIds.add(execution.getId()));
				this.moveExecutionsToSingleActivityId(executionIds, taskExtensionVo.getTaskDefinitionKey());

			}
			//插入自定义属性表
			flowableExtensionTaskService.saveBackExtensionTask(preBackTaskVo.getProcessInstanceId());
			result = Result.sucess("驳回成功!");
		} else {
			result = Result.failed("不存在任务实例,请确认!");
		}
		return result;
	}

	@Override
	public List<FlowNodeVo> getBackNodesByProcessInstanceId(String processInstanceId, String taskId) {
		List<FlowNodeVo> backNods = new ArrayList<>();
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		String currActId = taskEntity.getTaskDefinitionKey();
		// 获取运行节点表中usertask
		String sql = "select t.* from act_ru_actinst t where t.ACT_TYPE_ = 'userTask' "
				+ " and t.PROC_INST_ID_=#{processInstanceId} and t.END_TIME_ is not null ";
		List<ActivityInstance> activityInstances = runtimeService.createNativeActivityInstanceQuery().sql(sql)
				.parameter("processInstanceId", processInstanceId).list();
		// 获取运行节点表的parallelGateway节点并出重
		sql = "SELECT t.ID_, t.REV_,t.PROC_DEF_ID_,t.PROC_INST_ID_,t.EXECUTION_ID_,t.ACT_ID_, t.TASK_ID_, t.CALL_PROC_INST_ID_, t.ACT_NAME_, t.ACT_TYPE_, "
				+ " t.ASSIGNEE_, t.START_TIME_,t.END_TIME_ as END_TIME_, t.DURATION_, t.DELETE_REASON_, t.TENANT_ID_"
				+ " FROM  act_ru_actinst t WHERE t.ACT_TYPE_ = 'parallelGateway' AND t.PROC_INST_ID_ = #{processInstanceId} and t.END_TIME_ is not null"
				+ " and t.ACT_ID_ <> #{actId} ";

//		sql = "SELECT t.ID_, t.REV_,t.PROC_DEF_ID_,t.PROC_INST_ID_,t.EXECUTION_ID_,t.ACT_ID_, t.TASK_ID_, t.CALL_PROC_INST_ID_, t.ACT_NAME_, t.ACT_TYPE_, "
//				+ " t.ASSIGNEE_, t.START_TIME_, max(t.END_TIME_) as END_TIME_, t.DURATION_, t.DELETE_REASON_, t.TENANT_ID_"
//				+ " FROM  act_ru_actinst t WHERE t.ACT_TYPE_ = 'parallelGateway' AND t.PROC_INST_ID_ = #{processInstanceId} and t.END_TIME_ is not null"
//				+ " and t.ACT_ID_ <> #{actId} GROUP BY t.act_id_";
		List<ActivityInstance> parallelGatewaies = runtimeService.createNativeActivityInstanceQuery().sql(sql)
				.parameter("processInstanceId", processInstanceId).parameter("actId", currActId).list();
		// 排序
		if (CollectionUtils.isNotEmpty(parallelGatewaies)) {
			activityInstances.addAll(parallelGatewaies);
			activityInstances.sort(Comparator.comparing(ActivityInstance::getEndTime));
		}
		// 分组节点
		int count = 0;
		Map<ActivityInstance, List<ActivityInstance>> parallelGatewayUserTasks = new HashMap<>();
		List<ActivityInstance> userTasks = new ArrayList<>();
		ActivityInstance currActivityInstance = null;
		for (ActivityInstance activityInstance : activityInstances) {
			if (BpmnXMLConstants.ELEMENT_GATEWAY_PARALLEL.equals(activityInstance.getActivityType())) {
				count++;
				if (count % 2 != 0) {
					List<ActivityInstance> datas = new ArrayList<>();
					currActivityInstance = activityInstance;
					parallelGatewayUserTasks.put(currActivityInstance, datas);
				}
			}
			if (BpmnXMLConstants.ELEMENT_TASK_USER.equals(activityInstance.getActivityType())) {
				if (count % 2 == 0) {
					userTasks.add(activityInstance);
				} else {
					if (parallelGatewayUserTasks.containsKey(currActivityInstance)) {
						parallelGatewayUserTasks.get(currActivityInstance).add(activityInstance);
					}
				}
			}
		}
		// 组装人员名称
		List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).finished().list();
		Map<String, List<HistoricTaskInstance>> taskInstanceMap = new HashMap<>();
		List<String> userCodes = new ArrayList<>();
		historicTaskInstances.forEach(historicTaskInstance -> {
			userCodes.add(historicTaskInstance.getAssignee());
			String taskDefinitionKey = historicTaskInstance.getTaskDefinitionKey();
			if (taskInstanceMap.containsKey(historicTaskInstance.getTaskDefinitionKey())) {
				taskInstanceMap.get(taskDefinitionKey).add(historicTaskInstance);
			} else {
				List<HistoricTaskInstance> tasks = new ArrayList<>();
				tasks.add(historicTaskInstance);
				taskInstanceMap.put(taskDefinitionKey, tasks);
			}
		});
		// 组装usertask的数据
		List<User> userList = identityService.createUserQuery().userIds(userCodes).list();
		Map<String, String> activityIdUserNames = this.getApplyers(processInstanceId, userList, taskInstanceMap);
		if (CollectionUtils.isNotEmpty(userTasks)) {
			userTasks.forEach(activityInstance -> {
				FlowNodeVo node = new FlowNodeVo();
				node.setNodeId(activityInstance.getActivityId());
				node.setNodeName(activityInstance.getActivityName());
				node.setEndTime(activityInstance.getEndTime());
				node.setUserName(activityIdUserNames.get(activityInstance.getActivityId()));
				backNods.add(node);
			});
		}
		// 组装会签节点数据
		if (MapUtils.isNotEmpty(taskInstanceMap)) {
			parallelGatewayUserTasks.forEach((activity, activities) -> {
				FlowNodeVo node = new FlowNodeVo();
				node.setNodeId(activity.getActivityId());
				node.setEndTime(activity.getEndTime());
				StringBuffer nodeNames = new StringBuffer("会签:");
				StringBuffer userNames = new StringBuffer("审批人员:");
				if (CollectionUtils.isNotEmpty(activities)) {
					activities.forEach(activityInstance -> {
						nodeNames.append(activityInstance.getActivityName()).append(",");
						userNames.append(activityIdUserNames.get(activityInstance.getActivityId())).append(",");
					});
					node.setNodeName(nodeNames.toString());
					node.setUserName(userNames.toString());
					backNods.add(node);
				}
			});
		}
		// 去重合并
		List<FlowNodeVo> datas = backNods.stream()
				.collect(Collectors.collectingAndThen(
						Collectors
								.toCollection(() -> new TreeSet<>(Comparator.comparing(nodeVo -> nodeVo.getNodeId()))),
						ArrayList::new));

		// 排序
		datas.sort(Comparator.comparing(FlowNodeVo::getEndTime));
		return datas;
	}

	private Map<String, String> getApplyers(String processInstanceId, List<User> userList,
			Map<String, List<HistoricTaskInstance>> taskInstanceMap) {
		Map<String, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
		Map<String, String> applyMap = new HashMap<>();
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		taskInstanceMap.forEach((activityId, taskInstances) -> {
			StringBuffer applyers = new StringBuffer();
			StringBuffer finalApplyers = applyers;
			taskInstances.forEach(taskInstance -> {
				if (!taskInstance.getName().equals(FlowConstant.FLOW_SUBMITTER)) {
					User user = userMap.get(taskInstance.getAssignee());
					if (user != null) {
						if (StringUtils.indexOf(finalApplyers.toString(), user.getDisplayName()) == -1) {
							finalApplyers.append(user.getDisplayName()).append(",");
						}
					}
				} else {
					String startUserId = processInstance.getStartUserId();
					User user = identityService.createUserQuery().userId(startUserId).singleResult();
					if (user != null) {
						finalApplyers.append(user.getDisplayName()).append(",");
					}
				}
			});
			if (applyers.length() > 0) {
				applyers = applyers.deleteCharAt(applyers.length() - 1);
			}
			applyMap.put(activityId, applyers.toString());
		});
		return applyMap;
	}

	@Override
	public Result<String> beforeAddSignTask(AddSignTaskVo addSignTaskVo) {
		return this.addSignTask(addSignTaskVo, false);
	}

	@Override
	public Result<String> afterAddSignTask(AddSignTaskVo addSignTaskVo) {
		return this.addSignTask(addSignTaskVo, true);
	}

	@Override
	public Result<String> addSignTask(AddSignTaskVo addSignTaskVo, Boolean flag) {
		Result<String> result = null;
		TaskEntityImpl taskEntity = (TaskEntityImpl) taskService.createTaskQuery().taskId(addSignTaskVo.getTaskId())
				.singleResult();
		// 1.把当前的节点设置为空
		if (taskEntity != null) {
			// 如果是加签再加签
			String parentTaskId = taskEntity.getParentTaskId();
			if (StringUtils.isBlank(parentTaskId)) {
				taskEntity.setOwner(addSignTaskVo.getUserCode());
				taskEntity.setAssignee(null);
				taskEntity.setCountEnabled(true);
				if (flag) {
					taskEntity.setScopeType(FlowConstant.AFTER_ADDSIGN);
				} else {
					taskEntity.setScopeType(FlowConstant.BEFORE_ADDSIGN);
				}
				// 1.2 设置任务为空执行者
				taskService.saveTask(taskEntity);
			}
			// 2.添加加签数据
			this.createSignSubTasks(addSignTaskVo, taskEntity);
			// 3.添加审批意见
			String type = flag ? CommentTypeEnum.HJQ.toString() : CommentTypeEnum.QJQ.toString();
			this.addComment(addSignTaskVo.getTaskId(), addSignTaskVo.getUserCode(),
					addSignTaskVo.getProcessInstanceId(), type, addSignTaskVo.getMessage());
			String message = flag ? "后加签成功" : "前加签成功";
			result =  Result.sucess(message);
		} else {
			result =  Result.failed("不存在任务实例，请确认!");
		}
		return result;
	}

	/**
	 * 创建加签子任务
	 *
	 * @param signVo     加签参数
	 * @param taskEntity 父任务
	 */
	private void createSignSubTasks(AddSignTaskVo signVo, TaskEntity taskEntity) {
		if (CollectionUtils.isNotEmpty(signVo.getSignPersoneds())) {
			String parentTaskId = taskEntity.getParentTaskId();
			if (StringUtils.isBlank(parentTaskId)) {
				parentTaskId = taskEntity.getId();
			}
			String finalParentTaskId = parentTaskId;
			// 1.创建被加签人的任务列表
			signVo.getSignPersoneds().forEach(userCode -> {
				if (StringUtils.isNotBlank(userCode)) {
					this.createSubTask(taskEntity, finalParentTaskId, userCode);
				}
			});
			String taskId = taskEntity.getId();
			if (StringUtils.isBlank(taskEntity.getParentTaskId())) {
				// 2.创建加签人的任务并执行完毕
				Task task = this.createSubTask(taskEntity, finalParentTaskId, signVo.getUserCode());
				taskId = task.getId();
			}
			Task taskInfo = taskService.createTaskQuery().taskId(taskId).singleResult();
			if (null != taskInfo) {
				taskService.complete(taskId);
			}
			// 如果是候选人，需要删除运行时候选表种的数据。
			long candidateCount = taskService.createTaskQuery().taskId(parentTaskId)
					.taskCandidateUser(signVo.getUserCode()).count();
			if (candidateCount > 0) {
				taskService.deleteCandidateUser(parentTaskId, signVo.getUserCode());
			}
		}
	}

	@Override
	public Result<String> unClaimTask(ClaimTaskVo claimTaskVo) {
		Result<String> result = null;
		TaskEntityImpl currTask = (TaskEntityImpl) taskService.createTaskQuery().taskId(claimTaskVo.getTaskId())
				.singleResult();
		if (currTask != null) {
			// 1.添加审批意见
			this.addComment(claimTaskVo.getTaskId(), claimTaskVo.getProcessInstanceId(), CommentTypeEnum.QS.toString(),
					claimTaskVo.getMessage());
			List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(claimTaskVo.getTaskId());
			boolean flag = false;
			if (CollectionUtils.isNotEmpty(identityLinks)) {
				for (IdentityLink link : identityLinks) {
					if (IdentityLinkType.CANDIDATE.equals(link.getType())) {
						flag = true;
						break;
					}
				}
			}
			// 2.反签收
			if (flag) {
				taskService.claim(claimTaskVo.getTaskId(), null);
				result = Result.sucess("反签收成功");
			} else {
				result = Result.failed("由于没有候选人或候选组,会导致任务无法认领,请确认.");
			}
		} else {
			result =  Result.failed("反签收失败");
		}
		return result;
	}

	@Override
	public Result<String> claimTask(ClaimTaskVo claimTaskVo) {
		Result<String> result = null;
		TaskEntityImpl currTask = (TaskEntityImpl) taskService.createTaskQuery().taskId(claimTaskVo.getTaskId())
				.singleResult();
		if (currTask != null) {
			// 1.添加审批意见
			this.addComment(claimTaskVo.getTaskId(), claimTaskVo.getProcessInstanceId(), CommentTypeEnum.QS.toString(),
					claimTaskVo.getMessage());
			// 2.签收
			taskService.claim(claimTaskVo.getTaskId(), claimTaskVo.getUserCode());
			result = Result.sucess("签收成功");
		} else {
			result = Result.failed("签收失败");
		}
		return result;
	}

	@Override
	public Result<String> delegateTask(DelegateTaskVo delegateTaskVo) {
		Result<String> result = null;
		TaskEntityImpl currTask = (TaskEntityImpl) taskService.createTaskQuery().taskId(delegateTaskVo.getTaskId())
				.singleResult();
		if (currTask != null) {
			// 1.添加审批意见
			this.addComment(delegateTaskVo.getTaskId(), delegateTaskVo.getUserCode(),
					delegateTaskVo.getProcessInstanceId(), CommentTypeEnum.WP.toString(), delegateTaskVo.getMessage());
			// 2.设置审批人就是当前登录人
			taskService.setAssignee(delegateTaskVo.getTaskId(), delegateTaskVo.getUserCode());
			// 3.执行委派
			taskService.delegateTask(delegateTaskVo.getTaskId(), delegateTaskVo.getDelegateUserCode());
			result = Result.sucess( "委派成功");
		} else {
			result = Result.failed( "没有运行时的任务实例,请确认!");
		}
		return result;
	}

	@Override
	public Result<String> turnTask(TurnTaskVo turnTaskVo) {
		Result<String> result = null;
		TaskEntityImpl currTask = (TaskEntityImpl) taskService.createTaskQuery().taskId(turnTaskVo.getTaskId())
				.singleResult();
		if (currTask != null) {
			// 1.生成历史记录
			TaskEntity task = this.createSubTask(currTask, turnTaskVo.getUserCode());
			// 2.添加审批意见
			this.addComment(task.getId(), turnTaskVo.getUserCode(), turnTaskVo.getProcessInstanceId(),
					CommentTypeEnum.ZB.toString(), turnTaskVo.getMessage());
			taskService.complete(task.getId());
			// 3.转办
			taskService.setAssignee(turnTaskVo.getTaskId(), turnTaskVo.getTurnToUserId());
			taskService.setOwner(turnTaskVo.getTaskId(), turnTaskVo.getUserCode());
			result = Result.sucess( "转办成功");
		} else {
			result = Result.failed( "没有运行时的任务实例,请确认!");
		}
		return result;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Result<String> complete(CompleteTaskVo params) {
		Result<String> result = Result.sucess( "审批成功");
		if (StringUtils.isNotBlank(params.getProcessInstanceId()) && StringUtils.isNotBlank(params.getTaskId())) {
			// 1.查看当前任务是存在
			TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(params.getTaskId())
					.singleResult();
			if (taskEntity != null) {
				String taskId = params.getTaskId();
				// 2.委派处理
				if (DelegationState.PENDING.equals(taskEntity.getDelegationState())) {
					// 2.1生成历史记录
					TaskEntity task = this.createSubTask(taskEntity, params.getUserCode());
					taskService.complete(task.getId());
					taskId = task.getId();
					// 2.2执行委派
					taskService.resolveTask(params.getTaskId(), params.getVariables());
				} else {
					// 3.1修改执行人 其实我这里就相当于签收了
					taskService.setAssignee(params.getTaskId(), params.getUserCode());
					// 3.2执行任务
					taskService.complete(params.getTaskId(), params.getVariables());
					// 4.处理加签父任务
					String parentTaskId = taskEntity.getParentTaskId();
					if (StringUtils.isNotBlank(parentTaskId)) {
						String tableName = managementService.getTableName(TaskEntity.class);
						String sql = "select count(1) from " + tableName + " where PARENT_TASK_ID_=#{parentTaskId}";
						long subTaskCount = taskService.createNativeTaskQuery().sql(sql)
								.parameter("parentTaskId", parentTaskId).count();
						if (subTaskCount == 0) {
							Task task = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
							// 处理前后加签的任务
							taskService.resolveTask(parentTaskId);
							if (FlowConstant.AFTER_ADDSIGN.equals(task.getScopeType())) {
								taskService.complete(parentTaskId);
							}
						}
					}
				}
				//保存流程的自定义属性-最大审批天数
				flowableExtensionTaskService.saveExtensionTask(params.getProcessInstanceId(),taskEntity.getTaskDefinitionKey());
				String type = params.getType() == null ? CommentTypeEnum.SP.toString() : params.getType();
				// 5.生成审批意见
				this.addComment(taskId, params.getUserCode(), params.getProcessInstanceId(), type, params.getMessage());
			} else {
				result = Result.failed("没有此任务，请确认!");
			}
		} else {
			result = Result.failed("请输入正确的参数!");
		}
		return result;
	}

	@Override
	public Result<Task> findTaskById(String taskId) {
		Result<Task> result = Result.sucess("OK");
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		result.setData(task);
		return result;
	}

	@Override
	public PageModel<TaskVo> getApplyingTasks(TaskQueryVo params, Query query) {
		PageHelper.startPage(query.getPageNum(), query.getPageSize());
		Page<TaskVo> applyingTasks = flowableTaskDao.getApplyingTasks(params);
		return new PageModel<>(applyingTasks);
	}

	@Override
	public PageModel<TaskVo> getApplyedTasks(TaskQueryVo params, Query query) {
		PageHelper.startPage(query.getPageNum(), query.getPageSize());
		Page<TaskVo> applyedTasks = flowableTaskDao.getApplyedTasks(params);
		return new PageModel<>(applyedTasks);
	}

	@Override
	public List<User> getApprovers(String processInstanceId) {
		List<User> users = new ArrayList<>();
		List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
		if (CollectionUtils.isNotEmpty(list)) {
			list.forEach(task -> {
				if (StringUtils.isNotBlank(task.getAssignee())) {
					// 1.审批人ASSIGNEE_是用户id
					User user = identityService.createUserQuery().userId(task.getAssignee()).singleResult();
					if (user != null) {
						users.add(user);
					}
					// 2.审批人ASSIGNEE_是组id
					List<User> gusers = identityService.createUserQuery().memberOfGroup(task.getAssignee()).list();
					if (CollectionUtils.isNotEmpty(gusers)) {
						users.addAll(gusers);
					}
				} else {
					List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
					if (CollectionUtils.isNotEmpty(identityLinks)) {
						identityLinks.forEach(identityLink -> {
							// 3.审批人ASSIGNEE_为空,用户id
							if (StringUtils.isNotBlank(identityLink.getUserId())) {
								User user = identityService.createUserQuery().userId(identityLink.getUserId())
										.singleResult();
								if (user != null) {
									users.add(user);
								}
							} else {
								// 4.审批人ASSIGNEE_为空,组id
								List<User> gusers = identityService.createUserQuery()
										.memberOfGroup(identityLink.getGroupId()).list();
								if (CollectionUtils.isNotEmpty(gusers)) {
									users.addAll(gusers);
								}
							}
						});
					}
				}
			});
		}
		return users;
	}
	
	@Override
	public Result<List<FlowNodeVo>> getProcessNodeList(String modelkey) {
		Result<List<FlowNodeVo>> result =  new Result<List<FlowNodeVo>>();
		result.setData(getProcessNodes(modelkey));
		return result;
	}
	
	@Override
    public TaskVo getTask(FormInfoQueryVo formInfoQueryVo) {
		String taskId = formInfoQueryVo.getTaskId();
		List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(formInfoQueryVo.getProcessInstanceId()).list();

        for (HistoricVariableInstance variable : list) {
	        System.out.println("variable: " + variable.getVariableName() + " = " + variable.getValue());
        }
        HistoricTaskInstance taskHis = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        TaskVo rep = flowableTaskDao.getTaskById(taskId);
        ProcessDefinition processDefinition = null;
        String formKey = null;
        Object renderedTaskForm = null;
       
        if (StringUtils.isNotEmpty(taskHis.getProcessDefinitionId())) {
            processDefinition = repositoryService.getProcessDefinition(taskHis.getProcessDefinitionId());
            formKey = formService.getTaskFormKey(processDefinition.getId(), taskHis.getTaskDefinitionKey());
            if (taskHis.getEndTime() == null && formKey != null && formKey.length() > 0) {
                renderedTaskForm = formService.getRenderedTaskForm(taskId);
            }
        }

        rep.setFormKey(formKey);
        rep.setRenderedTaskForm(renderedTaskForm);
        rep.setVariables(taskHis.getProcessVariables());
        return rep;
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
