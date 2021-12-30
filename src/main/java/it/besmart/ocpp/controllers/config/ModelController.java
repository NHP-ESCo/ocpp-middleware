package it.besmart.ocpp.controllers.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.besmart.ocpp.exceptions.ModelException;
import it.besmart.ocpp.model.Brand;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ConfigurationParam;
import it.besmart.ocpp.model.ExtraParamType;
import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ModelCapability;
import it.besmart.ocpp.model.ModelConnector;
import it.besmart.ocpp.model.ModelUnit;
import it.besmart.ocpp.model.ParamSelectValue;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocpp.services.interfaces.IBrandService;
import it.besmart.ocpp.services.interfaces.IConfigParamService;
import it.besmart.ocpp.services.interfaces.IModelCapabilityService;
import it.besmart.ocpp.services.interfaces.IModelService;
import it.besmart.ocpp.services.interfaces.IParameterSelectService;
import it.besmart.ocpp.services.interfaces.IParameterTypeService;
import it.besmart.ocpp.services.interfaces.IStationService;
import it.besmart.ocpp.wrappers.APIError;
import it.besmart.ocppLib.dto.Capability;
import it.besmart.ocppLib.dto.ParameterOption;
import it.besmart.ocppLib.dto.config.BrandModel;
import it.besmart.ocppLib.dto.config.ConnectorModel;
import it.besmart.ocppLib.dto.config.ParameterKey;
import it.besmart.ocppLib.dto.config.StationModel;
import it.besmart.ocppLib.dto.config.UnitModel;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;
import it.besmart.ocppLib.wrappers.Response.AckResponse;
import it.besmart.ocppLib.wrappers.Response.ModelDataListResponse;
import it.besmart.ocppLib.wrappers.Response.ModelDataResponse;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@RestController
@RequestMapping("api/model")
public class ModelController {
	
	@Autowired
	private IModelService service;
	
	@Autowired
	private IStationService stationService;
	
	@Autowired
	private IConfigParamService paramService;
	
	@Autowired
	private IBrandService brandService;
	
	@Autowired
	private IParameterTypeService paramTypeService;
	
	@Autowired
	private IParameterSelectService paramSelectService;
	
	@Autowired
	private IModelCapabilityService capabilityService;
	
	
	private final Logger logger = LoggerFactory.getLogger(ModelController.class);
	
	
	@PostMapping("/save")
	public ResponseEntity<AckResponse> saveNewModel(HttpServletRequest request, 
			@RequestBody @Valid StationModel model)  {
		
		AckResponse res = new AckResponse(false, null);
		
		Model entity = null;
		
		try {
			entity = service.createModel(model);
		}
		catch( ModelException e) {
			return APIError.response(e.getLocalizedMessage());
		}
		
		res.setResult(true);
		res.setMessage(String.format("Model %s was succesfully saved", entity.getExternalCode()));
		return new ResponseEntity<>(res, HttpStatus.OK);
	
	}
	
	
	@PostMapping("/update")
	public ResponseEntity<AckResponse> updateModel(HttpServletRequest request, 
			@RequestBody StationModel model) {
		
		AckResponse res = new AckResponse(false, null);

		
		Model entity = service.findByExternalCode(model.getModelCode());
		
		if(entity==null) {
			res.setMessage("Unexistent Model");
			return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
		};
		
		entity = service.updateModel(model, entity);
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("model", Operations.EQUAL, entity);
		for(ChargingStation s : stationService.findBySpecification(builder.build())) {
			
			for(ExtraParamType t : entity.getExtraParams()) {
				ConfigurationParam param = paramService.findParameterInStation(t.getName(), s);

				if(param==null) {
					logger.debug(String.format("Aggiungo parametro %s alla stazione %s", t.getName(), s.getEvseID()));
					
					param = new ConfigurationParam(t);
					param.setStation(s);
					param.setValue(t.getDefaultValue());

					paramService.addOrUpdateParam(param);

				}
			}
			
		}
		
		
		if(entity==null) {
			res.setMessage("Unexistent Model");
			return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
		}
		
		res.setResult(true);
		res.setMessage(String.format("Updated Model %d", entity.getExternalCode()));
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	
	@PostMapping("/delete/{modelCode}") 
	public ResponseEntity<AckResponse> deleteModel(HttpServletRequest request, @PathVariable String modelCode) {
		
		AckResponse res = new AckResponse(false, null);
		
		Model entity = service.findByExternalCode(modelCode);
		if(entity==null) {
			res.setMessage("Unexistent Model");
			return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
		}
		
		service.deleteModel(entity.getModelId());
		
		
		res.setResult(true);
		res.setMessage(String.format("Cancelled Model %s", modelCode));
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@GetMapping("/{modelCode}") 
	public ResponseEntity<ModelDataResponse> getModel( HttpServletRequest request, 
			@PathVariable String modelCode ) {
		
		ModelDataResponse response = new ModelDataResponse();
		response.setResult(false);
		
		
		Model entity = service.findByExternalCode(modelCode);
		if(entity==null) {
			response.setMessage("Unexistent Model");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
		response.setModel( getDetailedModel(entity) );
		
		response.setResult(true);
		response.setMessage("Retrieved models data");
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	@GetMapping("/list/{brand_acronym}") 
	public ResponseEntity<ModelDataListResponse> getModelList( HttpServletRequest request, 
			@PathVariable String brand_acronym ) {
		
		ModelDataListResponse response = new ModelDataListResponse();
		response.setResult(false);
		
		Brand brand = brandService.findByAcronym(brand_acronym);
		
		if (brand==null) {
			response.setMessage(String.format("Brand %s does not exist", brand_acronym));
			
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
			
		List <Model> entities = service.findByBrand(brand);
		
		for(Model model : entities ) {
			response.addModel( getModel(model) );
		}
		
		response.setResult(true);
		response.setMessage("Retrieved models data");
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	/*** PRIVATE METHODS ***/
	
	
	private StationModel getDetailedModel(Model entity) {
		StationModel model = getModel(entity);
		model.setBrandCode(entity.getBrandCode());
		model.setBrandAcronym(entity.getBrand().getAcronym());
		model.setBrand(getBrandDTO(entity.getBrand()));
		model.setType(entity.getType());
		model.setFirmwareURL(entity.getFirmwareURL());
		model.setLastFirmware(entity.getLastFirmware());
		
		
		for(ModelCapability c : capabilityService.findAllCapabilities(entity)) {
			Capability cap = new Capability();
			cap.setEditable(c.isEditable());
			cap.setType(c.getCapability());
			
			model.addCapability(cap);
		}
		
		for (ModelUnit mu : entity.getUnits()) {
			UnitModel unit = new UnitModel();
			unit.setRef(mu.getRef());
			
			for (ModelConnector modelConn : mu.getConnectors() ) {
				ConnectorModel conn = new ConnectorModel();
				conn.setPlugType(modelConn.getPlug());
				conn.setRef(modelConn.getRef());
				conn.setMaxPower(modelConn.getMaxPower());
				conn.setMinPower(modelConn.getMinPower());
				conn.setMaxCurrent(modelConn.getMaxCurrent());
				conn.setMinCurrentM(modelConn.getMinCurrentM());
				conn.setMinCurrentT(modelConn.getMinCurrentT());
				unit.addConnector(conn);
			}

			model.addUnit(unit);
				
		}
		
		List<ParamType> parameters = new ArrayList<>();
		
		parameters.addAll(paramTypeService.findByModel(entity));
		
		for (ParamType param :  parameters) {
			ParameterKey p = new ParameterKey();
			p.setName(param.getName());
			p.setType(param.getType());
			p.setEditable(param.isEditable());
			p.setAutoconfigured(param.isAutoconfigured());
			p.setDefaultValue(param.getDefaultValue());
			p.setVisible(param.isVisible());
			p.setDescription(param.getDescription());
			switch(p.getType()) {
			case Integer:
				p.setMinValue(param.getMinValue());
				p.setMaxValue(param.getMaxValue());
				break;
			case MultiSelect:
			case Select:
				List<ParameterOption> values = new ArrayList<>();
				for(ParamSelectValue s : paramSelectService.findByParamAndModel(param, entity)) {
					ParameterOption o = new ParameterOption(s.getValue());
					if(s.getOptionName()!=null)
						o.setOptionName(s.getOptionName());
					
					values.add(o);
				}
				p.setSelectValues(values);
				break;
			default:
				break;
			
			}
			
			model.addExtraParam(p);
		}
		
		return model;
	}


	private StationModel getModel(Model entity) {
		
		StationModel model = new StationModel();
		model.setName(entity.getName());
		model.setModelCode(entity.getExternalCode());
		
		
		for (ProtocolVersion protocol : entity.getProtocols()) {
			model.addProtocol(protocol);
		}
		
		for (ConnectionPowerType powerType : ConnectionPowerType.values()) {
			if(entity.isPowerTypeEnabled(powerType))
				model.addPowerType(powerType);
		}
		
		return model;
	}
	
	private BrandModel getBrandDTO(Brand brand) {
		BrandModel b = new BrandModel();
		
		b.setName(brand.getName());
		b.setAcronym(brand.getAcronym());
		b.setSupplier(brand.getSupplier());	
		
		return b;
	}
	
}
