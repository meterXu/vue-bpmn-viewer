package com.sipsd.flow.service.flowable;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.vo.flowable.ExtensionTaskQueryVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : 高强
 * @projectName : flowable
 * @description: 运行时自定义属性任务service
 * @date : 2021/03/24 15:05
 */
public interface IFlowableExtensionTaskService
{

    /**
     * @param processDefinitionId
     * @return void
     * @Description 插入自定义属性表
     */
    public void saveExtensionTask(String processDefinitionId,String fromKey,String businessInfo);

    /**
     *
     * @param processDefinitionId
     * @return void
     * @Description 驳回插入自定义属性表
     */
    public void saveBackExtensionTask(String processDefinitionId);

    /**
     *并行网关内的串行任务节点跳转插入自定义属性表
     * @param processInstanceId
     * @param taskDefKey
     */
    public void saveBackExtensionTaskForJump(String processInstanceId,String taskDefKey);

    /**
     * @param processInstanceId
     * @return com.sipsd.flow.vo.flowable.ret.TaskExtensionVo
     * @Description 根据流程实例id查询自定义任务属性表
     */
    public PageModel<TaskExtensionVo>  getExtensionTaskByProcessInstanceId(String processInstanceId, Query query);


    /**
     *
     * @param processInstanceId
     * @param startTime
     * @return java.util.List<com.sipsd.flow.vo.flowable.ret.TaskExtensionVo>
     * @Description 根据实例id和创建时间查询节点
     */
    public List<TaskExtensionVo> getExtensionTaskByStartTime(String processInstanceId,String startTime);

    /**
     * @param processInstanceId
     * @return com.sipsd.flow.vo.flowable.ret.TaskExtensionVo
     * @Description 根据流程实例id查询历史自定义任务属性表(已办代办查询)
     */
    public PageModel<TaskExtensionVo> getAllExtensionTaskByProcessInstanceId(String processInstanceId,Query query);

    /**
     * @param processInstanceId
     * @return com.sipsd.flow.vo.flowable.ret.TaskExtensionVo
     * @Description 根据流程实例id查询历史自定义任务属性表(已办查询)
     */
    public PageModel<TaskExtensionVo> getFinishExtensionTaskByProcessInstanceId(String processInstanceId, Query query);


    /**
     *
     * @param processInstanceId
     * @param taskId
     * @return java.util.List<java.lang.String>
     * @Description 根据实例ID和任务ID查询当前并联或者串联的节点名称
     */
    public List<String> getParallelNodesByProcessInstanceIdAndTaskId(String processInstanceId,String taskId);


    /**
     * @param params
     * @return void
     * @Description 通过流程实例id来更新最大审批天数值
     */
    public Result<String> updateExtensionCustomTaskById(ExtensionTaskQueryVo params);

    /**
     *
     * @param params
     * @return com.sipsd.cloud.common.core.util.Result<java.lang.String>
     * @Description
     */
    public void updateAssigneeByProcessInstanceIdAndTaskID(String processInstanceId,String taskId,String assignee);

    /**
     *
     * @param params
     * @return void
     * @Description  根据流程ID和任务ID来更新督办信息
     */
    public Result<String> updateSupervisionTask(TaskExtensionVo params);

    /**
     *
     * @param processInstanceId 实例ID
     * @param state 激活/挂起状态
     * @param overtime 是否延长时间
     * @return void
     * @Description  通过实例定义ID更新激活/挂起状态
     */
    public void updateSuspensionStateByProcessInstanceId(String processInstanceId,String state,String overtime);

    /**
     *
     * @param processInstanceId 实例ID
     * @param state 激活/挂起状态
     * @return void
     * @Description  激活时更新结束时间-原本的结束时间+当前时间-挂起时间
     */
    public void updateEndTimeByProcessInstanceId(String processInstanceId,String state);

    /**
     *
     * @param processInstanceId
     * @param taskId
     * @return void
     * @Description 根据流程实例ID和任务ID来查询extension表中的数据
     */
    public TaskExtensionVo getExtensionTaskByProcessInstanceIdAndTaskID(@Param("processInstanceId") String processInstanceId, @Param("taskId") String taskId);


    /**
     *
     * @param params
     * @return void
     * @Description  根据流程实例ID和任务ID来更新结束时间
     */
    public void updateEndTimeByProcessInstanceIdAndTaskId(TaskExtensionVo params);


    /**
     *
     * @param
     * @return java.util.List<com.sipsd.flow.vo.flowable.ret.TaskExtensionVo>
     * @Description 获取所有的待办任务
     */
    public List<TaskExtensionVo> getRunTasks();

    /**
     *
     * @param processInstanceId
     * @param suspensionState
     * @return void
     * @Description 根据流程实例ID和激活状态来查询extension表中的数据
     */
    public List<TaskExtensionVo> getExtensionTaskByProcessInstanceIdAndState(String processInstanceId,String suspensionState);

}
