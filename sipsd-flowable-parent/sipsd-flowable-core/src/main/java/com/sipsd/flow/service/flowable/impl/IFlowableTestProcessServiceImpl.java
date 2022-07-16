package com.sipsd.flow.service.flowable.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.exception.SipsdBootException;
import com.sipsd.flow.service.flowable.IFlowableExtensionTaskService;
import com.sipsd.flow.service.flowable.IFlowableProcessInstanceService;
import com.sipsd.flow.service.flowable.IFlowableTaskService;
import com.sipsd.flow.service.flowable.IFlowableTestProcessService;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.CompleteTaskVo;
import com.sipsd.flow.vo.flowable.StartProcessInstanceVo;
import com.sipsd.flow.vo.flowable.TestProcessIntsanceVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: suxy
 * @Description: 流程测试
 * @DateTime:: 2021/11/26 13:34
 */
@Service
@Slf4j
public class IFlowableTestProcessServiceImpl implements IFlowableTestProcessService {

    @Autowired
    IFlowableProcessInstanceService processInstanceService;

    @Autowired
    private IFlowableExtensionTaskService flowableExtensionTaskService;

    @Autowired
    private IFlowableTaskService flowableTaskService;

    @Override
    public  List<String> testProcess(TestProcessIntsanceVo testProcessIntsanceVo) {
        StartProcessInstanceVo startProcessInstanceVo = new StartProcessInstanceVo();
        BeanUtils.copyProperties(testProcessIntsanceVo,startProcessInstanceVo);
        startProcessInstanceVo.setBusinessKey("test-"+testProcessIntsanceVo.getProcessDefinitionKey());
        startProcessInstanceVo.setFormName("测试流程-"+testProcessIntsanceVo.getProcessDefinitionKey());
        Result<ProcessInstance> processInstanceResult = processInstanceService.startProcessInstanceByKey(startProcessInstanceVo);
        int code = processInstanceResult.getCode();
        if(code != Result.SUCCESS){
            throw new SipsdBootException("流程启动失败");
        }
        ProcessInstance processInstance = processInstanceResult.getData();
        String processInstanceId = processInstance.getProcessInstanceId();

        List<String> resultList = new ArrayList<>();

        Query query = new Query();
        query.setPageIndex(1);
        query.setPageNum(1);
        query.setPageSize(1000);
        this.doComplete( processInstanceId, query, resultList);
        return resultList;
    }
    public void doComplete(String processInstanceId,Query query, List<String> resultList){
        PageModel<TaskExtensionVo> pageModel = flowableExtensionTaskService.getExtensionTaskByProcessInstanceId(processInstanceId, query);
        List<TaskExtensionVo> taskExtensionVoList = pageModel.getData();
        if(CollUtil.isEmpty(taskExtensionVoList)){
            return;
        }
        for(TaskExtensionVo extensionVo : taskExtensionVoList){
            boolean b = this.completeTask(extensionVo, resultList);
            if (b){
                this.doComplete(processInstanceId,query,resultList);
                break;
            }else{
                return;
            }
        }

    }
    public  boolean completeTask( TaskExtensionVo extensionVo, List<String> resultList){
        StringBuffer resultInfo = new StringBuffer();
        String assignee = extensionVo.getAssignee();
        try {
            String processInstanceId = extensionVo.getProcessInstanceId();
            String taskId = extensionVo.getTaskId();
            CompleteTaskVo completeTaskVo = new CompleteTaskVo();
            completeTaskVo.setMessage("测试审批");
            completeTaskVo.setProcessInstanceId(processInstanceId);
            completeTaskVo.setTaskId(taskId);
            completeTaskVo.setUserCode(assignee);
            Result<String> complete = flowableTaskService.complete(completeTaskVo);
            resultInfo.append("任务节点：").append(extensionVo.getTaskName()).append("; ");
            resultInfo.append("审批人：").append(assignee).append("; ");
            resultInfo.append("审批状态：");
            if (complete.getCode() != Result.SUCCESS) {
                resultInfo.append("失败").append("; ");
                resultInfo.append("失败信息：").append(complete.getMessage());
                resultList.add(resultInfo.toString());
                return false;
            } else {
                resultInfo.append("成功").append("; ");
                resultList.add(resultInfo.toString());
            }
            return true;

        }catch (Exception e){
            resultInfo.append("任务节点：").append(extensionVo.getTaskName()).append("; ");
            resultInfo.append("审批人：").append(assignee).append("; ");
            resultInfo.append("审批状态：");
            resultInfo.append("失败").append("; ");
            resultInfo.append("失败信息：").append(e.getMessage());
            resultList.add(resultInfo.toString());
            String s = JSONUtil.toJsonStr(resultList);
            throw new SipsdBootException(s);
        }

    }
}
