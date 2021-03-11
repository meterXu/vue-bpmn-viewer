//
//package com.sipsd.flow.vo.api;
//
//import java.io.Serializable;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//import lombok.experimental.Accessors;
//
///**
// * 响应信息主体
// *
// * @param <T>
// * @author chengtg
// */
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
//@Accessors(chain = true)
//public class Result<T> implements Serializable {
//	private static final long serialVersionUID = 1L;
//
//	public static int SUCCESS = 0;
//	public static int FAIL = 500;
//	@Getter
//	@Setter
//	private int code;
//
//	@Getter
//	@Setter
//	private String message = "操作成功";
//
//	@Getter
//	@Setter
//	private boolean success = true;
//
//	@Getter
//	@Setter
//	private T data;
//
//	public Result<T> success(String message) {
//		this.message = message;
//		this.code = SUCCESS;
//		this.success = true;
//		return this;
//	}
//
//	public static <T> Result<T> ok() {
//		return restResult(null, SUCCESS, null);
//	}
//
//	public static <T> Result<T> ok(T data) {
//		if (data instanceof String) {
//			return restResult(data, SUCCESS, (String) data);
//		}
//		return restResult(data, SUCCESS, "操作成功");
//	}
//
//	public static <T> Result<T> ok(T data, String msg) {
//		return restResult(data, SUCCESS, msg);
//	}
//
//	public static <T> Result<T> failed() {
//		return restResult(null, FAIL, null);
//	}
//
//	public static <T> Result<T> failed(String msg) {
//		return restResult(null, FAIL, msg);
//	}
//
//	public static <T> Result<T> failed(T data) {
//		return restResult(data, FAIL, null);
//	}
//
//	public static <T> Result<T> failed(T data, String msg) {
//		return restResult(data, FAIL, msg);
//	}
//
//	private static <T> Result<T> restResult(T data, int code, String msg) {
//		Result<T> apiResult = new Result<>();
//		apiResult.setCode(code);
//		apiResult.setData(data);
//		apiResult.setMessage(msg);
//		return apiResult;
//	}
//
//	public Result(int code, String msg) {
//		this.code = code;
//		this.message = msg;
//	}
//}
