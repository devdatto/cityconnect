package com.devdatto.sandbox.cityconnect.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootTest
public class SwaggerConfigTests {
	
	@Autowired
	SwaggerConfig swaggerConfig;
	
	@Test
	public void testSwaggerConfigApi() {
		Docket docket = swaggerConfig.api();
		assertNotNull(docket);
		assertThat(docket.getGroupName(), is("Restful Api"));
	}
	
	@Test
	public void testApiInfo() {
		ApiInfo apiInfo = swaggerConfig.metaData();
		assertNotNull(apiInfo);
		assertThat(apiInfo.getTitle(), is("Spring Boot REST API"));
		assertThat(apiInfo.getDescription(), is("City Connectivity Check Service"));
		assertThat(apiInfo.getVersion(), is("v1"));
		assertThat(apiInfo.getLicense(), is("Apache License Version 2.0"));
		assertThat(apiInfo.getLicenseUrl(), is("https://www.apache.org/licenses/LICENSE-2.0"));
	}
	
}
