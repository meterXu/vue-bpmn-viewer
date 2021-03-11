package com.sipsd.flow.service.flowable.impl;

import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.dao.flowable.IFlowableProcessDefinitionDao;
import com.sipsd.flow.service.flowable.IFlowableProcessDefinitionService;
import com.sipsd.flow.vo.flowable.ProcessDefinitionQueryVo;
import com.sipsd.flow.vo.flowable.ret.ProcessDefinitionVo;

/**
 * @author : chengtg
 * @title: : FlowableProcessDefinitionServiceImpl
 * @projectName : flowable
 * @description: 流程定义service
 * @date : 2019/11/1314:18
 */
@Service
public class FlowableProcessDefinitionServiceImpl extends BaseProcessService implements IFlowableProcessDefinitionService {

	@Autowired
	private RepositoryService repositoryService;

    @Autowired
    private IFlowableProcessDefinitionDao flowableProcessDefinitionDao;

    @Override
    public PageModel<ProcessDefinitionVo> getPagerModel(ProcessDefinitionQueryVo params, Query query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Page<ProcessDefinitionVo> page = flowableProcessDefinitionDao.getPagerModel(params);
        return new PageModel<>(page);
    }

    @Override
    public ProcessDefinitionVo getById(String processDefinitionId) {
        return flowableProcessDefinitionDao.getById(processDefinitionId);
    }

    @Override
    public Result suspendOrActivateProcessDefinitionById(String processDefinitionId,int suspensionState) {
        Result result = null;
        if (suspensionState == 1){
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            result = Result.sucess("挂起成功");
        }else {
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            result = Result.sucess("激活成功");
        }
        return result;
    }

}
