package it.besmart.ocpp.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import it.besmart.ocppLib.enumeration.CapabilityStatus;

@Entity
public class StationCapability {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_capability_generator")
	@SequenceGenerator(name="z_capability_generator", sequenceName = "z_capability_seq", allocationSize=1)
	private long capabilityID;
	
	
	@ManyToOne
	private ChargingStation station;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private ModelCapability capability;
	
	@Enumerated(EnumType.STRING)
	private CapabilityStatus value;

	
	public StationCapability() {
		super();
	}

	public ChargingStation getStation() {
		return station;
	}

	public void setStation(ChargingStation station) {
		this.station = station;
	}

	public ModelCapability getCapability() {
		return capability;
	}

	public void setCapability(ModelCapability capability) {
		this.capability = capability;
	}

	public CapabilityStatus getValue() {
		return value;
	}

	public void setValue(CapabilityStatus value) {
		this.value = value;
	}


}
