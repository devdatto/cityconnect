package com.devdatto.sandbox.cityconnect.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devdatto.sandbox.cityconnect.exception.BadRequestException;
import com.devdatto.sandbox.cityconnect.model.CityConnectApiResponse;
import com.devdatto.sandbox.cityconnect.model.CityConnectRequest;
import com.devdatto.sandbox.cityconnect.model.CityConnectResponse;
import com.devdatto.sandbox.cityconnect.service.CityConnectService;
import com.devdatto.sandbox.cityconnect.util.ApiConstants;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(value = "/", description = "City Connectivity operations")
public class CityConnectApiController {
	
	@Autowired
	CityConnectService cityConnectService;
	
	@ApiOperation(
			value = "city connectivity service",
			notes = "Checks if there is a path between two cities, " +
					"passed through URL parameters as origin and destination",
			response = CityConnectApiResponse.class
	)
	@ApiResponses(value = { 
            @ApiResponse(code = 200, message = "Success|OK"),
            @ApiResponse(code = 400, message = "bad request!")})
	@GetMapping(value="/connected", 
			headers = "Accept=application/json",
			produces = "application/json; charset=utf-8")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public CityConnectApiResponse<CityConnectRequest, CityConnectResponse> 
	getCityConnectivity(@RequestParam(name = "origin") String origin, 
						@RequestParam(name = "destination") String destination) {
		CityConnectRequest request = buildRequest(origin, destination);
		
		boolean pathExists = cityConnectService.validatePathExists(request);
		CityConnectResponse response = CityConnectResponse.builder().pathExists(pathExists).build();
		
		CityConnectApiResponse<CityConnectRequest, CityConnectResponse> apiResponse = 
				CityConnectApiResponse.<CityConnectRequest, CityConnectResponse>builder()
									  .request(request)
									  .response(response)
									  .hasErrorResponse(false)
									  .build();
		return apiResponse;
	}

	public CityConnectRequest buildRequest(String origin, String destination) {
		
		if (StringUtils.isBlank(origin) && StringUtils.isBlank(destination)) {
			log.error("Both Origin and Destination parameters are not provided");
			throw new BadRequestException("Both Origin and Destination parameters blank.", 
					ApiConstants.ERROR_CODE_BAD_REQUEST);
		} else if (StringUtils.isBlank(origin)) {
			log.error("Origin parameter is not provided");
			throw new BadRequestException("Origin parameter blank.", ApiConstants.ERROR_CODE_BAD_REQUEST);
		} else if (StringUtils.isBlank(destination)) {
			log.error("Destination parameter is not provided");
			throw new BadRequestException("Destination parameter blank.", ApiConstants.ERROR_CODE_BAD_REQUEST);
		}
		
		CityConnectRequest request = CityConnectRequest.builder()
													   .origin(origin)
													   .destination(destination)
													   .build();
		return request;
	}
}
