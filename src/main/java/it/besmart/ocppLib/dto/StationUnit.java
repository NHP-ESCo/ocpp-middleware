package it.besmart.ocppLib.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;


public class StationUnit {
	
	@NotNull
	private int ref; //# in the CS
	
	private Double maxPower; //only if not SC
	
	private Double minPower;

	private Set<StationConnector> connectors = new HashSet<>();
	
	@Override
	public String toString() {
		return "StationUnit [ref=" + ref + ", connectors=" + connectors + "]";
	}
	
	public StationUnit() {
		super();
	}


	public int getRef() {
		return ref;
	}


	public void setRef(int ref) {
		this.ref = ref;
	}

	
	public Set<StationConnector> getConnectors() {
		return connectors;
	}


	public void setConnectors(Set<StationConnector> connectors) {
		this.connectors = connectors;
	}

	public void addConnector(StationConnector StationConnector) {
		this.connectors.add(StationConnector);
		
	}

	public Double getMaxPower() {
		return maxPower;
	}

	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}

	public Double getMinPower() {
		return minPower;
	}

	public void setMinPower(Double minPower) {
		this.minPower = minPower;
	}
	
	public StationConnector getConn(int ref) {

		for (StationConnector conn : connectors) {
			if(conn.getRef() == ref) 
				return conn;
		}
		return null;
	}
}
