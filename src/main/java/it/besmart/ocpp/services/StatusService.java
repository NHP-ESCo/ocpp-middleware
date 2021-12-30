package it.besmart.ocpp.services;

import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.dtos.RecordConnectorStatusDTO;
import it.besmart.ocpp.dtos.RecordStationStatusDTO;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.enums.StationStatusComplete;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.model.RecordConnectorStatus;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.repositories.ConnectorStatusRepository;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocpp.services.interfaces.IMeterRecordService;
import it.besmart.ocpp.services.interfaces.IStationService;
import it.besmart.ocpp.services.interfaces.IStatusService;
import it.besmart.ocpp.services.interfaces.ITxService;

@Service
@Transactional
public class StatusService implements IStatusService {

	private final Logger logger = LoggerFactory.getLogger(StatusService.class);
	
	private static final int LAG_SECONDS = 1;
	
	@Autowired
	private ConnectorStatusRepository connRepo;
	
	@Autowired
	private IStationService stationService;
	
	@Autowired
	private ICUService cuService;
	
	@Autowired
	private IConnectorService connService;
	 
	@Autowired
	private ITxService txService;
	
	@Autowired
	private IMeterRecordService meterService;
	
	
	@Override
	public void updateCSStatus(RecordStationStatusDTO newStatus, ChargingStation station) {
		
		// Update status of Station (if new)
		ZonedDateTime lastUpdate = station.getLastUpdate();
		
		if (lastUpdate == null || lastUpdate.isBefore(newStatus.getSendTime()) ) {
			
			stationService.updateStatus(station, newStatus); 
		}
				
	}
	
	
	@Override
	public void changeConnectorStatus(Connector conn, ConnectorStatusComplete status, ZonedDateTime sendTime) {
		
		RecordConnectorStatusDTO record = new RecordConnectorStatusDTO();
		record.setStatus(status);
		record.setSendTime(sendTime);
		
		updateConnectorStatus(record, conn); 
		
	}
	
	
	@Override
	public void updateConnectorStatus(RecordConnectorStatusDTO newStatus, Connector connEntity) {
		
		// Update status of Unit (if new)
		RecordConnectorStatus lastStatus = findLastConnnectorStatus(connEntity);
		
		if (lastStatus == null || lastStatus.getSendTime().isBefore(newStatus.getSendTime().plusSeconds(LAG_SECONDS))) {
			if (lastStatus!=null ) {
				if ( !lastStatus.getStatus().equals(newStatus.getStatus()) || !lastStatus.getStatus().equals(ConnectorStatusComplete.AVAILABLE)) 
					logger.debug(connEntity.getUnit().getEvseCode() + " connector #" + connEntity.getRef() + ": " + lastStatus.getStatus() + " -> " + newStatus.getStatus() );
			}
			else
				logger.debug(connEntity.getUnit().getEvseCode() + " connector #" + connEntity.getRef() + ": " + newStatus.getStatus() );
			
			if(connEntity.getStatus()!=newStatus.getStatus()) {
				//Actual change of status
				
				connService.updateStatus(connEntity, newStatus.getStatus()); 
				
				Transaction tx = txService.findOngoingTransaction(connEntity);
				if (tx!=null)
					txService.updateTransactionStatus( tx, newStatus.getSendTime() ) ;
			}
			
			
			
		}
		else {
			logger.warn("Past status change. No update");
		}
				
		// Create new record in CUstatus table
		RecordConnectorStatus entity = new RecordConnectorStatus(newStatus);
		
		if (newStatus.getSendTime().isAfter(ZonedDateTime.now().plusSeconds(LAG_SECONDS))) {
			logger.warn("State update in the future: " + newStatus.getSendTime() );
			entity.setSendTime( ZonedDateTime.now() );
		}
		entity.setConnector(connEntity);
		entity = connRepo.save(entity);
		
		// Update Station status if it was unavailable/faulted
		ChargingStation cs = connEntity.getUnit().getChargingStation();
		if(newStatus.getStatus()!=ConnectorStatusComplete.UNAVAILABLE 
				&& cs.getStatus()!=StationStatusComplete.AVAILABLE) {
			
			RecordStationStatusDTO csRecord = new RecordStationStatusDTO(StationStatusComplete.AVAILABLE, newStatus.getSendTime());
			
			updateCSStatus(csRecord, connEntity.getUnit().getChargingStation());	
		}
		
				
	}



	@Override
	public RecordConnectorStatus findLastConnnectorStatus(Connector conn) {
		
		return connRepo.findFirstByConnectorOrderBySendTimeDesc(conn);
	
	}



	@Override
	public ZonedDateTime findLastStationUpdate(ChargingStation cs) {
		//find either last meter record or last status update 
		ZonedDateTime lastUpdate = cs.getLastUpdate();
		
		for (ChargingUnit cu : cuService.findByStation(cs)) {
			MeterRecord meterRecord = meterService.findLastEnergyMeter(cu);
			
			if(meterRecord!=null && (lastUpdate==null || meterRecord.getSendTime().isAfter(lastUpdate)) )
					lastUpdate = meterRecord.getSendTime();
			
			for(Connector conn : connService.findByUnit(cu)) {
				RecordConnectorStatus record = findLastConnnectorStatus(conn);
				
				if(record!=null && (lastUpdate==null || record.getSendTime().isAfter(lastUpdate)) )
					lastUpdate = record.getSendTime();
			}
		}
	
		return lastUpdate;
	}



}
