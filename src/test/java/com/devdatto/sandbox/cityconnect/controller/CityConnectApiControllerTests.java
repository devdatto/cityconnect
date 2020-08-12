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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.devdatto.sandbox.cityconnect.exception.BadRequestException;
import com.devdatto.sandbox.cityconnect.model.CityConnectApiResponse;
import com.devdatto.sandbox.cityconnect.model.CityConnectRequest;
import com.devdatto.sandbox.cityconnect.model.CityConnectResponse;
import com.devdatto.sandbox.cityconnect.service.CityConnectService;

public class CityConnectApiControllerTests {

	CityConnectApiController controller;
	@Mock
	CityConnectService serviceMock;
	
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		controller = new CityConnectApiController(); 
		ReflectionTestUtils.setField(
				controller, "cityConnectService", serviceMock);
	}
	
	@Test
	public void testBuildRequest() {
		CityConnectRequest request = controller.buildRequest("city001", "city002");
		assertThat(request.getOrigin(), is("city001"));
		assertThat(request.getDestination(), is("city002"));
	}
	
	@Test
	public void testBuildRequestBlankOrigin() {
		assertThrows(BadRequestException.class, 
				()-> controller.buildRequest("", "city002"));
		assertThrows(BadRequestException.class, 
				()-> controller.buildRequest(null, "city002"));
	}
	
	@Test
	public void testBuildRequestBlankDestination() {
		assertThrows(BadRequestException.class, 
				()-> controller.buildRequest("city001", ""));
		assertThrows(BadRequestException.class, 
				()-> controller.buildRequest("city001", null));
	}
	
	@Test
	public void testBuildRequestBlankOriginAndDestination() {
		assertThrows(BadRequestException.class, 
				()-> controller.buildRequest("", ""));
		assertThrows(BadRequestException.class, 
				()-> controller.buildRequest("null", null));
	}
	
	@Test
	public void getCityConnectivityPathExistsTrue() {
		Mockito.when(serviceMock.validatePathExists(anyObject())).thenReturn(true);
		
		CityConnectApiResponse<CityConnectRequest, CityConnectResponse> 
			apiResponse = controller.getCityConnectivity("test1", "test2");
		
		assertNotNull(apiResponse);
		assertFalse(apiResponse.isHasErrorResponse());
		CityConnectResponse cityConnectResponse = apiResponse.getResponse();
		assertNotNull(cityConnectResponse);
		assertTrue(cityConnectResponse.isPathExists());
		CityConnectRequest cityConnectRequest = apiResponse.getRequest();
		assertNotNull(cityConnectRequest);
		assertThat(cityConnectRequest.getOrigin(), is("test1"));
		assertThat(cityConnectRequest.getDestination(), is("test2"));
	}
	
	@Test
	public void getCityConnectivityPathExistsFalse() {
		Mockito.when(serviceMock.validatePathExists(anyObject())).thenReturn(false);
		
		CityConnectApiResponse<CityConnectRequest, CityConnectResponse> 
			apiResponse = controller.getCityConnectivity("test1", "test2");
		
		assertNotNull(apiResponse);
		assertFalse(apiResponse.isHasErrorResponse());
		CityConnectResponse cityConnectResponse = apiResponse.getResponse();
		assertNotNull(cityConnectResponse);
		assertFalse(cityConnectResponse.isPathExists());
		CityConnectRequest cityConnectRequest = apiResponse.getRequest();
		assertNotNull(cityConnectRequest);
		assertThat(cityConnectRequest.getOrigin(), is("test1"));
		assertThat(cityConnectRequest.getDestination(), is("test2"));
	}
}
