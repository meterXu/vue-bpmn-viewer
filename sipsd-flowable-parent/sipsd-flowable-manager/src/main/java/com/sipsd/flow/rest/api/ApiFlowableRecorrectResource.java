package com.sipsd.flow.rest.api;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.service.flowable.IFlowableExtensionTaskService;
import com.sipsd.flow.service.flowable.IFlowableTaskService;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : 高强
 * @title: : ApiFlowableRecorrectResource
 * @projectName : flowable
 * @description: 错误数据修正
 * @date : 2022/05/17下午3:24
 */
@Api(tags={"错误数据修正"})
@RestController
@RequestMapping("/rest/recorrect/task")
@Slf4j
public class ApiFlowableRecorrectResource extends BaseResource {

    @Autowired
    private IFlowableExtensionTaskService flowableExtensionTaskService;
    @Autowired
    private IFlowableTaskService flowableTaskService;

    /**
     * 修正act_run_task和act_run_extension_task中assignee中不一致的情况
     * @return
     */
    @ApiOperation("修正审批人")
    @PostMapping(value = "/correct-assignee-task")
    public Result<String> correctAssigneeTask() {
        //遍历所有act_run_task中的待办数据
        List<TaskExtensionVo> taskExtensionVoList = flowableExtensionTaskService.getAssigneeListByProcessInstanceIdAndTaskId();
        for(TaskExtensionVo taskExtensionVo:taskExtensionVoList)
        {
            //通过实例id和任务id查询act_run_task表中的assignee
            String assignee = flowableTaskService.getAssigneeByProcessInstanceIdAndTaskId(taskExtensionVo.getProcessInstanceId(),taskExtensionVo.getTaskId());
            if(StringUtils.isNotBlank(assignee) && StringUtils.isNotBlank(taskExtensionVo.getAssignee()))
            {
                if(!assignee.equals(taskExtensionVo.getAssignee()))
                {
                    log.info("实例ID:"+ taskExtensionVo.getProcessInstanceId() + "--taskId为:"+ taskExtensionVo.getTaskId()+"--错误的assignee为:"+ assignee+"--正确的assignee为:"+taskExtensionVo.getAssignee());
                    //不一致的情况下更新数据
                    //  flowableTaskService.updateAssigneeByProcessInstanceIdAndTaskId(taskExtensionVo.getAssignee(),taskExtensionVo.getProcessInstanceId(),taskExtensionVo.getTaskId());
                }
            }
        }
        return Result.ok();
    }
}
