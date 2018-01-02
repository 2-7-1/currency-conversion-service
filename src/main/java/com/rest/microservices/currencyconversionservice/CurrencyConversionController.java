package com.rest.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
 

@RestController
public class CurrencyConversionController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CurrencyExchenageServiceProxy proxy;

	@GetMapping(path = "/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		
		// Call currency-exchange-service
		RestTemplate restTemplate = new RestTemplate();		
		ResponseEntity<CurrencyConversionBean> responseEntity = restTemplate.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", 
				CurrencyConversionBean.class, 
				uriVariables);
		CurrencyConversionBean response = responseEntity.getBody(); 
		
		// Hydrate object with results returned from currency-exchange-service.  This magic works because ResponseEntity<CurrencyConversionBean> expects the responsEntity.body
		// to have properties that can map to the CurrencyConversionBean type.  This means there must some magic that turns the JSON returned from currency-exchange-service 
		// (ExchangeValue serialized by Jackson into JSON) into a CurrencyConversionBeen type. Which means the Type returned by currency-exchange-service (ExchangeValue) 
		// must have the same properties that CurrencyConversionBean needs to hydrate from this webservice.  So, because Java is a strongly typed language we don't work with the JSON objects
		// directly but convert them to classes.  I think this creates a code coupling (bad) between currency-exchange-service and currency-conversion-service
		// namely, the classes com.rest.microservices.currencyexchangeservice.ExchangeValue and com.rest.microservices.currencyconversionservice.CurrencyConversionBean.
		// By comparison, a platform like NodeJS would work directly with the JSON returned from a webservice.
		CurrencyConversionBean conversionBean = new CurrencyConversionBean(response.getId(), 
				from, 
				to, 
				response.getConversionMultiple(), 
				quantity, 
				quantity.multiply(response.getConversionMultiple()), 
				response.getPort());
		return conversionBean;
	}
	
	@GetMapping(path = "/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		CurrencyConversionBean response = proxy.retrieveExchangeValue(from, to);
		CurrencyConversionBean conversionBean = new CurrencyConversionBean(response.getId(), 
				from, 
				to, 
				response.getConversionMultiple(), 
				quantity, 
				quantity.multiply(response.getConversionMultiple()), 
				response.getPort());
		logger.info("Inside /currency-converter-feign/from/{from}/to/{to}/quantity/{quantity} .convertCurrencyFeign.  ConversionMultiple: {}", response.getConversionMultiple());
		return conversionBean;
	}
}
