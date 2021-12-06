package com.sipsd.flow.rest.api;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.exception.SipsdBootException;
import com.sipsd.flow.service.flowable.IFlowableTestProcessService;
import com.sipsd.flow.vo.flowable.TestProcessIntsanceVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class ApiFlowableTestProcessResource {

    @Autowired
    private IFlowableTestProcessService flowableTestProcessService;

    @ApiOperation("一键流程审批")
    @PostMapping("/testProcess")
    public Result<List<String>> testProcess(@RequestBody TestProcessIntsanceVo testProcessIntsanceVo){
        Result<List<String>> result = new Result<>();
        try {
            List<String> stringList = flowableTestProcessService.testProcess(testProcessIntsanceVo);
            result.setCode(Result.SUCCESS);
            result.setData(stringList);
            return result;
        }catch (Exception e){
            String message = e.getMessage();
            result.setCode(Result.FAIL);
            result.setSuccess(false);
            result.setMessage(message);
            if(e instanceof SipsdBootException){
                result.setMessage("操作失败");
                JSONArray objects = JSONUtil.parseArray(message);
                List<String> list = JSONUtil.toList(objects, String.class);
                result.setData(list);
            }
            return result;
        }

    }

}
