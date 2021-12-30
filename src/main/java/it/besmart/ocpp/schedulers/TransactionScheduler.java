package it.besmart.ocpp.schedulers;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.enums.MeasurandType;
import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.services.interfaces.IAuthorizationService;
import it.besmart.ocpp.services.interfaces.IMeterRecordService;
import it.besmart.ocpp.services.interfaces.ITxService;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.EndTxReason;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;
import it.besmart.specifications.query.GroupByWrapper;
import it.besmart.specifications.search.objects.CountWrapper;

@Component
public class TransactionScheduler {

	
	private final Logger logger = LoggerFactory.getLogger(TransactionScheduler.class);
	
	@Autowired
	private ITxService txService;
	
	@Autowired
	private IAuthorizationService authService;
	
	@Autowired
	private IMeterRecordService meterService;
	
	private final static int NUM_SAMPLE = 3; //at least 3 samples (about 6 minutes)

	private static final double POWER_TOL = 1;//kW


	@Scheduled(initialDelay = 2 * 60 * 1000, fixedDelay = 5 * 60 * 1000) 
	public void checkTxPower() {
		
		for(Transaction tx : txService.findOngoingTransactions() ) {
			
			CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
			builder.with("tx", Operations.EQUAL, tx);
			builder.with("sendTime", Operations.DESC, null);
			builder.with("measurand", Operations.EQUAL, MeasurandType.POWER);
			
			List<MeterRecord> list = meterService.findBySpec(builder.build());
			
			if(list.size()>=NUM_SAMPLE) {
				
				boolean changedPower = false;
				double max = 0;
				
				
				for(MeterRecord rec : list.subList(0, NUM_SAMPLE)) {
					
					if(rec.getValue()>max)
						max = rec.getValue();
				}
				
				
				//first computation: set powerType
				if(tx.getLastMaxPower()==null) {
					changedPower = true;
					
					switch(tx.getConnector().getUnit().getPowerType()) {
						case DC:
							tx.setPowerType(ConnectionPowerType.DC);
							break;
						case MONOPHASE:
							tx.setPowerType(ConnectionPowerType.MONOPHASE);
							break;
						case TRIPHASE: 
							
							//Look for records regarding Phase measures
							CustomSpecificationBuilder builderPhase = new CustomSpecificationBuilder();
							builderPhase.with("tx", Operations.EQUAL, tx);
							builderPhase.with("measurand", Operations.EQUAL, MeasurandType.POWER);
							builderPhase.with("phase", Operations.NOT_NULL, null);
							
							builderPhase.with("sendTime", Operations.DESC, null);
							
							List<CountWrapper> phaseList = meterService.countBy(builderPhase.build(), new GroupByWrapper("phase"));
							
							int phases = phaseList.size();
							if(phases!=0) {
								if(phases==3)
									tx.setPowerType(ConnectionPowerType.TRIPHASE);
								else  {
									tx.setPowerType(ConnectionPowerType.MONOPHASE);
									tx.setReducedPhase(true);
								}
							}
							else {
								Connector conn = tx.getConnector();
								
								//No info about phases
								if(max < conn.getMinPower()) {
									//Less than minimum is monophase for sure
									tx.setReducedPhase(true); 
								}
								else if(max > tx.getConnector().getActualMaxPower()/3)
									//More than one third of max power is triphase for sure
									tx.setReducedPhase(false);
								else
									tx.setReducedPhase(true); // we cannot say anything, stay conservative
									
							}
							
							break;
						default:
							break;
					
					}

				}
				else 
					changedPower = Math.abs(max - tx.getLastMaxPower()) > POWER_TOL;
				
				ChargingStation cs = tx.getConnector().getUnit().getChargingStation();
				
				if(changedPower) {
					tx.setLastMaxPower(max);
					if(max > tx.getMaxPower())
						tx.setMaxPower(max);
					
					txService.updateTransaction(tx);
					
				}
			}
			
		}
		
	
	}

	@Scheduled(initialDelay = 1 * 60 * 1000, fixedDelay = 5 * 60 * 1000) //every 5 minutes
	public void checkExpiredAuthorization() {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("response", Operations.EQUAL, AuthorizationResponse.ACCEPTED);
		builder.with("timestamp", Operations.BEFORE_ZONED, ZonedDateTime.now().minusMinutes(5));
		
		Set<String> sessions = new HashSet<>();
		
		for(Authorization auth : authService.findAll(builder.build())) {
			if(!sessions.contains(auth.getExternalSession())) {
				sessions.add(auth.getExternalSession());
				
				if(txService.findByExternalSession(auth.getExternalSession(), 
						auth.getCu().getChargingStation())!=null)
					logger.warn(String.format("Authorization %d was not started", auth.getAuthorizationID()));
				else
					authService.abortAuthorization(auth, EndTxReason.EXPIRED, null);
			}
			
		}
	}
}
