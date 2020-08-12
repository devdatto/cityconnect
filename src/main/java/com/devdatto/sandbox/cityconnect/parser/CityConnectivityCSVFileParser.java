package com.devdatto.sandbox.cityconnect.parser;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.devdatto.sandbox.cityconnect.model.CityPath;

import lombok.extern.slf4j.Slf4j;

@Component("CityConnectivityCSVFileParser")
@Slf4j
public class CityConnectivityCSVFileParser implements RecordParser{
	
	private static String FIELD_DELIMETER = ",";

	@Override
	public Optional<CityPath> parseRecord(String line) {
		if(StringUtils.isBlank(line) || !line.contains(FIELD_DELIMETER)) {
			log.error("Parse error :: " + line);
			return Optional.empty();
		}
		String[] cities = line.split(FIELD_DELIMETER);
		if(cities.length != 2 || StringUtils.isBlank(cities[0]) || StringUtils.isBlank(cities[1])) {
			log.error("Parse error :: " + line);
			return Optional.empty();
		} 
			
		CityPath cityPath = CityPath.builder()
									.origin(cities[0].toLowerCase())
									.destination(cities[1].toLowerCase())
									.build();
		Optional<CityPath> optCityPath = Optional.of(cityPath);
		return optCityPath;
	}

	@Override
	public String parseInputOrigin(String origin) {
		return origin.toLowerCase();
	}

	@Override
	public String parseInputDestination(String destination) {
		return destination.toLowerCase();
	}

}
