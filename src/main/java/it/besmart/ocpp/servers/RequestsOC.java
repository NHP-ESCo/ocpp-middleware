package it.besmart.ocpp.servers;

import java.time.ZonedDateTime;

import eu.chargetime.ocpp.model.core.ChargingRateUnitType;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.exceptions.ConfigurationException;
import it.besmart.ocpp.exceptions.UnsupportedRequestException;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.wrappers.AuthWrapper;
import it.besmart.ocpp.wrappers.DiagnosticsWrapper;
import it.besmart.ocppLib.wrappers.Request.ResetRequest.ResetType;

public interface RequestsOC {

	/** CONFIGURATION 
	 * @return TODO**/
	public ChargingStation updateStationConfiguration(ChargingStation cs) throws ConfigurationException;
	
	
	/** EMP FLOW **/
	
	public AuthWrapper requestRemoteStart(ChargingStation cs, int connector, String idTag);
	
	public AuthorizationResponse requestRemoteStop(ChargingStation cs, long txID);
	
	public boolean requestReserveNow(Reservation res);

	public boolean requestCancelReservation(Reservation res);
	
	
	/** CPO FLOW **/
	
	public boolean requestResetMsg(ChargingStation cs, ResetType type);
	
	public boolean requestChangeAvailabilityMsg(ChargingStation cs, boolean active, int connector);

	public boolean requestUnlockConnectorMsg(ChargingStation cs, int connector);

	
	/** EXTRA SETTINGS **/ 
	
	public boolean requestClearAuthorizationCacheMsg(ChargingStation cs);  //NEVED USED
	
	public DiagnosticsWrapper requestDiagnosticsMsg(ChargingStation cs, ZonedDateTime startTime, int hours, String uri);

	public boolean requestUpdateFirmwareMsg(ChargingStation cs, String externalUri) throws UnsupportedRequestException;

	public boolean requestTrigger(ChargingStation cs, TriggerMessageRequestType type, int connector);
	
	
	/** LOCAL LIST **/
	
	public boolean sendLocalList(ChargingStation cs);
	
	public int requestLocalListVersion(ChargingStation cs);
	
	/** SMART CHARGING  **/
	
	public boolean setTxPower(Transaction tx, ChargingRateUnitType unit, double max);
	
	public boolean setUnitSCPower(ChargingUnit cu, ChargingRateUnitType unit, double max, double min);

	
	
}
