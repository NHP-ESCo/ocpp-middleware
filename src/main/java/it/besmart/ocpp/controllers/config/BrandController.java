package it.besmart.ocpp.controllers.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.besmart.ocpp.model.Brand;
import it.besmart.ocpp.services.interfaces.IBrandService;
import it.besmart.ocppLib.dto.config.BrandModel;
import it.besmart.ocppLib.wrappers.Response.AckResponse;
import it.besmart.ocppLib.wrappers.Response.BrandListResponse;
import it.besmart.ocppLib.wrappers.Response.BrandResponse;

@RestController
@RequestMapping("api/brand")
public class BrandController {
	
	
	@Autowired
	private IBrandService service;

	@PostMapping("/save")
	public ResponseEntity<AckResponse> saveNewBrand(HttpServletRequest request, 
			@RequestBody @Valid BrandModel brand)  {
		
		AckResponse res = new AckResponse(false, null);

		
		Brand entity = service.createBrand(brand);
		
		res.setResult(true);
		res.setMessage("Saved Brand");
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	
	@PostMapping("/update")
	public ResponseEntity<AckResponse> updateBrand(HttpServletRequest request, 
			@RequestBody  @Valid BrandModel brand)  {
		
		AckResponse res = new AckResponse(false, null);
		
		Brand entity = service.updateBrand(brand);
		
		
		if(entity==null) {
			res.setResult(false);
			res.setMessage(String.format("Brand unexistent"));
			return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
		}
		res.setResult(true);
		res.setMessage("Updated Brand");
		return new ResponseEntity<>(res, HttpStatus.OK);
	}


	@GetMapping("/list") 
	public ResponseEntity<BrandListResponse> findBrandList( HttpServletRequest request ) {
		
		BrandListResponse res = new BrandListResponse();
		List<BrandModel> brands = new ArrayList<>();
		
		List <Brand> entities = service.findAll();
		
		for(Brand p : entities ) {
			brands.add(getBrandDTO(p));
		}
		
		res.setResult(true);
		res.setBrands(brands);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	@GetMapping("/{brand_acronym}") 
	public ResponseEntity<BrandResponse> getBrand(HttpServletRequest request, @PathVariable String brand_acronym ) {
		
		BrandResponse res = new BrandResponse();
		
		Brand entity = service.findByAcronym(brand_acronym);
		
		if(entity==null) {
			res.setResult(false);
			res.setMessage(String.format("Brand unexistent"));
			return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
		}
		
		res.setResult(true);
		res.setBrand(getBrandDTO(entity));
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	
	private BrandModel getBrandDTO(Brand brand) {
		BrandModel b = new BrandModel();
		
		b.setName(brand.getName());
		b.setAcronym(brand.getAcronym());
		b.setSupplier(brand.getSupplier());	
		
		return b;
	}
	
}
