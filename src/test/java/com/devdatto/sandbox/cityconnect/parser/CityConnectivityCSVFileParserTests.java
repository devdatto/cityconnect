package com.devdatto.sandbox.cityconnect.parser;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devdatto.sandbox.cityconnect.model.CityPath;

public class CityConnectivityCSVFileParserTests {
	
	CityConnectivityCSVFileParser parser;
	
	@BeforeEach
	public void setUp() {
		parser = new CityConnectivityCSVFileParser();
	}
	
	@Test
	public void testParseRecord() {
		Optional<CityPath> optCityPath = parser.parseRecord("Lorem,Ipsum");
		assertTrue(optCityPath.isPresent());
		CityPath cityPath = optCityPath.get();
		assertThat(cityPath.getOrigin(), is("lorem"));
		assertThat(cityPath.getDestination(), is("ipsum"));
	}
	
	@Test
	public void testParseRecordBlankLine() {
		Optional<CityPath> optCityPath = parser.parseRecord("");
		assertFalse(optCityPath.isPresent());
	}
	
	@Test
	public void testParseRecordNullLine() {
		Optional<CityPath> optCityPath = parser.parseRecord(null);
		assertFalse(optCityPath.isPresent());
	}
	
	@Test
	public void testParseRecordLineWithoutFieldDelimiter() {
		Optional<CityPath> optCityPath = parser.parseRecord("LoremIpsum");
		assertFalse(optCityPath.isPresent());
	}
	
	@Test
	public void testParseRecordLineIncorrectFieldDelimiter() {
		Optional<CityPath> optCityPath = parser.parseRecord("Lorem|Ipsum");
		assertFalse(optCityPath.isPresent());
	}
	
	@Test
	public void testParseRecordLineMoreThanTwoFields() {
		Optional<CityPath> optCityPath = parser.parseRecord("Lorem,Ipsum,Dolor");
		assertFalse(optCityPath.isPresent());
	}
	
	@Test
	public void testParseRecordLineFirstFieldBlank() {
		Optional<CityPath> optCityPath = parser.parseRecord(",Ipsum");
		assertFalse(optCityPath.isPresent());
	}
	
	@Test
	public void testParseRecordLineSecondFieldBlank() {
		Optional<CityPath> optCityPath = parser.parseRecord("Lorem,");
		assertFalse(optCityPath.isPresent());
	}
	
	@Test
	public void testParseRecordLineBothFieldsBlank() {
		Optional<CityPath> optCityPath = parser.parseRecord(",");
		assertFalse(optCityPath.isPresent());
	}
	
	@Test
	public void testParseInputOrigin() {
		String test = "Lorem";
		String matcher = "lorem";
		assertThat(parser.parseInputOrigin(test), is(matcher));
	}
	
	@Test
	public void testParseInputDestination() {
		String test = "Lorem";
		String matcher = "lorem";
		assertThat(parser.parseInputDestination(test), is(matcher));
	}
}
