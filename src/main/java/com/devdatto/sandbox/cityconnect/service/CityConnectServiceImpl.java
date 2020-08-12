package com.devdatto.sandbox.cityconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devdatto.sandbox.cityconnect.dao.CityConnectDAO;
import com.devdatto.sandbox.cityconnect.model.CityConnectRequest;

@Service
public class CityConnectServiceImpl implements CityConnectService {

	@Autowired
	CityConnectDAO cityConnectDAO;
	
	@Override
	public boolean validatePathExists(CityConnectRequest request) {
		return cityConnectDAO.checkCityConnectivity(request.getOrigin(), request.getDestination());
	}

}
