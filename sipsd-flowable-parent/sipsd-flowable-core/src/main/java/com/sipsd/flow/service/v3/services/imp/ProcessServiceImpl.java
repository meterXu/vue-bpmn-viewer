package com.sipsd.flow.service.v3.services.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sipsd.flow.bean.Process;
import com.sipsd.flow.bean.*;
import com.sipsd.flow.bean.enums.AuditStatus;
import com.sipsd.flow.bean.enums.OperateType;
import com.sipsd.flow.service.v3.services.ProcessService;
import com.sipsd.flow.util.FlowUtil;
import com.sipsd.flow.util.ProcessDefinitionUtils;
import com.sipsd.flow.vo.flowable.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.history.NativeHistoricActivityInstanceQuery;
import org.flowable.engine.impl.history.async.HistoryJsonConstants;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcessServiceImpl implements ProcessService
{

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private BPMDeploymentServiceImp bpmDeploymentServiceImp;
    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private ProcessDefinitionUtils processDefinitionUtils;

    private static final String USER_INITIATOR_STR="$INITIATOR";

    @Override
    @Transactional
    public Process start(User user, ProcessParam param) {
        Process res=null;
        if (null == param){
            return res;
        }
        Map<String, Object> vars = FlowUtil.initStartMap(param);
        String executor=user.getId();
        //本处可以从项类型查询出初审人员的要求，然后本处设置，实例中用创建者来代替。
        if(null!=param&&param.getParams()!=null&&param.getParams().containsKey("executor")){
            executor=param.getParams().get("executor")==null? user.getId():param.getParams().get("executor").toString();

        }
        vars.put("executor",executor);
        if(!CollectionUtils.isEmpty(param.getParams())){
            vars.putAll(param.getParams());
        }

        log.info("当前用户id: {} ", Authentication.getAuthenticatedUserId());
        // 设置发起人
        identityService.setAuthenticatedUserId(user.getId());

        Authentication.setAuthenticatedUserId(user.getId());
        // 另外生成编号，这里就使用uuid了 这个字段就是BUSINESS_KEY_
        String no = UUID.randomUUID().toString().replace("-", "");
        log.info("当前用户id: {} ", Authentication.getAuthenticatedUserId());
        String processKey=param.getProcessId();
        if (StringUtils.isEmpty(processKey)){
            return res;
        }
        String businessKey=processKey+":"+no;
        log.info("businessKey:="+businessKey);
        Map<String,Object> deployMap=null;
        Deployment deployment=null;
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().processDefinitionKey(processKey).latestVersion().singleResult();
        if(processDefinition==null){
            try {
                deployMap=bpmDeploymentServiceImp.deployWorkFloyByModeKey(processKey);
            } catch (Exception e) {
                log.debug("start Exception："+e);
                e.printStackTrace();
            }
            if(!CollectionUtils.isEmpty(deployMap)){
                deployment=(Deployment) deployMap.get("deployment");
            }

        }else{
            String deploymentId=processDefinition.getDeploymentId();
            deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();

        }
        if(deployment==null){
            try {
				deployMap=bpmDeploymentServiceImp.deployWorkFloyByModeKey(processKey);
			} catch (Exception e) {
				log.debug("start Exception："+e);
				e.printStackTrace();
			}
            if(!CollectionUtils.isEmpty(deployMap)){
                deployment=(Deployment) deployMap.get("deployment");
            }
        }
        res=new Process();

        ProcessDefinition createProcessDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).latestVersion().singleResult();
        ProcessInstance startProcessInstanceById = runtimeService.startProcessInstanceById(createProcessDefinition.getId(), businessKey, vars);
        log.info("startProcessID:="+startProcessInstanceById.getId());

        res.setProcessInstanceId(startProcessInstanceById.getId());
        res.setNo(businessKey);
        res.setStartTime(startProcessInstanceById.getStartTime());
        res.setFromUserName(user.getId());
        res.setToUserName(executor);
        res.setBusinessID(param.getBusinessID());
        res.setBusinessType(param.getBusinessType());
        Authentication.setAuthenticatedUserId(null);

        List<Task> activitiTasks=taskService.createTaskQuery().processInstanceId(startProcessInstanceById.getId()).active().list();
//        res.setTaskId(JSON.toJSONString(activitiTasks));
        if(!CollectionUtils.isEmpty(activitiTasks)){
            if(activitiTasks.size()>1){
                JSONArray taskSJsonArray=new JSONArray();
                for(Task task:activitiTasks){
                    JSONObject taskjson=new JSONObject();
                    taskjson.put("taskId",task.getId());
                    taskjson.put("taskName",task.getName());
                    taskjson.put("assignee",task.getAssignee());
                    taskSJsonArray.add(taskjson);
                }

                res.setTaskArray(taskSJsonArray.toJSONString());
            }else{
                res.withTask(activitiTasks.get(0));
            }
        }

        //TODO 带附件的情况 下一步考虑
//        runtimeService.addEventListener(new TaskBeforeListener());
        // 文件材料
//         this.createAttachment(null, startProcessInstanceById.getId(), param.getFiles());
        return res;
    }
    /**
     *  生成流程图列  png图形
     *@param  processId 流程实例ID
     */
    @Override
    public InputStream genProcessDiagram(String processId) throws Exception{
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        //流程走完的不显示图
        if (pi == null) {
            log.debug("processId:["+processId+ "]流程不存在");
            throw new RuntimeException("processId:["+processId+ "]流程不存在");
        }
        return genProcessDiagram(pi);
    }

    /**
     * 查询任务信息
     * @param taskId
     * @return
     * @throws Exception
     */

    @Override
    public Task getRunTimeTaskByID(String taskId){
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return task;
    }


    /**
     * 更加实例对象生成待办图片
     * @param pi
     * @return
     * @throws Exception
     */
    private InputStream genProcessDiagram(ProcessInstance pi) throws Exception{
        InputStream res=null;
        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = pi.getId();
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(InstanceId)
                .list();

        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            //排除重复添加
            if(activityIds.containsAll(ids)){
                continue;
            }
            activityIds.addAll(ids);
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        res = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0, false);
        return res;
    }



    private void createAttachment(String taskId, String processInstanceId, List<BaseBean> files) {
        if (files != null && !files.isEmpty()) {
            // 上传附件，可以直接上传字节流（不建议）
            files.forEach(file ->
                    taskService.createAttachment("application/octet-stream", taskId,
                            processInstanceId, file.getName(), null, file.getId()));
        }
    }

    @Override
    public boolean isAssigneeOrCandidate(User user, String taskId) {

        TaskQuery query = taskService.createTaskQuery()
                .or()
                .taskAssignee(user.getId())
                .taskCandidateUser(user.getId());
        if (StringUtils.isNotEmpty(user.getGroup())) {
            query.taskCandidateGroup(user.getGroup());

        }
        query.endOr();
        long count = query.count();
        return count > 0;
    }

    @Override
    @Transactional
    public void beforeAgreeOrReject(String taskId, User user, OperateType operateType, ReasonParam reason) {
        // 组成员操作后方便查询
        taskService.setAssignee(taskId, user.getId());
        if (StringUtils.isNotEmpty(reason.getText())) {
            // 审批意见
            taskService.addComment(taskId, null, null, reason.getText());
        }
        this.createAttachment(taskId, null, reason.getFiles());
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void agreeOrRejectOP(String taskId, User user, OperateType operateType, ReasonParam reason) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String proinstId=task.getProcessInstanceId();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("outcome", operateType);
        map.put("reasonText",reason.getText());
        if(!CollectionUtils.isEmpty(reason.getParams())){
            map.putAll(reason.getParams());
        }

        if (StringUtils.isNotEmpty(reason.getText())) {
            // 审批意见
            taskService.addComment(taskId, null, null, reason.getText());
        }
        taskService.complete(taskId, map);

        System.out.println("提交成功.流程信息为："+task);

    }



    private List<Process> convertProcessList(List<HistoricActivityInstance> activities) {
        return activities.stream().map(a -> {
            // 同上面的拿到这个任务的流程实例
            HistoricProcessInstance p = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(a.getProcessInstanceId())
                    .singleResult();
            // 因为任务已结束（我看到有提到删除任务TaskHelper#completeTask），所以只能从历史里获取
            Map<String, Object> params = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(a.getProcessInstanceId()).list()
                    // 拿到的是HistoricVariableInstance对象，需要转成原来存储的方式
                    .stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));

            return new Process(p).withActivity(a).withVariables(params);
        }).collect(Collectors.toList());
    }
    /**
     * 
     */
    @Override
    public List<Process> auditList(User user, String auditType, AuditStatus auditStatus, int pageNum, int pageSize) {
        // 如果不需要筛选自定义参数
        if (auditStatus == null && StringUtils.isEmpty(auditType)) {
            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                    // 我审批的
                    .taskAssignee(user.getId())
                    // 按照开始时间倒序
                    .orderByHistoricActivityInstanceStartTime().desc()
                    // 已结束的（其实就是判断有没有结束时间）
                    .finished()
                    // 分页
                    .listPage((pageNum - 1) * pageSize, pageSize);
            return convertProcessList(activities);
        }
        // 否则需要自定义sql
        // managementService.getTableName是用来获取表名的（加上上一篇提到的liquibase，估计flowable作者对数据表命名很纠结）
        // 这里从HistoricVariableInstance对应的表里找到自定义参数
        // 筛选对象类型不支持二进制，存储的时候尽量使用字符串、数字、布尔值、时间，用来比较的值也有很多限制，例如null不能用like比较。
        String sql = "SELECT DISTINCT RES.* " +
                "FROM " + managementService.getTableName(HistoricActivityInstance.class) + " RES " +
                "INNER JOIN " + managementService.getTableName(HistoricVariableInstance.class) + " var " +
                "ON var.PROC_INST_ID_ = res.PROC_INST_ID_  " +
                "WHERE RES.ASSIGNEE_ = #{assignee} " +
                "AND RES.TENANT_ID_ = #{tenantId} " +
                "AND RES.END_TIME_ IS NOT NULL ";

        List<VariableParam> keys = new ArrayList<>();
        if (auditStatus != null) {
            keys.add(new VariableParam("statusKey", FlowUtil.AUDIT_STATUS_KEY, "statusValue", auditStatus.toString()));
        }
        if (StringUtils.isNotEmpty(auditType)) {
            keys.add(new VariableParam("typeKey", FlowUtil.AUDIT_TYPE_KEY, "typeValue", auditType));
        }
        sql += VariableParam.concatVariableSql(" AND ", keys, " ORDER BY RES.START_TIME_ DESC");
        NativeHistoricActivityInstanceQuery query = historyService.createNativeHistoricActivityInstanceQuery().sql(sql)
                .parameter(HistoryJsonConstants.ASSIGNEE, user.getId());
        // 加入独特配方
        keys.forEach(v -> {
            query.parameter(v.getNameKey(), v.getName());
            query.parameter(v.getValueKey(), v.getValue());
        });
        List<HistoricActivityInstance> activities = query.listPage((pageNum - 1) * pageSize, pageSize);
        return convertProcessList(activities);
    }

    @Override
    public List<Process> waitAuditList(User user, String auditType, int pageNum, int pageSize) {
        TaskQuery query = taskService.createTaskQuery()
                // or() 和 endOr()就像是左括号和右括号，中间用or连接条件
                // 指定是我审批的任务或者所在的组别审批的任务
                // 实在太复杂的情况，建议不使用flowable的查询
                .or()
                .taskAssignee(user.getId())
                .taskCandidateUser(user.getId());

        if(StringUtils.isNotEmpty(user.getGroup())){
            query.taskCandidateGroup(user.getGroup());
        }


        query .endOr();
        // 查询自定义字段
        if (StringUtils.isNotEmpty(auditType)) {
            query.processVariableValueEquals(FlowUtil.AUDIT_TYPE_KEY, auditType);
        }
        // 根据创建时间倒序
        return query.orderByTaskCreateTime().desc()
                // 分页
                .listPage((pageNum - 1) * pageSize, pageSize)
                .stream().map(t -> {
                    // 拿到这个任务的流程实例，用于显示流程开始时间、结束时间、业务编号
                    HistoricProcessInstance p = historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(t.getProcessInstanceId())
                            .singleResult();
                    return new Process(p).withTask(t) // 拿到任务编号和任务名称
                            // 拿到创建时和中途加入的自定义参数
                            .withVariables(taskService.getVariables(t.getId()))
                            .withFiles(taskService.getProcessInstanceAttachments(p.getId()));
                }).collect(Collectors.toList());
    }

    private Process convertHostoryProcess(HistoricProcessInstance p) {
        // 不管流程是否结束，到历史里查，最方便
        List<HistoricVariableInstance>  historicVariableInstances= historyService.createHistoricVariableInstanceQuery().processInstanceId(p.getId()).list();
        Map<String, Object> params =null;
        if(!CollectionUtils.isEmpty(historicVariableInstances)) {
            params=new HashMap<>();
            for(HistoricVariableInstance historicVariableInstance:historicVariableInstances){
                if(StringUtils.isNotEmpty(historicVariableInstance.getVariableName())){
                    params.put(historicVariableInstance.getVariableName(),historicVariableInstance.getValue());
                }
                continue;
            }
        }
        // 获取最新的一个userTask，也就是任务活动纪录
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(p.getId())
                .orderByHistoricActivityInstanceStartTime().desc()
                .orderByHistoricActivityInstanceEndTime().asc().
                        listPage(0, 1);
        Process data = new Process(p);
        if (!activities.isEmpty()) {
            data.withActivity(activities.get(0));
        }
        return data.withVariables(params);
    }

    @Override
    public List<Process> mineList(User user, String auditType, AuditStatus auditStatus, int pageNum, int pageSize) {
            // startedBy：创建任务时设置的发起人
            HistoricProcessInstanceQuery instanceQuery = historyService.createHistoricProcessInstanceQuery()
                    .startedBy(user.getId());
            // 自定义参数筛选
            if (StringUtils.isNotEmpty(auditType)) {
                instanceQuery.variableValueEquals(FlowUtil.AUDIT_TYPE_KEY, auditType);
            }
        if (auditStatus != null) {
            instanceQuery.variableValueEquals(FlowUtil.AUDIT_STATUS_KEY, auditStatus.toString());
        }

        return instanceQuery
                .orderByProcessInstanceStartTime().desc()
                .listPage((pageNum - 1) * pageSize, pageSize).stream()
                //  获取其中的详细和自定义参数
                .map(this::convertHostoryProcess)
                .collect(Collectors.toList());
    }
    /**
     * 提前终止或撤销流程
     * @param user  当前用户信息
     * @param processInstanceId  实例ID
     * @param  reason 终止原因
     * @return
     */
    @Override
    @Transactional
    public void cancel(User user, String processInstanceId,String reason) {
        ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (process == null) {
            throw new RuntimeException("processInstanceId:"+processInstanceId+" ,该流程不在运行状态");
        }
        // ""这个参数本来可以写删除原因
        log.info("当前用户id: {} ", Authentication.getAuthenticatedUserId());
        // 设置删除人信息
        identityService.setAuthenticatedUserId(user.getId());
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        if (CollectionUtils.isEmpty(tasks)) {
            throw new FlowableException("processInstanceId："+processInstanceId +" 不存在对应的任务");
        }
        runtimeService.setVariable(tasks.get(0).getExecutionId(), FlowUtil.AUDIT_STATUS_KEY, AuditStatus.CANCEL.toString());
        if(StringUtils.isEmpty(reason)){
            reason="用户撤销";
        }
        runtimeService.deleteProcessInstance(processInstanceId, reason);
        // 重新置null
        identityService.setAuthenticatedUserId(null);
    }


    /**
     * 查询当前实例的可操作人列表（指定分组的情况未测试）
     * @param processInstanceId  实例ID
     * @return 返回当前实例的可操作人列表
     */
    @Override
    public List<String> getUserListByProcessInstanceID(String processInstanceId){
        List<String> res=null;
        //TODO 获得当前活动的任务 可以根据其中assingnee 活动对应任务的待办列表
        List <Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(processInstanceId)
                .list();
        //得到正在执行的Activity的Id
        res =  new ArrayList<>();
        for (Task task : tasks) {
            String assingnee=task.getAssignee();
            res.add(assingnee);
        }
        return res;
    }
    /**
     * 查询当前实例的可操作人列表（指定分组的情况未测试）
     * @param processInstanceId  实例ID
     * @return 返回当前实例的可操作人列表
     */
    @Override
    public List<org.flowable.idm.api.User> getUserObjecListByProcessInstanceID(String processInstanceId){
        List<org.flowable.idm.api.User > res=null;
        //TODO 获得当前活动的任务 可以根据其中assingnee 活动对应任务的待办列表
        List <Task> tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
//        List<Execution> executions = runtimeService
//                .createExecutionQuery()
//                .processInstanceId(processInstanceId)
//                .list();
//        //得到正在执行的Activity的Id
        res =  new ArrayList<>();

        for (Task task : tasks) {
            String assingnee=task.getAssignee();
            if(StringUtils.isNotEmpty(assingnee)){
               org.flowable.idm.api.User user= identityService.createUserQuery().userId(assingnee).singleResult();
               if (user!=null){
                   res.add(user);
               }
            }
        }
        return res;
    }

    /**
     * 查询当前实例的可操作人列表（指定分组的情况未测试）
     * @param processInstanceId  实例ID
     * @return 返回当前实例的可操作人列表
     */
    @Override
    public List<Task> getTaskListByProcessInstanceID(String processInstanceId){
        List<Task> res=null;
        res = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();

        for (Task task : res) {
            List<IdentityLink> identityLinks=taskService.getIdentityLinksForTask(task.getId());
            log.info(JSONArray.toJSONString(identityLinks));
        }
        return res;
    }

    /**
     * 根据实例ID 返回任务交互对象，在其中增加了候选人和候选组的查询
     * @param processInstanceId
     * @return
     */
    @Override
    public List<TaskVo> getTaskVoListByProcessInstanceID(String processInstanceId){
        List<TaskVo> res=null;
        List<Task> tasks=null;
        ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if(process==null){
            return res;
        }
        tasks = taskService.createTaskQuery().processInstanceId(processInstanceId).active().list();
        if(CollectionUtils.isEmpty(tasks)){
            return res;
        }
        res=new ArrayList<>();
        String startUserId=process.getStartUserId();

        for (Task task : tasks) {
            TaskVo taskVo=new TaskVo().with(task);
            String taskAssignee=task.getAssignee();
            //  如果taskAssignee 是$INITIATOR 创建者，就需要获得流程的创建人
            if(USER_INITIATOR_STR.equals(taskAssignee)){
                taskVo.setAssignee(startUserId);
            }
            int idLinkCount=((TaskEntityImpl) task).getIdentityLinkCount();
            log.info("idLinkCount:"+idLinkCount);
            if(idLinkCount>0){
                List<IdentityLink> identityLinks=taskService.getIdentityLinksForTask(task.getId());
                if(!CollectionUtils.isEmpty(identityLinks)){
                    List<IdentityLinkVo> identityLinkVos=new ArrayList<>();
                    log.info(JSONArray.toJSONString(identityLinks));
                    for(IdentityLink identityLink:identityLinks){
                        IdentityLinkVo identityLinkVo=new IdentityLinkVo().with(identityLink);
                        identityLinkVos.add(identityLinkVo);
                    }
                    taskVo.setLinkVoList(identityLinkVos);
                }

            }
            res.add(taskVo);
        }
        return res;
    }

    /**
     * 判断当前流程实例是否结束
     * @param processInstanceId  实例ID
     * @return 返回当前实例的可操作人列表
     */
    @Override
    public boolean   isFinishedProcessInstanceID(String processInstanceId)throws Exception{
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        boolean isExist=false;
        if (processInstance != null) {
            isExist=true;
            return false;
        } else  {
            HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService()
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (historicProcessInstance == null) {
                isExist=false;
                if(!isExist){
                    throw new Exception("流程实例:"+processInstanceId+",不存在！");
                }
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 根据流程实例ID获取定义流程的所有的用户节点，用来实现节点间的自由跳转 只支持运行中的流程
     * @param proInstanceId
     * @return 返回流程对应的用户任务列表
     */
    @Override
    public List<FlowNodeVo> getAllFlowTask(String proInstanceId){
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(proInstanceId).singleResult();
        //流程走完的不显示图
        if (pi == null) {
            log.debug("processId:["+proInstanceId+ "]流程不存在");
            throw new RuntimeException("processId:["+proInstanceId+ "]流程不存在");
        }
        List<FlowNodeVo> flowNodeVos=null;
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = pi.getId();
        String definId=pi.getProcessDefinitionId();
        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(definId);

        List<FlowElement>  flowElements=bpmnModel.getProcesses().get(0).getFlowElements().stream().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(flowElements)){
            return null;
        }
        flowNodeVos=new ArrayList<>();
        for(FlowElement flowElement:flowElements){
            if(flowElement instanceof UserTask){
                FlowNodeVo flowNodeVo=new FlowNodeVo();
                flowNodeVo.with(flowElement);
                flowNodeVos.add(flowNodeVo);
            }

        }
        Collections.sort(flowNodeVos, new Comparator<FlowNodeVo>() {
            @Override
            public int compare(FlowNodeVo o1, FlowNodeVo o2) {
                //升序
                return o1.getFlowNodeId().compareTo(o2.getFlowNodeId());
            }
        });


        return flowNodeVos;
    }



    /**
     * 1.这个api  并行网关是支持的,也可以从主线跳到支线
     * 2.每跳转一次,都会生成新的执行实例id(executionId)
     * 从当前节点回退到某个节点
     *@param  proInstanceId  需要驳回的流程实例id(当前发起节点的流程实例id)
     *@param  currentTaskID   驳回发起的当前节点key 为  act_ru_task 中TASK_DEF_KEY_ 字段的值
     * @param userID  用户ID
     * @param targetTaskDefKey  目标节点定义ID
     * @param reason  原因
     *@param  targetTaskDefKey  目标节点的key  为act_hi_taskinst 中 TASK_DEF_KEY_
     *
     */
    @Override
    @Transactional
    public void back2PreFlow(String userID, String proInstanceId,String currentTaskID,String  targetTaskDefKey,String reason) {
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(proInstanceId).list();
        Task currentTask=taskService.createTaskQuery().taskId(currentTaskID).singleResult();
        if(CollectionUtils.isEmpty(tasks)){
            //TODO 查询不到抛异常
            throw new FlowableException("当前实例："+proInstanceId+" 不存在任务。");
        }
        if(currentTask==null){
            throw new FlowableException("当前任务ID："+currentTaskID+" 不存在任务。");
        }
        FlowElement distActivity = processDefinitionUtils.findFlowElementById(tasks.get(0).getProcessDefinitionId(),targetTaskDefKey);
        //1. 保存任务信息
        currentTask.setAssignee(userID);
        taskService.saveTask(currentTask);
        //2. 如果上一个节点是发起节点的话要处理一下 ， 什么都不处理
        if(distActivity instanceof StartEvent){
            //TODO 退回到开始怎么处理？  获得开始后的第一个节点。 暂时不处理

        }
        List<String> currentActivityIds = new ArrayList<>();
        tasks.forEach(t -> currentActivityIds.add(t.getTaskDefinitionKey()));
        //3. 删除节点信息  跳转到结束事件，相当于撤销。
        if ((distActivity instanceof EndEvent)) {
            User user=new User();
            user.setId(userID);
            this.cancel(user,proInstanceId,reason);
        }
        //4.执行驳回操作  如果是处于并行网关上面，所有的节点都跳转到上一步。
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(proInstanceId)
                .moveActivityIdsToSingleActivityId(currentActivityIds, targetTaskDefKey)
                .changeState();
    }
    /**
     * 获取任务节点
     *
     * @param node   查询节点选择
     * @param taskId 任务id
     */
    @Override
    public List<FlowNodeVo> nextFlowNode(String node, String taskId) {
        Task task = processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntity ee = (ExecutionEntity) processEngine.getRuntimeService().createExecutionQuery()
                .executionId(task.getExecutionId()).singleResult();
        // 当前审批节点
        String crruentActivityId = ee.getActivityId();
        FlowElement FlowElement=null;

        List<FlowNodeVo> flowNodeVos=null;

        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId());
        FlowNode flowNode = (FlowNode) bpmnModel.getFlowElement(crruentActivityId);
        // 输出连线
        List<SequenceFlow> outFlows = flowNode.getOutgoingFlows();
        if(CollectionUtils.isEmpty(outFlows)){
            return flowNodeVos;
        }
        flowNodeVos=new ArrayList<>();

        for (SequenceFlow sequenceFlow : outFlows) {
            FlowNodeVo flowNodeVo=new FlowNodeVo();
            //当前审批节点
            if ("now".equals(node)) {
                FlowElement = sequenceFlow.getSourceFlowElement();
                log.info("当前节点: id=" + FlowElement.getId() + ",name=" + FlowElement.getName());

                flowNodeVo.setFlowNodeName(FlowElement.getName());
                flowNodeVo.setFlowNodeId(FlowElement.getId());

            } else if ("next".equals(node)) {
                // 下一个审批节点
                FlowElement = sequenceFlow.getTargetFlowElement();
                if (FlowElement instanceof UserTask) {
                    log.info("下一节点: id=" + FlowElement.getId() + ",name=" + FlowElement.getName());
                    flowNodeVo.setFlowNodeName(FlowElement.getName());
                    flowNodeVo.setFlowNodeId(FlowElement.getId());
                }
                // 如果下个审批节点为结束节点
                if (FlowElement instanceof EndEvent) {
                    log.info("下一节点为结束节点：id=" + FlowElement.getId() + ",name=" + FlowElement.getName());
                    flowNodeVo.setFlowNodeName(FlowElement.getName());
                    flowNodeVo.setFlowNodeId(FlowElement.getId());
                }

            }
            flowNodeVos.add(flowNodeVo);
        }
        return flowNodeVos;
    }

}
