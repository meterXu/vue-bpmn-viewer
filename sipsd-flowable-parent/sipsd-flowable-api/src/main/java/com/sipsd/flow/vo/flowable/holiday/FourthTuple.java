/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.vo.flowable.holiday;

/**
 * @author 高强
 * @title: FourthTuple
 * @projectName sipsdx
 * @description: TODO
 * @date 2021/9/30下午2:01
 */
public class FourthTuple <A,B,C,D> extends ThreeTuple<A,B,C>
{
	public final D fourth;

	public FourthTuple(A first, B second, C third,D fourth) {
		super(first, second,third);
		this.fourth = fourth;
	}

	@Override
	public String toString() {
		return "("+first+","+second+","+third+")";
	}
}