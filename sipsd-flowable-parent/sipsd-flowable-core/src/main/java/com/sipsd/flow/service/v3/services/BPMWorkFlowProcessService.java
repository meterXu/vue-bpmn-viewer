package com.sipsd.flow.service.v3.services;

import com.sipsd.flow.bean.ReasonParam;
import com.sipsd.flow.bean.enums.AuditStatus;
import com.sipsd.flow.bean.enums.OperateType;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import com.sipsd.flow.bean.Process;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface  BPMWorkFlowProcessService {

    /**
     * 启动工作流
     */
    ProcessInstance strartFlow(String processKey, Map<String,Object> paras)   throws Exception;
    
    /**
     * 获得某一个工作流处理流程的当前用户任务列表
     * @param ProcessKey
     * @param userID
     * @return
     * @throws Exception
     */
    List<Task> getProcessTaskByUserID(String ProcessKey, String userID) throws Exception;

    
    /**
     *  批准任务
     * @param taskId 任务ID
     * @param paras 传递 参数
     * @return
     * @throws Exception
     */
    Task applyTaskByID(String taskId , Map<String, Object> paras) throws Exception;
    
    /**
     *  获取流程的图形
     * @param processId
     * @return
     * @throws Exception
     */
    InputStream genProcessDiagram(String processId) throws Exception;
    
    /**
     *  根据流程关键字获取流程图形  无法准备的定义精准的流程，如果有多个发布，会默认选择第一个。
     * @param processKey
     * @return
     * @throws Exception
     */
    InputStream genProcessDiagramByKey(String processKey) throws Exception;

    

    /**
     * @param userID   当前用户
     * @param taskId 用户任务id
     * @return 当前用户是否是这个申请的用户任务的审批人、候选人或者候选组
     */
    boolean isAssigneeOrCandidate(String userID, String taskId);

    /**
     * 审批前操作
     * 因为流程引擎的用户任务完成后，会立马触发下一个流程，所以这里需要先处理，并且处于两个事物中
     *
     * @param taskId      用户任务id
     * @param userID        当前用户
     * @param operateType 审批类型：同意，拒绝
     * @param reason      审批材料
     */
    void beforeAgreeOrReject(String taskId, String userID, OperateType operateType, ReasonParam reason);

    /**
     * @param userID      当前登陆用户
     * @param auditType 类型
     * @param pageNum   页码，从1开始
     * @param pageSize  一页多少个
     * @return 待我审批的流程列表
     */
    List<Process> waitAuditList(String userID, String auditType, int pageNum, int pageSize);

    /**
     * @param userID        当前登陆用户
     * @param auditType   类型
     * @param auditStatus 流程状态
     * @param pageNum     页码，从1开始
     * @param pageSize    一页多少个
     * @return 我已审批的流程列表
     */
    List<Process> auditList(String userID, String auditType, AuditStatus auditStatus, int pageNum, int pageSize);

    /**
     * @param userID        当前登陆用户
     * @param auditType   类型
     * @param auditStatus 流程状态
     * @param pageNum     页码，从1开始
     * @param pageSize    一页多少个
     * @return 查看我创建的流程列表
     */
    List<Process> mineList(String userID, String auditType, AuditStatus auditStatus, int pageNum, int pageSize);

    void cancel(String userID, String processInstanceId);
    

}
