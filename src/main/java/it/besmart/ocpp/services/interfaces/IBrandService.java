package it.besmart.ocpp.services.interfaces;

import java.util.List;

import it.besmart.ocpp.model.Brand;
import it.besmart.ocppLib.dto.config.BrandModel;

public interface IBrandService {

	public Brand createBrand(BrandModel brand);
	
	public Brand updateBrand(BrandModel brand);
	
	public List<Brand> findAll();
	
	public Brand findByAcronym(String acronym);

	public Brand findById(long id);
	
}
