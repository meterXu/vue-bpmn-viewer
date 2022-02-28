/*
 *
 *      Copyright (c) 2018-2025, gaoq All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the sipsdcloudd.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: gaoq (wangiegie@gmail.com)
 *
 */

package com.sipsd.flow.fegin;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.vo.flowable.*;
import com.sipsd.flow.vo.flowable.ret.FlowNodeVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import com.sipsd.flow.vo.flowable.ret.TaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gaoq
 * @date 2020/6/22
 */
@Api(tags = "工作流fegin接口")
@FeignClient(name = "RemoteFlowableService", url = "http://192.168.126.25/sipsd-flow-modeler")
public interface RemoteFlowableService
{
	/**
	 * 流程启动
	 *
	 * @param startProcessInstanceVo 参数
	 * @return
	 */
	@ApiOperation("流程启动")
	@PostMapping(value = "/rest/formdetail/startProcess")
	public Result<String> startProcess(@Validated @RequestBody StartProcessInstanceVo startProcessInstanceVo);


	/**
	 * 审批任务
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("审批任务")
	@PostMapping(value = "/rest/formdetail/complete")
	public Result<String> complete(@RequestBody @Validated CompleteTaskVo params);

	/**
	 * 根据流程实例id查询自定义任务属性表
	 *
	 * @param processInstanceId 参数
	 * @return
	 */
	@ApiOperation("根据流程实例id查询待办任务")
	@GetMapping(value = "/rest/extension/task/get-extension-tasks")
	public PageModel<TaskExtensionVo> getExtensionTaskByProcessInstanceId(@RequestParam(required = false) String processInstanceId,@SpringQueryMap Query query);


	/**
	 * 根据流程实例id查询历史自定义任务属性表(已办代办查询)
	 *
	 * @param processInstanceId 参数
	 * @return
	 */
	@ApiOperation("根据流程实例id查询已办任务")
	@GetMapping(value = "/rest/extension/task/get-finish-extension-tasks")
	public PageModel<TaskExtensionVo> getFinishExtensionTaskByProcessInstanceId(@RequestParam(required = false) String processInstanceId,@SpringQueryMap Query query);


	/**
	 * 获取待办任务列表
	 *
	 * @param feginQueryVo 参数
	 * @return
	 */
	@ApiOperation("获取待办任务列表")
	@GetMapping(value = "/rest/task/get-applying-tasks")
	public PageModel<TaskVo> getApplyingTasks(@SpringQueryMap FeginQueryVo feginQueryVo);


	/**
	 * 获取已办任务列表
	 *
	 * @param feginQueryVo 参数
	 * @return
	 */
	@ApiOperation("获取已办任务列表")
	@GetMapping(value = "/rest/task/get-applyed-tasks")
	public PageModel<TaskVo> getApplyedTasks(@SpringQueryMap FeginQueryVo feginQueryVo);



	/**
	 * 跳转
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("跳转")
	@PostMapping(value = "/rest/formdetail/doJumpStep")
	public Result<String> doJumpStep(@Validated @RequestBody BackTaskVo params);

	/**
	 * 驳回
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("驳回")
	@PostMapping(value = "/rest/formdetail/doBackStep")
	public Result<String> doBackStep(@Validated @RequestBody PreBackTaskVo params);


	/**
	 * 终止
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("终止")
	@PostMapping(value = "/rest/formdetail/stopProcess")
	public Result<String> stopProcess(@Validated @RequestBody EndProcessVo params);


	/**
	 * 获取可驳回节点列表
	 *
	 * @param processInstanceId 流程实例id
	 * @return
	 */
	@ApiOperation("获取可驳回节点列表")
	@GetMapping(value = "/rest/formdetail/getBackNodesByProcessInstanceId/{processInstanceId}/{taskId}")
	public Result<List<FlowNodeVo>> getBackNodesByProcessInstanceId(@PathVariable String processInstanceId,
																	@PathVariable String taskId);


}
