package com.sipsd.flow.dao.flowable;

import com.github.pagehelper.Page;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

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
    public Page<TaskExtensionVo>  getExtensionTaskByProcessInstanceId(@RequestParam String processInstanceId);

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
    public TaskExtensionVo getExtensionTaskByProcessInstanceIdAndTaskId(@RequestParam String processInstanceId,@RequestParam String taskId);
}
