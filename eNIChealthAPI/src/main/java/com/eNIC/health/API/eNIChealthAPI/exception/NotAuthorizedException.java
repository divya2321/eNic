package com.eNIC.health.API.eNIChealthAPI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NotAuthorizedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NotAuthorizedException(String exception) {
		super(exception);
	}
	
	public NotAuthorizedException(String exception, Throwable cause) {
		super(exception, cause);
	}

}
