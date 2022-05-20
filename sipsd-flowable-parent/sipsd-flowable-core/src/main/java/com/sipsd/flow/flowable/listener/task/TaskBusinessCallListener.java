package com.sipsd.flow.flowable.listener.task;

import com.sipsd.flow.flowable.listener.BusinessCallListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.impl.el.FixedValue;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @Description: 任务监听回调
 * @Author: gaoqiang
 * @Since:13:39 2019/12/12
 * 2019 ~ 2030 版权所有
 */
@Scope
@Component(value = "taskBusinessCallListener")
public class TaskBusinessCallListener extends BusinessCallListener implements TaskListener {
    /**
     * rest接口
     */
    private FixedValue restUrl;
    /**
     * 参数 多个的话用分号隔开 实例 userCode:00004737;status:1
     */
    private FixedValue params;

    @Override
    public void notify(DelegateTask delegateTask) {
        //当使用create，assignment时这个会调用两次代表当前代办的taskId和即将跳转到的节点的taskId
        String restUrlStr = null, paramsStr = null;
        String processInstanceId = delegateTask.getProcessInstanceId();
        String taskId = delegateTask.getId();
        String processInstanceIdVar = delegateTask.getVariable("processInstanceIdVar")==null?"":delegateTask.getVariable("processInstanceIdVar").toString();
        String taskIdVar = delegateTask.getVariable("taskIdVar")==null?"":delegateTask.getVariable("taskIdVar").toString();
        restUrlStr =  delegateTask.getVariable("url")==null?"":delegateTask.getVariable("url").toString();
        paramsStr = delegateTask.getVariable("params")==null?"":delegateTask.getVariable("params").toString();

        if(processInstanceIdVar.equals(processInstanceId) && taskId.equals(taskIdVar))
        {
            this.callBack(processInstanceId, restUrlStr, paramsStr);
        }

//        String restUrlStr = null, paramsStr = null;
//        if (restUrl != null) {
//            restUrlStr = restUrl.getExpressionText();
//        }
//        if (params != null) {
//            paramsStr = params.getExpressionText();
//        }
//        //执行回调
//        //TODO 临时处理
//        restUrlStr = "http://192.168.75.5:9001/sipsd-flow-modeler/rest/extension/task/update-extension-tasks";
//        paramsStr = "processInstanceId:1;customTaskMaxDay:5;taskId:3ea43d97e2ae11eb989bacde48001122";
//        this.callBack(processInstanceId, restUrlStr, paramsStr);
    }
}
