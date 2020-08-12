package com.devdatto.sandbox.cityconnect.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CityConnectRequest implements Serializable{
	String origin;
	String destination;
}
