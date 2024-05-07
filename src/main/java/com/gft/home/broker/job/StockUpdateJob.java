package com.gft.home.broker.job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gft.home.broker.config.ComponentConfig;
import com.gft.home.broker.services.AssetServices;
import com.gft.home.broker.services.SetAssetsRunnable;

@Component
public class StockUpdateJob {

	private static final Logger LOG = LogManager.getLogger(StockUpdateJob.class);
	
	@Autowired
	ComponentConfig componentConfig;

	@Autowired
	AssetServices assetServices;
	
	@Scheduled(fixedDelayString = "${home-broker.trigger.stocks-update.job}")
	public void triggerJob() {
		//limit thread pool
    	ExecutorService executorService = Executors.newFixedThreadPool(componentConfig.getThreadQty());
    	
    	try {
    		
    		String[] listStocks = componentConfig.getStockList().split(",");
    		for(String stock : listStocks) {
    			 LOG.info("get stock "+stock);
    			// Execute the tasks using the executor
    			 SetAssetsRunnable setStock = new SetAssetsRunnable(stock, componentConfig, assetServices);
    			 executorService.execute(setStock);

    		}
            // Shutdown the executor
    		executorService.shutdown();
        } catch (Exception e) {
            LOG.error("Erro running thread stock "+e.getMessage());
        }

    }
}
