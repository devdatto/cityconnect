package com.devdatto.sandbox.cityconnect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@ComponentScan({"com.devdatto.sandbox.cityconnect"})
public class SwaggerConfig {
	
	@Value("${api.version}")
	private String apiVersion;
	@Value("${api.description}")
	private String apiDescription;
	
	public Docket api() {
		//Register the controllers to swagger
        //Also it is configuring the Swagger Docket
        return new Docket(DocumentationType.SWAGGER_2)
        		.groupName("Restful Api")
        		.select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
	}
	
	ApiInfo metaData() {
		return new ApiInfoBuilder()
                .title("Spring Boot REST API")
                .description(apiDescription)
                .version(apiVersion)
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();
	}
}
