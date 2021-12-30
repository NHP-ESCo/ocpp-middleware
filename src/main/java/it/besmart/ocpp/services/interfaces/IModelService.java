package it.besmart.ocpp.services.interfaces;

import java.util.List;
import java.util.Set;

import it.besmart.ocpp.exceptions.ModelException;
import it.besmart.ocpp.model.Brand;
import it.besmart.ocpp.model.Model;
import it.besmart.ocppLib.dto.Capability;
import it.besmart.ocppLib.dto.config.StationModel;

public interface IModelService {
	
	public Model createModel(StationModel m) throws ModelException;
	
	public Model updateModel(StationModel m, Model model);
	
	
	public Model findByBrandAndCode(Brand brand, String brandCode);
	
	public Model findByExternalCode(String extCode);

	public List<Model> findAll();
	
	public List<Model> findByBrand(Brand brand);
	
	
	public void deleteModel(long id);

	Model findById(long id);
	
}
