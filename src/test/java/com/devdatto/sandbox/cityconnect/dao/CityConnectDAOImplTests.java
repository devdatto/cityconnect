package com.devdatto.sandbox.cityconnect.dao;

import static org.mockito.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.Optional;
import java.util.Set;
import java.io.FileNotFoundException;

import com.devdatto.sandbox.cityconnect.exception.ServerErrorException;
import com.devdatto.sandbox.cityconnect.exception.DataNotFoundException;
import com.devdatto.sandbox.cityconnect.model.CityPath;
import com.devdatto.sandbox.cityconnect.parser.CityConnectivityCSVFileParser;
import com.devdatto.sandbox.cityconnect.parser.RecordParser;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import com.devdatto.sandbox.cityconnect.config.CityConnectivityConfig;

@SpringBootTest
public class CityConnectDAOImplTests {
	
	@Autowired
	ResourceLoader resourceLoader;
	@Autowired
	CityConnectDAOImpl cityConnectDAOImpl;
	@Autowired
	CityConnectivityConfig config;
	
	@Mock
	CityConnectivityConfig mockConfig;
	
	// happy path based on actual data file
	// loaded by Spring ApplicationContext
	@Test
	public void testGetResourceFromClasspath() throws IOException {
		Resource resource = cityConnectDAOImpl.getResourceFromClasspath();
		assertNotNull(resource);
		assertThat(resource.getFilename(), is("city.txt"));
		assertTrue(resource.getURI().toString().endsWith("data/city.txt"));
	}
	
	@Test
	public void testProcessCityConnectivityDataMockFileFileNotFoundException() throws IOException {
		CityConnectDAOImpl cityConnectDAOImplMock = new CityConnectDAOImpl();
		ReflectionTestUtils.setField(cityConnectDAOImplMock, "resourceLoader", resourceLoader);
		Mockito.when(mockConfig.getInboundFilePath()).thenReturn("test/");
		Mockito.when(mockConfig.getFilename()).thenReturn("test.txt");
		ReflectionTestUtils.setField(cityConnectDAOImplMock, "config", mockConfig);
		Resource resource = cityConnectDAOImplMock.getResourceFromClasspath();
		assertThrows(FileNotFoundException.class, 
				() -> resource.getInputStream());
		assertThrows(ServerErrorException.class, 
				() -> cityConnectDAOImplMock.processCityConnectivityData());
	}
	
	@Test
	public void testProcessCityConnectivityMalformedInputException() throws IOException {
		CityConnectDAOImpl cityDao = new CityConnectDAOImpl();
		
		ResourceLoader resourceLoaderMock = Mockito.mock(ResourceLoader.class);
		Resource resourceMock = Mockito.mock(Resource.class);
		CityConnectivityConfig configMock = Mockito.mock(CityConnectivityConfig.class);
		
		Mockito.when(resourceMock.getInputStream())
			.thenThrow(new MalformedInputException(16));
		Mockito.when(resourceLoaderMock.getResource(anyString()))
			.thenReturn(resourceMock);
		
		ReflectionTestUtils.setField(
				cityDao, "resourceLoader", resourceLoaderMock);
		ReflectionTestUtils.setField(
				cityDao, "config", configMock);
		
		assertThrows(ServerErrorException.class, 
				() -> cityDao.processCityConnectivityData());
	}
	
	@Test
	public void testProcessCityConnectivityIOException() throws IOException {
		CityConnectDAOImpl cityDao = new CityConnectDAOImpl();
		
		ResourceLoader resourceLoaderMock = Mockito.mock(ResourceLoader.class);
		Resource resourceMock = Mockito.mock(Resource.class);
		CityConnectivityConfig configMock = Mockito.mock(CityConnectivityConfig.class);
		
		Mockito.when(resourceMock.getInputStream())
			.thenThrow(new IOException("Read Failure"));
		Mockito.when(resourceLoaderMock.getResource(anyString()))
			.thenReturn(resourceMock);
		
		ReflectionTestUtils.setField(
				cityDao, "resourceLoader", resourceLoaderMock);
		ReflectionTestUtils.setField(
				cityDao, "config", configMock);
		
		assertThrows(ServerErrorException.class, 
				() -> cityDao.processCityConnectivityData());
	}
	
	// happy path based on actual data file
	// loaded by Spring ApplicationContext
	@Test
	public void testProcessCityConnectivityData() {
		Graph<String, DefaultEdge> cityGraph = 
				(Graph<String, DefaultEdge>)ReflectionTestUtils
				.getField(cityConnectDAOImpl, "cityConnectivityGraph");
		Set<String> vertices = cityGraph.vertexSet();	
		assertTrue(vertices.contains("boston"));
		assertTrue(vertices.contains("albany"));
		assertFalse(vertices.contains("test123"));
		assertTrue(cityGraph.containsEdge("albany", "trenton"));
		assertFalse(cityGraph.containsEdge("test1", "test2"));
	}
	
	// happy path based on actual data file
	// loaded by Spring ApplicationContext
	@Test
	public void testCheckCityConnectivityWithRealData() {
		assertTrue(cityConnectDAOImpl.checkCityConnectivity("Boston", "New York"));
		assertFalse(cityConnectDAOImpl.checkCityConnectivity("Boston", "Trenton"));
	}
	
	
	@Test
	public void testProcessCityConnectivityDataWithMockDAO() {
		// mock the behaviour to use the test data file
		// from the test context
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		CityConnectivityConfig configMock = 
				Mockito.mock(CityConnectivityConfig.class);
		RecordParser parser = new CityConnectivityCSVFileParser();
		
		Mockito.when(configMock.getInboundFilePath())
		   .thenReturn("testData/");
		Mockito.when(configMock.getFilename())
		   .thenReturn("testCityData.txt");
		
		ReflectionTestUtils.setField(dao, "resourceLoader", resourceLoader);
		ReflectionTestUtils.setField(dao, "config", configMock);
		ReflectionTestUtils.setField(dao, "recordParser", parser);
		
		dao.processCityConnectivityData();
		
		Graph<String, DefaultEdge> cityGraph = 
				(Graph<String, DefaultEdge>)ReflectionTestUtils
				.getField(dao, "cityConnectivityGraph");
		
		assertNotNull(cityGraph);
		Set<String> vertices = cityGraph.vertexSet();
		assertFalse(vertices.isEmpty());
		assertTrue(vertices.contains("portland"));
		assertTrue(vertices.contains("thousand oaks"));
		assertFalse(vertices.contains("test123"));
		assertFalse(cityGraph.containsEdge("beaverton", "san diego"));
		assertTrue(cityGraph.containsEdge("beaverton", "portland"));
		assertFalse(cityGraph.containsEdge("test1", "test2"));
	}
	
	@Test
	public void testProcessDataWithMockParser() {
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		RecordParser parserMock = Mockito.mock(CityConnectivityCSVFileParser.class);
		
		CityPath cityPath = CityPath.builder().origin("Origin123")
									.destination("Destination999").build();
		Optional<CityPath> optCityPath = Optional.of(cityPath);
		
		Mockito.when(parserMock.parseRecord(anyString()))
			   .thenReturn(optCityPath);
		
		ReflectionTestUtils.setField(dao, "recordParser", parserMock);
		
		dao.processData("testSomeData");
		
		Graph<String, DefaultEdge> cityGraph = 
				(Graph<String, DefaultEdge>)ReflectionTestUtils
				.getField(dao, "cityConnectivityGraph");
		
		assertNotNull(cityGraph);
		Set<String> vertices = cityGraph.vertexSet();
		assertFalse(vertices.isEmpty());
		assertTrue(vertices.contains("Origin123"));
		assertTrue(vertices.contains("Destination999"));
		assertTrue(cityGraph.containsEdge("Origin123", "Destination999"));
		assertFalse(vertices.contains("test"));
		assertFalse(cityGraph.containsEdge("test1", "test2"));
	}
	
	@Test
	public void testProcessDataWithMockParserAndSimilarDataParsingToLowercase() {
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		RecordParser parserMock = Mockito.mock(CityConnectivityCSVFileParser.class);
		
		CityPath cityPath = CityPath.builder().origin("origin123")
									.destination("destination999").build();
		Optional<CityPath> optCityPath = Optional.of(cityPath);
		
		Mockito.when(parserMock.parseRecord(anyString()))
			   .thenReturn(optCityPath);
		
		ReflectionTestUtils.setField(dao, "recordParser", parserMock);
		
		dao.processData("testSomeData");
		
		Graph<String, DefaultEdge> cityGraph = 
				(Graph<String, DefaultEdge>)ReflectionTestUtils
				.getField(dao, "cityConnectivityGraph");
		
		assertNotNull(cityGraph);
		Set<String> vertices = cityGraph.vertexSet();
		assertFalse(vertices.isEmpty());
		assertTrue(vertices.contains("origin123"));
		assertTrue(vertices.contains("destination999"));
		assertTrue(cityGraph.containsEdge("origin123", "destination999"));
		assertFalse(vertices.contains("test"));
		assertFalse(cityGraph.containsEdge("test1", "test2"));
	}
	
	@Test
	public void testProcessDataWithActualParser() {
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		RecordParser parser = new CityConnectivityCSVFileParser();
		
		ReflectionTestUtils.setField(dao, "recordParser", parser);
		
		dao.processData("Lorem,Ipsum");
		
		Graph<String, DefaultEdge> cityGraph = 
				(Graph<String, DefaultEdge>)ReflectionTestUtils
				.getField(dao, "cityConnectivityGraph");
		
		assertNotNull(cityGraph);
		Set<String> vertices = cityGraph.vertexSet();
		assertFalse(vertices.isEmpty());
		assertTrue(vertices.contains("lorem"));
		assertTrue(vertices.contains("ipsum"));
		assertTrue(cityGraph.containsEdge("lorem", "ipsum"));
		assertFalse(vertices.contains("test"));
		assertFalse(cityGraph.containsEdge("test1", "test2"));
	}
	
	@Test
	public void testProcessDataWithMockParserReturningNullCityPath() {
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		RecordParser parserMock = Mockito.mock(CityConnectivityCSVFileParser.class);
		
		Optional<CityPath> optCityPath = Optional.ofNullable(null);
		
		Mockito.when(parserMock.parseRecord(anyString()))
			   .thenReturn(optCityPath);
		
		ReflectionTestUtils.setField(dao, "recordParser", parserMock);
		
		dao.processData("Origin123,Destination999");
		
		Graph<String, DefaultEdge> cityGraph = 
				(Graph<String, DefaultEdge>)ReflectionTestUtils
				.getField(dao, "cityConnectivityGraph");
		
		assertNotNull(cityGraph);
		Set<String> vertices = cityGraph.vertexSet();
		assertTrue(vertices.isEmpty());
		assertFalse(vertices.contains("Origin123"));
		assertFalse(vertices.contains("Destination999"));
		assertFalse(cityGraph.containsEdge("Origin123", "Destination999"));
	}
	
	@Test
	public void testProcessDataWithActualParserReturningNullCityPath() {
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		RecordParser parser = new CityConnectivityCSVFileParser();
		
		ReflectionTestUtils.setField(dao, "recordParser", parser);
		
		dao.processData("");
		
		Graph<String, DefaultEdge> cityGraph = 
				(Graph<String, DefaultEdge>)ReflectionTestUtils
				.getField(dao, "cityConnectivityGraph");
		
		assertNotNull(cityGraph);
		Set<String> vertices = cityGraph.vertexSet();
		assertTrue(vertices.isEmpty());
		assertFalse(vertices.contains("Origin123"));
		assertFalse(vertices.contains("Destination999"));
		assertFalse(cityGraph.containsEdge("Origin123", "Destination999"));
	}
	
	@Test
	public void testUpdateGraph() {
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		CityPath cityPath = CityPath.builder().origin("origin123")
				.destination("destination999").build();
		
		dao.updateGraph(cityPath);
		
		Graph<String, DefaultEdge> cityGraph = 
				(Graph<String, DefaultEdge>)ReflectionTestUtils
				.getField(dao, "cityConnectivityGraph");
		
		assertNotNull(cityGraph);
		Set<String> vertices = cityGraph.vertexSet();
		assertFalse(vertices.isEmpty());
		assertTrue(vertices.contains("origin123"));
		assertTrue(vertices.contains("destination999"));
		assertTrue(cityGraph.containsEdge("origin123", "destination999"));
		assertFalse(vertices.contains("test"));
		assertFalse(cityGraph.containsEdge("test1", "test2"));
	}
	
	@Test
	public void testCheckCityConnectivityWithMockDataAndRealParserAndLowercaseData() {
		
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		RecordParser parser = new CityConnectivityCSVFileParser();
		
		ReflectionTestUtils.setField(dao, "recordParser", parser);
		
		CityPath cityPath1 = CityPath.builder().origin("origin123")
				.destination("destination999").build();
		CityPath cityPath2 = CityPath.builder().origin("start111")
				.destination("stop456").build();
		
		dao.updateGraph(cityPath1);
		dao.updateGraph(cityPath2);
		
		assertTrue(dao.checkCityConnectivity("Origin123", "destination999"));
		assertTrue(dao.checkCityConnectivity("stop456", "START111"));
		assertFalse(dao.checkCityConnectivity("origin123", "start111"));
		assertThrows(DataNotFoundException.class, 
				() -> dao.checkCityConnectivity("origin", "start111"));
	}
	
	@Test
	public void testCheckCityConnectivityWithMockDataAndMockParser() {
		
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		RecordParser parserMock = Mockito.mock(CityConnectivityCSVFileParser.class);
		
		Mockito.when(parserMock.parseInputOrigin(eq("Origin123")))
			   .thenReturn("origin123");
		Mockito.when(parserMock.parseInputDestination(eq("Destination999")))
		   .thenReturn("destination999");
		Mockito.when(parserMock.parseInputDestination(eq("STaRt111")))
		   .thenReturn("start111");
		Mockito.when(parserMock.parseInputOrigin(eq("stop456")))
		   .thenReturn("stop456");
		Mockito.when(parserMock.parseInputOrigin(eq("origin")))
		   .thenReturn("origin");
		Mockito.when(parserMock.parseInputDestination(eq("start111")))
		   .thenReturn("start111");
		Mockito.when(parserMock.parseInputOrigin(eq("origin123")))
		   .thenReturn("origin123");
		
		ReflectionTestUtils.setField(dao, "recordParser", parserMock);
		
		CityPath cityPath1 = CityPath.builder().origin("origin123")
				.destination("destination999").build();
		CityPath cityPath2 = CityPath.builder().origin("start111")
				.destination("stop456").build();
		
		dao.updateGraph(cityPath1);
		dao.updateGraph(cityPath2);
		
		assertTrue(dao.checkCityConnectivity("Origin123", "Destination999"));
		assertTrue(dao.checkCityConnectivity("stop456", "STaRt111"));
		assertFalse(dao.checkCityConnectivity("origin123", "STaRt111"));
		assertThrows(DataNotFoundException.class, 
				() -> dao.checkCityConnectivity("origin", "start111"));
	}
	
	@Test
	public void testCheckCityConnectivityServerErrorException() {
		CityConnectDAOImpl dao = new CityConnectDAOImpl();
		ReflectionTestUtils.setField(dao, "cityConnectivityGraph", null);
		assertThrows(ServerErrorException.class, 
				() -> dao.checkCityConnectivity("origin", "start111"));
	}
	
}
