package com.sipsd.flow.dao.flowable;

import com.github.pagehelper.Page;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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
     * @param params
     * @return void
     * @Description 根据流程实例id查询自定义任务属性表
     */
    public void updateExtensionCustomTaskById(TaskExtensionVo params);

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

}
