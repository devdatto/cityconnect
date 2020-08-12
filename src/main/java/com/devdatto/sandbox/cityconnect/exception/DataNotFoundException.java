package com.devdatto.sandbox.cityconnect.exception;

public class DataNotFoundException extends ApiException{
	
	public DataNotFoundException(String str) {
		super(str);
	}

	public DataNotFoundException(String str, String statusCode) {
		super(str, statusCode);
	}
	
	public DataNotFoundException(Exception ex) {
		super(ex);
	}

}
