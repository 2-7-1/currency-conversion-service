package com.rest.microservices.currencyconversionservice;

import java.math.BigDecimal;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Feign Proxy to another microservice needed for this microservice.  This is using the other microservice's name directly
//@FeignClient(name = "currency-exchange-service")

// Feign Proxy to the Zuul API Gateway server.  This one annotation channels all requests to this proxy through 
// Zuul where Zuul can apply its filters, allowing two microservices to communicate through the Zuul API Gateway
@FeignClient(name = "netflix-zuul-api-gateway-server")
@RibbonClient(name = "currency-exchange-service")
public interface CurrencyExchenageServiceProxy {

	// Notice the /currency-exchange-service name so that Zuul will know the full path to the URI endpoint of this Proxy
	@GetMapping(path = "/currency-exchange-service/currency-exchange/from/{from}/to/{to}")
	public CurrencyConversionBean retrieveExchangeValue(@PathVariable String from, @PathVariable String to);
}
