package com.sipsd.flow.service.flowable;


import com.sipsd.flow.vo.flowable.StartProcessInstanceVo;
import com.sipsd.flow.vo.flowable.TestProcessIntsanceVo;

import java.util.List;

/**
 * @Author: suxy
 * @Description: 流程测试
 * @DateTime:: 2021/11/26 13:33
 */
public interface IFlowableTestProcessService {

    public List<String> testProcess(TestProcessIntsanceVo testProcessIntsanceVo);
}
