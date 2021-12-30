package it.besmart.ocpp.services;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.chargetime.ocpp.model.core.ChargingRateUnitType;
import it.besmart.ocpp.client.external.IClientService;
import it.besmart.ocpp.dtos.TransactionDTO;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.enums.ReserveStatus;
import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.repositories.TransactionRepository;
import it.besmart.ocpp.servers.ServerMap;
import it.besmart.ocpp.services.interfaces.IAuthorizationService;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IReservationService;
import it.besmart.ocpp.services.interfaces.ITxService;
import it.besmart.ocppLib.enumeration.EndTxReason;
import it.besmart.ocppLib.enumeration.TransactionStatus;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;
import it.besmart.ocpp.services.interfaces.IMeterRecordService;

@Service
@Transactional
public class TxService implements ITxService {

	private final Logger logger = LoggerFactory.getLogger(TxService.class);
	
	@Autowired
	private TransactionRepository repo;
	
	@Autowired
	private ApplicationContext ctx;  //to avoid loop with smart charging service 
	
	@Autowired
	private IErrorService errorService;
	
	@Autowired
	private ICUService cuService;
	
	@Autowired
	private IConnectorService connService;
	
	@Autowired
	private IMeterRecordService meterService;
	
	@Autowired
	private IClientService clientService;
	
	@Autowired
	private IAuthorizationService authService;
	
	@Autowired
	private IReservationService reserveService;
	
	
	@Autowired
	private ServerMap serverMap;
	

	@Override
	public Transaction findById(long id) {
		Optional<Transaction> o = repo.findById(id);
        if(o.isPresent()){
            return o.get();
        }
        else{
        	return null;
        }
	}

	@Override
	public Transaction addTransaction(TransactionDTO tx, Authorization auth, Connector conn) {
		
		checkOldTx(conn.getUnit()); // in case messages were missed
		
		boolean authorized = false;
		
		Transaction entity = new Transaction(tx);
		
		entity.setConnector(conn);
		if(auth!=null && auth.getResponse().equals(AuthorizationResponse.ACCEPTED)) {
			if(conn.getStatus().equals(ConnectorStatusComplete.CHARGING))
				entity.setStatus(TransactionStatus.ACTIVE);
			else
				entity.setStatus(TransactionStatus.INACTIVE);
			
			authorized = true;
		}
		else
			entity.setStatus(TransactionStatus.UNAUTHORIZED);
		
		if(tx.getReservation()!=null) { //WITH RESERVATION
			
			//update reservation status
			
			Reservation res = reserveService
					.updateReservationStatus(reserveService.findByDTO(tx.getReservation()), ReserveStatus.STARTED);
			
			entity.setReservation(res);
		} 
		
		if(authorized) { 
				
			//update authorization with status started
			auth = authService.startTx(auth);
			
			
		}
		
		entity.setAuthorization(auth);
		entity = repo.save(entity);
		
		//Coherence of states
		connService.updateStatus(conn, ConnectorStatusComplete.OCCUPIED);
		
		setAuthorizedPower(entity);
		
		clientService.sendRechargeStart(entity);
		
		
		return entity;
	}

	
	private void checkOldTx(ChargingUnit unit) {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("endReason", Operations.IS_NULL, null);
		builder.with("connector", Operations.IN, connService.findByUnit(unit));
		
		Specification<Transaction> spec = builder.build();
		
		for(Transaction tx : repo.findAll(spec)) {
						
			String msg = String.format("Recharge session stopped without CDR. Manual Checkout necessary");
			errorService.saveConnectorError(msg, ErrorType.RECHARGE_ERROR, tx.getConnector(), tx.getExternalSession());
			
			tx.setEndReason(EndTxReason.Unknown);
			tx.setStatus(TransactionStatus.MANUAL_CHECKOUT);
			repo.save(tx);
			
		}
		
	}

	
	@Override //TODO better define what was updated, do not edit all fields
	public Transaction updateTransaction(Transaction tx) {
		
		return repo.save(tx);
	}
	
	@Override
	public Transaction findByExternalSession(String externalSession, ChargingStation cs) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("connector", Operations.IN, connService.findByStation(cs));
		builder.with("externalSession", Operations.JOIN, externalSession, "authorization");
		
		List<Transaction> list = repo.findAll(builder.build());
		
		return list.isEmpty()? null : list.get(0);
	}

	@Override
	public Transaction findOngoingTransaction(Connector conn) {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("connector", Operations.EQUAL, conn);
		builder.with("endReason", Operations.IS_NULL, null);
		//builder.with("meterStart", Operations.DESC, null);
		
		Specification<Transaction> spec = builder.build();
		
		List<Transaction> list = repo.findAll(spec);
		
		if (list.size()>0)
			return list.get(0);
		else
			return null;
	}

	@Override
	public List<Transaction> findOngoingTransactions(ChargingStation cs) {

		List<Transaction> list = new ArrayList<Transaction>();
		
		for(ChargingUnit cu : cuService.findByStation(cs)) {
			Transaction tx = findOngoingTransaction(cu);
			if(tx!=null)
				list.add(tx);
		}
		return list;
	}

	@Override
	public List<Transaction> findPastTransactions(Connector conn) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("connector", Operations.EQUAL, conn);
		builder.with("endReason", Operations.NOT_NULL, null);
		//builder.with("meterStart", Operations.DESC, null);
		
		Specification<Transaction> spec = builder.build();
		 
		
		return repo.findAll(spec);
	}

	@Override
	public Transaction findOngoingTransaction(ChargingUnit cu) {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("unit", Operations.JOIN, cu, "connector");
		builder.with("endReason", Operations.IS_NULL, null);
		//builder.with("meterStart", Operations.DESC, null);
		
		List<Transaction> list = repo.findAll( builder.build() ) ;
		
		if(list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	
	@Override
	public void updateTransactionStatus(Transaction tx, ZonedDateTime timestamp) {
		ConnectorStatusComplete status = tx.getConnector().getStatus();
		
		if (tx.getStatus().isStandby()!=status.isStandby()) { //change of standby status
//			recomputeOptimalPower(tx, OptimizationTrigger.STATUS_CHANGE);
		}
		
		switch(status) {
		case AVAILABLE:
			//checkOldTx(tx.getConnector().getUnit());
			return;
		case OCCUPIED:
		case CHARGING:
			tx.setStatus(TransactionStatus.ACTIVE);
			tx.setStopChargingDate(null);
			break;
		case SUSPENDEDEV:
			tx.setStatus(TransactionStatus.STAND_BY);
			tx.setStopChargingDate(timestamp);
			
			meterService.addStandbyTxRecord(tx);
			
			break;
		case PREPARING:
		case SUSPENDEDEVSE:
		case FAULTED:
		case UNAVAILABLE:
		case FINISHING:
			tx.setStatus(TransactionStatus.INACTIVE);
			
			break;
		case RESERVED:
		default:
			logger.warn(String.format("Transaction %d: Connector %s", tx.getTxID(), status));
			return;
		}
	
		
		tx = repo.saveAndFlush(tx);
		
		logger.debug(String.format("Transaction %d: %s", tx.getTxID(), tx.getStatus()));
		
	}

	@Override
	public Transaction findByCsSession(ChargingStation cs, String txId) {
		//cs session could be not unique, depending on cs
		List<Transaction> list = repo.findByAuthorization_CsSession(txId);
		if(list.size()>1)
			logger.warn(String.format("Found more txs %s : %d ", txId, list.size()));
		
		for(Transaction tx : list) {
			if(tx.getConnector().getUnit().getChargingStation().equals(cs))
				return tx;
		}
		
		return null;
	}

	@Override
	public Transaction stopTransaction(Transaction tx, ZonedDateTime stopDate, EndTxReason reason, boolean saveMeter, boolean backoffice) {
		logger.debug("Stop tx " + tx.getTxID());
		logger.debug("End reason: " + reason);
		
		TransactionStatus status = tx.getStatus();
		boolean stopped = status.equals(TransactionStatus.FINISHED) || status.equals(TransactionStatus.EXPIRED); //already finished
		
		if(tx.getStopChargingDate()==null) 
			tx.setStopChargingDate(stopDate);
		else {
			if(stopDate.isBefore(tx.getStopChargingDate()))
				tx.setStopChargingDate(stopDate); //TODO protocol error, check stopdate
		}
		tx.setStopDate( stopDate );
		tx.setStatus(TransactionStatus.FINISHED);
		tx.setEndReason(reason);
		
		/** Check meter records **/
		MeterRecord lastRec = meterService.findLastEnergyMeter(tx, false);
		Double lastMeter = null;
		
		if(lastRec!=null) { 
			lastMeter = lastRec.getValue();
		}
		else {
			logger.warn(String.format("No meter records found for tx %s", tx.getExternalSession()));
			if(tx.getMeterStart()==null)
				tx.setMeterStart(0.0);
			lastMeter = tx.getMeterStart();
		}
		
		
		if(tx.getMeterStop()==null) {
			
			logger.debug("Save last meter: " + lastMeter);
			tx.setMeterStop(lastMeter);
		}
		else if(tx.getMeterStop() < lastMeter - 0.1) { // || Math.abs(lastMeter - tx.getMeterStop())>10) { //TODO manage
			
			String msg = String.format("Error on meter stop. Last record %.3f, tx stop %.3f", lastMeter, tx.getMeterStop());
			errorService.saveConnectorError(msg, ErrorType.RECHARGE_ERROR, tx.getConnector(), tx.getExternalSession());
			
			tx.setMeterStop(lastMeter);
			
		}
		
		/** Check meter reading coherence **/
		if(!backoffice || !tx.getStatus().equals(TransactionStatus.MANUAL_CHECKOUT) ) { 
			//Not check if forced by backoffice during manual checkout
			double consumedEnergy = (tx.getMeterStop() - tx.getMeterStart());
			long time = 1 + Duration.between(tx.getStartDate(), tx.getStopChargingDate()).toMinutes() ;
			double averagePower = consumedEnergy / ( (double) time /60);
			
			logger.debug(String.format("Consumed Energy: %.2f kWh", consumedEnergy));
			
			if(averagePower > 1.1*tx.getConnector().getActualMaxPower()) {
				String msg = String.format("Recharge consumed too much energy [%.2f kWh in %d minutes] . Check it", consumedEnergy, time);
				errorService.saveConnectorError(msg, ErrorType.RECHARGE_ERROR, tx.getConnector(), tx.getExternalSession());
				
				//do not finish tx before checking
				stopped = true;
				tx.setStatus(TransactionStatus.MANUAL_CHECKOUT);
			}
			
			if(consumedEnergy < 0) {
				String msg = String.format("Recharge consumed negative energy . Check it");
				errorService.saveConnectorError(msg, ErrorType.RECHARGE_ERROR, tx.getConnector(), tx.getExternalSession());
				
				//do not finish tx before checking
				stopped = true;
				tx.setStatus(TransactionStatus.MANUAL_CHECKOUT);
			}
			else if(consumedEnergy==0 && Duration.between(tx.getStartDate(), tx.getStopDate()).toMinutes()>3) {
				errorService.saveConnectorError("Zero energy during recharge", ErrorType.RECHARGE_ERROR, tx.getConnector(), tx.getExternalSession());
				
			}
			
			/** Check End reason **/
			
			if(tx.getEndReason().isFault()) {
				errorService.saveConnectorError("Stopped recharge for reason " + tx.getEndReason(), 
						ErrorType.RECHARGE_ERROR, tx.getConnector(), tx.getExternalSession());
			}
		}
		
		
		tx = repo.save(tx);
		
		if(saveMeter)
			meterService.addStopTxRecord(tx);

		/**CLOSE SESSION OPERATIONS**/
		
		
		if(!stopped) {
			
			//Send data to provider. 
			clientService.sendRechargeDetails(tx);
		
			//Check configuration
			reconfigureStation(tx.getConnector().getUnit().getChargingStation());
		
		}
		
		return tx;
	}	
	
	
	
	private void reconfigureStation(ChargingStation cs) {
		
		if(cs.isConfigureAtStopTx() && findOngoingTransactions(cs).isEmpty()) {
			//async to first stop tx
			
			new Thread(() -> {
				
				Map<String, ServerMap> implementations = ctx.getBeansOfType(ServerMap.class);
				ServerMap map = implementations.get("serverMap");
				map.getServer(cs.getProtocol()).configureStation(cs, false, false, true);
				
			}).start();
			
		}
	}
	
	
	private void setAuthorizedPower(Transaction tx) {
		Connector conn = tx.getConnector();
		ChargingStation cs = conn.getUnit().getChargingStation();
		if(cs.isScEnabled()) {
			double maxCurrent = conn.computeMaxCurrent();
			serverMap.getServer(cs.getProtocol()).setTxPower(tx, ChargingRateUnitType.A, maxCurrent);
		}
	}

	@Override
	public Transaction findByAuthorization(Authorization auth) {
		
		return repo.findByAuthorization(auth);
	}

	@Override
	public List<Transaction> findOngoingTransactions() {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();

		builder.with("endReason", Operations.IS_NULL, null);
		
		Specification<Transaction> spec = builder.build();
		
		return repo.findAll(spec);
	}

	@Override
	public String findOngoingSession(Connector conn) {
		String session = null;
		Authorization auth = authService.findPendingAuth(conn.getUnit(), null, null);
		if(auth!=null) {
			session = auth.getExternalSession();
		}
		else {
			Transaction tx = findOngoingTransaction(conn);
			
			if(tx!=null)
				session = tx.getExternalSession();
		}
		
		return session;
	}

		
	
}
