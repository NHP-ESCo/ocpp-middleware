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

import it.besmart.ocpp.exceptions.ModelException;
import it.besmart.ocpp.utils.PlugUtils;
import it.besmart.ocppLib.dto.config.ConnectorModel;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.PlugPowerType;
import it.besmart.ocppLib.enumeration.PlugType;

@Entity
public class ModelConnector {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_modelconnector_generator")
	@SequenceGenerator(name="z_modelconnector_generator", sequenceName = "z_modelconnector_seq", allocationSize=1)
	private long connectorID;
	
	@NotNull
	@JsonBackReference
	@ManyToOne
	private ModelUnit unit;
	
	@NotNull
	private int ref;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private PlugType plug;
	
	
	private Double maxCurrent;
	
	private Double minCurrentM;

	private Double minCurrentT;
	
	
	private Double minPower;

	private Double maxPower;
	
	
	public ModelConnector() {
		super();
	}

	
	
	public ModelUnit getUnit() {
		return unit;
	}

	public void setUnit(ModelUnit unit) {
		this.unit = unit;
	}

	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}

	public PlugType getPlug() {
		return plug;
	}

	public void setPlug(PlugType plug) {
		this.plug = plug;
	}

	public long getConnectorID() {
		return connectorID;
	}



	@Override
	public String toString() {
		return "ModelConnector [connectorID=" + connectorID + ", ref=" + ref + ", plug=" + plug + "]";
	}



	public Double getMaxCurrent() {
		return maxCurrent;
	}



	public void setMaxCurrent(Double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}



	public Double getMinCurrentM() {
		return minCurrentM;
	}



	public void setMinCurrentM(Double minCurrentM) {
		this.minCurrentM = minCurrentM;
	}



	public Double getMinCurrentT() {
		return minCurrentT;
	}



	public void setMinCurrentT(Double minCurrentT) {
		this.minCurrentT = minCurrentT;
	}



	public Double getMinPower() {
		return minPower;
	}



	public void setMinPower(Double minPower) {
		this.minPower = minPower;
	}



	public Double getMaxPower() {
		return maxPower;
	}



	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}
	
	
	
	public double computeMaxPower(ConnectionPowerType powerType) {
	
		switch(plug.getPlugPowerType()) {
		
			case DC :
				return this.maxPower;
			case CCS :
				return this.maxPower;
			case AC_1_PHASE :
				return PlugUtils.computePower(ConnectionPowerType.MONOPHASE, this.maxCurrent);
			case AC_3_PHASE :
			case AC :
				return PlugUtils.computePower(powerType, this.maxCurrent);
			default:
				return 0;
		}
	}

	public double computeMinPower(ConnectionPowerType powerType) {
		
		if(minPower!=null)
			return minPower;
		else {
			if(plug.getPlugPowerType().equals(PlugPowerType.AC_1_PHASE) || powerType == ConnectionPowerType.MONOPHASE) {
				return PlugUtils.computePower(ConnectionPowerType.MONOPHASE, this.minCurrentM);
			}
			else {
				return PlugUtils.computePower(powerType, this.minCurrentT);
			}
		}
	}



	public void setLimits(ConnectorModel connDTO) {

		switch(plug.getPlugPowerType()) {
		
		case AC_1_PHASE:
			if(connDTO.getMinCurrentM()==null)
				throw new ModelException("MinCurrentM has to be defined for connector " + ref);
			setCurrentLimits(connDTO);
			break;
		case AC_3_PHASE:
			if(connDTO.getMinCurrentT()==null)
				throw new ModelException("MinCurrentT has to be defined for connector " + ref);
			setCurrentLimits(connDTO);
			break;
		case AC:
			setCurrentLimits(connDTO);
			break;
		case CCS:
		case DC:
			if(connDTO.getMaxPower()==null || connDTO.getMinPower()==null  
				|| connDTO.getMaxPower() < connDTO.getMinPower() )
				throw new ModelException("Unacceptable values for power limits of connector " + ref);
		
			this.maxPower = connDTO.getMaxPower();
			this.minPower = connDTO.getMinPower();
			break;
		default:
			break;
		
		}
		
	}
	
	private void setCurrentLimits(ConnectorModel connDTO) {
		if(connDTO.getMaxCurrent()==null || (connDTO.getMinCurrentM()==null && connDTO.getMinCurrentT()==null ) )
			throw new ModelException("Current limits have to be defined for connector " + ref);
		this.maxCurrent = connDTO.getMaxCurrent();
		
		if( (connDTO.getMinCurrentM()!=null && connDTO.getMaxCurrent() < connDTO.getMinCurrentM()) || 
				(connDTO.getMinCurrentT()!=null && connDTO.getMaxCurrent() < connDTO.getMinCurrentT()) ) 
			throw new ModelException("Unacceptable values for current limits of connector " + ref);
		
		if(connDTO.getMinCurrentM()==null) {
			this.minCurrentT = connDTO.getMinCurrentT();
			this.minCurrentM = connDTO.getMinCurrentT();
		}
		else {
			this.minCurrentT = connDTO.getMinCurrentM();
			this.minCurrentM = connDTO.getMinCurrentM();
		}
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
		ModelConnector other = (ModelConnector) obj;
		if (connectorID != other.connectorID)
			return false;
		return true;
	}
}

