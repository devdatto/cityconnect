package com.devdatto.sandbox.cityconnect.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityConnectApiErrorResponse implements Serializable{
	
	private String errorMessage;
	private int errorCode;
	private String errorClassName;
	private String errorDescriptionUrl;
}
