package com.sipsd.flow.service.v3.services;

import com.sipsd.flow.bean.Process;
import com.sipsd.flow.bean.*;
import com.sipsd.flow.bean.enums.AuditStatus;
import com.sipsd.flow.bean.enums.OperateType;
import org.flowable.task.api.Task;

import java.io.InputStream;
import java.util.List;

public interface ProcessService {


    /**
     * 启动流程
     * @param user 当前用户信息
     * @param param  启动相关参数
     * @return  返回启动流程的相关信息，如果启动后，是单个待办任务，就返回taskID ，如果是都过，taskID为空，在taskArray中体现
     */
    Process start(User user, ProcessParam param);


    /**
     *  获取流程的图形
     * @param processId
     * @return
     * @throws Exception
     */
    InputStream genProcessDiagram(String processId) throws Exception;

    /**
     * @param user   当前用户
     * @param taskId 用户任务id
     * @return 当前用户是否是这个申请的用户任务的审批人、候选人或者候选组
     */
    boolean isAssigneeOrCandidate(User user, String taskId);


    /**
     *  根据用户任务ID，查询当前的运行时任务。
     * @param taskId 用户任务id
     * @return 返回当前taskID对应的对象信息，只是针对于运行时状态。
     */
    Task getRunTimeTaskByID(String taskId);
    /**
     * 审批前操作
     * 因为流程引擎的用户任务完成后，会立马触发下一个流程，所以这里需要先处理，并且处于两个事物中
     *
     * @param taskId      用户任务id
     * @param user        当前用户
     * @param operateType 审批类型：同意，拒绝
     * @param reason      审批材料
     */
    void beforeAgreeOrReject(String taskId, User user, OperateType operateType, ReasonParam reason);
    /**
     * 审批操作
     *
     * @param taskId      用户任务id
     * @param user        当前用户
     * @param operateType 审批类型：同意，拒绝
     * @param reason      审批材料
     */
    void agreeOrRejectOP(String taskId, User user, OperateType operateType, ReasonParam reason);
    /**
     * @param user      当前登陆用户
     * @param auditType 类型
     * @param pageNum   页码，从1开始
     * @param pageSize  一页多少个
     * @return 待我审批的流程列表
     */
    List<Process> waitAuditList(User user, String auditType, int pageNum, int pageSize);

    /**
     * @param user        当前登陆用户
     * @param auditType   类型
     * @param auditStatus 流程状态
     * @param pageNum     页码，从1开始
     * @param pageSize    一页多少个
     * @return 我已审批的流程列表
     */
    List<Process> auditList(User user, String auditType, AuditStatus auditStatus, int pageNum, int pageSize);

    /**
     * @param user        当前登陆用户
     * @param auditType   类型
     * @param auditStatus 流程状态
     * @param pageNum     页码，从1开始
     * @param pageSize    一页多少个
     * @return 查看我创建的流程列表
     */
    List<Process> mineList(User user, String auditType, AuditStatus auditStatus, int pageNum, int pageSize);

    /**
     * 提前终止或撤销流程
     * @param user   当前登陆用户
     * @param processInstanceId  流程实例
     * @param reason  终止原因
     */
    void cancel(User user, String processInstanceId,String reason);

    /**
     * 查询当前实例的可操作人列表
     * @param processInstanceId  实例ID
     * @return 返回当前实例的可操作人列表
     */
    List<String> getUserListByProcessInstanceID(String processInstanceId);

    /**
     * 查询当前实例的可操作人列表
     * @param processInstanceId  实例ID
     * @return 返回当前实例的可操作人列表
     */
    List<org.flowable.idm.api.User> getUserObjecListByProcessInstanceID(String processInstanceId);

    /**
     * 查询当前实例的任务列表
     * @param processInstanceId  实例ID
     * @return 返回当前实例的可操作人列表
     */
    List<Task> getTaskListByProcessInstanceID(String processInstanceId);

    /**
     * 根据实例ID 返回任务交互对象，在其中增加了候选人和候选组的查询
     * @param processInstanceId
     * @return
     */
    List<TaskVo> getTaskVoListByProcessInstanceID(String processInstanceId);
    /**
     * 判断当前流程实例是否结束
     * @param processInstanceId  实例ID
     * @return 返回当前实例的可操作人列表
     */
    boolean   isFinishedProcessInstanceID(String processInstanceId)throws Exception;

    /**
     * 根据流程实例ID获取定义流程的所有节点，用来实现节点间的自由跳转  只支持运行中的流程
     * @param proInstanceId
     * @return
     */
    List<FlowNodeVo> getAllFlowTask(String proInstanceId);

    /**

     * @param proInstanceId
     * @param currentTaskID
     * @param targetTaskDefKey
     */
    /**
     * 退回到某个节点
     * 从当前节点回退到某个节点
     * @param userID 当前用户
     * @param proInstanceId  实例ID
     * @param currentTaskID 任务ID
     * @param targetTaskDefKey  要退回的节点ID
     * @param reason 退回原因
     */
    void back2PreFlow(String userID, String proInstanceId,String currentTaskID,String  targetTaskDefKey,String reason);

    /**
     * 获取任务节点
     *
     * @param node   查询节点选择
     * @param taskId 任务id
     */
    List<FlowNodeVo>  nextFlowNode(String node, String taskId);

}
