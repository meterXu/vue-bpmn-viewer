package com.sipsd.flow.rest.api;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.dao.flowable.IFlowableExtensionTaskDao;
import com.sipsd.flow.dao.flowable.IFlowableTaskDao;
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
    private IFlowableExtensionTaskDao flowableExtensionTaskDao;
    @Autowired
    private IFlowableTaskDao flowableTaskDao;

    /**
     * 修正act_run_task和act_run_extension_task中assignee中不一致的情况
     * @return
     */
    @ApiOperation("修正审批人")
    @PostMapping(value = "/correct-assignee-task")
    public Result<String> correctAssigneeTask() {
        //遍历所有act_run_task中的待办数据
        List<TaskExtensionVo> taskExtensionVoList = flowableTaskDao.getAssigneeListByProcessInstanceIdAndTaskId();
        for(TaskExtensionVo taskExtensionVo:taskExtensionVoList)
        {
            //根据实例ID和任务ID以及taskdefKey判断是否是有多个
            Integer count = flowableExtensionTaskDao.getCountByProcessInstanceIdAndTaskId(taskExtensionVo.getProcessInstanceId(),taskExtensionVo.getTaskId(),taskExtensionVo.getTaskDefinitionKey());
            if(count>1)
            {
                //该节点被执行过，可能是被驳回了 1.确实是驳回的节点待办,extension的assignee肯定不为空 2.正常审批的节点再次待办有可能assignee是空
                String assignee = flowableExtensionTaskDao.getAssigneeByProcessInstanceIdAndTaskId(taskExtensionVo.getProcessInstanceId(),taskExtensionVo.getTaskId());
                assignee = assignee==null?"":assignee;
                String taskAssignee = taskExtensionVo.getAssignee()==null?"":taskExtensionVo.getAssignee();
                if(StringUtils.isNotBlank(assignee))
                {
                    if(!assignee.equals(taskAssignee))
                    {
                        log.info("实例ID:"+ taskExtensionVo.getProcessInstanceId() + "--taskId为:"+ taskExtensionVo.getTaskId()+"--错误的assignee为:"+ assignee+"--正确的assignee为:"+taskExtensionVo.getAssignee());
                        //不一致的情况下更新数据
                        //  flowableTaskDao.updateAssigneeByProcessInstanceIdAndTaskId(taskExtensionVo.getAssignee(),taskExtensionVo.getProcessInstanceId(),taskExtensionVo.getTaskId());
                    }
                }
            }
        }
        return Result.ok();
    }
}
