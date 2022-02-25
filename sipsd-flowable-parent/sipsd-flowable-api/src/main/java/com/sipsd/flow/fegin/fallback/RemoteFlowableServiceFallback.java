package com.sipsd.flow.fegin.fallback;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RemoteFlowableServiceFallback implements FallbackFactory<RemoteFlowableServiceFallback>
{
	@Override
	public RemoteFlowableServiceFallback create(Throwable cause)
	{
		log.error(cause.getMessage());
		return new RemoteFlowableServiceFallback();
	}
}