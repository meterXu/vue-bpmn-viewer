package com.sipsd.flow.event;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * 
 * @author puhaiyang
 * @date 2018/12/19
 */
public class ManagerTaskHandler implements TaskListener
{

    @Override
    public void notify(DelegateTask delegateTask) {
        //TODO  查询出当前属于老板角色的人员
        delegateTask.setAssignee("经理");
        System.out.println("delegateTask.setAssignee(经理)");
    }

}
