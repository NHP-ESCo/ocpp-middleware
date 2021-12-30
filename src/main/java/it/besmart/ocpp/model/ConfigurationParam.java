package it.besmart.ocpp.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

import it.besmart.ocpp.exceptions.ParameterException;
import it.besmart.ocpp.utils.BooleanUtils;
import it.besmart.ocpp.utils.ParamUtils;
import it.besmart.ocppLib.enumeration.ParameterClassType;

// Parameters received by charging station, related to protocol and model


@Entity
public class ConfigurationParam implements Comparable<ConfigurationParam>{
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_configparam_generator")
	@SequenceGenerator(name="z_configparam_generator", sequenceName = "z_configparam_seq", allocationSize=1)
	private long paramId;
	
	
	@ManyToOne
	private ProtocolParamType protocolParam;
	
	@ManyToOne
	private ExtraParamType modelParam;
	
	@ManyToOne
	private BasicParamType basicParam;
	
	
	private String value;
	
	
	@ManyToOne
	@JsonBackReference
	private ChargingStation station;
	
	private boolean unsupported = false;

	public ConfigurationParam() {
		super();
	}
	
	
	public ConfigurationParam(ProtocolParamType p) {
		this.protocolParam = p;
	}
	
	
	public ConfigurationParam(ExtraParamType p) {
		this.modelParam = p;
	}
	
	public ConfigurationParam(BasicParamType p) {
		this.basicParam = p;
	}


	public ConfigurationParam(ParamType p) {
		if(p.getClass() == ExtraParamType.class )
				this.modelParam = (ExtraParamType) p;
		else if(p.getClass() == ProtocolParamType.class )
			this.protocolParam = (ProtocolParamType) p;
		else
			this.basicParam = (BasicParamType) p;
	}


	public ParamType getParam() {
		if(this.protocolParam!=null) 
			return protocolParam;
		else if (this.modelParam!=null)
			return modelParam;
		else if(this.basicParam!=null)
			return basicParam;
		else
			return null;
	}
	

	public long getParamId() {
		return paramId;
	}


	public String getValue() {
		//some stations throw error with true/false values
		if(value!= null
				&& this.getParam().getType().equals(ParameterClassType.Boolean)) {
			
			if (BooleanUtils.isFalse(value))
				return "0";
			else
				return "1";
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setValidatedValue(String value, List<String> acceptedValues) throws ParameterException {
		
		if(value==null) {
			this.value = null;
			return;
		}
		
		switch(this.getParam().type) {
		case Boolean:
			if(value.equals("1") || value.equalsIgnoreCase("true"))
				this.value = "true";
			else if (value.equals("0") || value.equalsIgnoreCase("false"))
				this.value = "false";
			else
				throw new ParameterException(String.format("Parameter %s is not boolean", this.getParam().getName()));
			
			break;
		case Integer:
			int val = Integer.valueOf( value );	
			
			if(val<this.getParam().minValue || val > this.getParam().maxValue)
				throw new ParameterException(String.format("Integer parameter %s is out of predefined range", this.getParam().getName()));
			
			this.value = value;
			break;
		case MultiSelect:
			String[] values = value.split(",");
			for(String v : values) {
				if(acceptedValues==null || ! acceptedValues.contains(v))
					throw new ParameterException(String.format("Value %s for parameter %s is not acceptable", value, this.getParam().getName()));
			}
			this.value = value;
			break;
		case Select:
			if(acceptedValues==null || ! acceptedValues.contains(value))
				throw new ParameterException(String.format("Value %s for parameter %s is not acceptable", value, this.getParam().getName()));
			this.value = value;
			break;
		case String:
			this.value = value;
			break;
		case Unknown:
			throw new ParameterException("Unknown type of parameter " + this.getParam().getName());
		
		}
	
	}

	public ChargingStation getStation() {
		return station;
	}

	public void setStation(ChargingStation station) {
		this.station = station;
	}


	public ProtocolParamType getProtocolParam() {
		return protocolParam;
	}


	public void setProtocolParam(ProtocolParamType protocolParam) {
		this.protocolParam = protocolParam;
	}


	public ExtraParamType getModelParam() {
		return modelParam;
	}


	public void setModelParam(ExtraParamType modelParam) {
		this.modelParam = modelParam;
	}


	public BasicParamType getBasicParam() {
		return basicParam;
	}


	public void setBasicParam(BasicParamType basicParam) {
		this.basicParam = basicParam;
	}


	@Override
	public String toString() {
		return "\n[Param=" + getParam() + ", value=" + value + "]";
	}


	public boolean isUnsupported() {
		return unsupported;
	}


	public void setUnsupported(boolean unsupported) {
		this.unsupported = unsupported;
	}


	@Override
	public int compareTo(ConfigurationParam o) {
		
		return this.getParam().getName().compareTo(o.getParam().getName());
	}
	
	

}
