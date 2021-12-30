package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.WlCard;

public interface WlCardRepository extends JpaRepository<WlCard, Long>, JpaSpecificationExecutor<WlCard> {

	public WlCard findByIdTag(String s);
	
}
