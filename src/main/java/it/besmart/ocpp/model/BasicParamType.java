package it.besmart.ocpp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import it.besmart.ocpp.enums.ParamDefinitionType;

//static elements in BasicParam
@Entity
public class BasicParamType extends ParamType {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_stationparamtype_generator")
	@SequenceGenerator(name="z_stationparamtype_generator", sequenceName = "z_stationparamtype_seq", allocationSize=1)
	private long paramID;
	
	public BasicParamType() {
		super();
	}

	public long getParamID() {
		return paramID;
	}

	@Override
	public ParamDefinitionType getDefType() {
		return ParamDefinitionType.Basic;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (paramID ^ (paramID >>> 32));
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
		BasicParamType other = (BasicParamType) obj;
		if (paramID != other.paramID)
			return false;
		return true;
	}
	
}
