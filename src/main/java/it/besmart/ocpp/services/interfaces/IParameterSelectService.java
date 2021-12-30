package it.besmart.ocpp.services.interfaces;

import java.util.List;

import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ParamSelectValue;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocppLib.dto.ParameterOption;

public interface IParameterSelectService {

	List<ParamSelectValue> findByParamAndModel(ParamType param, Model model);

	List<ParamSelectValue> saveOptions(ParamType param, List<ParameterOption> list, Model model);

	List<ParamSelectValue> updateOptions(ParamType param, List<ParameterOption> list, Model model);
}
