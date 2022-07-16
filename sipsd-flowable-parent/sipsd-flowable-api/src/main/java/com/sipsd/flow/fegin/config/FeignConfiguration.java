/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.fegin.config;

import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.fegin.RemoteFlowableService;
import com.sipsd.flow.fegin.vo.AbstractModel;
import com.sipsd.flow.fegin.vo.FeginQueryVo;
import com.sipsd.flow.fegin.vo.FlowElementDto;
import com.sipsd.flow.fegin.vo.NoticeTaskQueryDto;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.*;
import com.sipsd.flow.vo.flowable.ret.FlowNodeVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import com.sipsd.flow.vo.flowable.ret.TaskVo;
import feign.Feign;
import feign.Retryer;
import feign.querymap.BeanQueryMapEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author 高强
 * @title: FeignConfiguration
 * @projectName sipsdx
 * @description: spirngboot版本问题-Feign发送get请求使用对象传参问题，@SpringQueryMap解析传参对象父类属性解决方案
 * @date 2022/3/28下午1:31
 */
@Configuration
@Slf4j
public class FeignConfiguration implements RemoteFlowableService
{

	/**
	 * @Description 替换解析queryMap的类，实现父类中变量的映射
	 * @date 2019/5/21 16:59
	 * @version V1.0.0
	 */
	@Bean
	public Feign.Builder feignBuilder() {
		return Feign.builder()
				.queryMapEncoder(new BeanQueryMapEncoder())
				.retryer(Retryer.NEVER_RETRY);
	}

	@Override
	public Result<String> startProcess(StartProcessInstanceVo startProcessInstanceVo)
	{

		return null;
	}

	@Override
	public Result<String> complete(CompleteTaskVo params)
	{

		return null;
	}

	@Override
	public PageModel<TaskExtensionVo> getExtensionTaskByProcessInstanceId(String processInstanceId, Query query)
	{

		return null;
	}

	@Override
	public PageModel<TaskExtensionVo> getFinishExtensionTaskByProcessInstanceId(String processInstanceId, Query query)
	{

		return null;
	}

	@Override
	public PageModel<TaskExtensionVo> getAllExtensionTaskByProcessInstanceId(String processInstanceId, Query query)
	{

		return null;
	}

	@Override
	public PageModel<TaskVo> getApplyingTasks(FeginQueryVo feginQueryVo)
	{

		return null;
	}

	@Override
	public PageModel<TaskVo> getApplyedTasks(FeginQueryVo feginQueryVo)
	{

		return null;
	}

	@Override
	public PageModel<TaskVo> getAllTasks(FeginQueryVo feginQueryVo)
	{

		return null;
	}

	@Override
	public Result<String> doJumpStep(BackTaskVo params)
	{

		return null;
	}

	@Override
	public Result<String> doBackStep(PreBackTaskVo params)
	{

		return null;
	}

	@Override
	public Result<String> stopProcess(EndProcessVo params)
	{

		return null;
	}

	@Override
	public Result<List<FlowNodeVo>> getBackNodesByProcessInstanceId(String processInstanceId, String taskId)
	{

		return null;
	}

	@Override
	public Result nextFlowNode(String node, String taskId)
	{

		return null;
	}

	@Override
	public Result<List<FlowElementDto>> getBackNodesByProcessInstanceId(String processKey)
	{

		return null;
	}

	@Override
	public Result<List<FlowElementDto>> getAllNodeListByProcessInstanceId(String processInstanceId)
	{

		return null;
	}

	@Override
	public Result<PageModel<AbstractModel>> pageModel(Query query)
	{

		return null;
	}

	@Override
	public Result<String> claimTask(ClaimTaskVo params)
	{

		return null;
	}

	@Override
	public Result<String> unClaimTask(ClaimTaskVo params)
	{

		return null;
	}

	@Override
	public Result<String> delegateTask(DelegateTaskVo params)
	{

		return null;
	}

	@Override
	public Result<String> turnTask(TurnTaskVo params)
	{

		return null;
	}

	@Override
	public Result<String> saDefinitionById(String id, int suspensionState, int overtime)
	{

		return null;
	}

	@Override
	public Result<String> updateExtensionCustomTaskById(ExtensionTaskQueryVo params)
	{

		return null;
	}

	@Override
	public Result<String> updateSupervisionTask(TaskExtensionVo params)
	{

		return null;
	}

	@Override
	public Result<String> addMultiInstanceExecution(AddSignTaskVo params)
	{

		return null;
	}

	@Override
	public Result<String> beforeAddSignTask(AddSignTaskVo params)
	{

		return null;
	}

	@Override
	public Result<String> afterAddSignTask(AddSignTaskVo params)
	{

		return null;
	}

	@Override
	public Result<String> queryUserListByGroupIds(List<String> groupIds)
	{

		return null;
	}

	@Override
	public PageModel<NoticeTask> getNoticeTasks(NoticeTaskQueryDto noticeTaskQueryDto)
	{

		return null;
	}

	@Override
	public Result<String> deleteProcessInstanceById(String processInstanceId)
	{

		return null;
	}

	@Override
	public Result<String> saveUser(UserEntityVo user)
	{

		return null;
	}

	@Override
	public Result<String> updateUser(UserEntityVo user)
	{

		return null;
	}

	@Override
	public Result<String> updateUser(String userId)
	{

		return null;
	}
}