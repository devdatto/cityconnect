package com.devdatto.sandbox.cityconnect.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.devdatto.sandbox.cityconnect.dao.CityConnectDAO;
import com.devdatto.sandbox.cityconnect.exception.DataNotFoundException;
import com.devdatto.sandbox.cityconnect.exception.ServerErrorException;
import com.devdatto.sandbox.cityconnect.model.CityConnectRequest;
import com.devdatto.sandbox.cityconnect.util.ApiConstants;

public class CityConnectServiceImplTests {
	
	CityConnectServiceImpl service;
	
	@Mock
	CityConnectDAO mockDAO;
	
	@BeforeEach
	public void setUp() {
		service = new CityConnectServiceImpl();
		
		MockitoAnnotations.initMocks(this);
		
		Mockito.when(mockDAO.checkCityConnectivity(eq("start"), eq("dest")))
		   	   .thenReturn(true);
		Mockito.when(mockDAO.checkCityConnectivity(eq("lorem"), eq("ipsum")))
			   .thenReturn(false);
		Mockito.when(mockDAO.checkCityConnectivity(eq("dolor"), eq("sit")))
		   	   .thenThrow(new DataNotFoundException(
		   			   "No Data Found", ApiConstants.ERROR_CODE_DATA_NOT_FOUND));
		Mockito.when(mockDAO.checkCityConnectivity(eq("northpole"), eq("southpole")))
	   	   .thenThrow(new ServerErrorException(
	   			   "Internal Server Error", ApiConstants.ERROR_CODE_SERVER_ERROR));
		
		ReflectionTestUtils.setField(service, "cityConnectDAO", mockDAO);
	}
	
	@Test
	public void validatePathExistsTrue() {
		CityConnectRequest request = CityConnectRequest
				.builder().origin("start").destination("dest").build();
		assertTrue(service.validatePathExists(request));
	}
	
	@Test
	public void validatePathExistsFalse() {
		CityConnectRequest request = CityConnectRequest
				.builder().origin("lorem").destination("ipsum").build();
		assertFalse(service.validatePathExists(request));
	}
	
	@Test
	public void validatePathExistsDataNotFoundException() {
		CityConnectRequest request = CityConnectRequest
				.builder().origin("dolor").destination("sit").build();
		assertThrows(DataNotFoundException.class, 
				()-> service.validatePathExists(request));
	}
	
	@Test
	public void validatePathExistsServerErrorException() {
		CityConnectRequest request = CityConnectRequest
				.builder().origin("northpole").destination("southpole").build();
		assertThrows(ServerErrorException.class, 
				()-> service.validatePathExists(request));
	}

}
