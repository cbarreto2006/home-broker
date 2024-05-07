package com.gft.home.broker.resource;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Response implements Serializable{

	private static final long serialVersionUID = 8928090586462484989L;
	String status;
	String statusDescription;
	int qty;
	List<Asset> listAssetsPanel;
}
