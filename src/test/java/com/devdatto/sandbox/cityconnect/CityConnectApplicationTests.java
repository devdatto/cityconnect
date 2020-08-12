package com.devdatto.sandbox.cityconnect;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devdatto.sandbox.cityconnect.controller.CityConnectApiController;

@SpringBootTest
class CityConnectApplicationTests {
	
	@Autowired
	CityConnectApiController controller;

	@Test
	void contextLoads() {
		assertNotNull(controller);
	}

}
