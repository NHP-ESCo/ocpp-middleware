package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.ParamSelectValue;
import it.besmart.ocpp.model.ProtocolParamType;

public interface ParamSelectRepository extends JpaRepository<ParamSelectValue, Long>, JpaSpecificationExecutor<ParamSelectValue> {

	List<ParamSelectValue> findByProtocolParamAndModelIsNull(ProtocolParamType param);

}
