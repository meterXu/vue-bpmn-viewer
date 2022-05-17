package com.sipsd.flow.dao.flowable;

import com.github.pagehelper.Page;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : 高强
 * @title: : IFlowableTaskDao
 * @projectName : flowable
 * @description: flowabletask自定义属性的查询
 * @date : 2019/11/2316:34
 */
@Mapper
@Repository
public interface IFlowableExtensionTaskDao
{
    /**
     * @param processInstanceId
     * @return com.sipsd.flow.vo.flowable.ret.TaskExtensionVo
     * @Description 根据流程实例id查询自定义任务属性表
     */
    public Page<TaskExtensionVo>  getExtensionTaskByProcessInstanceId(@Param("processInstanceId") String processInstanceId);

    /**
     * @param processInstanceId
     * @return com.sipsd.flow.vo.flowable.ret.TaskExtensionVo
     * @Description 根据流程实例id查询历史自定义任务属性表(已办代办查询)
     */
    public Page<TaskExtensionVo>  getAllExtensionTaskByProcessInstanceId(@Param("processInstanceId") String processInstanceId);


    /**
     * @param processInstanceId
     * @return com.sipsd.flow.vo.flowable.ret.TaskExtensionVo
     * @Description 根据流程实例id查询历史自定义任务属性表(已办代办查询)
     */
    public Page<TaskExtensionVo>  getFinishExtensionTaskByProcessInstanceId(@Param("processInstanceId") String processInstanceId);


    /**
     *
     * @param processInstanceId
     * @param startTime
     * @return java.util.List<com.sipsd.flow.vo.flowable.ret.TaskExtensionVo>
     * @Description 根据实例id和创建时间查询节点
     */
    public List<TaskExtensionVo> getExtensionTaskByStartTime(@Param("processInstanceId") String processInstanceId,@Param("startTime") String startTime);

    /**
     *
     * @param processInstanceId
     * @param taskId
     * @return java.util.List<java.lang.String>
     * @Description 根据实例ID和任务ID查询当前并联或者串联的节点名称
     */
    public List<String> getParallelNodesByProcessInstanceIdAndTaskId(@Param("processInstanceId") String processInstanceId,@Param("taskId") String taskId);




    /**
     * @param params
     * @return void
     * @Description 根据流程实例id查询自定义任务属性表
     */
    public void updateExtensionCustomTaskById(TaskExtensionVo params);


    /**
     *
     * @param params
     * @return void
     * @Description 根据实例ID和任务ID更新实际审批人
     */
    public void updateAssigneeByProcessInstanceIdAndTaskID(@Param("processInstanceId") String processInstanceId,@Param("taskId") String taskId,@Param("assignee") String assignee,@Param("variables") String variables);


    /**
     *
     * @param params
     * @return void
     * @Description 根据流程ID和任务ID来更新督办信息
     */
    public void updateSupervisionTask(TaskExtensionVo params);

    /**
     * @param params
     * @return void
     * @Description 插入自定义属性表
     */
    public void insertExtensionTask(TaskExtensionVo params);


    /**
     *
     * @param processInstanceId
     * @param taskId
     * @return java.lang.Integer
     * @Description  根据流程实例id和taskId查询记录
     */
    public TaskExtensionVo getExtensionTaskByProcessInstanceIdAndTaskId(@Param("processInstanceId") String processInstanceId,@Param("taskId") String taskId);

    public TaskExtensionVo getExtensionTaskByTaskDefinitionKey(@Param("processInstanceId") String processInstanceId,@Param("taskDefinitionKey") String taskDefinitionKey);

    /**
     *
     * @param processInstanceId
     * @return void
     * @Description  通过实例ID更新激活/挂起状态
     */
    public void updateSuspensionStateByProcessInstanceId(@Param("processInstanceId") String processInstanceId,@Param("state") String state,@Param("overtime") String overtime);

    /**
     *
     * @param processInstanceId 实例ID
     * @param state 激活/挂起状态
     * @return void
     * @Description  激活时更新结束时间-原本的结束时间+当前时间-挂起时间
     */
    public void updateEndTimeByProcessInstanceId(@Param("processInstanceId") String processInstanceId,@Param("state") String state);




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
    public List<TaskExtensionVo> getExtensionTaskByProcessInstanceIdAndState(@Param("processInstanceId") String processInstanceId, @Param("suspensionState") String suspensionState);


    /**
     *
     * @param
     * @return java.util.List<java.lang.String>
     * @Description 获取所有审批人列表
     */
    public Integer getCountByProcessInstanceIdAndTaskId(@Param("processInstanceId") String processInstanceId,@Param("taskDefKey") String taskDefKey);


    /**
     *
     * @param processInstanceId
     * @param taskId
     * @return java.lang.String
     * @Description 通过实例id和任务id查询审批人
     */
    public String getAssigneeByProcessInstanceIdAndTaskId(@Param("processInstanceId") String processInstanceId, @Param("taskId") String taskId);

}
