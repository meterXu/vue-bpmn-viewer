package com.sipsd.flow.rest.api;

import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.model.leave.Leave;
import com.sipsd.flow.service.flowable.IFlowableProcessInstanceService;
import com.sipsd.flow.service.flowable.IFlowableTaskService;
import com.sipsd.flow.service.leave.ILeaveService;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.*;
import com.sipsd.flow.vo.flowable.ret.FormInfoVo;
import com.sipsd.flow.vo.flowable.ret.ProcessInstanceVo;
import com.sipsd.flow.vo.flowable.ret.TaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : gaoqiang
 * @title: : ApiFlowableTaskResource
 * @projectName : flowable
 * @description: 任务列表
 * @date : 2019/11/2115:34
 */
@Api(tags={"任务列表"})
@RestController
@RequestMapping("/rest/task")
public class ApiFlowableTaskResource extends BaseResource {

    @Autowired
    private IFlowableTaskService flowableTaskService;
    @Autowired
    private IFlowableProcessInstanceService flowableProcessInstanceService;
    @Autowired
    private ILeaveService leaveService;
    @Autowired
    private HistoryService historyService;

    /**
     * 获取待办任务列表
     *
     * @param params 参数
     * @param query  查询条件
     * @return
     */
    @ApiOperation("获取待办任务列表")
    @GetMapping(value = "/get-applying-tasks")
    public PageModel<TaskVo> getApplyingTasks(TaskQueryVo params, Query query) {
        //params.setUserCode(this.getLoginUser().getId());
        PageModel<TaskVo> pm = flowableTaskService.getApplyingTasks(params, query);
        return pm;
    }

    /**
     * 获取已办任务列表
     *
     * @param params 参数
     * @param query  查询条件
     * @return
     */
    @ApiOperation("获取已办任务列表")
    @GetMapping(value = "/get-applyed-tasks")
    public PageModel<TaskVo> getApplyedTasks(TaskQueryVo params, Query query) {
        //params.setUserCode(this.getLoginUser().getId());
        PageModel<TaskVo> pm = flowableTaskService.getApplyedTasks(params, query);
        return pm;
    }


    /**
     * 获取已办任务列表
     *
     * @param params 参数
     * @param query  查询条件
     * @return
     */
    @ApiOperation("获取全部任务列表(包括已办待办)")
    @GetMapping(value = "/get-all-tasks")
    public PageModel<TaskVo> getAllTasks(TaskQueryVo params, Query query) {
        PageModel<TaskVo> pm = flowableTaskService.getApplyedTasks(params, query);
        return pm;
    }

    @ApiOperation("获取已办Http任务列表")
    @GetMapping(value = "/get-http-tasks")
    public List<HistoricActivityInstance> getHttpTasks(@RequestParam("processInstanceId") String processInstanceId) {
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).activityType("httpServiceTask").list();
        return historicActivityInstances;
    }

    /**
     * 获取已办任务列表
     *
     * @param params 参数
     * @param query  查询条件
     * @return
     */
    @ApiOperation("获取我创建的流程")
    @GetMapping(value = "/my-processInstances")
    public PageModel<ProcessInstanceVo> myProcessInstances(ProcessInstanceQueryVo params, Query query) {
        //params.setUserCode(this.getLoginUser().getId());
        PageModel<ProcessInstanceVo> pm = flowableProcessInstanceService.getMyProcessInstances(params, query);
        return pm;
    }

    
//    @ApiOperation("查询下一任务")
//    @GetMapping(value = "/my-processInstances")
//    public PagerModel<ProcessInstanceVo> myProcessInstances(ProcessInstanceQueryVo params, Query query) {
//        params.setUserCode(this.getLoginUser().getId());
//        PagerModel<ProcessInstanceVo> pm = flowableProcessInstanceService.getMyProcessInstances(params, query);
//        return pm;
//    }


    /**
     * 查询表单详情
     * @param params 参数
     * @return
     */
    @ApiOperation("查询表单详情")
    @PostMapping(value = "/find-formInfo")
    public Result<FormInfoVo> findFormInfoByFormInfoQueryVo(FormInfoQueryVo params) throws Exception{
        Result<FormInfoVo> result = Result.sucess("OK");
        FormInfoVo<Leave> formInfoVo = new FormInfoVo<Leave>(params.getTaskId(),params.getProcessInstanceId());
        String processInstanceId = params.getProcessInstanceId();
        String businessKey = null;
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null){
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            businessKey = historicProcessInstance.getBusinessKey();
        }else {
            businessKey = processInstance.getBusinessKey();
        }
        Leave leave = leaveService.getLeaveById(businessKey);
        formInfoVo.setFormInfo(leave);
        result.setData(formInfoVo);
        return result;
    }

    /**
     * 任务详情
     * @param params 参数
     * @return
     */
    @ApiOperation("任务详情")
    @GetMapping(value = "/get-task")
    public Result<TaskVo> getTask(FormInfoQueryVo params) throws Exception{
        Result<TaskVo> result = Result.sucess("OK");
        result.setData(flowableTaskService.getTask(params));
        return result;
    }

    /**
     * 获取抄送任务列表
     *
     * @param params 参数
     * @param query  查询条件
     * @return
     */
    @ApiOperation("获取抄送任务列表")
    @GetMapping(value = "/getNoticeTasks")
    public PageModel<NoticeTask> getNoticeTasks(NoticeTaskQuery params, Query query) {
        return flowableTaskService.getNoticeTasks(params, query);
    }

}
