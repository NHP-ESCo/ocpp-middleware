package it.besmart.ocpp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

import it.besmart.ocpp.enums.ParamDefinitionType;
import it.besmart.ocppLib.dto.config.ParameterKey;


@Entity
public class ExtraParamType extends ParamType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_extraparamtype_generator")
	@SequenceGenerator(name="z_extraparamtype_generator", sequenceName = "z_extraparamtype_seq", allocationSize=1)
	private long paramID;
	
	@JsonBackReference
	@ManyToOne
	private Model model;
	
	
	public ExtraParamType() {
		super();
	}
	
	
	public ExtraParamType(ParameterKey p) {
		super(p);
		
	}


	public long getParamID() {
		return paramID;
	}


	public Model getModel() {
		return model;
	}


	public void setModel(Model model) {
		this.model = model;
	}


	@Override
	public String toString() {
		return "ExtraParamType [name=" + name + ", type=" + type
				+ ", editable=" + editable + ", visible=" + visible + ", autoconfigured=" + autoconfigured + "]";
	}


	@Override
	public ParamDefinitionType getDefType() {
		return ParamDefinitionType.Model;
	}

}
