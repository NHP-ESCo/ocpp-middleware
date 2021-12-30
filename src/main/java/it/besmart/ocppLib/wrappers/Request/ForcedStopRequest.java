package it.besmart.ocppLib.wrappers.Request;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotEmpty;
import it.besmart.ocppLib.enumeration.EndTxReason;

public class ForcedStopRequest {

	@NotEmpty
	private String sessionId;
	
	private String roamingUserCode;
	
    private Double consumedEnergy;
    private ZonedDateTime timestamp;
    private EndTxReason reason;
    
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getRoamingUserCode() {
		return roamingUserCode;
	}
	public void setRoamingUserCode(String roamingUserCode) {
		this.roamingUserCode = roamingUserCode;
	}
	public ZonedDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public EndTxReason getReason() {
		return reason;
	}
	public void setReason(EndTxReason reason) {
		this.reason = reason;
	}
	public Double getConsumedEnergy() {
		return consumedEnergy;
	}
	public void setConsumedEnergy(Double consumedEnergy) {
		this.consumedEnergy = consumedEnergy;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ForcedStopRequest [sessionId=").append(sessionId).append(", roamingUserCode=")
				.append(roamingUserCode).append(", consumedEnergy=").append(consumedEnergy).append(", timestamp=")
				.append(timestamp).append(", reason=").append(reason).append("]");
		return builder.toString();
	}

	
}
