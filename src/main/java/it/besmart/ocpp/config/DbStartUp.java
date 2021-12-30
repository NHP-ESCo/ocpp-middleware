package it.besmart.ocpp.config;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.besmart.ocpp.model.Brand;
import it.besmart.ocpp.services.interfaces.IBrandService;
import it.besmart.ocpp.services.interfaces.IModelService;
import it.besmart.ocpp.services.interfaces.IParameterTypeService;
import it.besmart.ocppLib.dto.config.BrandModel;
import it.besmart.ocppLib.dto.config.StationModel;
import it.besmart.ocppLib.dto.config.ParameterKey;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

@Configuration
public class DbStartUp {

	@Autowired
	private IParameterTypeService paramService;
	
	@Autowired
	private IBrandService brandService;
	
	@Autowired
	private IModelService modelService;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	private final Logger logger = LoggerFactory.getLogger(DbStartUp.class);
	
	@PostConstruct
	public void firstStart() {
		
		//store ocpp 1.6 parameters
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			File parameterJson = resourceLoader.getResource("classpath:data/parameters_ocpp1_6.json").getFile();
			
			ParameterKey[] params = mapper.readValue(parameterJson, ParameterKey[].class);
			
			logger.debug(String.format("Saving %d parameters", params.length));
			
			for(ParameterKey p : params) {
				
				if(paramService.findByNameInProtocol(p.getName(), ProtocolVersion.OCPPJ16)==null)
					paramService.saveProtocolParameter(p, ProtocolVersion.OCPPJ16);
			}
		} catch (IOException e) {
	
			e.printStackTrace();
		}
		
		
		//save a brand test
		Brand b = brandService.findByAcronym("XXX");
		if(b==null) {
			BrandModel brand = new BrandModel();
			brand.setName("TestBrand");
			brand.setAcronym("XXX");
			brand.setSupplier("eCharge");
			
			b = brandService.createBrand(brand);
		}
		
		//save a model test
		
		try {
			File modelJson = resourceLoader.getResource("classpath:data/model.json").getFile();
			
			StationModel model;
			
			model = mapper.readValue(modelJson, StationModel.class);
			model.setBrandAcronym("XXX");
			model.setBrandCode("Test");
			if(modelService.findByBrandAndCode(b, model.getBrandCode())==null) {
				modelService.createModel(model);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
	}
	
	
}
