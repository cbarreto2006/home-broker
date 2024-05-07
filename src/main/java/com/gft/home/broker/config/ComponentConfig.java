package com.gft.home.broker.config;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "com.gft.home.broker")
@Data
public class ComponentConfig {
	
	@Value("${home-broker.enviroment}")
	private String enviroment;
	
	@Value("${home-broker.measure.max-list}")
	private int maxList;
	
	@Value("${home-broker.measure.time-spread}")
	private String timeSpread;
	
	@Value("${home-broker.measure.average-measure-1}")
	private BigDecimal averageMeasure1;
	
	@Value("${home-broker.measure.average-measure-2}")
	private BigDecimal averageMeasure2;
	
	@Value("${home-broker.measure.average-measure-3}")
	private BigDecimal averageMeasure3;
	
	@Value("${home-broker.measure.average-measure-4}")
	private BigDecimal averageMeasure4;
	
	@Value("${home-broker.measure.average-measure-5}")
	private BigDecimal averageMeasure5;
	
	@Value("${home-broker.trigger.dollar-update.url}")
	private String urlDollar;

	@Value("${home-broker.trigger.stocks-update.thread-qty}")
	private int threadQty;

	@Value("${home-broker.trigger.stocks-update.list-stock}")
	private String stockList;
	
	@Value("${home-broker.trigger.stocks-update.url}")
	private String stocksUrl;
	
}
