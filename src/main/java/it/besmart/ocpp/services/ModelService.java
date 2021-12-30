package it.besmart.ocpp.services;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.besmart.ocpp.exceptions.ModelException;
import it.besmart.ocpp.model.Brand;
import it.besmart.ocpp.model.ExtraParamType;
import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ModelConnector;
import it.besmart.ocpp.model.ModelUnit;
import it.besmart.ocpp.repositories.ModelRepository;
import it.besmart.ocpp.services.interfaces.IBrandService;
import it.besmart.ocpp.services.interfaces.IModelCapabilityService;
import it.besmart.ocpp.services.interfaces.IModelService;
import it.besmart.ocpp.services.interfaces.IParameterTypeService;
import it.besmart.ocppLib.dto.config.ConnectorModel;
import it.besmart.ocppLib.dto.config.StationModel;
import it.besmart.ocppLib.dto.config.UnitModel;
import it.besmart.ocppLib.dto.config.ParameterKey;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

@Service
@Transactional
public class ModelService implements IModelService {
	
	@Autowired
	private ModelRepository repo;
	
	@Autowired
	private IParameterTypeService paramService;
	
	@Autowired
	private IModelCapabilityService modelCapabilityService;
	
	@Autowired
	private IBrandService brandService;

	
	private final Logger logger = LoggerFactory.getLogger(ModelService.class);
	
	
	@Override 
	public Model createModel(StationModel m) {
		Model entity = new Model();
		entity.setName(m.getName());
		entity.setType(m.getType());
		
		Brand brand = brandService.findByAcronym(m.getBrandAcronym());
		if(brand==null)
			throw new ModelException("Unknown brand");
		
		entity.setBrand(brand);
		
		if(repo.findByBrandCodeAndBrand(m.getBrandCode(), brand)!=null) 
			throw new ModelException("Model with this code already exist");
		entity.setBrandCode(m.getBrandCode());
		entity.setExternalCode(brand.getAcronym()+"*"+m.getBrandCode());
		

		entity.setFirmwareURL(m.getFirmwareURL());
		
		for(ProtocolVersion protocol : m.getProtocols()) {
			entity.addProtocol(protocol);
		}
		
		for(ConnectionPowerType type : m.getPowerTypes()) {
			entity.addPowerType(type);
		}
		
		int units = m.getUnits().size();
		
		//create entities ModelUnit 
		for(int i=1; i<=units; i++) {
			
			UnitModel muDTO = m.getUnit(i);
			if(muDTO==null)
				throw new ModelException(String.format("Unit %d has to be defined for this model", i));
			
			ModelUnit muEntity = new ModelUnit();
			muEntity.setRef(muDTO.getRef());
			muEntity.setModel(entity);
			
			//TODO: check values of ref from 1 to N
			//add referenced entities
			for(ConnectorModel connDTO : muDTO.getConnectors() ) {
				
				ModelConnector connEntity = new ModelConnector();
				connEntity.setRef(connDTO.getRef());
				connEntity.setUnit(muEntity);
				
				connEntity.setPlug( connDTO.getPlugType() );
				
				connEntity.setLimits(connDTO);
				
				muEntity.addConnector(connEntity);
			}
			
			entity.addUnit(muEntity);
			
			logger.debug(muEntity.toString());
		}
		
		//create entities ExtraParamType
		for(ParameterKey p : m.getParameters()) {
			
			ExtraParamType paramEntity = paramService.saveModelParameter(p, entity);
			
			entity.addExtraParam(paramEntity);
			
		}
		
		
		entity =  repo.save(entity);
		
		modelCapabilityService.saveModelCapabilities(entity, m.getCapabilities());
		
		return entity;
	}


	@Override
	public Model updateModel(StationModel m, Model entity) {
		
		if(m.getFirmwareURL()!=null)
			entity.setFirmwareURL(m.getFirmwareURL());
		
		//only protocols, power types and params
		if(m.getProtocols()!=null) {
			entity.setProtocols(null);
			for(ProtocolVersion protocol : m.getProtocols()) {
				entity.addProtocol(protocol);
			}
		}
		
		if(m.getPowerTypes()!=null) {
			entity.setPowerTypes(null);
			for(ConnectionPowerType type : m.getPowerTypes()) {
				entity.addPowerType(type);
			}
		}
		
		if(m.getCapabilities()!=null) {
			modelCapabilityService.saveModelCapabilities(entity, m.getCapabilities());
		}
		
		if(m.getParameters()!=null) {
			entity.cleanParameters();
			
			for(ParameterKey p : m.getParameters()) {
				ExtraParamType paramEntity = paramService.findByNameInModel(p.getName(), entity);
				if( paramEntity ==null) {
					logger.debug("Add parameter " + p.getName());
					paramEntity = paramService.saveModelParameter(p, entity);
				}
				else {
					
					logger.debug("Already present: " + p.getName());
					paramEntity = paramService.updateModelParameter(paramEntity, p);
				}
				entity.addExtraParam(paramEntity);
				
			}
			logger.debug(entity.getExtraParams().toString());
		}
		
		return repo.save(entity);
		
	}

	@Override
	public Model findById(long id) {
		Optional<Model> o = repo.findById(id);
        if(o.isPresent()){
            return o.get();
        }
        else{
        	return  null;
        }
	}



	@Override
	public List<Model> findAll() {

		return repo.findAll();
	}



	@Override
	public void deleteModel(long id) {
		
		Model entity = findById(id);
		if(entity!=null)
			repo.delete(entity);
		
	}


	@Override
	public List<Model> findByBrand(Brand brand) {
		
		return repo.findByBrand(brand);
	}


	@Override
	public Model findByBrandAndCode(Brand brand, String brandCode) {

		return repo.findByBrandCodeAndBrand(brandCode, brand);
	}


	@Override
	public Model findByExternalCode(String extCode) {
		
		return repo.findByExternalCode(extCode);
	}
	
}
