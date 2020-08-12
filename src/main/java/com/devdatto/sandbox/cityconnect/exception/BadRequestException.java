package com.devdatto.sandbox.cityconnect.exception;

public class BadRequestException extends ApiException{
	
	public BadRequestException(String str) {
		super(str);
	}

	public BadRequestException(String str, String statusCode) {
		super(str, statusCode);
	}
	
	public BadRequestException(Exception ex) {
		super(ex);
	}

}
