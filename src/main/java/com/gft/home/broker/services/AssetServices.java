package com.gft.home.broker.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gft.home.broker.config.ComponentConfig;
import com.gft.home.broker.resource.Asset;
import com.gft.home.broker.resource.Response;

import lombok.Data;

@Service
@Data
public class AssetServices {

	private static final Logger LOG = LogManager.getLogger(AssetServices.class);
	
	@Autowired
	ComponentConfig componentConfig;
	
	
	List<Asset> listAssets = null;
	List<Asset> listAssetsPanel = null;
	int timeSpread = 1;
	
	public String getAssetPanel(Asset asset) {
		try {
			setLakeOnlinePanel(asset);
			return new JSONObject(new Response("OK","Success",listAssetsPanel.size(),  listAssetsPanel)).toString();
		} catch (Exception e) {
			return new JSONObject(new Response("ERROR",asset.toString()+"  erro:"+e.getMessage(),listAssetsPanel.size(),  listAssetsPanel)).toString();
		}
		
	}

	public void setLakeOnlinePanel(Asset assetStock) throws Exception {
		LOG.info("Beggining asset update: "+assetStock);
		if(listAssets==null) {
			listAssets = new ArrayList<Asset>();
			listAssetsPanel = new ArrayList<Asset>();
			timeSpread = componentConfig.getTimeSpread().equalsIgnoreCase("minutes") ? 60 : componentConfig.getTimeSpread().equalsIgnoreCase("minutes") ? 60*60 : 1;
		}
		calculateAverageStatistics(assetStock);
		LOG.info("Ending asset update: "+assetStock+ listAssets.size());
	}

	private void calculateAverageStatistics(Asset assetStock) throws Exception {
		
		listAssets.add(assetStock);
		//verify if exceeds maximum list set
		if(listAssets.size()>componentConfig.getMaxList()) {
			listAssets.remove(0);
		}
		//sort to format detais for average calculating
		Collections.sort(listAssets, Comparator.comparing(Asset::getAssetCode).thenComparing(Asset::getDateReference).reversed());
		listAssetsPanel = getSummary(assetStock);
		LOG.info("Painel Updated" + listAssetsPanel.toString());
	}

	private List<Asset> getSummary(Asset assetStock)  throws Exception {
		// TODO calculate average values as setup		
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime minDate = now.minusSeconds(componentConfig.getAverageMeasure5().longValue()*timeSpread-1);
		LocalDateTime maxDate = now.plusSeconds(componentConfig.getAverageMeasure1().longValue()*timeSpread);
		
		Asset assetAverage = new Asset(assetStock.getAssetCode(),assetStock.getAssetId(), now, assetStock.getAssetValue());
		for (Asset asset: listAssets) {
			if(asset.getAssetCode().equals(assetStock.getAssetCode()) 
				&& asset.getDateReference().isAfter(minDate) && asset.getDateReference().isBefore(maxDate) ) {
				assetAverage.setAverageValue1(calculateAvg(now, asset, componentConfig.getAverageMeasure1().intValue()));
				assetAverage.setAverageValue2(calculateAvg(now, asset, componentConfig.getAverageMeasure2().intValue()));
				assetAverage.setAverageValue3(calculateAvg(now, asset, componentConfig.getAverageMeasure3().intValue()));
				assetAverage.setAverageValue4(calculateAvg(now, asset, componentConfig.getAverageMeasure4().intValue()));
				assetAverage.setAverageValue5(calculateAvg(now, asset, componentConfig.getAverageMeasure5().intValue()));
			}
		}
		int found=0;
		
		for(Asset asset: listAssetsPanel) {
			if(asset.getAssetCode().equals(assetStock.getAssetCode())) {
				found++;
				asset = assetAverage;
			}
		}
		if(found==0) {
			listAssetsPanel.add(assetAverage);
		}
		Collections.sort(listAssetsPanel, Comparator.comparing(Asset::getAssetCode));
		return listAssetsPanel;
	}

	private BigDecimal calculateAvg(LocalDateTime now, Asset assetStock, int interval)  throws Exception {
		LocalDateTime dateLimit = now.minusSeconds(interval*timeSpread);
		BigDecimal totalValue = new BigDecimal(0).setScale(4);
		BigDecimal count = new BigDecimal(0).setScale(0);
		for (Asset asset: listAssets) {
			if(asset.getAssetCode().equals(assetStock.getAssetCode()) 
				&& asset.getDateReference().isAfter(dateLimit) 
				&& asset.getDateReference().isBefore(now)) {
				totalValue=totalValue.add(asset.getAssetValue());
				count.add(new BigDecimal(1));
			}
		}
		
		totalValue = count.intValue()>0 ? totalValue.divide(count): totalValue ;
		return totalValue;
	}


	
}
