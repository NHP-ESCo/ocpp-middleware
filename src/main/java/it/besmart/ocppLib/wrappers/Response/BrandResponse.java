package it.besmart.ocppLib.wrappers.Response;

import it.besmart.ocppLib.dto.config.BrandModel;

public class BrandResponse extends AckResponse {

	
	private BrandModel brand;
	
	public BrandResponse() {
		super();
	}

	public BrandModel getBrand() {
		return brand;
	}

	public void setBrand(BrandModel brand) {
		this.brand = brand;
	}

	
}
