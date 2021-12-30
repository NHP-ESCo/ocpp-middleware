package it.besmart.ocpp.client.external;

import org.springframework.scheduling.annotation.Async;

import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.DiagnosticsRecordStatus;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocppLib.enumeration.EndTxReason;
import it.besmart.ocppLib.wrappers.Response.AuthorizationStartResponse;
import it.besmart.ocppLib.wrappers.Response.AuthorizationStopResponse;


public interface IClientService {

	AuthorizationStartResponse authorizeStart(ChargingStation cs, String idTag);
	
	AuthorizationStopResponse authorizeStop(Transaction tx, String idTag);
	
	@Async
	void sendRechargeStart(Transaction tx);
	
	@Async
	void sendRechargeDetails(Transaction tx);

	@Async
	void sendRechargeDetails(Authorization auth, EndTxReason reason);

	@Async
	void sendDiagnosticsResult(DiagnosticsRecordStatus record);

	
}
