package it.besmart.ocppLib.wrappers.Request;

import java.util.Arrays;

import javax.validation.constraints.NotNull;

public class WhitelistRequest {

	@NotNull
	private String evseID;
	
	private String[] cardIDs;
	
	public WhitelistRequest() {
		super();
	}

	public String getEvseID() {
		return evseID;
	}

	public void setEvseID(String evseID) {
		this.evseID = evseID;
	}

	public String[] getCardIDs() {
		return cardIDs;
	}

	public void setCardIDs(String[] cardIDs) {
		this.cardIDs = cardIDs;
	}

	@Override
	public String toString() {
		return "WhitelistRequest [evseID=" + evseID + ", cardIDs=" + Arrays.toString(cardIDs)
				+ "]";
	}
	
	
}
