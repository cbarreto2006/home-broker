package com.gft.home.broker.resource;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset implements Serializable {

	public Asset(String assetCode,String assetId, LocalDateTime dateReference, BigDecimal assetValue) {
		this.assetCode=assetCode;
		this.assetId = assetId;
		this.dateReference = dateReference;
		this.assetValue=assetValue;
	}
	private static final long serialVersionUID = -6666163088582895219L;
	LocalDateTime dateReference;
	String assetId;
	String assetCode;
	String assetName;
	BigDecimal assetValue;
	BigDecimal oscilationValue;
	BigDecimal averageValue1;
	BigDecimal averageValue2;
	BigDecimal averageValue3;
	BigDecimal averageValue4;
	BigDecimal averageValue5;
	 
}
