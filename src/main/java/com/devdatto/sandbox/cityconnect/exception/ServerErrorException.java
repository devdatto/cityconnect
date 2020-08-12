package com.devdatto.sandbox.cityconnect.exception;

public class ServerErrorException extends ApiException {

	public ServerErrorException(String str) {
		super(str);
	}

	public ServerErrorException(String str, String statusCode) {
		super(str, statusCode);
	}

	public ServerErrorException(Exception ex) {
		super(ex);
	}

}
