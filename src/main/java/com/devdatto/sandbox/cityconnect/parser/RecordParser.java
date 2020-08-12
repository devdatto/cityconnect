package com.devdatto.sandbox.cityconnect.parser;

import java.util.Optional;

import com.devdatto.sandbox.cityconnect.model.CityPath;

public interface RecordParser {
	public Optional<CityPath> parseRecord(String line);
	public String parseInputOrigin(String origin);
	public String parseInputDestination(String destination);
}
