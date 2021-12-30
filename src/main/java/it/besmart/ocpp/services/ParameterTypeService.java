package it.besmart.ocpp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.model.ExtraParamType;
import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocpp.model.ProtocolParamType;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.repositories.ExtraParamTypeRepository;
import it.besmart.ocpp.repositories.ProtocolParamTypeRepository;
import it.besmart.ocpp.services.interfaces.IParameterSelectService;
import it.besmart.ocpp.services.interfaces.IParameterTypeService;
import it.besmart.ocppLib.dto.config.ParameterKey;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

@Service
@Transactional
public class ParameterTypeService implements IParameterTypeService {

	@Autowired
	private ProtocolParamTypeRepository protocolRepo;
	
	@Autowired
	private ExtraParamTypeRepository modelRepo;

	@Autowired
	private IParameterSelectService selectService;
	
	
	@Override
	public List<ProtocolParamType> findByProtocol(ProtocolVersion protocol) {
		
		return protocolRepo.findByProtocol(protocolSimpleVersion(protocol));
	}
	
	@Override
	public List<ExtraParamType> findByModel(Model model) {
		
		return modelRepo.findByModel(model);
	}

	
	private ProtocolVersion protocolSimpleVersion(ProtocolVersion protocol) {
		if (protocol == ProtocolVersion.OCPPS16 ) {
			return ProtocolVersion.OCPPJ16;
		}
		else
			return protocol;
	}
	
	@Override
	public ProtocolParamType saveProtocolParameter(ParameterKey param, ProtocolVersion p) {
		ProtocolParamType entity = new ProtocolParamType(param);
		entity.setProtocol(protocolSimpleVersion(p));
		
		entity = protocolRepo.save(entity);
		
		
		if(entity.isSelectable()) {
			selectService.saveOptions(entity, param.getSelectValues(), null);
		}
		
		return entity;
	}

	
	@Override
	public ExtraParamType saveModelParameter(ParameterKey param, Model model) {
		ExtraParamType entity = new ExtraParamType(param);
		entity.setModel(model);
		
		entity = modelRepo.save(entity);
		
		if(entity.isSelectable()) {
			selectService.saveOptions(entity, param.getSelectValues(), model);
		}
		
		return entity;
	}

	@Override
	public ExtraParamType updateModelParameter(ExtraParamType entity, ParameterKey param) {
		entity.setProperties(param);
		
		if(entity.isSelectable()) {
			selectService.updateOptions(entity, param.getSelectValues(), entity.getModel());
		}
		
		return modelRepo.save(entity);
	}
	
	@Override
	public ParamType findByNameInStation(String s, ChargingStation cs) {
		ParamType p = protocolRepo.findByNameAndProtocol(s, protocolSimpleVersion(cs.getProtocol()));
		if(p==null)
			p = modelRepo.findByNameAndModel(s, cs.getModel());
		return p;
	}

	@Override
	public ExtraParamType findByNameInModel(String name, Model model) {
		
		return modelRepo.findByNameAndModel(name, model);
	}

	@Override
	public ProtocolParamType findByNameInProtocol(String name, ProtocolVersion p) {
		
		return protocolRepo.findByNameAndProtocol(name, protocolSimpleVersion(p));
	}


}
