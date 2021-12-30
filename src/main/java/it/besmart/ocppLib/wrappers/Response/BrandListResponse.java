package it.besmart.ocppLib.wrappers.Response;

import java.util.ArrayList;
import java.util.List;

import it.besmart.ocppLib.dto.config.BrandModel;

public class BrandListResponse extends AckResponse {

	
	private List<BrandModel> brands = new ArrayList<>();
	
	public BrandListResponse() {
		super();
	}

	public List<BrandModel> getBrands() {
		return brands;
	}

	public void setBrands(List<BrandModel> brands) {
		this.brands = brands;
	}
	
	public void addBrand(BrandModel brand) {
		this.brands.add(brand);
	}
}
