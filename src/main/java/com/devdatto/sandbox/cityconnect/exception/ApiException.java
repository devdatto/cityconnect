package com.devdatto.sandbox.cityconnect.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends RuntimeException {
	private String statusCode;
	
	public ApiException(String str) {
		super(str);
	}
	
	public ApiException(String str, String statusCode) {
		super(str);
		this.statusCode = statusCode;
	}
	
	public ApiException(Exception ex) {
		super(ex);
	}

}
