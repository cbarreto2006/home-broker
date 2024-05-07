package com.gft.home.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.gft.home.broker.config.ComponentConfig;

@SpringBootApplication
@EnableScheduling
@ComponentScan({"com.gft.home.broker"})
@EnableConfigurationProperties(ComponentConfig.class)
public class HomeBrokerApplication {

	public static void main(String[] args) {
		System.setProperty("spring.config.name", "home-broker-config");
		SpringApplication.run(HomeBrokerApplication.class, args);
	}
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    	System.setProperty("spring.config.name", "home-broker-config");
		return application.sources(HomeBrokerApplication.class);
	}
	
}
