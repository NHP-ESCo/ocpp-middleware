package it.besmart.ocppLib.wrappers.Response;


import it.besmart.ocppLib.dto.RechargeMeterData;
import it.besmart.ocppLib.enumeration.TransactionStatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


public class RechargeDetailsResponse extends AckResponse {

	private TransactionStatus status;
	
	private ZonedDateTime parkDate;
	
	private int connRef;
	
	private String providerCode;
	
	private String rechargeProfileCode;
	
	private List<RechargeMeterData> data = new ArrayList<RechargeMeterData>();

	
	public RechargeDetailsResponse() {
		super();
	}
	
	public RechargeDetailsResponse(boolean b, String message) {
		super(b, message);
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public String getRechargeProfileCode() {
		return rechargeProfileCode;
	}

	public void setRechargeProfileCode(String rechargeProfileCode) {
		this.rechargeProfileCode = rechargeProfileCode;
	}

	public List<RechargeMeterData> getData() {
		return data;
	}

	public void setData(List<RechargeMeterData> data) {
		this.data = data;
	}
	
	public void addData(RechargeMeterData data) {
		this.data.add(data);
	}

	public int getConnRef() {
		return connRef;
	}

	public void setConnRef(int connRef) {
		this.connRef = connRef;
	}

	@Override
	public String toString() {
		return "RechargeDetailsResponse [status=" + status + ", connRef=" + connRef + ", providerCode=" + providerCode
				+ ", rechargeProfileCode=" + rechargeProfileCode + ", data=" + data + "]";
	}

	public ZonedDateTime getParkDate() {
		return parkDate;
	}

	public void setParkDate(ZonedDateTime parkDate) {
		this.parkDate = parkDate;
	}
	
	
}
