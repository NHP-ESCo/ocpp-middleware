package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import it.besmart.ocpp.model.ParamType;

@NoRepositoryBean //abstract superclass
public interface ParamTypeRepository<P extends ParamType> extends JpaRepository<P, Long> {
	
}
