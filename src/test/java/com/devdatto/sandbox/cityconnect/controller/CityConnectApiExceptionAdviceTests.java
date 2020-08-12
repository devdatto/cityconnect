package com.devdatto.sandbox.cityconnect.controller;

import static org.mockito.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import com.devdatto.sandbox.cityconnect.controller.CityConnectApiExceptionAdvice;
import com.devdatto.sandbox.cityconnect.exception.ApiException;
import com.devdatto.sandbox.cityconnect.exception.BadRequestException;
import com.devdatto.sandbox.cityconnect.exception.DataNotFoundException;
import com.devdatto.sandbox.cityconnect.exception.ServerErrorException;
import com.devdatto.sandbox.cityconnect.model.CityConnectApiErrorResponse;
import com.devdatto.sandbox.cityconnect.model.CityConnectApiResponse;
import com.devdatto.sandbox.cityconnect.model.CityConnectRequest;
import com.devdatto.sandbox.cityconnect.model.CityConnectResponse;

import com.devdatto.sandbox.cityconnect.util.ApiConstants;

public class CityConnectApiExceptionAdviceTests {
	CityConnectApiExceptionAdvice exceptionAdvice;
	
	@BeforeEach
	public void setUp() {
		exceptionAdvice = new CityConnectApiExceptionAdvice();
	}
	
	@Test
	public void testHandleBadRequestException() {
		BadRequestException badRequestException = new BadRequestException(
				"Bad Request", ApiConstants.ERROR_CODE_BAD_REQUEST);
		CityConnectApiResponse<CityConnectRequest, CityConnectResponse> 
		apiResponse = exceptionAdvice.handleApplicationServiceException(
				badRequestException);
		
		assertTrue(apiResponse.isHasErrorResponse());
		assertNotNull(apiResponse.getError());
		
		CityConnectApiErrorResponse error = apiResponse.getError();
		
		assertThat(error.getErrorClassName(), 
				is(badRequestException.getClass().getName()));
		assertThat(error.getErrorCode(), is(HttpStatus.BAD_REQUEST.value()));
		assertThat(error.getErrorMessage(), is(badRequestException.getMessage()));
		assertThat(error.getErrorDescriptionUrl(), 
				is(badRequestException.getStatusCode() 
						+ ":" + HttpStatus.BAD_REQUEST.name()));
	}
	
	@Test
	public void testHandleServerErrorException() {
		ServerErrorException serverErrorException = new ServerErrorException(
				"Internal Server Error", ApiConstants.ERROR_CODE_SERVER_ERROR);
		CityConnectApiResponse<CityConnectRequest, CityConnectResponse> 
		apiResponse = exceptionAdvice.handleApplicationServiceException(
				serverErrorException);
		
		assertTrue(apiResponse.isHasErrorResponse());
		assertNotNull(apiResponse.getError());
		
		CityConnectApiErrorResponse error = apiResponse.getError();
		
		assertThat(error.getErrorClassName(), 
				is(serverErrorException.getClass().getName()));
		assertThat(error.getErrorCode(), 
				is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		assertThat(error.getErrorMessage(), is(serverErrorException.getMessage()));
		assertThat(error.getErrorDescriptionUrl(), 
				is(serverErrorException.getStatusCode()
						+ ":" + HttpStatus.INTERNAL_SERVER_ERROR.name()));
	}
	
	@Test
	public void testHandleApiException() {
		ApiException apiException = new ApiException(
				"Generic Exception", ApiConstants.ERROR_CODE_API_EXCEPTION);
		CityConnectApiResponse<CityConnectRequest, CityConnectResponse> 
		apiResponse = exceptionAdvice.handleApplicationServiceException(
				apiException);
		
		assertTrue(apiResponse.isHasErrorResponse());
		assertNotNull(apiResponse.getError());
		
		CityConnectApiErrorResponse error = apiResponse.getError();
		
		assertThat(error.getErrorClassName(), 
				is(apiException.getClass().getName()));
		assertThat(error.getErrorCode(), 
				is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		assertThat(error.getErrorMessage(), is(apiException.getMessage()));
		assertThat(error.getErrorDescriptionUrl(), 
				is(apiException.getStatusCode()
						+ ":" + HttpStatus.INTERNAL_SERVER_ERROR.name()));
	}
	
	@Test
	public void testHandleGenericException() {
		Exception genericException = new Exception("Generic Exception");
		CityConnectApiResponse<CityConnectRequest, CityConnectResponse> 
		apiResponse = exceptionAdvice.handleApplicationServiceException(
				genericException);
		
		assertTrue(apiResponse.isHasErrorResponse());
		assertNotNull(apiResponse.getError());
		
		CityConnectApiErrorResponse error = apiResponse.getError();
		
		assertThat(error.getErrorClassName(), 
				is(genericException.getClass().getName()));
		assertThat(error.getErrorCode(), 
				is(HttpStatus.INTERNAL_SERVER_ERROR.value()));
		assertThat(error.getErrorMessage(), is(genericException.getMessage()));
		assertThat(error.getErrorDescriptionUrl(), 
				is(HttpStatus.INTERNAL_SERVER_ERROR.name()));
	}
	
	@Test
	public void testDataNotFoundException() {
		DataNotFoundException dataException = new DataNotFoundException(
				"Data Not Found", ApiConstants.ERROR_CODE_DATA_NOT_FOUND);
		
		WebRequest request = Mockito.mock(WebRequest.class);
		Mockito.when(request.getParameter(eq(ApiConstants.PARAM_ORIGIN)))
			.thenReturn("city1");
		Mockito.when(request.getParameter(eq(ApiConstants.PARAM_DESTINATION)))
		.thenReturn("city2");
		
		CityConnectApiResponse<CityConnectRequest, CityConnectResponse> 
		apiResponse = exceptionAdvice.handleApplicationServiceException(
				dataException, request);
		
		assertTrue(apiResponse.isHasErrorResponse());
		assertNotNull(apiResponse.getError());
		assertNotNull(apiResponse.getRequest());
		assertNotNull(apiResponse.getResponse());
		
		CityConnectApiErrorResponse error = apiResponse.getError();
		assertThat(error.getErrorClassName(), 
				is(dataException.getClass().getName()));
		assertThat(error.getErrorCode(), 
				is(HttpStatus.NOT_FOUND.value()));
		assertThat(error.getErrorMessage(), is(dataException.getMessage()));
		assertThat(error.getErrorDescriptionUrl(), 
				is(dataException.getStatusCode()
						+ ":" + HttpStatus.NOT_FOUND.name()));
		
		CityConnectRequest cityConnectRequest = apiResponse.getRequest();
		assertThat(cityConnectRequest.getOrigin(), is("city1"));
		assertThat(cityConnectRequest.getDestination(), is("city2"));
		
		CityConnectResponse cityConnectResponse = apiResponse.getResponse();
		assertFalse(cityConnectResponse.isPathExists());
	}
}
