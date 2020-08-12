package com.devdatto.sandbox.cityconnect.service;

import com.devdatto.sandbox.cityconnect.model.CityConnectRequest;

public interface CityConnectService {
	public boolean validatePathExists(CityConnectRequest request);
}
