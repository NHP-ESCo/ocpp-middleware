package it.besmart.ocpp.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import it.besmart.ocpp.model.Brand;
import it.besmart.ocpp.repositories.BrandRepository;
import it.besmart.ocpp.services.interfaces.IBrandService;
import it.besmart.ocppLib.dto.config.BrandModel;


@Service
@Transactional
public class BrandService implements IBrandService {
	
	@Autowired
	private BrandRepository repo;
	
	
	@Override
	public Brand createBrand(BrandModel brand) {
		
		if( repo.findByAcronym(brand.getAcronym()) != null ) 
			throw new EntityExistsException(String.format("Brand %s already exists", brand.getAcronym()));
		
		Brand entity = new Brand(brand);
		
		return repo.save(entity);
	}


	@Override
	public Brand updateBrand(BrandModel brand) {
		Brand entity = findByAcronym(brand.getAcronym());
		
		if(entity!=null) {
			entity.setName(brand.getName());
			entity.setSupplier(brand.getSupplier());
				
			return repo.save(entity);
		}
		
		return null;
	}
	

	@Override
	public List<Brand> findAll() {
		
		return repo.findAll();
	}

	@Override
	public Brand findById(long id) {
		Optional<Brand> o = repo.findById(id);
        if(o.isPresent()){
            return o.get();
        }
        else{
        	throw new EntityNotFoundException(String.format("Brand %d non esistente", id));
        }
		
	}


	@Override
	public Brand findByAcronym(String acronym) {
		
		return repo.findByAcronym(acronym);
	}
	
}
