package com.sipsd.flow.exception;

public class SipsdBootException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SipsdBootException(String message){
		super(message);
	}
	
	public SipsdBootException(Throwable cause)
	{
		super(cause);
	}
	
	public SipsdBootException(String message,Throwable cause)
	{
		super(message,cause);
	}
}
