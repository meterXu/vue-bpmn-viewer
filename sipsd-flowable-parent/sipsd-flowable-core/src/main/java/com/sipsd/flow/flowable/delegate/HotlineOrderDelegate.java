package com.sipsd.flow.flowable.delegate;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class HotlineOrderDelegate implements JavaDelegate {

	private Expression fieldName;

	@Override
	public void execute(DelegateExecution execution) {
		// TODO Auto-generated method stub

		System.out.println(1);
	}

}
