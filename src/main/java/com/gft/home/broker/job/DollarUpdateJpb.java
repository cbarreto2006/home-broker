package com.gft.home.broker.job;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.gft.home.broker.config.ComponentConfig;
import com.gft.home.broker.resource.Asset;
import com.gft.home.broker.services.AssetServices;

@Component
public class DollarUpdateJpb {
	private static final Logger LOG = LogManager.getLogger(DollarUpdateJpb.class);
	
	@Autowired
	ComponentConfig componentConfig;
	
	@Autowired
	AssetServices assetServices;
	
	
	
	@Scheduled(fixedDelayString = "${home-broker.trigger.dollar-update.job}")
	public void triggerJob() {
		LOG.info(" - INICIANDO..." + new java.util.Date());
		executeJob();
		LOG.info(" - FINALIZANDO..." + new java.util.Date());
	}

	private void executeJob() {
		try {
			   LOG.info("Begin Update Dollar Online");
		       String url = componentConfig.getUrlDollar();
		        try {
		            // Fetch the HTML content of the webpage
		            Document doc = Jsoup.connect(url).get();
		            
		            // Extract the desired information
		            String title = doc.title();
		            LOG.info("Title: " + title);
		            
		            // Select the element containing the value
		            Element cotMoedaElement = doc.selectFirst("span.cotMoeda.nacional");
		           
		            
		            // Extract the value
		            if (cotMoedaElement != null) {
		                String value = cotMoedaElement.select("input").attr("value");
		                LOG.info("Value: " + value);
		                Asset asset = new Asset("USD","USD", LocalDateTime.now() ,new BigDecimal(value.replace(",", ".")));
		                LOG.info("PROCESSED...:"+assetServices.getAssetPanel(asset));
		            } else {
		            	LOG.info("Element not found.");
		            }
		            
		            // Extract the desired information
		            Element cotacaoElement = doc.selectFirst("span.cotMoeda.nacional");

		            if (cotacaoElement != null) {
		                String cotacao = cotacaoElement.text();

		    		    LOG.info("BRL exchange rate: " + cotacao);
		            } else {
		            	LOG.info("Exchange rate element not found.");
		            }
		            
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		} catch (Exception e) {
			LOG.info("ERRO DollarUpdateJpb..." + e.getMessage());
		}
		
	}
}
