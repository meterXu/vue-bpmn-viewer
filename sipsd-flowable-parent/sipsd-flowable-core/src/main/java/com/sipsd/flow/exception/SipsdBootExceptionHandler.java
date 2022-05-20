package com.sipsd.flow.exception;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.sipsd.cloud.common.core.util.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理器
 * 
 * @author gaoqiang
 * @Date 2019
 */
@RestControllerAdvice
@Slf4j
public class SipsdBootExceptionHandler {

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(SipsdBootException.class)
	public Result<?> handleRRException(SipsdBootException e) {
		log.error(e.getMessage(), e);
		return Result.failed(e.getMessage());
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public Result<?> handlerNoFoundException(Exception e) {
		log.error(e.getMessage(), e);
		return Result.failed(404, "路径不存在，请检查路径是否正确");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
		log.error(e.getMessage(), e);
		return Result.failed("操作失败，" + e.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e) {
		log.error(e.getMessage(), e);
		return Result.failed("操作失败，" + e.getMessage());
	}

	/**
	 * @Author 政辉
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Result<?> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		StringBuffer sb = new StringBuffer();
		sb.append("不支持");
		sb.append(e.getMethod());
		sb.append("请求方法，");
		sb.append("支持以下");
		String[] methods = e.getSupportedMethods();
		if (methods != null) {
			for (String str : methods) {
				sb.append(str);
				sb.append("、");
			}
		}
		log.error(sb.toString(), e);
		// return Result.failed("没有权限，请联系管理员授权");
		return Result.failed(405, sb.toString());
	}

	/**
	 * spring默认上传大小100MB 超出大小捕获异常MaxUploadSizeExceededException
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public Result<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		log.error(e.getMessage(), e);
		return Result.failed("文件大小超出10MB限制, 请压缩或降低文件质量! ");
	}

}
