package it.besmart.ocpp.services.interfaces;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.besmart.ocpp.dtos.AuthorizationDTO;
import it.besmart.ocpp.dtos.ReservationDTO;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.model.StoredLog;
import it.besmart.ocppLib.enumeration.EndTxReason;
 
public interface IAuthorizationService {

	Authorization addAuthorization(AuthorizationDTO auth, ChargingUnit cu);
	
	Authorization updateResponse(Authorization auth, AuthorizationResponse response, StoredLog fault);
	
	Authorization askLocalAuthorization(ChargingStation station, ChargingUnit cu, String idTag, String csSession, ZonedDateTime timestamp);
	
	Authorization saveFreeModeAuthorization(AuthorizationDTO auth, ChargingUnit unit);

	List<Authorization> findAll(Specification<Authorization> build);

	Authorization findByDTO(AuthorizationDTO auth);
	
	Authorization findByExternalSession(String string);
	
	Authorization findAuthorization(ChargingStation cs, String txId, String idTag);
	
	Authorization findPendingAuth(ChargingUnit unit, String txId, String idTag);

	
	void delete(Authorization auth);
	
	/** Abort authorization on specific unit in case of: status change 
	or new validated authorization on specific unit **/
	boolean abortPendingAuthorization(ChargingUnit cu, EndTxReason reason, StoredLog fault);
	
	 /** Abort specific station authorization: if local there are authorizations on every units **/
	boolean abortPendingAuthorization(String csSessionId, ChargingStation cs, EndTxReason reason);	
	
	/** Abort authorization (on all the units) and send CDR **/
	void abortAuthorization(Authorization oldAuth, EndTxReason reason, StoredLog fault);
	
	boolean isPreAuthorized(ChargingStation cs, String idTag);

	Authorization startTx(Authorization auth);

	Authorization updateAuthorization(Authorization auth);

	Authorization findActiveReservation(ChargingUnit cu);
	
	Authorization findByReservationId(long resId);

	Authorization addReservation(ReservationDTO reservation, ChargingUnit cu);

	void abortReservationAuthorization(Reservation reservation);

}
