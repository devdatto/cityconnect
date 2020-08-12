package com.devdatto.sandbox.cityconnect.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CityConnectivityConfigTests {
	
	@Autowired
	CityConnectivityConfig config;

	@Test
	public void testConfigValues() {
		assertNotNull(config.getFilename());
		assertNotNull(config.getInboundFilePath());
		assertNotNull(config.getLoaderFeedBatchSize());
	}
}
