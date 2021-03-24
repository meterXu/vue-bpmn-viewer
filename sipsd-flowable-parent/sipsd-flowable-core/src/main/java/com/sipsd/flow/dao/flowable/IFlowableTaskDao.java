package com.sipsd.flow.dao.flowable;

import com.github.pagehelper.Page;
import com.sipsd.flow.vo.flowable.TaskQueryVo;
import com.sipsd.flow.vo.flowable.ret.TaskVo;
import org.apache.ibatis.annotations.Mapper;
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
     * 根据任务ID 查询任务
     * @param taskId
     * @return
     */
    public TaskVo getTaskById(String taskId) ;

}
