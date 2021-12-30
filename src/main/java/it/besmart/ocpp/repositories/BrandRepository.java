package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import it.besmart.ocpp.model.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long>{

	public Brand findByAcronym(String acronym);
	

}
