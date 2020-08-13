package com.devdatto.sandbox.cityconnect;

import java.time.Duration;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.JamonPerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@SpringBootApplication
public class CityConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CityConnectApplication.class, args);
	}
	
	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
	    return factory -> factory.configureDefault(
	    					id -> new Resilience4JConfigBuilder(id)
	    								.timeLimiterConfig(
	    										TimeLimiterConfig
	    											.custom()
	    											.timeoutDuration(Duration.ofSeconds(4))
	    											.build())
	    								.circuitBreakerConfig(
	    										CircuitBreakerConfig.custom()
	    								           .slidingWindowSize(25)
	    								           .permittedNumberOfCallsInHalfOpenState(10)
	    								           .failureRateThreshold(50.0F)
	    								           .waitDurationInOpenState(Duration.ofMillis(50))
	    								           .slowCallDurationThreshold(Duration.ofMillis(200))
	    								           .slowCallRateThreshold(50.0F)
	    								           .build())
	    								.build());
	}
	
    @Bean
    public JamonPerformanceMonitorInterceptor jamonPerformanceMonitorInterceptor() {
        return new JamonPerformanceMonitorInterceptor(true, true);
    }

    @Bean
    public Advisor performanceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(
        		"execution(public * com.devdatto.sandbox.cityconnect..*.*(..))");
        return new DefaultPointcutAdvisor(
        		pointcut, jamonPerformanceMonitorInterceptor());
    }

}
