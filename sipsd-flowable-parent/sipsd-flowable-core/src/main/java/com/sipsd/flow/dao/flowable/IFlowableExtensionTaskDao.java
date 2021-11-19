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
    public void updateAssigneeByProcessInstanceIdAndTaskID(@Param("processInstanceId") String processInstanceId,@Param("taskId") String taskId,@Param("assignee") String assignee);


    /**
     *
     * @param params
     * @return void
     * @Description 根据ID来更新督办信息
     */
    public void updateDbInfoById(TaskExtensionVo params);

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
    public void updateSuspensionStateByProcessInstanceId(@Param("processInstanceId") String processInstanceId,@Param("state") String state);
}
