package it.besmart.ocppLib.dto.config;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class UnitModel {

	@JsonIgnore
	private long unitID;
	
	@NotNull
	private int ref;
	
	@Valid
	@NotNull
	@Size(min = 1)
	private Set<ConnectorModel> connectors = new HashSet<>();
	
	
	public UnitModel() {
		super();
	}


	public long getUnitID() {
		return unitID;
	}

	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}



	public Set<ConnectorModel> getConnectors() {
		return connectors;
	}



	public void setConnectors(Set<ConnectorModel> connectors) {
		this.connectors = connectors;
	}



	public void addConnector(ConnectorModel connDTO) {
		this.connectors.add(connDTO);
		
	}
	

	@Override
	public String toString() {
		return "ModelUnitDTO [unitID=" + unitID + ", ref=" + ref + ", connectors=" + connectors + "]";
	}




}
