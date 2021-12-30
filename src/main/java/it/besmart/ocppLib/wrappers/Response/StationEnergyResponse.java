package it.besmart.ocppLib.wrappers.Response;

public class StationEnergyResponse extends AckResponse {

	Double totalEnergy;
	
	Double rechargeEnergy;
	
	
	public StationEnergyResponse() {
		super();
	}


	public Double getTotalEnergy() {
		return totalEnergy;
	}


	public void setTotalEnergy(Double totalEnergy) {
		this.totalEnergy = totalEnergy;
	}


	public Double getRechargeEnergy() {
		return rechargeEnergy;
	}


	public void setRechargeEnergy(Double rechargeEnergy) {
		this.rechargeEnergy = rechargeEnergy;
	}
	
	
	
}
