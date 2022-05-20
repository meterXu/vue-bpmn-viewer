package com.sipsd.flow.rest.api;


import com.sipsd.flow.bean.NoticeTask;
import com.sipsd.flow.service.flowable.IFlowableNoticeTaskService;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.NoticeTaskQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: suxy
 * @Description: 抄送任务
 * @DateTime:: 2021/11/15 13:37
 */
@RestController
@Api(tags = "抄送任务")
@RequestMapping("/rest/noticeTask")
public class ApiFlowableNoticeTaskResource {

    @Autowired
    IFlowableNoticeTaskService noticeTaskService;

    @ApiOperation(value = "查询抄送任务列表")
    @GetMapping("/noticeTaskList")
    public Result<List<NoticeTask>> getNoticeTaskList(NoticeTaskQuery taskQuery){
        List<NoticeTask> noticeTaskList = noticeTaskService.noticeTaskList(taskQuery);
        return Result.ok(noticeTaskList);
    }

    @ApiOperation(value = "查询抄送任务详情")
    @GetMapping("/queryById")
    public Result<NoticeTask> queryById(String  id){
        NoticeTask noticeTask = noticeTaskService.queryById(id);
        return Result.ok(noticeTask);
    }
}
