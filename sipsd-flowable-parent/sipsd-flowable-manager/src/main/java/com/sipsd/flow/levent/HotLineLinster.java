//package com.sipsd.flow.levent;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.core.annotation.Order;
//import org.springframework.scheduling.annotation.Async;
//
//import com.sipsd.flow.model.hotline.HotlineBaseInfo;
//import com.sipsd.flow.service.flowable.IFlowableProcessInstanceService;
//import com.sipsd.flow.vo.flowable.StartProcessInstanceVo;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@AllArgsConstructor
//public class HotLineLinster {
//	@Autowired
//	private IFlowableProcessInstanceService flowableProcessInstanceService;
//
//	@Async
//	@Order
//	@EventListener(HotLineEvent.class)
//	public void saveHotLine(HotLineEvent event) {
//		HotlineBaseInfo hotlineBaseInfo = event.getHotline();
//		StartProcessInstanceVo startProcessInstanceVo = new StartProcessInstanceVo();
//		startProcessInstanceVo.setBusinessKey(hotlineBaseInfo.getId());
//		startProcessInstanceVo.setProcessDefinitionKey("hotline");
//		startProcessInstanceVo.setCurrentUserCode(hotlineBaseInfo.getSource());
//		startProcessInstanceVo.setSystemSn("flow");
//
//		flowableProcessInstanceService.startProcessInstanceByKey(startProcessInstanceVo);
//	}
//}
