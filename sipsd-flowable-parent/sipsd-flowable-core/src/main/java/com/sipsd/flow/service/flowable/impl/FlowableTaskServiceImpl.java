package com.sipsd.flow.service.flowable.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sipsd.flow.bean.*;
import com.sipsd.flow.cmd.SaveExecutionCmd;
import com.sipsd.flow.common.DateUtil;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.constant.FlowConstant;
import com.sipsd.flow.dao.flowable.IFlowableExtensionTaskDao;
import com.sipsd.flow.dao.flowable.IFlowableTaskDao;
import com.sipsd.flow.enm.flowable.CommentTypeEnum;
import com.sipsd.flow.enm.flowable.GatewayJumpTypeEnum;
import com.sipsd.flow.exception.SipsdBootException;
import com.sipsd.flow.service.flowable.*;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.*;
import com.sipsd.flow.vo.flowable.ret.FlowNodeVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import com.sipsd.flow.vo.flowable.ret.TaskVo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.idm.api.User;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : gaoqiang
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
	@Autowired
	private ProcessEngineConfiguration processEngineConfiguration;
	@Autowired
	private BpmProcessService bpmProcessService;
	@Autowired
	private IFlowableNoticeTaskService noticeTaskService;

	protected final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";

	@Value("${sipsd.flowable.approveKey}")
	private String approveKey;

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
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
	public Result<String> jumpToStepTask(BackTaskVo backTaskVo) {
		// 1.获取任务实例
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().processInstanceId(backTaskVo.getProcessInstanceId()).taskId(backTaskVo.getTaskId()).singleResult();
		if(taskEntity == null){
			return Result.failed("不存在任务实例,请确认!");
		}

		String distFlowElementId = backTaskVo.getDistFlowElementId();
		String[] split = distFlowElementId.split(",");
		List<String> distList = Arrays.asList(split);

		BpmTaskModelQuery query = new BpmTaskModelQuery();
		query.setDefineId(taskEntity.getProcessDefinitionId());
		UserTaskModelDTO userTaskModelsDTO = bpmProcessService.getUserTaskModelDto(query);
		List<BpmTaskModelEntity> taskModelEntities = userTaskModelsDTO.getAllUserTaskModels();
		Map<String, BpmTaskModelEntity> taskModelEntitiesMap = taskModelEntities.stream().collect(
				Collectors.toMap(BpmTaskModelEntity::getTaskDefKey, a -> a, (k1, k2) -> k1));

		// 当前节点A
		BpmTaskModelEntity currentNode = taskModelEntitiesMap.get(taskEntity.getTaskDefinitionKey());

		//要跳转的节点B
		List<BpmTaskModelEntity> targetNodes = new ArrayList<>(4);
		for(String dist : distList){
			BpmTaskModelEntity bpmTaskModelEntity = taskModelEntitiesMap.get(dist);
			if(bpmTaskModelEntity != null){
				targetNodes.add(bpmTaskModelEntity);
			}
		}

		if (currentNode == null || CollUtil.isEmpty(targetNodes)) {
		  	return 	Result.failed("当前节点或目标节点不存在");
		}

		// （1）如果B有多个节点
		//        必须为同一个并行网关内的任务节点（网关开始、合并节点必须一致）
		//        必须不是同一条流程线上的任务节点
		checkjumpTargetNodes(targetNodes);

		// 2.设置审批人
		taskEntity.setAssignee(backTaskVo.getUserCode());
		taskService.saveTask(taskEntity);
		// 3.添加驳回意见
		this.addComment(backTaskVo.getTaskId(), backTaskVo.getUserCode(), backTaskVo.getProcessInstanceId(),
				CommentTypeEnum.BH.toString(), backTaskVo.getMessage());


		for(BpmTaskModelEntity entity : targetNodes){
			// 4.删除节点
			String taskDefKey = entity.getTaskDefKey();
			this.deleteActivity(taskDefKey, taskEntity.getProcessInstanceId());
			// 5.如果跳转到---提交人节点
				//设置本次全局变量信息
			FlowNode distActivity = flowableBpmnModelService.findFlowNodeByActivityId(taskEntity.getProcessDefinitionId(), taskDefKey);
			if (distActivity != null) {
				if (FlowConstant.FLOW_SUBMITTER.equals(distActivity.getName())) {
					ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
							.processInstanceId(taskEntity.getProcessInstanceId()).singleResult();
					runtimeService.setVariable(backTaskVo.getProcessInstanceId(), FlowConstant.FLOW_SUBMITTER_VAR,
							processInstance.getStartUserId());
				}
			}
		}



		if(targetNodes.size() == 1){
			if (flowableBpmnModelService.checkActivitySubprocessByActivityId(taskEntity.getProcessDefinitionId(),
					targetNodes.get(0).getTaskDefKey())
					&& flowableBpmnModelService.checkActivitySubprocessByActivityId(taskEntity.getProcessDefinitionId(),
					taskEntity.getTaskDefinitionKey())) {
				List<String> executionIds = new ArrayList<>();
				// 6.1 子流程内部驳回
				Execution executionTask = runtimeService.createExecutionQuery().executionId(taskEntity.getExecutionId())
						.singleResult();
				String parentId = executionTask.getParentId();
				List<Execution> executions = runtimeService.createExecutionQuery().parentId(parentId).list();
				executions.forEach(execution -> executionIds.add(execution.getId()));
				this.moveExecutionsToSingleActivityId(executionIds, backTaskVo.getDistFlowElementId());

				//TODO 跳转只能跳转到以前审批过的节点，如果跳转到后面的节点无法知道当前节点是审批还是未审批(需要讨论)
				flowableExtensionTaskService.saveBackExtensionTask(backTaskVo.getProcessInstanceId());
				return Result.sucess("跳转成功!");
			}
		}



		// （2）如果A和B为同一条顺序流程线上（其中包含了A/B都是非并行网关上的节点 或都为并行网关中同一条流程线上的节点），则可以直接跳转
		if (targetNodes.size() == 1 && currentNode.getParallelGatewayForkRef().equals(targetNodes.get(0).getParallelGatewayForkRef())) {

			List<String> executionIds = new ArrayList<>();
			List<Execution> executions = runtimeService.createExecutionQuery()
					.parentId(taskEntity.getProcessInstanceId()).list();

			String taskDefKey = targetNodes.get(0).getTaskDefKey();

			//如果A/B并行网关中同一条流程线上
			if(targetNodes.get(0).getInParallelGateway() && currentNode.getInParallelGateway()){
				List<String> collect = executions.stream().filter(p -> StrUtil.equals(p.getActivityId(), currentNode.getTaskDefKey())).map(Execution::getId).collect(Collectors.toList());
				executionIds.addAll(collect);
				this.moveExecutionsToSingleActivityId(executionIds,taskDefKey);
				flowableExtensionTaskService.saveBackExtensionTaskForJump(backTaskVo.getProcessInstanceId(),taskDefKey);
			}else{
				executions.forEach(execution -> executionIds.add(execution.getId()));
				this.moveExecutionsToSingleActivityId(executionIds, taskDefKey);
				flowableExtensionTaskService.saveBackExtensionTask(backTaskVo.getProcessInstanceId());

			}
			return Result.sucess("跳转成功!");


		}
		//跳转目标节点B 的任务节点编码
		distList = targetNodes.stream().map(BpmTaskMinModelEntity::getTaskDefKey).collect(Collectors.toList());

		//（3）如果A非并行分支上的任务节点
		//    则根据以上判定，B一定是为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
		if (!currentNode.getInParallelGateway()) {
			String forkParallelGatwayId = targetNodes.get(0).getForkParallelGatewayId();
			ParallelGatwayDTO forkGatewayB = userTaskModelsDTO.getAllForkGatewayMap().get(forkParallelGatwayId);
			// B为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
			dealParallelGatewayFinishLog(forkGatewayB, taskEntity, targetNodes.size());
			// 跳转
			runtimeService.createChangeActivityStateBuilder().processInstanceId(
					taskEntity.getProcessInstanceId())
					.moveSingleActivityIdToActivityIds(taskEntity.getTaskDefinitionKey(), distList)
					.changeState();

		} else {
			//（4）如果A是并行分支上的任务节点
			//   4.1 从外向里面跳转（父并网关 》子并网关）
			//    B是为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
			//   4.2 从里向外面跳转 （子并网关 》父并网关 【或】 非并行网关上的节点 【或】 其他非父子关系的并行网关节点）
			//    需要清除本任务节点并行网关上（包括父网关）所有的其他未完成的用户任务
			//    B如果是为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
			gatewayJump(userTaskModelsDTO, targetNodes, currentNode, taskEntity, distList);

		}
		//TODO 跳转只能跳转到以前审批过的节点，如果跳转到后面的节点无法知道当前节点是审批还是未审批(需要讨论)
		flowableExtensionTaskService.saveBackExtensionTask(backTaskVo.getProcessInstanceId());
		return Result.sucess("跳转成功!");	}


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
		TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(preBackTaskVo.getTaskId()).singleResult();
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

			Map<String, UserTask> allUserTaskMap = getAllUserTaskMap(taskEntity.getProcessDefinitionId());
			UserTask userTaskModel = allUserTaskMap.get(taskExtensionVo.getTaskDefinitionKey());

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
			}
			// 驳回到会签节点
			else if(userTaskModel.hasMultiInstanceLoopCharacteristics()){
				SequenceFlow sequenceFlow = userTaskModel.getIncomingFlows().get(0);
				String sourceRef = sequenceFlow.getSourceRef();
				List<Execution> executions = runtimeService.createExecutionQuery()
						.parentId(taskEntity.getProcessInstanceId()).list();
				executions.forEach(execution -> executionIds.add(execution.getId()));
				this.moveExecutionsToSingleActivityId(executionIds, sourceRef);
			}
			// 驳回到并联网关内节点
			else if(taskExtensionVo.getFlowType().equals(FlowConstant.FLOW_PARALLEL)){
				//驳回到并联节点
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
			//TODO 没有更新当前任务的实际审批人
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
//		sql = "SELECT t.ID_, t.REV_,t.PROC_DEF_ID_,t.PROC_INST_ID_,t.EXECUTION_ID_,t.ACT_ID_, t.TASK_ID_, t.CALL_PROC_INST_ID_, t.ACT_NAME_, t.ACT_TYPE_, "
//				+ " t.ASSIGNEE_, t.START_TIME_,t.END_TIME_ as END_TIME_, t.DURATION_, t.DELETE_REASON_, t.TENANT_ID_"
//				+ " FROM  act_ru_actinst t WHERE t.ACT_TYPE_ = 'parallelGateway' AND t.PROC_INST_ID_ = #{processInstanceId} and t.END_TIME_ is not null"
//				+ " and t.ACT_ID_ <> #{actId} ";

//		sql = "SELECT t.ID_, t.REV_,t.PROC_DEF_ID_,t.PROC_INST_ID_,t.EXECUTION_ID_,t.ACT_ID_, t.TASK_ID_, t.CALL_PROC_INST_ID_, t.ACT_NAME_, t.ACT_TYPE_, "
//				+ " t.ASSIGNEE_, t.START_TIME_, max(t.END_TIME_) as END_TIME_, t.DURATION_, t.DELETE_REASON_, t.TENANT_ID_"
//				+ " FROM  act_ru_actinst t WHERE t.ACT_TYPE_ = 'parallelGateway' AND t.PROC_INST_ID_ = #{processInstanceId} and t.END_TIME_ is not null"
//				+ " and t.ACT_ID_ <> #{actId} GROUP BY t.act_id_";
//		List<ActivityInstance> parallelGatewaies = runtimeService.createNativeActivityInstanceQuery().sql(sql)
//				.parameter("processInstanceId", processInstanceId).parameter("actId", currActId).list();
		// 排序
//		if (CollectionUtils.isNotEmpty(parallelGatewaies)) {
//			activityInstances.addAll(parallelGatewaies);
//			activityInstances.sort(Comparator.comparing(ActivityInstance::getEndTime));
//		}
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
				node.setUserCode(activityInstance.getAssignee());
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
		//查询当前节点和当前的并行节点，剔除
		List<String> nodeList = flowableExtensionTaskDao.getParallelNodesByProcessInstanceIdAndTaskId(processInstanceId,taskId);
		for (Iterator<FlowNodeVo> itA = datas.iterator(); itA.hasNext();)
		{
			FlowNodeVo temp = itA.next();
			for (int i = 0; i < nodeList.size(); i++)
			{
				if (temp.getNodeId().equals(nodeList.get(i)))
				{
					itA.remove();
				}
			}
		}
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

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public Result<String> addMultiInstanceExecution(AddSignTaskVo form) {
		TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(form.getTaskId()).processInstanceId(form.getProcessInstanceId()).singleResult();
		List<String> signPersoneds = form.getSignPersoneds();
		if (task == null) {
			return  Result.failed("不存在任务实例，请确认!");
		}else{

			Map<String, UserTask> allUserTaskMap = getAllUserTaskMap(task.getProcessDefinitionId());
			UserTask userTaskModel = allUserTaskMap.get(task.getTaskDefinitionKey());
			if (!userTaskModel.hasMultiInstanceLoopCharacteristics()) {
				return   Result.failed("the task(id=" + form.getTaskId() + ") not a MultiInstance Task");
			}
			ArrayList<String> collectionValueList = new ArrayList<>(4);
			// 重新设置多实例人员集合变量的列表值
			Map<String, Object> executionVariables = new HashMap<>(4);
			String collectionKey = StrUtil.replaceChars(
					StrUtil.replaceChars(
							StrUtil.replaceChars(
									userTaskModel.getLoopCharacteristics().getInputDataItem(),
									"$", ""),
							"{", ""),
					"}", "");

			Object collectionValue = runtimeService.getVariable(task.getProcessInstanceId(), collectionKey);
			if (ObjectUtil.isNotEmpty(collectionValue) && collectionValue instanceof List) {
				collectionValueList = new ArrayList<>((List<String>) collectionValue);
				for(String assignee : signPersoneds){
					if (!collectionValueList.contains(assignee)) {
						collectionValueList.add(assignee);
						executionVariables.put(collectionKey, collectionValueList);
						runtimeService.setVariables(task.getProcessInstanceId(), executionVariables);
					}
				}
			}

			for(String assignee : signPersoneds){
				// 设置局部变量, 并行实例必须采用该变量设置
				executionVariables.clear();
				executionVariables.put(userTaskModel.getLoopCharacteristics().getElementVariable(), assignee);
				//添加多实例
				runtimeService.addMultiInstanceExecution(task.getTaskDefinitionKey(), task.getProcessInstanceId(), executionVariables);

				flowableExtensionTaskService.saveExtensionTask(task.getProcessInstanceId(), task.getTaskDefinitionKey(), null,false);
			}


			// 并行多实例 Flowabel6.4.2 BUG(加签后 nrOfActiveInstances 没有变化) ，
			if (!userTaskModel.getLoopCharacteristics().isSequential()) {
				// 部分控制变量需要修改值
				Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
				executionVariables.clear();
				executionVariables.put(NUMBER_OF_ACTIVE_INSTANCES, collectionValueList.size());
				runtimeService.setVariables(execution.getParentId(), executionVariables);
			}
			return Result.sucess("加签成功");
		}
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
			TaskEntity taskEntity = (TaskEntity) taskService.createTaskQuery().taskId(params.getTaskId()).processInstanceId(params.getProcessInstanceId())
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
				//是否审批驳回的时候驳回到原有的审批人
				boolean hasApproveKey  = "0".equals(params.getVariables().get(approveKey)==null?"":params.getVariables().get(approveKey).toString());
				flowableExtensionTaskService.saveExtensionTask(params.getProcessInstanceId(),taskEntity.getTaskDefinitionKey(),params.getBusinessInfo(),hasApproveKey);
				//更新当前节点的实际审批人
				//flowableExtensionTaskService.updateAssigneeByProcessInstanceIdAndTaskID(params.getProcessInstanceId(),params.getTaskId(),params.getUserCode(),JSONUtils.toJSONString(params.getVariables()));
				String type = params.getType() == null ? CommentTypeEnum.SP.toString() : params.getType();
				// 5.生成审批意见
				this.addComment(taskId, params.getUserCode(), params.getProcessInstanceId(), type, params.getMessage());
				//修改variables


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
	public PageModel<NoticeTask> getNoticeTasks(NoticeTaskQuery params, Query query) {
		return noticeTaskService.getNoticeTasks(params, query);
	}

	@Override
	public PageModel<TaskVo> getAllTasks(TaskQueryVo params, Query query)
	{
		PageHelper.startPage(query.getPageNum(), query.getPageSize());
		Page<TaskVo> allTasks = flowableTaskDao.getAllTasks(params);
		return new PageModel<>(allTasks);
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
	public Result<List<FlowElementVo>> getProcessNodeList(String modelkey) {
		Result<List<FlowElementVo>> result =  new Result<List<FlowElementVo>>();
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

	/**
	 * 递归加签（并联审批）的父任务
	 * @param taskEntity
	 * @return
	 */
	private  String  getParentTaskId(TaskEntity taskEntity){
		String parentTaskId = taskEntity.getParentTaskId();
		if(StrUtil.isNotEmpty(parentTaskId)){
			TaskEntity result = (TaskEntity) taskService.createTaskQuery().taskId(parentTaskId).singleResult();
			getParentTaskId(result);
		}
		return parentTaskId;
	}

	/**
	 * B为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
	 *
	 * @param forkGatewayB    B所在的并行网关
	 * @param task            当然任务
	 * @param targetNodesSize B的数量
	 */
	private void dealParallelGatewayFinishLog(
			ParallelGatwayDTO forkGatewayB, Task task, int targetNodesSize) {
		dealParallelGatewayFinishLog(forkGatewayB, task, targetNodesSize, null);
	}

	/**
	 * B为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
	 *
	 * @param forkGatewayB             B所在的并行网关
	 * @param task                     当然任务
	 * @param targetNodesSize          B的数量
	 * @param untilParentForkGatewayId b的最上层父并行网关
	 */
	private void dealParallelGatewayFinishLog(
			ParallelGatwayDTO forkGatewayB, Task task, int targetNodesSize, String untilParentForkGatewayId) {
		int reduceForkSize = forkGatewayB.getForkSize() - targetNodesSize;
		if (reduceForkSize < 0) {
			throw new SipsdBootException("目标节点数量不能大于并行网关的总分支数量");
		}
		if (reduceForkSize > 0) {
			for (int i = 0; i < reduceForkSize; i++) {
				logger.debug("插入目标节点的合并网关 {} 一条分支线上的完成记录", forkGatewayB.getJoinId());
				insertExecution(forkGatewayB.getJoinId(), task.getProcessInstanceId(), task.getProcessDefinitionId(), task.getTenantId());
			}
		}

		// 如果该网关是子网关则，还需要处理父网关信息
		ParallelGatwayDTO parentParallelGatwayDTO = forkGatewayB.getParentParallelGatwayDTO();
		while (parentParallelGatwayDTO != null) {
			if (org.apache.commons.lang3.StringUtils.isNotBlank(untilParentForkGatewayId)
					&& parentParallelGatwayDTO.getForkId().equals(untilParentForkGatewayId)) {
				break;
			}

			for (int i = 0; i < parentParallelGatwayDTO.getForkSize() - 1; i++) {
				logger.debug("插入目标节点的父合并网关 {} 一条分支线上的完成记录 ", parentParallelGatwayDTO.getJoinId());
				insertExecution(parentParallelGatwayDTO.getJoinId(), task.getProcessInstanceId(), task.getProcessDefinitionId(), task.getTenantId());
			}
			parentParallelGatwayDTO = parentParallelGatwayDTO.getParentParallelGatwayDTO();
		}
	}
	protected void insertExecution(String gatewayId, String processInstanceId, String processDefinitionId, String tenantId) {
		ExecutionEntityManager executionEntityManager = ((ProcessEngineConfigurationImpl) processEngineConfiguration).getExecutionEntityManager();
		ExecutionEntity executionEntity = executionEntityManager.create();
		IdGenerator idGenerator = processEngineConfiguration.getIdGenerator();
		executionEntity.setId(idGenerator.getNextId());
		executionEntity.setRevision(0);
		executionEntity.setProcessInstanceId(processInstanceId);

		executionEntity.setParentId(processInstanceId);
		executionEntity.setProcessDefinitionId(processDefinitionId);

		executionEntity.setRootProcessInstanceId(processInstanceId);
		((ExecutionEntityImpl) executionEntity).setActivityId(gatewayId);
		executionEntity.setActive(false);

		executionEntity.setSuspensionState(1);
		executionEntity.setTenantId(tenantId);

		executionEntity.setStartTime(new Date());
		((ExecutionEntityImpl) executionEntity).setCountEnabled(true);

		managementService.executeCommand(new SaveExecutionCmd(executionEntity));
		//executionEntityManager.insert(executionEntity);
	}

	private Map<String, UserTask> getAllUserTaskMap(String processDefinitionId) {
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
		Process process = bpmnModel.getProcesses().get(0);
		return process.findFlowElementsOfType(UserTask.class)
				.stream().collect(Collectors.toMap(UserTask::getId, a -> a, (k1, k2) -> k1));
	}

	/**
	 * （1）如果B有多个节点
	 * 必须为同一个并行网关内的任务节点（网关开始、合并节点必须一致）
	 * 必须不是同一条流程线上的任务节点
	 *
	 * @param targetNodes 要跳转到的目标节点集合
	 */
	private void checkjumpTargetNodes(List<BpmTaskModelEntity> targetNodes) {
		if (targetNodes.size() < 2) {
			return;
		}
		//判断节点
		for(int i=0;i<targetNodes.size()-1;i++){
			BpmTaskModelEntity item1 = targetNodes.get(i);
			BpmTaskModelEntity item2 = targetNodes.get(i + 1);
			if (!item1.getInParallelGateway() || !item2.getInParallelGateway()) {
				throw new SipsdBootException("目标节点非并行网关中的节点");
			}
			if(!StrUtil.equals(item1.getForkParallelGatewayId(),item2.getForkParallelGatewayId()) ||
					!StrUtil.equals(item1.getJoinParallelGatewayId(),item2.getJoinParallelGatewayId() )){
				throw new SipsdBootException("目标节点不是同一个并行网关");
			}
			if(StrUtil.equals(item1.getParallelGatewayForkRef(),item2.getParallelGatewayForkRef())){
				throw new SipsdBootException("目标节点不能在同一条业务线上");
			}
		}

	}

	private void gatewayJump(UserTaskModelDTO userTaskModelsDTO, List<BpmTaskModelEntity> targetNodes, BpmTaskModelEntity currentNode, Task task, List<String> distList) {
		// A是并行分支上的任务节点，获得跳转类别
		GatewayJumpTypeEnum gatewayJumpFlag = getGatewayJumpFlag(
				currentNode, targetNodes.get(0), userTaskModelsDTO.getAllForkGatewayMap());

		if (gatewayJumpFlag.equals(GatewayJumpTypeEnum.TO_IN_CHILD)) {
			String forkParallelGatwayId = targetNodes.get(0).getForkParallelGatewayId();
			ParallelGatwayDTO forkGatewayB = userTaskModelsDTO.getAllForkGatewayMap().get(forkParallelGatwayId);
			// B为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
			dealParallelGatewayFinishLog(forkGatewayB, task, targetNodes.size(), currentNode.getForkParallelGatewayId());
			runtimeService.createChangeActivityStateBuilder().processInstanceId(task.getProcessInstanceId())
					.moveSingleActivityIdToActivityIds(task.getTaskDefinitionKey(), distList)
					.changeState();
		} else if (gatewayJumpFlag.equals(GatewayJumpTypeEnum.TO_OUT_NO_PARALLEL)) {
			// B非并行网关上的节点,则只可能有一个, 因此只能全部终结现有的任务，跳转到B点
			List<String> executionIds = new ArrayList<>();
			List<Execution> executions = runtimeService.createExecutionQuery()
					.parentId(task.getProcessInstanceId()).list();
			executions.forEach(execution -> executionIds.add(execution.getId()));
			this.moveExecutionsToSingleActivityId(executionIds,distList.get(0));

		} else if (gatewayJumpFlag.equals(GatewayJumpTypeEnum.TO_OUT_PARENT)) {
			//获取本 targetTaskDefineKey 所在的并行网关中，下游所有用户任务，查找直到targetTaskDefineKey的合并网关位置
			Set<String> taskKeys = new HashSet<>(4);
			targetNodes.forEach(item -> taskKeys.addAll(getMyNextFlowForkGatewayTaskKeys(item, userTaskModelsDTO.getProcess())));
			// 跳转
			moveActivityIdToOtherParallelGateway(taskKeys, task, distList);
		} else if (gatewayJumpFlag.equals(GatewayJumpTypeEnum.TO_OUT_OTHER_PARALLEL)) {
			// 获取本并行网关上的全部任务
			Set<String> taskKeys = getMyOtherForkGatewayTaskKeys(
					currentNode, userTaskModelsDTO.getAllForkGatewayMap(),task);

			String forkParallelGatwayId = targetNodes.get(0).getForkParallelGatewayId();
			ParallelGatwayDTO forkGatewayB = userTaskModelsDTO.getAllForkGatewayMap().get(forkParallelGatwayId);
			// B为并行网关上节点，需要创建其B所在并行网关内其他任务节点已完成日志
			dealParallelGatewayFinishLog(forkGatewayB, task, targetNodes.size());
			// 跳转
			moveActivityIdsToOtherParallelGateway(taskKeys, task, distList);
		} else {
			throw new SipsdBootException("暂不支持这样的流程跳转");
		}
	}

	/**
	 * 如果A是并行分支上的任务节点，获得跳转类别
	 *
	 * @param currentNode       当前节点
	 * @param targetNode        其中一个目标节点
	 * @param allForkGatewayMap 所有的（不分父子网关）并行网关
	 * @return 一共4中情况
	 * 1 从外向里面跳转（父并网关 》子并网关）
	 * 2 从里向外面跳转 B为非并行行网关上的节点
	 * 3 从里向外面跳转 B为父并行网关上的节点
	 * 4 从里向外面跳转 B为其他独立并行网关上的节点
	 */
	private GatewayJumpTypeEnum getGatewayJumpFlag(
			BpmTaskModelEntity currentNode,
			BpmTaskModelEntity targetNode,
			Map<String, ParallelGatwayDTO> allForkGatewayMap) {

		if (!targetNode.getInParallelGateway()) {
			return GatewayJumpTypeEnum.TO_OUT_NO_PARALLEL;
		} else {
			if (currentNode.getChildForkParallelGatewayIds().contains(targetNode.getForkParallelGatewayId())) {
				return GatewayJumpTypeEnum.TO_IN_CHILD;
			} else if (targetNode.getChildForkParallelGatewayIds().contains(currentNode.getForkParallelGatewayId())) {
				return GatewayJumpTypeEnum.TO_OUT_PARENT;
			} else if (targetNode.getForkParallelGatewayId().equals(currentNode.getForkParallelGatewayId())) {
				logger.info("相同网关中的节点不可进行跳转操作，currentNode={} targetNode={}", currentNode.getTaskDefKey(), targetNode.getTaskDefKey());
				return GatewayJumpTypeEnum.NO_SUPPORT_JUMP;
			} else {
				// B为其他并行网关上的节点
				ParallelGatwayDTO currentTopPgw = allForkGatewayMap.get(currentNode.getForkParallelGatewayId()).getParentParallelGatwayDTO();
				while (currentTopPgw != null && currentTopPgw.getParentParallelGatwayDTO() != null) {
					currentTopPgw = currentTopPgw.getParentParallelGatwayDTO();
				}
				ParallelGatwayDTO targetTopPgw = allForkGatewayMap.get(targetNode.getForkParallelGatewayId()).getParentParallelGatwayDTO();
				while ( targetTopPgw != null && targetTopPgw.getParentParallelGatwayDTO() != null) {
					targetTopPgw = targetTopPgw.getParentParallelGatwayDTO();
				}

				if ( currentTopPgw != null &&  targetTopPgw!= null && currentTopPgw.getForkId().equals(targetTopPgw.getForkId())) {
					logger.info("相同顶级父并行网关下的节点不可进行跳转操作，currentNode={} targetNode={}", currentNode.getTaskDefKey(), targetNode.getTaskDefKey());
					return GatewayJumpTypeEnum.NO_SUPPORT_JUMP;
				}

				return GatewayJumpTypeEnum.TO_OUT_OTHER_PARALLEL;
			}
		}
	}
	/**
	 * 获取本 targetTaskDefineKey 所在的并行网关中，下游所有用户任务，查找直到targetTaskDefineKey的合并网关位置
	 *
	 * @param targetTask 要查找的节点id
	 * @param process    所在Process
	 * @return Set<String>
	 */
	private Set<String> getMyNextFlowForkGatewayTaskKeys(BpmTaskModelEntity targetTask, Process process) {
		Set<String> taskKeys = new HashSet<>(4);
		FlowElement flowElement = process.getFlowElementMap().get(targetTask.getTaskDefKey());
		loopOutcomingFlows(((FlowNode) flowElement).getOutgoingFlows(),
				taskKeys, process.getFlowElementMap(), targetTask.getJoinParallelGatewayId());
		return taskKeys;
	}
	/**
	 * 递归获取某个节点后面的所有相关节点
	 *
	 * @param outcomingFlows 相邻来源节点引用集合
	 * @param taskKeys       最后输出的 List结果集
	 * @param flowElementMap Process # getFlowElementMap()
	 */
	private void loopOutcomingFlows(
			List<SequenceFlow> outcomingFlows,
			Set<String> taskKeys,
			Map<String, FlowElement> flowElementMap,
			String limitFlowId) {
		if (org.springframework.util.CollectionUtils.isEmpty(outcomingFlows)) {
			return;
		}

		for (SequenceFlow item : outcomingFlows) {
			if (org.apache.commons.lang3.StringUtils.isNotBlank(limitFlowId) && item.getId().equals(limitFlowId)) {
				break;
			}
			FlowElement flowElement = flowElementMap.get(item.getTargetRef());
			if (flowElement instanceof FlowNode) {
				if (flowElement instanceof UserTask) {
					UserTask task = (UserTask) flowElement;
					taskKeys.add(task.getId());
				}

				loopOutcomingFlows(
						((FlowNode) flowElement).getOutgoingFlows(),
						taskKeys,
						flowElementMap,
						limitFlowId);
			}
		}
	}
	/**
	 * 跳转其他并行网关上的用户任务.
	 *
	 * @param otherUserTaskKeys   本并行网关上的其他在运行的全部任务
	 * @param currentTask         当前并行网关上用户任务
	 * @param targetTaskDefineKes 跳转的目标节点
	 */
	private void moveActivityIdToOtherParallelGateway(
			Set<String> otherUserTaskKeys,
			Task currentTask,
			List<String> targetTaskDefineKes) {
		if (targetTaskDefineKes.size() == 1) {
			otherUserTaskKeys.add(currentTask.getTaskDefinitionKey());
			runtimeService.createChangeActivityStateBuilder().processInstanceId(currentTask.getProcessInstanceId())
					.moveActivityIdsToSingleActivityId(new ArrayList<>(otherUserTaskKeys), targetTaskDefineKes.get(0))
					.changeState();
		} else {
			// 删除本并行网关上的其他在运行的全部任务
			deleteMyRelateTask(otherUserTaskKeys, currentTask);
			runtimeService.createChangeActivityStateBuilder().processInstanceId(currentTask.getProcessInstanceId())
					.moveSingleActivityIdToActivityIds(currentTask.getTaskDefinitionKey(), targetTaskDefineKes)
					.changeState();
		}
	}

	/**
	 * 并行网关内节点跳转其他并行网关上的用户任务.
	 *
	 * @param otherUserTaskKeys   本并行网关上的其他在运行的全部任务
	 * @param currentTask         当前并行网关上用户任务
	 * @param targetTaskDefineKes 跳转的目标节点
	 */
	private void moveActivityIdsToOtherParallelGateway(
			Set<String> otherUserTaskKeys,
			Task currentTask,
			List<String> targetTaskDefineKes) {
		// 删除本并行网关上的其他在运行的全部任务
		deleteMyRelateTask(otherUserTaskKeys, currentTask);
		runtimeService.createChangeActivityStateBuilder().processInstanceId(currentTask.getProcessInstanceId())
				.moveSingleActivityIdToActivityIds(currentTask.getTaskDefinitionKey(), targetTaskDefineKes)
				.changeState();
	}
	/**
	 * 并行网关跳转时，本节点之外的并行网关上的节点任务需要删除
	 *
	 * @param deleteTaskKeys 本并行网关上的其他任务节点key
	 * @param currentTask    当前要准备跳转的任务
	 */
	private void deleteMyRelateTask(Set<String> deleteTaskKeys, Task currentTask) {
		deleteTaskKeys.remove(currentTask.getTaskDefinitionKey());
		TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(currentTask.getProcessInstanceId());
		for(String deleteDefKey : deleteTaskKeys){
			List<Task> runningTasks = taskQuery.taskDefinitionKey(deleteDefKey).list();
			if (runningTasks != null) {
				runningTasks.forEach(item -> deleteExecutionByTaskId(item.getId()));
			}
		}

	}
	/**
	 * 场景一、由于多实例（串行）减签操作有异常数据产生，所以该方法用于删除部分异常数据，flowable BUG
	 * 场景二、并行网关中的任务跳转到其他并行网关中，需要先删除一些本网关其他任务，然后跳转
	 *
	 * @param taskId 并行网关上的任务id 或 多实例任务id
	 */
	protected void deleteExecutionByTaskId(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			return;
		}
		String executionId = task.getExecutionId();
		logger.info("删除executionId=" + executionId);
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_hi_identitylink  WHERE TASK_ID_ = '" + taskId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_ru_identitylink  WHERE TASK_ID_ = '" + taskId + "'").list();

		deleteExecutionById(executionId);

	}
	/**
	 * 场景一、由于多实例（串行）减签操作有异常数据产生，所以该方法用于删除部分异常数据，flowable BUG
	 * 场景二、并行网关中的任务跳转到其他并行网关中，需要先删除一些本网关其他任务，然后跳转
	 *
	 * @param executionId 并行网关上的任务对应的executionId
	 */
	protected void deleteExecutionById(String executionId) {
		logger.info("删除executionId=" + executionId);
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_hi_actinst  WHERE EXECUTION_ID_ = '" + executionId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_hi_taskinst  WHERE EXECUTION_ID_ = '" + executionId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_hi_varinst  WHERE EXECUTION_ID_ = '" + executionId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_ru_suspended_job  WHERE EXECUTION_ID_ = '" + executionId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_ru_deadletter_job  WHERE EXECUTION_ID_ = '" + executionId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_ru_timer_job  WHERE EXECUTION_ID_ = '" + executionId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_ru_actinst  WHERE EXECUTION_ID_ = '" + executionId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_ru_task  WHERE EXECUTION_ID_ = '" + executionId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_ru_variable  WHERE EXECUTION_ID_ = '" + executionId + "'").list();
		runtimeService.createNativeExecutionQuery().sql(
				"DELETE FROM act_ru_execution  WHERE ID_ = '" + executionId + "'").list();
	}

	/**
	 * 本节点所在的全部相关网(包括父子并行网关)内的任务定义的key，除了本节点之外(正在运行当中)
	 *
	 * @param currentNode       ignore
	 * @param allForkGatewayMap ignore
	 * @return Set<String>
	 */
	private Set<String> getMyOtherForkGatewayTaskKeys(BpmTaskModelEntity currentNode, Map<String, ParallelGatwayDTO> allForkGatewayMap,Task task) {
		Set<String> otherTaskKeys = new HashSet<>(4);

		if (!currentNode.getInParallelGateway()) {
			return otherTaskKeys;
		}

		Set<String> taskKeys = new HashSet<>(4);
		// 所有子网关内的用户keys
		currentNode.getChildForkParallelGatewayIds().forEach(item -> taskKeys.addAll(allForkGatewayMap.get(item).getUserTaskModels().keySet()));
		// 所有并行网关内的用户keys
		ParallelGatwayDTO parallelGatwayDTO = allForkGatewayMap.get(currentNode.getForkParallelGatewayId());
		LinkedHashMap<String, BpmTaskModelEntity> userTaskModels = parallelGatwayDTO.getUserTaskModels();
		taskKeys.addAll(userTaskModels.keySet());
		taskKeys.remove(currentNode.getTaskDefKey());

		for(String defKey : taskKeys){
			List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).taskDefinitionKey(defKey).active().list();
			if(CollUtil.isNotEmpty(taskList)){
				otherTaskKeys.add(defKey);
			}
		}
		return otherTaskKeys;
	}

	@Override
	public String getPreTaskAssignee(String processInstanceId, String taskDefKey)
	{
		return flowableTaskDao.getPreTaskAssignee(processInstanceId,taskDefKey);
	}

	@Override
	public void updateAssigneeByProcessInstanceIdAndTaskId(String assignee, String processInstanceId, String taskId)
	{
		 flowableTaskDao.updateAssigneeByProcessInstanceIdAndTaskId(assignee,processInstanceId,taskId);
	}
}
