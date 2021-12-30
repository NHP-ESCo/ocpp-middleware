package it.besmart.ocppLib.wrappers.Response;

public class ReserveNowResponse extends AckResponse{
	
	private String reservationId;

	public ReserveNowResponse(boolean b, String message) {
		super(b, message);
	}
	
	public ReserveNowResponse() {
		super();
	}

	public String getReservationId() {
		return reservationId;
	}

	public void setReservationId(String reservationId) {
		this.reservationId = reservationId;
	}

}
