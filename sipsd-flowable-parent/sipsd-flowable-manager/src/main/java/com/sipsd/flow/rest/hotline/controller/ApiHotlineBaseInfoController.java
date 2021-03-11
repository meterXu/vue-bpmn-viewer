//package com.sipsd.flow.rest.hotline.controller;
//
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.sipsd.cloud.common.log.annotation.SysLog;
//import com.sipsd.flow.levent.HotLineEvent;
//import com.sipsd.flow.model.hotline.HotlineBaseInfo;
//import com.sipsd.flow.service.hotline.IHotlineBaseInfoService;
//import com.sipsd.flow.vo.api.Result;
//
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * @Description: 便民热线API接口
// * @Author: chengtg
// * @Date: 2020-11-19
// * @Version: V1.0
// */
//@Slf4j
//@Api(tags = "便民热线API接口")
//@RestController
//@RequestMapping("/api/flow/hotline")
//@AllArgsConstructor
//public class ApiHotlineBaseInfoController {
//	private final IHotlineBaseInfoService hotlineBaseInfoService;
//	private final ApplicationEventPublisher publisher;
//
//	/**
//	 * 添加-启动工作流程
//	 *
//	 * @param hotlineBaseInfo
//	 * @return
//	 */
//	@SysLog(name = "便民热线12345", operateType = 1)
//	@ApiOperation(value = "工单基本信息-添加", notes = "工单基本信息-添加")
//	@PostMapping(value = "/add")
//	public Result<?> add(@RequestBody HotlineBaseInfo hotlineBaseInfo) {
//		hotlineBaseInfoService.save(hotlineBaseInfo);
//		
//		// 发布事件--启动流程
//		publisher.publishEvent(new HotLineEvent(hotlineBaseInfo));
//		return Result.ok("添加成功！");
//	}
//	
//
//}
