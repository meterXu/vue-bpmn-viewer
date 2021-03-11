package com.sipsd.flow.flowable.listener.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.impl.el.FixedValue;
import org.springframework.stereotype.Component;

import com.sipsd.flow.flowable.listener.BusinessCallListener;

/**
 * @Description: 任务监听回调
 * @Author: chengtg
 * @Since:13:39 2019/12/12
 * 2019 ~ 2030 版权所有
 */
@Component(value = "executionBusinessCallListener")
public class ExecutionBusinessCallListener extends BusinessCallListener implements ExecutionListener {
    /**
     * rest接口
     */
    private FixedValue restUrl;
    /**
     * 参数 多个的话用分号隔开 实例 userCode:00004737;status:1
     */
    private FixedValue params;

    @Override
    public void notify(DelegateExecution delegateExecution) {
        String processInstanceId = delegateExecution.getProcessInstanceId();
        String restUrlStr = null, paramsStr = null;
        if (restUrl != null) {
            restUrlStr = restUrl.getExpressionText();
        }
        if (params != null) {
            paramsStr = params.getExpressionText();
        }
        FlowElement f = delegateExecution.getCurrentFlowElement();
        List<String> assigneeList = new ArrayList<String>();
        
        // 设置下一节点参数：会签人列表：assigneeList
        delegateExecution.setVariable("assigneeList", Arrays.asList(params.getExpressionText().split(",")));
        //执行回调
        //TODO 临时处理
//        restUrlStr = "http://127.0.0.1:8989/rest/leave/updateLeaveStatus";
//        paramsStr = "status:1";
//        this.callBack(processInstanceId, restUrlStr, paramsStr);
    }

}
