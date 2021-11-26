package com.sipsd.flow.rest.api;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.service.flowable.IFlowableTestProcessService;
import com.sipsd.flow.vo.flowable.StartProcessInstanceVo;
import com.sipsd.flow.vo.flowable.TestProcessIntsanceVo;
import com.sipsd.flow.vo.flowable.ret.ProcessInstanceVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: suxy
 * @Description: 流程测试
 * @DateTime:: 2021/11/26 14:48
 */
@RestController
@Api(tags = "流程测试")
@RequestMapping("/tool")
public class ApiFlowableTestProcessResource {

    @Autowired
    private IFlowableTestProcessService flowableTestProcessService;

    @ApiOperation("一键流程审批")
    @PostMapping("/testProcess")
    public Result<List<String>> testProcess(@RequestBody TestProcessIntsanceVo testProcessIntsanceVo){
        List<String> stringList = flowableTestProcessService.testProcess(testProcessIntsanceVo);
        return Result.ok(stringList);
    }

}
