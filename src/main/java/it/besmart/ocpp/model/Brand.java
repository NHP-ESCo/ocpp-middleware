package it.besmart.ocpp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotEmpty;

import it.besmart.ocppLib.dto.config.BrandModel;


@Entity
public class Brand {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_brand_generator")
	@SequenceGenerator(name="z_brand_generator", sequenceName = "z_brand_seq", allocationSize=1)
	private long brandID;
	
	@NotEmpty
	private String name;
	
	@NotEmpty
	@Column(unique=true)
	private String acronym;
	
	private String supplier;
	
	
	
	public Brand() {
		super();
	}
	
	public Brand(BrandModel brand) {
		this.name = brand.getName();
		this.acronym = brand.getAcronym();
		this.supplier = brand.getSupplier();
	}
	
	
	public long getBrandId() {
		return brandID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAcronym() {
		return acronym;
	}
	
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}


	public String getSupplier() {
		return supplier;
	}


	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	

}
