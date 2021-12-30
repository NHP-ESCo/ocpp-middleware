package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.model.Authorization;

public interface AuthorizationRepository extends JpaRepository<Authorization, Long>, JpaSpecificationExecutor<Authorization> {

	List<Authorization> findByExternalSession(String s);
	
	List<Authorization> findByExternalSessionAndResponse(String externalSession, AuthorizationResponse accepted); 
}
