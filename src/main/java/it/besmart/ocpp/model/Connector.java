package it.besmart.ocpp.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.utils.PlugUtils;

@Entity
public class Connector {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_connector_generator")
	@SequenceGenerator(name="z_connector_generator", sequenceName = "z_connector_seq", allocationSize=1)
	private long connectorID;
	
	@NotNull
	@JsonBackReference
	@ManyToOne
	private ChargingUnit unit;
	
	@NotNull
	private int ref; //# in the CS
	
	@ManyToOne
	private ModelConnector plug;
	
	private Double maxPower; //model limit
	
	private Double minPower; //model limit
	
	private Double actualMaxPower;
	
	private Double actualMinPower;
	
	@Enumerated(EnumType.STRING)
	private ConnectorStatusComplete status;

	
	
	public Connector() {
		super();
	}

	
	public boolean isCharging() {
		if(status==ConnectorStatusComplete.CHARGING || status==ConnectorStatusComplete.OCCUPIED
				|| status==ConnectorStatusComplete.SUSPENDEDEV || status==ConnectorStatusComplete.SUSPENDEDEVSE)
			return true;
		else 
			return false;
	}
	
	public boolean isReady() {
		if(status==ConnectorStatusComplete.AVAILABLE || status==ConnectorStatusComplete.PREPARING)
			return true;
		else 
			return false;
	}


	public ChargingUnit getUnit() {
		return unit;
	}

	public void setUnit(ChargingUnit unit) {
		this.unit = unit;
	}

	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}

	public ModelConnector getPlug() {
		return plug;
	}

	public void setPlug(ModelConnector plug) {
		this.plug = plug;
	}

	public ConnectorStatusComplete getStatus() {
		return status;
	}

	public void setStatus(ConnectorStatusComplete status) {
		this.status = status;
	}

	public long getConnectorID() {
		return connectorID;
	}
	
	public Double getMaxPower() {
		return maxPower;
	}


	public void setMaxPower() {
		this.maxPower = plug.computeMaxPower(unit.getPowerType());
	}
	

	public void setMinPower() {
		this.minPower = plug.computeMinPower(unit.getPowerType());
	}

	public Double getMinPower() {
		return minPower;
	}


	public void setMinPower(Double minPower) {
		this.minPower = minPower;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Connector [connectorID=").append(connectorID).append(", unit=").append(unit).append(", ref=")
				.append(ref).append(", plug=").append(plug).append(", maxPower=").append(maxPower)
				.append(", actualMaxPower=").append(actualMaxPower).append(", minPower=").append(minPower)
				.append(", status=").append(status).append("]");
		return builder.toString();
	}


	public Double getActualMaxPower() {
		return actualMaxPower;
	}


	public void setActualMaxPower(Double actualMaxPower) {
		this.actualMaxPower = actualMaxPower;
	}

	public double computeMaxCurrent() {
		
		return PlugUtils.computeCurrent(this.unit.getPowerType(), this.actualMaxPower);
	
	}
	
	public double computeMinCurrent() {
		
		return PlugUtils.computeCurrent(this.unit.getPowerType(), this.minPower);
	
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (connectorID ^ (connectorID >>> 32));
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
		Connector other = (Connector) obj;
		if (connectorID != other.connectorID)
			return false;
		return true;
	}


	public boolean isFinishing() {
		return this.status.equals(ConnectorStatusComplete.FINISHING);
	}


	public Double getActualMinPower() {
		return actualMinPower;
	}


	public void setActualMinPower(Double actualMinPower) {
		this.actualMinPower = actualMinPower;
	}

	
	
}
