/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.utils;

import com.sipsd.flow.constant.CommonConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author gaoqiang
 */
@ApiModel(value = "响应信息主体")
public class Result<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() { return "Result(code=" + getCode() + ", message=" + getMessage() + ", success=" + isSuccess() + ", data=" + getData() + ")"; }

    public Result(int code, String message, boolean success, T data) { this.code = code;
        this.message = message;
        this.success = success;
        this.data = data; }

    public static int SUCCESS = 0;

    public static int FAIL = 1;

    @ApiModelProperty("返回标记：成功标记=0，失败标记=1")
    private int code;

    public int getCode() { return this.code; }

    public Result<T> setCode(int code) { this.code = code;
        return this; }

    @ApiModelProperty("返回信息")
    private String message = "操作成功";

    public String getMessage() { return this.message; }

    public Result<T> setMessage(String message) { this.message = message;
        return this; }

    @ApiModelProperty("成功标志")
    private boolean success = true;

    @ApiModelProperty("数据")
    private T data;

    public boolean isSuccess() { return this.success; }

    public Result<T> setSuccess(boolean success) { this.success = success;
        return this; }

    public T getData() { return this.data; }

    public Result<T> setData(T data) { this.data = data;
        return this; }

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

    public static <T> Result<T> ok() { return restResult(null, CommonConstants.SUCCESS, null); }

    public static <T> Result<T> ok(T data) { return restResult(data, CommonConstants.SUCCESS, null); }

    public static <T> Result<T> ok(T data, String msg) { return restResult(data, CommonConstants.SUCCESS, msg); }

    public static <T> Result<T> failed() { return restResult(null, CommonConstants.FAIL, null); }

    public static <T> Result<T> failed(String msg) { return restResult(null, CommonConstants.FAIL, msg); }

    public static <T> Result<T> failed(T data) { return restResult(data, CommonConstants.FAIL, null); }

    public static <T> Result<T> failed(T data, String msg) { return restResult(data, CommonConstants.FAIL, msg); }

    public static <T> Result<T> sucess(String msg) { return restResult(null, CommonConstants.SUCCESS, msg); }

    private static <T> Result<T> restResult(T data, int code, String msg) {
        Result<T> apiResult = new Result<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(msg);
        return apiResult;
    }

    public Result() {}
}
