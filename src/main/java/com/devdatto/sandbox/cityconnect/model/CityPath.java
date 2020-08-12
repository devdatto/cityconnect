package com.devdatto.sandbox.cityconnect.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityPath implements Serializable{
	private String origin;
	private String destination;
}
