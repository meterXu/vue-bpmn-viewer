/*
 *
 *      Copyright (c) 2018-2025,  gaoqiang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the sipsdcloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author:  gaoqiang (mr6201745@163.com)
 *
 */

package com.sipsd.flow.util;

import com.sipsd.cloud.common.core.constant.CommonConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author  www.dpark.com.cn
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "响应信息主体")
public class Result<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@ApiModelProperty(value = "返回标记：成功标记=0，失败标记=1")
	private int code;

	@Getter
	@Setter
	@ApiModelProperty(value = "返回信息")
	private String message = "操作成功";
	
	@Getter
	@Setter
	@ApiModelProperty(value = "成功标志")
	private boolean success = true;

//	@Getter
//	@Setter
//	@ApiModelProperty(value = "数据")
//	private T result;
	
	/**
	 * 数据字典接口专用
	 */
	@Getter
	@Setter
	@ApiModelProperty(value = "数据")
	private T data;
	public static <T> Result<T> data(T data) {
		Result<T> apiResult = new Result<>();
		apiResult.setCode(0);
		apiResult.setData(data);
		apiResult.setMessage(null);
		return apiResult;
	}
	
	
	public Result<T> success(String message) {
		this.message = message;
		this.code = CommonConstants.SUCCESS;
		this.success = true;
		return this;
	}

	public static <T> Result<T> ok() {
		return restResult(null, CommonConstants.SUCCESS, null);
	}

	public static <T> Result<T> ok(T data) {
		return restResult(data, CommonConstants.SUCCESS, null);
	}

	public static <T> Result<T> ok(T data, String msg) {
		return restResult(data, CommonConstants.SUCCESS, msg);
	}

	public static <T> Result<T> failed() {
		return restResult(null, CommonConstants.FAIL, null);
	}

	public static <T> Result<T> failed(String msg) {
		return restResult(null, CommonConstants.FAIL, msg);
	}

	public static <T> Result<T> failed(T data) {
		return restResult(data, CommonConstants.FAIL, null);
	}

	public static <T> Result<T> failed(T data, String msg) {
		return restResult(data, CommonConstants.FAIL, msg);
	}

	private static <T> Result<T> restResult(T data, int code, String msg) {
		Result<T> apiResult = new Result<>();
		apiResult.setCode(code);
		apiResult.setData(data);
		//apiResult.setResult(data);
		apiResult.setMessage(msg);
		return apiResult;
	}
}
