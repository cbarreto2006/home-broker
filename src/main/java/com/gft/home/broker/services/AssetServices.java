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
	
	/**
     * Retrieves list updates home broker data.
     *
     * @return a list of assets
     */
	public List<Asset> getAssetPanel() {
		try {
			if(listAssetsPanel==null) {
				return initialAssetPannel();
			}
			return listAssetsPanel;
		} catch (Exception e) {
			return initialAssetPannel();
		}
		
	}
	
	/**
     * Retrieves initial list for closed market when access data is running
     *
     * @return a list of assets
     */
	private List<Asset> initialAssetPannel() {
	    // Populate the list with sample data
		List<Asset> listAssetsPanelIni = new ArrayList<Asset>();
		listAssetsPanelIni.add(new Asset("MERCADO FECHADO", "MERCADO FECHADO",  LocalDateTime.now(), new BigDecimal(0)));
        listAssetsPanelIni.add(new Asset("M1TA34", "c6jar7",  LocalDateTime.now(), new BigDecimal(0)));
        listAssetsPanelIni.add(new Asset("NVDC34", "bpwu7w",  LocalDateTime.now(), new BigDecimal(0)));
        listAssetsPanelIni.add(new Asset("PETR3", "apn4dm",  LocalDateTime.now(), new BigDecimal(0)));
        listAssetsPanelIni.add(new Asset("USD", "USD",  LocalDateTime.now(), new BigDecimal(0)));
        listAssetsPanelIni.add(new Asset("VALE3", "apnjsm",  LocalDateTime.now(), new BigDecimal(0)));
		return listAssetsPanelIni; 
	}

	/**
     * Update assets to listPanel
     *
     * @return a Response class as a json string 
     */
	public String getAssetPanel(Asset asset) {
		try {
			setLakeOnlinePanel(asset);
			return new JSONObject(new Response("OK","Success",listAssetsPanel.size(),  listAssetsPanel)).toString();
		} catch (Exception e) {
			return new JSONObject(new Response("ERROR",asset.toString()+"  erro:"+e.getMessage(),listAssetsPanel.size(),  listAssetsPanel)).toString();
		}
		
	}

	
	/**
     * Set assets to listPanel and calculate value average per asset 
     *
     * @return set  listAssetsPanel
     */
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

	/**
     * Set assets to listAssets and control maximnum items for all list configured
     *
     * @return set  listAssetsPanel throws listAssets
     */
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

	/**
     * Summarize list per asset ordering by assetCode
     *
     * @return set  listAsset throws List<Asset>
     */
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

	/**
     * Calculate simple average
     *
     * @return BigDecimal
     */
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
