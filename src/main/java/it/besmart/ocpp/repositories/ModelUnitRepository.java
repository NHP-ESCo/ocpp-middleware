package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ModelUnit;

public interface ModelUnitRepository extends JpaRepository<ModelUnit, Long> {

	public List<ModelUnit> findByModel(Model m);
}
