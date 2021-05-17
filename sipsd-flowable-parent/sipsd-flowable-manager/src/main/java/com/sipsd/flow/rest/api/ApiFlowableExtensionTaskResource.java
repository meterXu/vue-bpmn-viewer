package com.sipsd.flow.rest.api;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.service.flowable.IFlowableExtensionTaskService;
import com.sipsd.flow.vo.flowable.ExtensionTaskQueryVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author : 高强
 * @title: : ApiFlowableTaskResource
 * @projectName : flowable
 * @description: 自定义属性任务列表
 * @date : 2021/3/23下午3:24
 */
@Api(tags={"自定义属性任务列表"})
@RestController
@RequestMapping("/rest/extension/task")
public class ApiFlowableExtensionTaskResource extends BaseResource {

    @Autowired
    private IFlowableExtensionTaskService flowableExtensionTaskService;

    /**
     * 根据流程实例id查询自定义任务属性表
     *
     * @param processInstanceId 参数
     * @return
     */
    @ApiOperation("根据流程实例id查询代办任务")
    @GetMapping(value = "/get-extension-tasks")
    public PageModel<TaskExtensionVo> getExtensionTaskByProcessInstanceId(@RequestParam(required = false) String processInstanceId, Query query) {
        return flowableExtensionTaskService.getExtensionTaskByProcessInstanceId(processInstanceId,query);
    }

    /**
     * 根据流程实例id查询历史自定义任务属性表(已办代办查询)
     *
     * @param processInstanceId 参数
     * @return
     */
    @ApiOperation("根据流程实例id查询已办代办任务")
    @GetMapping(value = "/get-all-extension-tasks")
    public PageModel<TaskExtensionVo> getAllExtensionTaskByProcessInstanceId(@RequestParam(required = false) String processInstanceId, Query query) {
        return flowableExtensionTaskService.getAllExtensionTaskByProcessInstanceId(processInstanceId,query);
    }

    /**
     * 根据流程实例id查询历史自定义任务属性表(已办代办查询)
     *
     * @param processInstanceId 参数
     * @return
     */
    @ApiOperation("根据流程实例id查询已办任务")
    @GetMapping(value = "/get-finish-extension-tasks")
    public PageModel<TaskExtensionVo> getFinishExtensionTaskByProcessInstanceId(@RequestParam(required = false) String processInstanceId, Query query) {
        return flowableExtensionTaskService.getFinishExtensionTaskByProcessInstanceId(processInstanceId,query);
    }

    /**
     * 通过流程实例id来更新最大审批天数值
     *
     * @param params 参数
     * @return
     */
    @ApiOperation("通过流程实例id来更新最大审批天数值")
    @PostMapping(value = "/update-extension-tasks")
    public Result<String> updateExtensionCustomTaskById(@RequestBody ExtensionTaskQueryVo params) {
        return flowableExtensionTaskService.updateExtensionCustomTaskById(params);
    }
}
