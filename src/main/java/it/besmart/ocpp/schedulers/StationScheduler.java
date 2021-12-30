package it.besmart.ocpp.schedulers;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import it.besmart.ocpp.dtos.RecordConnectorStatusDTO;
import it.besmart.ocpp.dtos.RecordStationStatusDTO;
import it.besmart.ocpp.enums.CSLifeStatus;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.enums.ErrorMessageResponse;
import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.enums.ReserveStatus;
import it.besmart.ocpp.enums.StationStatusComplete;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.servers.ServerMap;
import it.besmart.ocpp.servers.ServerOC;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IReservationService;
import it.besmart.ocpp.services.interfaces.IStationService;
import it.besmart.ocpp.services.interfaces.IStatusService;
import it.besmart.ocpp.services.interfaces.ITxService;
import it.besmart.ocppLib.wrappers.Request.ResetRequest.ResetType;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Component
@DependsOn("serverMap")
public class StationScheduler {

	
	private final Logger logger = LoggerFactory.getLogger(StationScheduler.class);
	
	private final static int MAX_CONNECTION_TIMEOUT_SEC = 120;
	
	public final static int RESERVE_BEFORE_MIN = 60;
	
	private final static int DEAD_AFTER_MIN = 60;
	
	@Autowired
	private ServerMap serverMap;
	
	@Autowired
	private IReservationService reservationService;
	
	@Autowired
	private IStationService stationService;
	
	@Autowired
	private ICUService cuService;
	
	@Autowired
	private IConnectorService connService;
	
	@Autowired
	private IStatusService statusService;
	
	@Autowired
	private ITxService txService;
	
	@Autowired
	private IErrorService errorService;
	
	//not used
	@Async
	@Scheduled(initialDelay = MAX_CONNECTION_TIMEOUT_SEC*1000, fixedDelay = 10 * 60 * 1000)
	public void checkReservation() {
		
		//Waiting or accepted
		for (Reservation reservation : reservationService.findActive()) {
			
			if( reservation.getExpiryDate().isBefore(ZonedDateTime.now()) ) {
				
				logger.debug("Expired reservation " + reservation.getResID());
				
				reservationService.updateReservationStatus(reservation, ReserveStatus.EXPIRED);
				
				//Set connector available again
				Connector conn = connService.findByUnitAndRef(reservation.getUnit(), reservation.getConnectorRef());
				
				if(conn!=null && conn.getStatus().equals(ConnectorStatusComplete.RESERVED)) {
					RecordConnectorStatusDTO record = new RecordConnectorStatusDTO();
					record.setStatus(ConnectorStatusComplete.AVAILABLE);
					record.setSendTime(ZonedDateTime.now());
					
					statusService.updateConnectorStatus(record, conn); 
				}
			}
			else if( Duration.between( ZonedDateTime.now().toLocalDateTime() , reservation.getStartDate().toLocalDateTime() ).toMinutes() 
					< RESERVE_BEFORE_MIN ) {
				
				ChargingUnit cu = reservation.getUnit();
				
				//Should be already reserved for our flow -> commented
				
				if (! cuService.isReserved(cu) ) {
					
					//Quanto manca alla startDate 
					logger.debug( "La prenotazione " + reservation.getResID() + " inizia tra minuti: " +
							String.valueOf( Duration.between( ZonedDateTime.now().toLocalDateTime(), reservation.getStartDate().toLocalDateTime() ).toMinutes() ) );
					
					
//					ChargingStation cs = cu.getChargingStation();
//					boolean success = false;
//					if (cs.isConnected()) 
//						success = serverMap.getServer(cs.getProtocol()).requestReserveNow(reservation);
//					
//					if(success) {
//						reservationService.updateReservationStatus(reservation, ReserveStatus.ACCEPTED);
//					}
//					else {
//						//TODO: -> se startDate passata invio alert al provider?
//					}
				}
				
			}

		}
	}
	
	
	@Async
	@Scheduled(initialDelay = MAX_CONNECTION_TIMEOUT_SEC*1000, fixedDelay = 30*60*1000)
	public void checkStationConnection() {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		HashSet<CSLifeStatus> states = new HashSet<>();
		Collections.addAll(states, CSLifeStatus.values());
		states.remove(CSLifeStatus.DISMISSED);
		states.remove(CSLifeStatus.INSTALLED);
		builder.with("lifeStatus", Operations.IN_SET, states);
		builder.with("actualSession", Operations.NOT_NULL, null);
		
		for(ChargingStation cs : stationService.findBySpecification(builder.build())) {
			
			ServerOC server = serverMap.getServer(cs.getProtocol());
			ZonedDateTime lastUpdate = statusService.findLastStationUpdate(cs);
					
			if ( lastUpdate!=null && Duration.between( lastUpdate, ZonedDateTime.now() ).toMinutes() > DEAD_AFTER_MIN ) {
				
				if(cs.isConnected()) {
					if(server.requestTrigger(cs, TriggerMessageRequestType.Heartbeat, 0)) {
						server.requestTrigger(cs, TriggerMessageRequestType.StatusNotification, 0);
					}
					else
						lostConnection(cs); //No signals in past hour
				}
				else if ( !cs.getStatus().equals(StationStatusComplete.UNAVAILABLE) ) {
					lostConnection(cs);
				}
			}
			
			if(cs.getLifeStatus().isInConfiguration() && txService.findOngoingTransactions(cs).isEmpty()) {
				server.requestResetMsg(cs, ResetType.Soft);
			}
			
		}
	}
	
	private void lostConnection(ChargingStation cs) {
		stationService.updateStatus(cs, new RecordStationStatusDTO(StationStatusComplete.UNAVAILABLE, cs.getLastUpdate()));
		
		errorService.saveError(ErrorMessageResponse.LOST_NETWORK.getValue(), ErrorType.NETWORK_ERROR, cs, cs.getLastUpdate(), null);
	
	}
	
	
	@Async
	@Scheduled(cron="0 0 0 * * *") //(initialDelay = MAX_CONNECTION_TIMEOUT_SEC*1000, fixedDelay = 24* 60 * 60 * 1000) //
	public void getDiagnostics() {
		
		for(ChargingStation cs : stationService.findConnectedStations()) {
			
			//serverMap.getServer(cs.getProtocol()).requestDiagnosticsMsg(cs);
			
		}
	}

}
