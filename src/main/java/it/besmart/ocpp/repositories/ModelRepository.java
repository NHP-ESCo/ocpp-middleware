package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.Brand;
import it.besmart.ocpp.model.Model;

public interface ModelRepository extends JpaRepository<Model, Long>, JpaSpecificationExecutor<Model>{

	public List<Model> findByBrand(Brand b);
	
	public Model findByBrandCodeAndBrand(String code, Brand brand);

	public Model findByExternalCode(String extCode);
}
