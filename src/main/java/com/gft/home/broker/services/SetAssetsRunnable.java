package com.gft.home.broker.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gft.home.broker.config.ComponentConfig;
import com.gft.home.broker.resource.Asset;


public class SetAssetsRunnable implements Runnable {

	private static final Logger LOG = LogManager.getLogger(SetAssetsRunnable.class);

	private final ComponentConfig componentConfig;

	String stock;

	private AssetServices assetServices;
	
	public SetAssetsRunnable(String asset, ComponentConfig componentConfig, AssetServices assetServices) {
		this.stock = asset;
		this.componentConfig = componentConfig;
		this.assetServices = assetServices;
	}

	@Override
	public void run() {
		try {
			
			LocalDateTime now = LocalDateTime.now();
			long threadId = Thread.currentThread().getId();
			LOG.info("Searching asset: " + stock + ", Thread ID: " + threadId);

			String url = componentConfig.getStocksUrl().replaceAll("STOCK-ID", stock);
			LOG.info("Searching scrapStock: " + stock + "  url: " + url);
			Asset assetStock = new Asset();
			// Load the HTML document
			Document doc = Jsoup.connect(componentConfig.getStocksUrl().replaceAll("STOCK-ID", stock)).get();

			// Find the div with class name floatingBar-DS-EntryPoint1-1 ==> apn4gh PETR4
			// apn4dm PETR3
			Element floatingBar = doc.selectFirst("div.floatingBar-DS-EntryPoint1-1.detail");

			// Check if the div is found
			if (floatingBar != null) {
				// Find span elements within the div and set to class Asset
				LOG.info("Find the div with class "+stock);
				Elements spanElements = floatingBar.select("span");
				assetStock.setDateReference(LocalDateTime.now());
				assetStock.setAssetId(stock);
				assetStock.setAssetName(spanElements.get(0).text());
				assetStock.setAssetValue(new BigDecimal(spanElements.get(1).text().replace(",", ".")).setScale(2));
				assetStock.setOscilationValue(convertToBigDecimal(spanElements.get(2).text().replace("%", "").replace(",", ".")));
				LOG.info("End seatching asset: " + stock);
			} else {
				LOG.error("Div with class 'floatingBar-DS-EntryPoint1-1 detail' not found.");
			}

			// Find the div with class name header-DS-EntryPoint1-1
			Element headerDiv = doc.selectFirst("div.header-DS-EntryPoint1-1");

			// Check if the div is found
			if (headerDiv != null) {
				// Find code
				Element symbolSpan = headerDiv.selectFirst("span.symbolWithBtn-DS-EntryPoint1-1");

				// Check if the span element is found
				if (symbolSpan != null) {
					// Extract the text content
					String symbol = symbolSpan.text();
					assetStock.setAssetCode(symbol);

					try {
						LOG.info("updating List Stock Summary " + assetStock.getAssetCode() + " " + assetStock.getAssetName());
						LOG.info("Setting scrapStock: " + assetStock );
						LOG.info("Ended Setting scrapStock: "+ assetServices.getAssetPanel(assetStock));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				} else {
					LOG.error("Span with class 'symbolWithBtn-DS-EntryPoint1-1' not found within header div.");
				}
			} else {
				assetStock = null;
				LOG.error("Div with class 'header-DS-EntryPoint1-1' not found.");
			}

			
		} catch (Exception e) {
			LOG.error("Error processing Runnable: " + stock + " error: " + e.getMessage());
		}

	}

	private BigDecimal convertToBigDecimal(String value) throws Exception{
		// Remove non-numeric characters except for the dot, minus sign, and spaces
		String numericString = value.replaceAll("[^\\d.\\s-]", "");

		// Remove spaces
		numericString = numericString.replaceAll("\\s", "");

		// Convert String to BigDecimal
		BigDecimal bigDecimal = new BigDecimal(numericString);
		return bigDecimal;
	}
}
