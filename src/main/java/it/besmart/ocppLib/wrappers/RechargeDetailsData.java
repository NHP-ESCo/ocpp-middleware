package it.besmart.ocppLib.wrappers;


import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;


public class RechargeDetailsData {
	  @JsonProperty("session_id")
	  private String sessionId;
	
	  @JsonProperty("partner_session_id")
	  private String partnerSessionId;
	
	  @JsonProperty("evse_id")
	  private String evseId;
	  
	  private int connector;
	
	  @JsonProperty("id_tag")
	  private String idTag;
	  
	  @JsonProperty("stopping_id_tag")
	  private String stoppingIdTag;
	
	  @JsonProperty("charging_start")
	  private ZonedDateTime chargingStart;
	
	  @JsonProperty("charging_end")
	  private ZonedDateTime chargingEnd;
	
	  @JsonProperty("session_start")
	  private ZonedDateTime sessionStart;
	
	  @JsonProperty("session_end")
	  private ZonedDateTime sessionEnd;
	
	  @JsonProperty("meter_value_start")
	  private Double meterValueStart;
	
	  @JsonProperty("meter_value_end")
	  private Double meterValueEnd;
	
	  @JsonProperty("consumed_energy")
	  private Double consumedEnergy;
	
	  @JsonProperty("metering_signature")
	  private String meteringSignature;
	
	  @JsonProperty("roa_operator_id")
	  private String roaOperatorId;
	
	  @JsonProperty("roa_provider_id")
	  private String roaProviderId;
	
	  @JsonProperty("end_session_reason_code")
	  private Integer endSessionReasonCode;
	
	  @JsonProperty("end_session_reason_info")
	  private String endSessionReasonInfo;
	  
	  @JsonProperty("end_session_reason_fault")
	  private Long endSessionReasonFault;
	  
	  public RechargeDetailsData () {
	    super();
	  }

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPartnerSessionId() {
		return partnerSessionId;
	}

	public void setPartnerSessionId(String partnerSessionId) {
		this.partnerSessionId = partnerSessionId;
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public String getIdTag() {
		return idTag;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}

	public ZonedDateTime getChargingStart() {
		return chargingStart;
	}

	public void setChargingStart(ZonedDateTime chargingStart) {
		this.chargingStart = chargingStart;
	}

	public ZonedDateTime getChargingEnd() {
		return chargingEnd;
	}

	public void setChargingEnd(ZonedDateTime chargingEnd) {
		this.chargingEnd = chargingEnd;
	}

	public ZonedDateTime getSessionStart() {
		return sessionStart;
	}

	public void setSessionStart(ZonedDateTime sessionStart) {
		this.sessionStart = sessionStart;
	}

	public ZonedDateTime getSessionEnd() {
		return sessionEnd;
	}

	public void setSessionEnd(ZonedDateTime sessionEnd) {
		this.sessionEnd = sessionEnd;
	}

	public Double getMeterValueStart() {
		return meterValueStart;
	}

	public void setMeterValueStart(Double meterValueStart) {
		this.meterValueStart = meterValueStart;
	}

	public Double getMeterValueEnd() {
		return meterValueEnd;
	}

	public void setMeterValueEnd(Double meterValueEnd) {
		this.meterValueEnd = meterValueEnd;
	}

	public Double getConsumedEnergy() {
		return consumedEnergy;
	}

	public void setConsumedEnergy(Double consumedEnergy) {
		this.consumedEnergy = consumedEnergy;
	}

	public String getMeteringSignature() {
		return meteringSignature;
	}

	public void setMeteringSignature(String meteringSignature) {
		this.meteringSignature = meteringSignature;
	}

	public String getRoaOperatorId() {
		return roaOperatorId;
	}

	public void setRoaOperatorId(String roaOperatorId) {
		this.roaOperatorId = roaOperatorId;
	}

	public String getRoaProviderId() {
		return roaProviderId;
	}

	public void setRoaProviderId(String roaProviderId) {
		this.roaProviderId = roaProviderId;
	}

	public Integer getEndSessionReasonCode() {
		return endSessionReasonCode;
	}

	public void setEndSessionReasonCode(Integer endSessionReasonCode) {
		this.endSessionReasonCode = endSessionReasonCode;
	}

	public int getConnector() {
		return connector;
	}

	public void setConnector(int connector) {
		this.connector = connector;
	}

	@Override
	public String toString() {
		return "RechargeDetailsData [\n sessionId=" + sessionId + ", \n partnerSessionId=" + partnerSessionId + ", \n evseId="
				+ evseId + ",\n idTag=" + idTag + ",\n chargingStart=" + chargingStart + ",\n chargingEnd=" + chargingEnd
				+ ",\n sessionStart=" + sessionStart + ",\n sessionEnd=" + sessionEnd + ",\n meterValueStart="
				+ meterValueStart + ",\n meterValueEnd=" + meterValueEnd + ",\n consumedEnergy=" + consumedEnergy
				+ ",\n meteringSignature=" + meteringSignature + ",\n roaOperatorId=" + roaOperatorId + ",\n roaProviderId="
				+ roaProviderId + ",\n endSessionReasonCode=" + endSessionReasonCode + "]";
	}

	public String getStoppingIdTag() {
		return stoppingIdTag;
	}

	public void setStoppingIdTag(String stoppingIdTag) {
		this.stoppingIdTag = stoppingIdTag;
	}

	public Long getEndSessionReasonFault() {
		return endSessionReasonFault;
	}

	public void setEndSessionReasonFault(Long endSessionReasonFault) {
		this.endSessionReasonFault = endSessionReasonFault;
	}

	public String getEndSessionReasonInfo() {
		return endSessionReasonInfo;
	}

	public void setEndSessionReasonInfo(String endSessionReasonInfo) {
		this.endSessionReasonInfo = endSessionReasonInfo;
	}
	  
}