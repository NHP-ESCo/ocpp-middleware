package it.besmart.ocpp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

@Entity
public class ParamSelectValue {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_paramselec_generator")
	@SequenceGenerator(name="z_paramselect_generator", sequenceName = "z_paramselec_seq", allocationSize=1)
	private long selectId;
	
	@ManyToOne
	private ProtocolParamType protocolParam;
	
	@ManyToOne
	private ExtraParamType modelParam;
	
	@ManyToOne
	private BasicParamType basicParam;
	
	@ManyToOne
	private Model model; //only for protocol param where more specificity is necessary
	
	@NotNull
	private String value;

	private String optionName;
	
	public ParamSelectValue() {
		super();
	}
	
	
	public long getSelectId() {
		return selectId;
	}

	public void setSelectId(long selectId) {
		this.selectId = selectId;
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

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	public String getOptionName() {
		return optionName;
	}


	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParamSelectValue [selectId=").append(selectId).append(", protocolParam=").append(protocolParam)
				.append(", modelParam=").append(modelParam).append(", basicParam=").append(basicParam)
				.append(", model=").append(model).append(", value=").append(value).append(", optionName=")
				.append(optionName).append("]");
		return builder.toString();
	}
	
	
	
	
}
