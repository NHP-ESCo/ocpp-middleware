package it.besmart.ocppLib.dto.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class BrandModel {
	
	@NotNull
	@NotEmpty
	private String name;
	
	
	private String acronym;
	
	
	private String supplier;
	
	
	public BrandModel() {
		super();
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

	
	@Override
	public String toString() {
		return "Brand {name=" + name + ", acronym=" + acronym + ", supplier=" + supplier
				+ "}";
	}

}
