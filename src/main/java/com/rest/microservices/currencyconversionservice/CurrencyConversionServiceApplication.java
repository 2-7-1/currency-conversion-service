package com.rest.microservices.currencyconversionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients("com.rest.microservices.currencyconversionservice")
// Enables this microservice to register itself with Eureka naming service
@EnableDiscoveryClient
public class CurrencyConversionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurrencyConversionServiceApplication.class, args);
	}
	
	// Spring Cloud Sleuth dependency in .pom file plus this @Bean Enables Spring Cloud Sleuth
	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}
}