package it.besmart.ocpp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.besmart.ocpp.dtos.ChargingUnitDTO;
import it.besmart.ocpp.utils.PlugUtils;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;

//TODO: also charging unit should have ConnectionPowerType (??)

@Entity
public class ChargingUnit {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_chargingunit_generator")
	@SequenceGenerator(name="z_chargingunit_generator", sequenceName = "z_chargingunit_seq", allocationSize=1)
	private long unitID;
	
	private Double maxPower; 
	
	private Double minPower; 
	
	@NotNull
	@JsonBackReference
	@ManyToOne
	private ChargingStation chargingStation;
	
	@NotNull
	private int ref; //# in the CS
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private ConnectionPowerType powerType;
	
	@NotNull
	@NotEmpty
	private String evseCode; //evseCS*ref
	
	@JsonManagedReference
	@OneToMany(mappedBy="unit", cascade = CascadeType.ALL, orphanRemoval=true)
	private Set<Connector> connectors = new HashSet<>();

	
	public ChargingUnit() {
		super();
	}
	
	
	public ChargingUnit(ChargingUnitDTO cu) {
		this.ref = cu.getRef();
	}


	public int getRef() {
		return ref;
	}


	public void setRef(int ref) {
		this.ref = ref;
	}


	public ChargingStation getChargingStation() {
		return chargingStation;
	}


	public void setChargingStation(ChargingStation chargingStation) {
		this.chargingStation = chargingStation;
	}


	public Set<Connector> getConnectors() {
		return connectors;
	}


	public void setConnectors(Set<Connector> connectors) {
		this.connectors = connectors;
	}


	public long getUnitID() {
		return unitID;
	}


	public Double getMaxPower() {
		return maxPower;
	}


	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}

	public ConnectionPowerType getPowerType() {
		return powerType;
	}


	public void setPowerType(ConnectionPowerType powerType) {
		this.powerType = powerType;
	}


	public String getEvseCode() {
		return evseCode;
	}


	public void setEvseCode(String evseCode) {
		this.evseCode = evseCode;
	}
	
	public void createEvse() {
		this.evseCode = this.chargingStation.getEvseID() + "*" + this.ref;
		
	}


	public void addConnector(Connector conn) {
		this.connectors.add(conn);
	}
	
	
	public double computeMaxCurrent() {
		
		return PlugUtils.computeCurrent(this.powerType, this.maxPower);
	
	}
	
	
	public double computeMinCurrent() {
		
		return PlugUtils.computeCurrent(this.powerType, this.minPower);
	
	}


	@Override
	public String toString() {
		return "\n [unitID=" + unitID + ", evseCode=" + evseCode + ", maxPower=" + maxPower + ", minPower=" 
				+ minPower + "]";
	}


	public void setMinPower(Double minPower) {
		this.minPower = minPower;
	}


	public Double getMinPower() {
		return minPower;
	}


	public void setMaxPower() {
		double max = 0;
		for (Connector conn : this.connectors) {
			double plugPower = conn.getActualMaxPower();
			if( plugPower > max) {
				max = plugPower;
			}
		}
		maxPower =  max;

	}


	public void setMinPower() {
		double min = 0;
		for (Connector conn : this.connectors) {
			double plugPower = 0;
			plugPower = conn.getActualMinPower();
			if( plugPower > min) {
				min = plugPower;
			}
		}
		
		minPower = min;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (unitID ^ (unitID >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChargingUnit other = (ChargingUnit) obj;
		if (unitID != other.unitID)
			return false;
		return true;
	}


	
	
}
