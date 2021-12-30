package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.ExtraParamType;
import it.besmart.ocpp.model.Model;

public interface ExtraParamTypeRepository extends ParamTypeRepository<ExtraParamType>, JpaSpecificationExecutor<ExtraParamType> {

	List<ExtraParamType> findByModel(Model model);

	ExtraParamType findByNameAndModel(String s, Model model);
	
		
}
