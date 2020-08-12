package com.devdatto.sandbox.cityconnect.dao;

public interface CityConnectDAO {
	public boolean checkCityConnectivity(String source, String destination);
	public void processCityConnectivityData();
}
