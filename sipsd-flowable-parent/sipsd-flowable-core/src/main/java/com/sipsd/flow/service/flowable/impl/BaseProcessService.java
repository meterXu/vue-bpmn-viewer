package com.sipsd.flow.service.flowable.impl;

import com.sipsd.flow.vo.flowable.AssigneeVo;
import com.sipsd.flow.bean.FlowElementVo;
import com.sipsd.flow.common.UUIDGenerator;
import com.sipsd.flow.dao.flowable.IHisFlowableActinstDao;
import com.sipsd.flow.dao.flowable.IRunFlowableActinstDao;
import com.sipsd.flow.service.flowable.IFlowableCommentService;
import com.sipsd.flow.service.flowable.IFlowableUserService;
import com.sipsd.flow.vo.flowable.ret.CommentVo;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.*;
import org.flowable.engine.impl.persistence.entity.ActivityInstanceEntity;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author : gaoqiang
 * @title: : BaseProcessService
 * @projectName : flowable
 * @description: 基本的流程service
 * @date : 2019/12/220:50
 */
public abstract class BaseProcessService {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected ManagementService managementService;
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected IdentityService identityService;
    @Autowired
    protected IFlowableCommentService flowableCommentService;
    @Autowired
    private IRunFlowableActinstDao runFlowableActinstDao;
    @Autowired
    private IHisFlowableActinstDao hisFlowableActinstDao;
    @Autowired
    private IFlowableUserService flowableUserService;

	@Autowired
	private ModelService modelService;
	@Autowired
	private ModelRepository modelRepository;
	
	
    @Autowired
    protected FormService formService;
    /**
     * 添加审批意见
     *
     * @param taskId            任务id
     * @param userCode          处理人工号
     * @param processInstanceId 流程实例id
     * @param type              审批类型
     * @param message           审批意见
     */
    protected void addComment(String taskId, String userCode, String processInstanceId, String type, String message) {
        //1.添加备注
        CommentVo commentVo = new CommentVo(taskId, userCode, processInstanceId, type, message);
        flowableCommentService.addComment(commentVo);
        //TODO 2.修改扩展的流程实例表的状态以备查询待办的时候能带出来状态
        //TODO 3.发送mongodb的数据到消息队列里面
    }

    /**
     * 添加审批意见
     *
     * @param userCode          处理人工号
     * @param processInstanceId 流程实例id
     * @param type              审批类型
     * @param message           审批意见
     */
    protected void addComment(String userCode, String processInstanceId, String type, String message) {
        this.addComment(null, userCode, processInstanceId, type, message);
    }

    /**
     * 删除跳转的历史节点信息
     *
     * @param disActivityId     跳转的节点id
     * @param processInstanceId 流程实例id
     */
    protected void deleteActivity(String disActivityId, String processInstanceId) {
        String tableName = managementService.getTableName(ActivityInstanceEntity.class);
        String sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and t.ACT_ID_ = #{disActivityId} " +
                " order by t.END_TIME_ ASC";
        List<ActivityInstance> disActivities = runtimeService.createNativeActivityInstanceQuery().sql(sql)
                .parameter("processInstanceId", processInstanceId)
                .parameter("disActivityId", disActivityId).list();
        //删除运行时和历史节点信息
        if (CollectionUtils.isNotEmpty(disActivities)) {
            ActivityInstance activityInstance = disActivities.get(0);
            sql = "select t.* from " + tableName + " t where t.PROC_INST_ID_=#{processInstanceId} and (t.END_TIME_ >= #{endTime} or t.END_TIME_ is null)";
            List<ActivityInstance> datas = runtimeService.createNativeActivityInstanceQuery().sql(sql).parameter("processInstanceId", processInstanceId)
                    .parameter("endTime", activityInstance.getEndTime()).list();
            List<String> runActivityIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(datas)) {
                datas.forEach(ai -> runActivityIds.add(ai.getId()));
                runFlowableActinstDao.deleteRunActinstsByIds(runActivityIds);
                hisFlowableActinstDao.deleteHisActinstsByIds(runActivityIds);
            }
        }
    }

    /**
     * 执行跳转
     */
    protected void moveExecutionsToSingleActivityId(List<String> executionIds, String activityId) {
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionsToSingleActivityId(executionIds, activityId)
                .changeState();
    }

    protected TaskEntity createSubTask(TaskEntity ptask, String assignee) {
        return this.createSubTask(ptask, ptask.getId(), assignee);
    }

    /**
     * 创建子任务
     *
     * @param ptask    创建子任务
     * @param assignee 子任务的执行人
     * @return
     */
    protected TaskEntity createSubTask(TaskEntity ptask, String ptaskId, String assignee) {
        TaskEntity task = null;
        if (ptask != null) {
            //1.生成子任务
            task = (TaskEntity) taskService.newTask(UUIDGenerator.generate());
            task.setCategory(ptask.getCategory());
            task.setDescription(ptask.getDescription());
            task.setTenantId(ptask.getTenantId());
            task.setAssignee(assignee);
            task.setName(ptask.getName());
            task.setParentTaskId(ptaskId);
            task.setProcessDefinitionId(ptask.getProcessDefinitionId());
            task.setProcessInstanceId(ptask.getProcessInstanceId());
            task.setTaskDefinitionKey(ptask.getTaskDefinitionKey());
            task.setTaskDefinitionId(ptask.getTaskDefinitionId());
            task.setPriority(ptask.getPriority());
            task.setCreateTime(new Date());
            taskService.saveTask(task);
        }
        return task;
    }

	public List<FlowElementVo> getProcessNodes(String modelkey) {
		List<org.flowable.ui.modeler.domain.Model> mdels = modelRepository.findByKeyAndType(modelkey,0);
		org.flowable.ui.modeler.domain.Model modelData = modelService.getModel(mdels.get(0).getId());
		// 获取模型
		BpmnModel model = modelService.getBpmnModel(modelData);
		// Process对象封装了全部的节点、连线、以及关口等信息。拿到这个对象就能够为所欲为了。
		org.flowable.bpmn.model.Process process = model.getProcesses().get(0);
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
		return flowNodeVos;
	}

}
