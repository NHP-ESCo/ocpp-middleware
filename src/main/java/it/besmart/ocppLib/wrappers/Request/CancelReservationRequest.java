package it.besmart.ocppLib.wrappers.Request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CancelReservationRequest {

	@NotNull
	private String reservationId;  	//OCPP id
	
	@NotNull
	@NotEmpty
	private String providerCode;
	
	public CancelReservationRequest() {
		super();
	}

	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	@Override
	public String toString() {
		return "CancelReservationRequest [reservationId=" + reservationId + ", providerCode=" + providerCode + "]";
	}
}
