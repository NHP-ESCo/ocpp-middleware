package it.besmart.ocpp.services.interfaces;

import java.util.List;

import it.besmart.ocpp.model.ExtraParamType;
import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocpp.model.ProtocolParamType;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocppLib.dto.config.ParameterKey;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

public interface IParameterTypeService {
	
	List<ProtocolParamType> findByProtocol(ProtocolVersion p);
	
	List<ExtraParamType> findByModel(Model model);

	ParamType findByNameInStation(String s, ChargingStation cs);

	
	ProtocolParamType findByNameInProtocol(String name, ProtocolVersion p);
	
	ProtocolParamType saveProtocolParameter(ParameterKey param, ProtocolVersion p);
	
	
	ExtraParamType findByNameInModel(String name, Model entity);
	
	ExtraParamType saveModelParameter(ParameterKey param, Model model);

	ExtraParamType updateModelParameter(ExtraParamType paramEntity, ParameterKey p);
	
}
