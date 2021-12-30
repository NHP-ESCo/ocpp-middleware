package it.besmart.ocpp.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.model.BasicParamType;
import it.besmart.ocpp.model.ExtraParamType;
import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ParamSelectValue;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocpp.model.ProtocolParamType;
import it.besmart.ocpp.repositories.ParamSelectRepository;
import it.besmart.ocpp.services.interfaces.IParameterSelectService;
import it.besmart.ocppLib.dto.ParameterOption;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Service
@Transactional
public class ParameterSelectService implements IParameterSelectService {

	@Autowired
	ParamSelectRepository repo;
	
	private final Logger logger = LoggerFactory.getLogger(ParameterSelectService.class);
	
	
	@Override
	public List<ParamSelectValue> findByParamAndModel(ParamType param, Model model) {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		
		switch(param.getDefType()) {
		case Basic:
			builder.with("basicParam", Operations.EQUAL, param);
			break;
		case Model:
			builder.with("modelParam", Operations.EQUAL, param);
			break;
		case Protocol:
			//Std options without model
			List<ParamSelectValue> specificList = repo.findByProtocolParamAndModelIsNull((ProtocolParamType) param);
			
			//Plus extra options related to model //TODO not repeat
			if(((ProtocolParamType) param).isModelSpecific()) {
				builder.with("protocolParam", Operations.EQUAL, param);
				builder.with("model", Operations.EQUAL, model);
				specificList.addAll(repo.findAll(builder.build()));
			}
			return specificList;
		default:
			return new ArrayList<ParamSelectValue>();
		}
		
		return repo.findAll(builder.build());
	}
	

	@Override
	public List<ParamSelectValue> saveOptions(ParamType param, List<ParameterOption> val, Model model) {
		logger.debug(String.format("Check parameter %s", param.getName()));
		
		List<ParamSelectValue> options = new ArrayList<>();
		
		for(ParameterOption o : val) {
			ParamSelectValue select = new ParamSelectValue();
				
			switch(param.getDefType()) {
			case Basic:
				select.setBasicParam((BasicParamType) param);
				break;
			case Model:
				select.setModelParam((ExtraParamType) param);
				break;
			case Protocol:
				select.setProtocolParam((ProtocolParamType) param);
				select.setModel(model);
				break;
			}
			
			select.setValue(o.getOptionValue());
			select.setOptionName(o.getOptionName());
			logger.debug(String.format("Added option %s", o.getOptionValue()));

			options.add(select);
			
		}
		
		repo.saveAll(options);
		
		return options;
	}
	
	@Override
	public List<ParamSelectValue> updateOptions(ParamType param, List<ParameterOption> val, Model model) {
		logger.debug(String.format("Check parameter %s", param.getName()));
		
		List<ParamSelectValue> oldOptions = findByParamAndModel(param, model);
		//logger.debug(String.format("Parameter %s. Actual values: %s", param.getName(), oldOptions.toString()));
		
		List<ParamSelectValue> options = new ArrayList<>();
		List<ParamSelectValue> removedOptions = oldOptions;
		
		for(ParameterOption o : val) {
			boolean present = false;
			ParamSelectValue select = null;
			
			for(ParamSelectValue oldValue : oldOptions) {
				
				if(oldValue.getValue().equalsIgnoreCase(o.getOptionName())) {
					present = true;
					select = oldValue;
					removedOptions.remove(oldValue);
					break;
				}
			}
			
			if(!present) {
				select = new ParamSelectValue();
				
				switch(param.getDefType()) {
				case Basic:
					select.setBasicParam((BasicParamType) param);
					break;
				case Model:
					select.setModelParam((ExtraParamType) param);
					break;
				case Protocol:
					select.setProtocolParam((ProtocolParamType) param);
					select.setModel(model);
					break;
				}
				
				select.setValue(o.getOptionValue());
				select.setOptionName(o.getOptionName());
				logger.debug(String.format("Added option %s", o.getOptionValue()));
				
			}
			else {
				//logger.debug(String.format("Option %s already present", v));
			}

			options.add(select);
			
		}
		
		repo.saveAll(options);
		
		repo.deleteAll(removedOptions);
		
		return options;
	}
	
	
}
