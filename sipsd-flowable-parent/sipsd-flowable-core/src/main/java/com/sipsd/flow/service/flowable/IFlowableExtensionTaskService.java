package com.sipsd.flow.service.flowable;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.vo.flowable.ExtensionTaskQueryVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;

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
    public void saveExtensionTask(String processDefinitionId);

    /**
     * @param processInstanceId
     * @return com.sipsd.flow.vo.flowable.ret.TaskExtensionVo
     * @Description 根据流程实例id查询自定义任务属性表
     */
    public PageModel<TaskExtensionVo>  getExtensionTaskByProcessInstanceId(String processInstanceId, Query query);

    /**
     * @param params
     * @return void
     * @Description 通过流程实例id来更新最大审批天数值
     */
    public Result<String> updateExtensionCustomTaskById(ExtensionTaskQueryVo params);
}
