package it.besmart.ocppLib.enumeration;

public enum TransactionStatus {

	AUTHORIZED("AUTHORIZED"),  //never started
	
	UNAUTHORIZED("UNAUTHORIZED"), //never authorized
	
	EXPIRED("EXPIRED"),		 //never started because authorization expired
	
    ACTIVE("ACTIVE"),  

    STAND_BY("STAND_BY"), //suspended by vehicle
    
    INACTIVE("INACTIVE"), //suspended by CU
    
    MANUAL_CHECKOUT("MANUAL CHECKOUT"),

    FINISHED("FINISHED");

	
    String rechargeStatus;

    private TransactionStatus(String rechargeStatus){
        this.rechargeStatus = rechargeStatus;
    }

    public String getRechargeStatus(){
        return rechargeStatus;
    }

    public void setRechargeStaus(String RechargeStaus) {
        this.rechargeStatus = RechargeStaus;
    }

	public boolean isStandby() {
		
		return this.equals(INACTIVE) || this.equals(STAND_BY);
	}

}
