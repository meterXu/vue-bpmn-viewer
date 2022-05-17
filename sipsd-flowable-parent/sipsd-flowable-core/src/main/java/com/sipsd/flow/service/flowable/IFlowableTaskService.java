package com.sipsd.flow.service.flowable;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.bean.FlowElementVo;
import com.sipsd.flow.bean.NoticeTask;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.vo.flowable.*;
import com.sipsd.flow.vo.flowable.ret.FlowNodeVo;
import com.sipsd.flow.vo.flowable.ret.TaskVo;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;

import java.util.List;

/**
 * @author : chengtg
 * @projectName : flowable
 * @description: 运行时的任务service
 * @date : 2019/11/1315:05
 */
public interface IFlowableTaskService {

    /**
     * 驳回任意节点 暂时没有考虑子流程
     *
     * @param backTaskVo 参数
     * @return
     */
    public Result<String> jumpToStepTask(BackTaskVo backTaskVo);

    /**
     * 驳回到原节点原处理人 暂时没有考虑子流程
     *
     * @param preBackTaskVo 参数
     * @return
     */
    public Result<String> backToStepTask(PreBackTaskVo preBackTaskVo);


    /**
     * 获取可驳回节点列表
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @return
     */
    public List<FlowNodeVo> getBackNodesByProcessInstanceId(String processInstanceId,String taskId) ;
    /**
     * 任务前加签 （如果多次加签只能显示第一次前加签的处理人来处理任务）
     *
     * @param addSignTaskVo 参数
     * @return
     */
    public Result<String> beforeAddSignTask(AddSignTaskVo addSignTaskVo);

    /**
     * 任务后加签
     *
     * @param addSignTaskVo 参数
     * @return
     */
    public Result<String> afterAddSignTask(AddSignTaskVo addSignTaskVo);


    /**
     * 多实例-加签
     *
     * @param addSignTaskVo ignore
     */
    Result<String> addMultiInstanceExecution(AddSignTaskVo addSignTaskVo);



    /**
     * 任务加签
     *
     * @param addSignTaskVo 参数
     * @param flag  true向后加签  false向前加签
     * @return
     */
    public Result<String> addSignTask(AddSignTaskVo addSignTaskVo, Boolean flag);

    /**
     * 反签收任务
     *
     * @param claimTaskVo 参数
     * @return
     */
    public Result<String> unClaimTask(ClaimTaskVo claimTaskVo);

    /**
     * 签收任务
     *
     * @param claimTaskVo 参数
     * @return
     */
    public Result<String> claimTask(ClaimTaskVo claimTaskVo);

    /**
     * 委派任务
     *
     * @param delegateTaskVo 参数
     * @return
     */
    public Result<String> delegateTask(DelegateTaskVo delegateTaskVo);

    /**
     * 转办
     *
     * @param turnTaskVo 转办任务VO
     * @return 返回信息
     */
    public Result<String> turnTask(TurnTaskVo turnTaskVo);

    /**
     * 执行任务
     *
     * @param params 参数
     */
    public Result<String> complete(CompleteTaskVo params);

    /**
     * 通过任务id获取任务对象
     *
     * @param taskId 任务id
     * @return
     */
    public Result<Task> findTaskById(String taskId);

    /**
     * 查询待办任务列表
     *
     * @param params 参数
     * @return
     */
    public PageModel<TaskVo> getApplyingTasks(TaskQueryVo params, Query query);

    /**
     * 查询已办任务列表
     *
     * @param params 参数
     * @return
     */
    public PageModel<TaskVo> getApplyedTasks(TaskQueryVo params, Query query);


    /**
     * 抄送任务
     * @param params
     * @param query
     * @return
     */
    PageModel<NoticeTask> getNoticeTasks(NoticeTaskQuery params, Query query);


    /**
     * 查询全部任务列表
     *
     * @param params 参数
     * @return
     */
    public PageModel<TaskVo> getAllTasks(TaskQueryVo params, Query query);

    /**
     * 通过流程实例id获取流程实例的待办任务审批人列表
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    public List<User> getApprovers(String processInstanceId);

    /**
     * 通过任务id判断当前节点是不是并行网关的节点
     * @param taskId 任务id
     * @return
     */
    public boolean checkParallelgatewayNode(String taskId) ;

	Result<List<FlowElementVo>> getProcessNodeList(String modelkey);

	TaskVo getTask(FormInfoQueryVo formInfoQueryVo);

	/**
	 *
	 * @param processInstanceId
	 * @param taskDefKey
	 * @return java.lang.String
	 * @Description 获取上个审批节点的实际审批人
	 */
	public String getPreTaskAssignee(String processInstanceId,String taskDefKey);

    /**
     *
     * @param processInstanceId
     * @param taskId
     * @return java.lang.String
     * @Description 通过实例id和任务id查询审批人
     */
    public String getAssigneeByProcessInstanceIdAndTaskId(String processInstanceId, String taskId);

    /**
     *
     * @param assignee 审批人
     * @param processInstanceId 实例Id
     * @param taskId 任务Id
     * @return void
     * @Description 根据实例ID和taskId来更新审批人
     */
    public void updateAssigneeByProcessInstanceIdAndTaskId(String assignee, String processInstanceId, String taskId);

}
