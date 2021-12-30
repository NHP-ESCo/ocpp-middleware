package it.besmart.ocpp.services;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.client.external.IClientService;
import it.besmart.ocpp.dtos.AuthorizationDTO;
import it.besmart.ocpp.dtos.ReservationDTO;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.enums.AuthorizationType;
import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.enums.ReserveStatus;
import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.model.StoredLog;
import it.besmart.ocpp.repositories.AuthorizationRepository;
import it.besmart.ocpp.services.interfaces.IAuthorizationService;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IReservationService;
import it.besmart.ocppLib.enumeration.EndTxReason;
import it.besmart.ocppLib.wrappers.Response.AuthorizationStartResponse;
import it.besmart.ocppLib.wrappers.Response.AuthorizationStartResponse.AuthorizationStatusEnum;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Service
@Transactional
public class AuthorizationService implements IAuthorizationService {

	private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
	
	@Autowired
	private AuthorizationRepository repo;
	
	@Autowired
	private IClientService clientService;
	
	@Autowired
	private ICUService cuService;
	
	
	@Autowired
	private IReservationService reservationService;
	
	@Autowired
	private IErrorService errorService;
	
	@Override
	public Authorization addAuthorization(AuthorizationDTO auth, ChargingUnit unit) {
		
		abortPendingAuthorization(unit, EndTxReason.EXPIRED, null); // in case messages were missed
		
		Authorization entity = new Authorization(auth);
		
		entity.setCu(unit);
		
		logger.debug(entity.toString());
		
		return repo.save(entity);
	}
	

	@Override
	public Authorization addReservation(ReservationDTO res, ChargingUnit cu) {
		Reservation reserve = reservationService.addReservation(res, cu);
		
		AuthorizationDTO authDTO = new AuthorizationDTO(res);
		
		Authorization auth = addAuthorization(authDTO, cu);
		auth.setReservation(reserve);
		
		return repo.save(auth);
	}

	
	@Override
	public Authorization saveFreeModeAuthorization(AuthorizationDTO auth, ChargingUnit unit) {
		Authorization entity = new Authorization(auth);
		
		entity.setFreeMode(true);
		entity.setCu(unit);
		
		return repo.save(entity);
	}


	
	@Override
	public Authorization updateResponse(Authorization auth, AuthorizationResponse response, StoredLog fault) {
		
		auth.setResponse(response);
		auth.setEndFault(fault);
		
		logger.debug(auth.toString());
		
		return repo.saveAndFlush(auth);
	}


	private Authorization findById(long id) {
		Optional<Authorization> o = repo.findById(id);
        if(o.isPresent())
            return o.get();
        else
        	return null;
	}


	@Override
	public Authorization findByDTO(AuthorizationDTO auth) {
		
		return findById(auth.getAuthorizationID());
	}

	@Override
	public void delete(Authorization auth) {
		repo.delete(auth);
		
	}


	@Override
	public Authorization findByExternalSession(String sessionID) {
		
		List<Authorization> list = repo.findByExternalSession(sessionID);
		
		if (list.size()==0)
			return null;
		
		for(Authorization auth : list)
			if(auth.getResponse().equals(AuthorizationResponse.STARTED))
				return auth;
		
		return list.get(0);
	}


	@Override
	public Authorization askLocalAuthorization(ChargingStation station, ChargingUnit cu, String idTag, String csSession, ZonedDateTime timestamp) {
			
		AuthorizationDTO authorization = new AuthorizationDTO();
		authorization.setType(AuthorizationType.LOCAL);
		authorization.setCsSession(csSession);
		authorization.setIdTag(idTag);
		authorization.setTimestamp(timestamp);
		
		AuthorizationStartResponse result = clientService.authorizeStart(station, idTag);
		
		if (result!=null && result.getStatus().equals(AuthorizationStatusEnum.AUTHORIZED) ) {
		
			if(result.getReservationId()!=null) {
				Authorization auth = findByReservationId(result.getReservationId());
				if(auth!=null) {
					//Already present authorization related to reservation 
					logger.debug(String.format("Authorization related to reservation %d", result.getReservationId()));
					;
					return auth;
				}
				
			}
			//Authorized
//			logger.debug(result.toString());	
			
			authorization.setResponse(AuthorizationResponse.ACCEPTED);
			authorization.setExternalSession(result.getSessionId());
			authorization.setProviderId(result.getProviderId());
			authorization.setEvcoId(result.getEvcoId());
			
			
			if(cu==null) { 
				Authorization auth = null;
				for(ChargingUnit unit : cuService.findByStation(station)) {
					auth = addAuthorization(authorization, unit);
				}
				return auth;
			}
			else {
				return addAuthorization(authorization, cu);
			}
		}	

		//Not validated
		
		authorization.setResponse(AuthorizationResponse.INVALID);
		logger.debug("Invalid idTag");
		if(cu==null) { 
			Authorization auth = null;
			for(ChargingUnit unit : cuService.findByStation(station)) {
				auth = addAuthorization(authorization, unit);
				logger.debug(auth.toString());
			}
			return auth;
		}
		else {
			return addAuthorization(authorization, cu);
		}
		
	}

	@Override
	public Authorization findAuthorization(ChargingStation cs, String txId, String idTag) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("cu", Operations.IN, cuService.findByStation(cs));
		if(txId!=null)
			builder.with("csSession", Operations.EQUAL, txId);
		if(idTag!=null)
			builder.with("idTag", Operations.EQUAL, idTag);
		
		Specification<Authorization> spec = builder.build();
		
		List<Authorization> list = repo.findAll(spec);
		
		if (list.size()>0)
			return list.get(0);
		else
			return null;
	}



	
	@Override
	public Authorization findPendingAuth(ChargingUnit unit, String txId, String idTag) {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("cu", Operations.EQUAL, unit);
		builder.with("response", Operations.EQUAL, AuthorizationResponse.ACCEPTED);
		if(txId!=null)
			builder.with("csSession", Operations.EQUAL, txId);
		if(idTag!=null)
			builder.with("idTag", Operations.EQUAL, idTag);
		
		Specification<Authorization> spec = builder.build();
		
		List<Authorization> list = repo.findAll(spec);
		
		if (list.size()>0)
			return list.get(0);
		else
			return null;
	}


	@Override 
	public boolean abortPendingAuthorization(ChargingUnit cu, EndTxReason reason, StoredLog fault) {
		
		Authorization oldAuth = findPendingAuth(cu, null, null);
		
		if (oldAuth!=null) {
			abortAuthorization(oldAuth, reason, fault);
			
			return true;
		}
		else
			return false;
	}
	
	@Override
	public boolean abortPendingAuthorization(String csSessionId, ChargingStation cs, EndTxReason reason) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("response", Operations.EQUAL, AuthorizationResponse.ACCEPTED);
		builder.with("csSession", Operations.EQUAL, csSessionId);
		builder.with("cu", Operations.IN, cuService.findByStation(cs));
		
		Specification<Authorization> spec = builder.build();
		
		List<Authorization> list = repo.findAll(spec);
		
		if(list.size()>0) {
			abortAuthorization(list.get(0), reason, null);
			return true;
		}
		return false;
	}
	
	/** Abort authorization (on all the units) and send CDR **/
	@Override
	public void abortAuthorization(Authorization oldAuth, EndTxReason reas, StoredLog fault) {
		if(oldAuth.getResponse().equals(AuthorizationResponse.ABORTED))
			return;
		
		oldAuth = updateResponse(oldAuth, AuthorizationResponse.ABORTED, fault);
		
		if(oldAuth.getReservation()!=null) {
			reservationService.updateReservationStatus(oldAuth.getReservation(), ReserveStatus.EXPIRED);
			//TODO different CDR ?
		}
		
		//Abort any other possible authorization active on other cus of the same station
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("externalSession", Operations.EQUAL, oldAuth.getExternalSession());
		builder.with("response", Operations.EQUAL, AuthorizationResponse.ACCEPTED);
		for(Authorization auth : repo.findAll(builder.build())) {
			if(!auth.equals(oldAuth))
				updateResponse(auth, AuthorizationResponse.ABORTED, fault);
		}
		
		if(fault!=null) {
			errorService.saveConnectorError("Error starting recharge: " 
					+ fault.getMessage(), 
					ErrorType.RECHARGE_ERROR, fault.getConnector(), 
					oldAuth.getExternalSession());
		}

		//only one CDR for session
		clientService.sendRechargeDetails(oldAuth, reas);
	}


	@Override
	public boolean isPreAuthorized(ChargingStation cs, String idTag) {
		for(ChargingUnit cu : cuService.findByStation(cs)) {
			Authorization auth = findPendingAuth(cu, null, idTag);
			if(auth!=null )
				return true;
		}
		return false;
	}


	@Override
	public Authorization startTx(Authorization auth) {
		String evcoId = auth.getEvcoId();
		ChargingUnit unit = auth.getCu();
		
		//remove other auth
		if(auth.getType().equals(AuthorizationType.LOCAL)) {
			CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
			builder.with("externalSession", Operations.EQUAL, auth.getExternalSession());
			builder.with("chargingStation", Operations.JOIN, unit.getChargingStation(), "cu");
			for(Authorization a : repo.findAll(builder.build())) {
				if(!auth.equals(a)) 
					updateResponse(a, AuthorizationResponse.ABORTED, null);
				else
					auth = updateResponse(auth, AuthorizationResponse.STARTED, null);
			}
		}
		else
			auth = updateResponse(auth, AuthorizationResponse.STARTED, null);
		
			
	
		//remove previous errors for this evco
		if(evcoId!=null) {
			int minInterval = 10;
			
			logger.debug(String.format("Solve all errors for sessions of evco %s on unit %s in last %d minutes", 
					evcoId, unit.getEvseCode(), minInterval));
			CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
			builder.with("evcoId", Operations.EQUAL, evcoId);
			builder.with("cu", Operations.EQUAL, unit);
			builder.with("timestamp", Operations.AFTER_ZONED, ZonedDateTime.now().minusMinutes(minInterval)); //last 10 minutes error
			
			for(Authorization a : repo.findAll(builder.build())) {
				
				errorService.solveSessionErrors(unit, a.getExternalSession());
			}
		}
		
		return auth;
	}

	@Override
	public Authorization findByReservationId(long resId) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("reservationId", Operations.JOIN, resId, "reservation");
		builder.with("response", Operations.EQUAL, AuthorizationResponse.ACCEPTED);
		
		Optional<Authorization> o = repo.findOne(builder.build());
		
		if(o.isPresent())
			return o.get();
		return null;
	}

	
	@Override
	public Authorization findActiveReservation(ChargingUnit cu) {
		Reservation res = reservationService.findActive(cu);
		
		if(res!=null) {
			CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
			builder.with("reservation", Operations.EQUAL, res);
			builder.with("response", Operations.EQUAL, AuthorizationResponse.ACCEPTED);
			
			Optional<Authorization> o = repo.findOne(builder.build());
			
			if(o.isPresent())
				return o.get();
		}
		
		
		return null;
	}
	
	@Override
	public void abortReservationAuthorization(Reservation reservation) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("reservation", Operations.EQUAL, reservation);
		
		Optional<Authorization> o = repo.findOne(builder.build());
		
		if(o.isPresent())
			updateResponse(o.get(), AuthorizationResponse.ABORTED, null);
	}

	@Override
	public Authorization updateAuthorization(Authorization auth) {
		
		return repo.save(auth);
	}


	@Override
	public List<Authorization> findAll(Specification<Authorization> spec) {
		
		return repo.findAll(spec);
	}


}
