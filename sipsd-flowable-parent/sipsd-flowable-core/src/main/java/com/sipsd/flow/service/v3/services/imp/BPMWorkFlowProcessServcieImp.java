package com.sipsd.flow.service.v3.services.imp;


import com.sipsd.flow.bean.BaseBean;
import com.sipsd.flow.bean.Process;
import com.sipsd.flow.bean.ReasonParam;
import com.sipsd.flow.bean.VariableParam;
import com.sipsd.flow.bean.enums.AuditStatus;
import com.sipsd.flow.bean.enums.OperateType;
import com.sipsd.flow.event.TaskBeforeListener;
import com.sipsd.flow.service.v3.services.BPMDeploymentService;
import com.sipsd.flow.service.v3.services.BPMWorkFlowProcessService;
import com.sipsd.flow.util.FlowUtil;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.history.NativeHistoricActivityInstanceQuery;
import org.flowable.engine.impl.history.async.HistoryJsonConstants;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于统一modelKey 和processKey 一致的情况下，进行重现实现。
 * @author hul

 */
@Service
@CommonsLog
public class BPMWorkFlowProcessServcieImp implements BPMWorkFlowProcessService
{
 
    @Autowired
    private ProcessEngine processEngine;
    
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private RuntimeService runtimeService;
    
    @Autowired
    private ModelService modelService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private HistoryService historyService;
    
    @Autowired
    private BPMDeploymentServiceImp bpmDeploymentServiceImp;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private BPMDeploymentService bpmDeploymentService;

   
    @Override
    public ProcessInstance strartFlow(String processKey, Map<String, Object> paras)  throws Exception{
        Map<String,Object> deployMap=null;
        if (StringUtils.isEmpty(processKey)){
            return null;
        }
        if (null == paras){
            paras = new HashMap<>();
        }
        Deployment deployment = repositoryService.createDeploymentQuery().processDefinitionKey(processKey).singleResult();
        if(deployment==null){
            deployMap=bpmDeploymentServiceImp.deployWorkFloyByModeKey(processKey);
            if(!CollectionUtils.isEmpty(deployMap)){
                deployment=(Deployment) deployMap.get("deployment");
            }
        }
        ProcessDefinition createProcessDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance startProcessInstanceById = runtimeService.startProcessInstanceById(createProcessDefinition.getId(), paras);
        //TODO 测试添加全局事件  TODO
        
        runtimeService.addEventListener(new TaskBeforeListener());
      
        return startProcessInstanceById;
    }
    
    @Override
    public List<Task> getProcessTaskByUserID(String ProcessKey, String userId) throws Exception {
//        List<Task> tasks = taskService.createTaskQuery().processDefinitionName(ProcessKey).taskAssignee(userId).orderByTaskCreateTime().desc().list();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        return tasks;
    }
    
   
    
    /**
     * 根据模型Key 发布模型 直接从数据库
     * @param paras
     * @throws Exception
     */
    @Override
    public Task applyTaskByID(String taskId , Map<String, Object> paras) throws Exception{
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            log.debug("["+taskId+ "]流程不存在");
            throw new RuntimeException("流程不存在");
        }
        taskService.complete(taskId, paras);
        log.debug("提交成功.流程信息为："+task);
        return task;
    }
    /**
     *@param  processId 流程实例ID
     */
    @Override
    public InputStream genProcessDiagram(String processId) throws Exception{
        InputStream res=null;
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        //流程走完的不显示图
        if (pi == null) {
            HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService()
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processId)
                    .singleResult();
            if(historicProcessInstance!=null){
                String proceDefID=historicProcessInstance.getProcessDefinitionId();

                BpmnModel bpmnModel=repositoryService.getBpmnModel(proceDefID);
                res = getModeDiagram(bpmnModel);
                return res;

            }else{
                log.debug("processId:["+processId+ "]流程不存在");
                throw new RuntimeException("processId:["+processId+ "]流程不存在");
            }
        }else {
            res=genProcessDiagram(pi);
            return res;
        }

    }

    /**
     * 根据流程模型返回流程图
     * @param bpmnModel
     * @return
     */
    private InputStream getModeDiagram(BpmnModel bpmnModel){
        InputStream res=null;
        if(bpmnModel==null) {
            return res;
        }
        DefaultProcessDiagramGenerator defaultProcessDiagramGenerator = new DefaultProcessDiagramGenerator();
        List<String> highLightedActivities=new ArrayList<String>();
        res = defaultProcessDiagramGenerator.generateDiagram(bpmnModel, "PNG", highLightedActivities, true);
        return  res;
    }
    /**
     * @param processKey  流程定义的key，返回其中随机第一个实例。
     */
    @Override
    public InputStream genProcessDiagramByKey(String processKey) throws Exception{
        List<ProcessInstance> pis = runtimeService.createProcessInstanceQuery().processDefinitionKey(processKey).active().list();
//        List<HistoricProcessInstance> his =null;
        Map<String,Object> deployMap=null;
        if(CollectionUtils.isEmpty(pis)){
            log.debug("processKey:["+processKey+ "]流程不存在");
//            his = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processKey).list();
//            if(CollectionUtils.isEmpty(his))
                throw new RuntimeException("processKey:["+processKey+ "]流程不存在");
            
        }
 
        ProcessInstance pi = pis.get(0);
        return genProcessDiagram(pi);
    }
    
  
    /**
     * 生成流程图列  png图形
     * @param pi
     * @return
     * @throws Exception
     */

    private  InputStream genProcessDiagram(ProcessInstance pi) throws Exception{
        InputStream res=null;
        //TODO 并联审批的时候，本处返回多个task
//        List <Task> tasks = taskService.createTaskQuery().processInstanceId(pi.getId()).active().list();
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
    
  
    /**
     * @param userID   当前用户
     * @param taskId 用户任务id
     * @return 当前用户是否是这个申请的用户任务的审批人、候选人
     */
    @Override
    public boolean isAssigneeOrCandidate(String userID, String taskId) {
        long count = taskService.createTaskQuery()
                .taskId(taskId)
                .or()
                .taskAssignee(userID)
                .taskCandidateUser(userID)               
                .endOr().count();
        return count > 0;
    }


    /**
     * 审批前操作
     * 因为流程引擎的用户任务完成后，会立马触发下一个流程，所以这里需要先处理，并且处于两个事物中
     *
     * @param taskId      用户任务id
     * @param userID        当前用户
     * @param operateType 审批类型：同意，拒绝
     * @param reason      审批材料
     */
    @Override
    @Transactional
    public void beforeAgreeOrReject(String taskId, String userID, OperateType operateType, ReasonParam reason) {
        // 组成员操作后方便查询
        taskService.setAssignee(taskId, userID);
        if (StringUtils.isNotEmpty(reason.getText())) {
            // 审批意见
            taskService.addComment(taskId, null, null, reason.getText());
        }
        this.createAttachment(taskId, null, reason.getFiles());
    }
    
    private void createAttachment(String taskId, String processInstanceId, List<BaseBean> files) {
        if (files != null && !files.isEmpty()) {
            // 上传附件，可以直接上传字节流（不建议）
            files.forEach(file ->
                    taskService.createAttachment("application/octet-stream", taskId,
                            processInstanceId, file.getName(), null, file.getId()));
        }
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
     * @param userID      当前登陆用户
     * @param auditType 类型
     * @param pageNum   页码，从1开始
     * @param pageSize  一页多少个
     * @return 待我审批的流程列表
     */
    @Override
    public List<Process> waitAuditList(String userID, String auditType, int pageNum, int pageSize) {
        TaskQuery query = taskService.createTaskQuery()
                // or() 和 endOr()就像是左括号和右括号，中间用or连接条件
                // 指定是我审批的任务或者所在的组别审批的任务
                // 实在太复杂的情况，建议不使用flowable的查询
                .or()
                .taskAssignee(userID)
                .taskCandidateUser(userID)
                .endOr();
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
        Map<String, Object> params = historyService.createHistoricVariableInstanceQuery().processInstanceId(p.getId()).list()
                .stream().collect(Collectors.toMap(HistoricVariableInstance::getVariableName, HistoricVariableInstance::getValue));
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
    /**
     * @param userID        当前登陆用户
     * @param auditType   类型
     * @param auditStatus 流程状态
     * @param pageNum     页码，从1开始
     * @param pageSize    一页多少个
     * @return 我已审批的流程列表
     */
    @Override
    public List<Process> auditList(String userID, String auditType, AuditStatus auditStatus, int pageNum, int pageSize) {
        // 如果不需要筛选自定义参数
        if (auditStatus == null && StringUtils.isEmpty(auditType)) {
            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                    // 我审批的
                    .taskAssignee(userID)
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
                .parameter(HistoryJsonConstants.ASSIGNEE, userID);
        // 加入独特配方
        keys.forEach(v -> {
            query.parameter(v.getNameKey(), v.getName());
            query.parameter(v.getValueKey(), v.getValue());
        });
        List<HistoricActivityInstance> activities = query.listPage((pageNum - 1) * pageSize, pageSize);
        return convertProcessList(activities);
    }

    /**
     * @param userID        当前登陆用户
     * @param auditType   类型
     * @param auditStatus 流程状态
     * @param pageNum     页码，从1开始
     * @param pageSize    一页多少个
     * @return 查看我创建的流程列表
     */
    @Override
    public List<Process> mineList(String userID, String auditType, AuditStatus auditStatus, int pageNum, int pageSize) {
        // startedBy：创建任务时设置的发起人
        HistoricProcessInstanceQuery instanceQuery = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userID);
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
    
    @Override
    @Transactional
    public void cancel(String userID, String processInstanceId) {
        ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (process == null) {
            throw new RuntimeException("该流程不在运行状态");
        }
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
        runtimeService.setVariable(task.getExecutionId(), FlowUtil.AUDIT_STATUS_KEY, AuditStatus.CANCEL.toString());
        runtimeService.deleteProcessInstance(processInstanceId, "用户撤销");
    }
}
