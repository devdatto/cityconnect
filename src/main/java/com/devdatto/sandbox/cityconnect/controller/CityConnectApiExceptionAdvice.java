package com.devdatto.sandbox.cityconnect.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.devdatto.sandbox.cityconnect.exception.ApiException;
import com.devdatto.sandbox.cityconnect.exception.BadRequestException;
import com.devdatto.sandbox.cityconnect.exception.DataNotFoundException;
import com.devdatto.sandbox.cityconnect.exception.ServerErrorException;
import com.devdatto.sandbox.cityconnect.model.CityConnectApiErrorResponse;
import com.devdatto.sandbox.cityconnect.model.CityConnectApiResponse;
import com.devdatto.sandbox.cityconnect.model.CityConnectRequest;
import com.devdatto.sandbox.cityconnect.model.CityConnectResponse;
import com.devdatto.sandbox.cityconnect.util.ApiConstants;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class CityConnectApiExceptionAdvice {

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
	public <R, T> CityConnectApiResponse<R,T> handleApplicationServiceException(BadRequestException ex){
		log.error("Bad request status code returned. Message:{}", ex.getMessage());
		CityConnectApiErrorResponse error = CityConnectApiErrorResponse
				.builder()
				.errorClassName(ex.getClass().getName())
				.errorCode(HttpStatus.BAD_REQUEST.value())
				.errorMessage(ex.getMessage())
				.errorDescriptionUrl(ex.getStatusCode() + ":" + HttpStatus.BAD_REQUEST.name())
				.build();
										
		return CityConnectApiResponse.<R, T>builder().error(error).hasErrorResponse(true).build();
	}
	
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(DataNotFoundException.class)
	public CityConnectApiResponse<CityConnectRequest, CityConnectResponse> 
	handleApplicationServiceException(DataNotFoundException ex, WebRequest request){
		log.error("Input data not found. Message:{}", ex.getMessage());
		CityConnectApiErrorResponse error = CityConnectApiErrorResponse
				.builder()
				.errorClassName(ex.getClass().getName())
				.errorCode(HttpStatus.NOT_FOUND.value())
				.errorMessage(ex.getMessage())
				.errorDescriptionUrl(ex.getStatusCode() + ":" + HttpStatus.NOT_FOUND.name())
				.build();
		
		CityConnectRequest cityConnectRequest = 
				CityConnectRequest.builder()
								  .origin(request.getParameter(ApiConstants.PARAM_ORIGIN))
								  .destination(request.getParameter(ApiConstants.PARAM_DESTINATION))
								  .build();
		
		CityConnectResponse response = CityConnectResponse.builder().pathExists(false).build();
										
		return CityConnectApiResponse
				.<CityConnectRequest, CityConnectResponse>builder()
				.error(error)
				.hasErrorResponse(true)
				.request(cityConnectRequest)
				.response(response)
				.build();
	}
	
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServerErrorException.class)
	public <R, T> CityConnectApiResponse<R,T> handleApplicationServiceException(ServerErrorException ex){
		log.error("Internal exception status code returned. Message:{}", ex.getMessage());
		CityConnectApiErrorResponse error = CityConnectApiErrorResponse
				.builder()
				.errorClassName(ex.getClass().getName())
				.errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.errorMessage(ex.getMessage())
				.errorDescriptionUrl(ex.getStatusCode() + ":" + HttpStatus.INTERNAL_SERVER_ERROR.name())
				.build();
										
		return CityConnectApiResponse.<R, T>builder().error(error).hasErrorResponse(true).build();
	}
	
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ApiException.class)
	public <R, T> CityConnectApiResponse<R,T> handleApplicationServiceException(ApiException ex){
		log.error("Generic API exception status code returned. Message:{}", ex.getMessage());
		CityConnectApiErrorResponse error = CityConnectApiErrorResponse
				.builder()
				.errorClassName(ex.getClass().getName())
				.errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.errorMessage(ex.getMessage())
				.errorDescriptionUrl(ex.getStatusCode() + ":" + HttpStatus.INTERNAL_SERVER_ERROR.name())
				.build();
										
		return CityConnectApiResponse.<R, T>builder().error(error).hasErrorResponse(true).build();
	}
	
    @ExceptionHandler(Exception.class)
	public <R, T> CityConnectApiResponse<R,T> handleApplicationServiceException(Exception ex){
		log.error("Generic exception status code returned. Message:{}", ex.getMessage());
		CityConnectApiErrorResponse error = CityConnectApiErrorResponse
				.builder()
				.errorClassName(ex.getClass().getName())
				.errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.errorMessage(ex.getMessage())
				.errorDescriptionUrl(HttpStatus.INTERNAL_SERVER_ERROR.name())
				.build();
										
		return CityConnectApiResponse.<R, T>builder().error(error).hasErrorResponse(true).build();
	}

}
