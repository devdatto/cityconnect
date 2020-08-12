package com.devdatto.sandbox.cityconnect.dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import com.devdatto.sandbox.cityconnect.config.CityConnectivityConfig;
import com.devdatto.sandbox.cityconnect.exception.DataNotFoundException;
import com.devdatto.sandbox.cityconnect.exception.ServerErrorException;
import com.devdatto.sandbox.cityconnect.model.CityPath;
import com.devdatto.sandbox.cityconnect.parser.RecordParser;
import com.devdatto.sandbox.cityconnect.util.ApiConstants;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CityConnectDAOImpl implements CityConnectDAO {
	Graph<String, DefaultEdge> cityConnectivityGraph = 
			new SimpleGraph<>(DefaultEdge.class);
	
	@Autowired
    private ResourceLoader resourceLoader;
	@Autowired
	CityConnectivityConfig config;
	@Autowired
	@Qualifier("CityConnectivityCSVFileParser")
	RecordParser recordParser;
	
	Resource getResourceFromClasspath() {
		Resource classPathResource = resourceLoader.getResource(
                ApiConstants.RESOURCE_CLASSPATH_PREFIX + config.getInboundFilePath() + config.getFilename());
		return classPathResource;
	}
	
	@PostConstruct
	public void processCityConnectivityData() {
		Resource resource = getResourceFromClasspath();
		try(BufferedReader buffReader = new BufferedReader(
				new InputStreamReader(resource.getInputStream(), StandardCharsets.ISO_8859_1))) {
			buffReader.lines().forEach(line -> processData(line));				
			log.info("Data load from input file complete");
		} catch (FileNotFoundException fnfEx) {
			log.error(
                    "File Not Found Exception occurred while reading file, cause: {}, message : {}",
                    fnfEx.getCause(), fnfEx.getMessage());
			throw new ServerErrorException(
					"Data file not found :: " + fnfEx.getMessage() , 
					ApiConstants.ERROR_CODE_SERVER_ERROR);
		} catch (MalformedInputException miEx) {
            log.error(
                    "Malformed Input Exception occurred while processing file, " +
                            "cause: {}, message: {}",
                    miEx.getCause(), miEx.getMessage());
            throw new ServerErrorException(
					"Data file processing error :: " + miEx.getMessage() , 
					ApiConstants.ERROR_CODE_SERVER_ERROR);
        }catch (IOException ioEx) {
            log.error(
                    "IOException occurred while processing file, cause: {}, message : {}",
                    ioEx.getCause(), ioEx.getMessage());
            throw new ServerErrorException(
					"Data file read error :: " + ioEx.getMessage() , 
					ApiConstants.ERROR_CODE_SERVER_ERROR);
        } 
	}

	void processData(String line) {
		Optional<CityPath> optCityPath = recordParser.parseRecord(line);
		if(optCityPath.isPresent()) {
			updateGraph(optCityPath.get());
		} else {
			log.info("Unable to add info to graph :: " + line);
		}
	}

	void updateGraph(CityPath cityPath) {
		cityConnectivityGraph.addVertex(cityPath.getOrigin());
		cityConnectivityGraph.addVertex(cityPath.getDestination());
		cityConnectivityGraph.addEdge(cityPath.getOrigin(), cityPath.getDestination());
	}
	
	public boolean checkCityConnectivity(String origin, String destination) {
		GraphPath<String, DefaultEdge> path = null;
		try {
			path = DijkstraShortestPath.findPathBetween(
					cityConnectivityGraph, recordParser.parseInputOrigin(origin), 
					recordParser.parseInputDestination(destination));
			
		} catch(IllegalArgumentException iaEx) {
			log.info("input location(s) not found in graph :: " + origin + " and / or " + destination);
			throw new DataNotFoundException("input location(s) not found in graph :: " 
			+ origin + " and / or " + destination, ApiConstants.ERROR_CODE_DATA_NOT_FOUND);
		} catch (Exception ex) {
			log.error("error fetching data from graph for " + origin + " and " + destination);
			throw new ServerErrorException(
					"Internal error fetching data from graph for" + origin + " and " + destination,
					ApiConstants.ERROR_CODE_SERVER_ERROR);
		}
		
		return null != path ? true : false;
	}
}
