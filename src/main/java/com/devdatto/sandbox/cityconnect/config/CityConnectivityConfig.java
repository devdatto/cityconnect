package com.devdatto.sandbox.cityconnect.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class CityConnectivityConfig {
	@Value("${city.connectivity.filepath}")
	private String inboundFilePath;
	@Value("${city.connectivity.filename}")
	private String filename;
	@Value("${loader.feed.batch.size}")
	private int loaderFeedBatchSize;
}
