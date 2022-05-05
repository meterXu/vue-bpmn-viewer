package com.sipsd.flow.dao.flowable;

import com.github.pagehelper.Page;
import com.sipsd.flow.vo.flowable.TaskQueryVo;
import com.sipsd.flow.vo.flowable.ret.TaskVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author : chengtg
 * @title: : IFlowableTaskDao
 * @projectName : flowable
 * @description: flowabletask的查询
 * @date : 2019/11/2316:34
 */
@Mapper
@Repository
public interface IFlowableTaskDao {
    /**
     * 查询待办任务
     * @param params 参数
     * @return
     */
    public Page<TaskVo> getApplyingTasks(TaskQueryVo params) ;

    /**
     * 查询已办任务列表
     * @param params 参数
     * @return
     */
    public Page<TaskVo> getApplyedTasks(TaskQueryVo params);


    /**
     * 查询全部任务列表
     *
     * @param params 参数
     * @return
     */
    public Page<TaskVo> getAllTasks(TaskQueryVo params);
    
    /**
     * 根据任务ID 查询任务
     * @param taskId
     * @return
     */
    public TaskVo getTaskById(String taskId) ;

    /**
     *
     * @param processInstanceId
     * @param taskDefKey
     * @return java.lang.String
     * @Description 获取上个审批节点的实际审批人
     */
    public String getPreTaskAssignee(@Param("processInstanceId") String processInstanceId, @Param("taskDefKey") String taskDefKey);

    /**
     *
     * @param assignee 审批人
     * @param processInstanceId 实例Id
     * @param taskId 任务Id
     * @return void
     * @Description 根据实例ID和taskId来更新审批人
     */
    public void updateAssigneeByProcessInstanceIdAndTaskId(@Param("assignee") String assignee,@Param("processInstanceId") String processInstanceId, @Param("taskId") String taskId);

}
