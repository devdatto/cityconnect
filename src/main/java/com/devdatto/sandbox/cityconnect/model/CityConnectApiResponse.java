package com.devdatto.sandbox.cityconnect.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityConnectApiResponse<R, T> implements Serializable {
	private R request;
	private T response;
	private CityConnectApiErrorResponse error;
	private boolean hasErrorResponse;
	
	public CityConnectApiResponse(T response) {
		this.response = response;
		this.hasErrorResponse = false;
	}

	public CityConnectApiResponse(CityConnectApiErrorResponse error) {
		this.error = error;
		this.hasErrorResponse = true;
	}

	public CityConnectApiResponse(R request, T response) {
		this.request = request;
		this.response = response;
		this.hasErrorResponse = false;
	}

	public CityConnectApiResponse(R request, CityConnectApiErrorResponse error) {
		this.request = request;
		this.error = error;
		this.hasErrorResponse = true;
	}
	
	
}
