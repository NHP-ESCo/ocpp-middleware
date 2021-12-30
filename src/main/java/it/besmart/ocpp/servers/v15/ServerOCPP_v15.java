package it.besmart.ocpp.servers.v15;

import java.time.ZonedDateTime;

import eu.chargetime.ocpp.model.core.ChargingRateUnitType;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.servers.ServerOC;
import it.besmart.ocpp.wrappers.AuthWrapper;
import it.besmart.ocpp.wrappers.DiagnosticsWrapper;
import it.besmart.ocppLib.wrappers.Request.ResetRequest.ResetType;
import it.besmart.ocppLib.wrappers.Response.AckResponse;

public class ServerOCPP_v15 extends ServerOC {

	@Override
	public boolean requestResetMsg(ChargingStation cs, ResetType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AuthWrapper requestRemoteStart(ChargingStation cs, int connector, String idTag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthorizationResponse requestRemoteStop(ChargingStation cs, long txID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requestReserveNow(Reservation res) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requestCancelReservation(Reservation res) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean requestChangeAvailabilityMsg(ChargingStation cs, boolean active, int connector) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requestUnlockConnectorMsg(ChargingStation cs, int connector) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requestClearAuthorizationCacheMsg(ChargingStation cs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DiagnosticsWrapper requestDiagnosticsMsg(ChargingStation cs, ZonedDateTime startTime, int hours, String uri) {
		return new DiagnosticsWrapper();
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean requestUpdateFirmwareMsg(ChargingStation cs, String externalUri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requestTrigger(ChargingStation cs, TriggerMessageRequestType type, int connector) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setTxPower(Transaction tx, ChargingRateUnitType unit, double max) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setUnitSCPower(ChargingUnit cu, ChargingRateUnitType unit, double max, double min) {
		return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean sendLocalList(ChargingStation cs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int requestLocalListVersion(ChargingStation cs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ChargingStation updateStationConfiguration(ChargingStation cs) {
		return cs;
		
	}

}
