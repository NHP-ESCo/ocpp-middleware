package it.besmart.ocpp.services;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.dtos.MeterRecordDTO;
import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.enums.MeasurandType;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.repositories.MeterRecordRepository;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IMeterRecordService;
import it.besmart.ocpp.services.interfaces.ITxService;
import it.besmart.ocppLib.dto.RechargeMeterData;
import it.besmart.ocppLib.enumeration.TransactionStatus;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomRepository;
import it.besmart.specifications.query.CustomSpecificationBuilder;
import it.besmart.specifications.query.GroupByWrapper;
import it.besmart.specifications.search.objects.CountWrapper;

@Service
@Transactional
public class MeterRecordService implements IMeterRecordService {

	private final Logger logger = LoggerFactory.getLogger(MeterRecordService.class);
	
	public final static double ENERGY_TOL = 0.1;
	
	private static final double POWER_TOL = 0.1;
	
	public final static int TIME_STANDBY_TOL = 30;

	private static final int ERROR_ENERGY_TOL = 10; // % tolerance w.r.t to max power
	
	
	@Autowired
	private MeterRecordRepository repo;
	
	@Autowired
	private CustomRepository customRepo;
	
	@Autowired
	private IErrorService errorService;
	
	@Autowired
	private ApplicationContext ctx;  //to avoid loop with tx service 
	
	//ONLY FOR CREATE - DTO
	@Autowired
	private ICUService cuService;
	
	
	@Override
	public List<CountWrapper> countBy(Specification<MeterRecord> spec, GroupByWrapper groupBy) {
		
		return customRepo.getCountByGroup(spec, MeterRecord.class, groupBy);
		
	}
	
	@Override
	public MeterRecord addRecord(MeterRecordDTO record, ChargingStation station, Transaction tx) {
		
		MeterRecord entity = new MeterRecord(record);
		
		if(tx!=null) { //during recharge 
			
			if(record.getSendTime().isAfter(ZonedDateTime.now().plusMinutes(1)))
				logger.warn(String.format("%s: meter record in the future %s. Necessity to resynchronize station", 
						station.getEvseID(), record.getSendTime().toString()));
			
			MeterRecord lastRec = findPreviousRecord(tx, 
					record.getMeasurand(), record.getPhase());
			
			if( tx.getStatus().equals(TransactionStatus.ACTIVE) &&  //during active tx
					lastRec!=null && record.getSendTime().isAfter(lastRec.getSendTime()) ) { //sequence of meter values
					
					if( record.getMeasurand().equals(MeasurandType.ENERGY) ) {
					
						if(record.getValue()+ENERGY_TOL<lastRec.getValue()) { //decremented register
							
							String msg = "Error on energy meter value: decremented register";
							//logger.error();
							
							errorService.saveConnectorError(msg, ErrorType.RECHARGE_ERROR, tx.getConnector(), tx.getExternalSession());
							
							
						} //check missing standby
						else if ( record.getValue() - lastRec.getValue() < 0.001) {
							//TODO: manage error
							logger.warn(String.format("Zero energy on active tx %d", tx.getTxID()));
							
//							connService.updateStatus(tx.getConnector(), ConnectorStatusComplete.SUSPENDEDEV); 
//							txService.updateTransactionStatus( tx, mv.getTimestamp() );
						}
						
					}
					else if (record.getMeasurand().equals(MeasurandType.POWER) && record.getValue()==0) {
						logger.warn(String.format("Zero power on active tx %d", tx.getTxID()));
						
					}
			}
			else {
				 
				/**Data during standby, do not save if close to 0 **/
				
				if(lastRec!=null 
						&& Duration.between(lastRec.getSendTime(), record.getSendTime()).toMinutes() < TIME_STANDBY_TOL) {
					
					if( (record.isEnergy() && record.getValue()-lastRec.getValue() < ENERGY_TOL) 
							|| (record.isPower() && record.getValue()< POWER_TOL) )
						return null;  
					else
						logger.warn(String.format("Tx %d : Not null value in standby. Save this record", tx.getTxID()));
				}
				else {
					logger.debug(String.format("Tx %d : More than %d minutes in standby. Save this record", tx.getTxID(), TIME_STANDBY_TOL));
				}
			}
			
			
			entity.setTx(tx);
			entity.setCu(tx.getConnector().getUnit());
			if(record.isPower())
				entity.setSetValue(tx.getLastSetpoint());
			
		}
		else {  // or clock-aligned
			if(record.getValue()==0.0)  //useless to store if empty
				return null;
			
			if (record.getCu()!=null) {  
				ChargingUnit cu = cuService.findByDTO(record.getCu());
				entity.setCu(cu);
//				logger.debug(String.format("Clock-aligned meter data %s", cu.getEvseCode()));
				
			}
			else {  // record related to the whole station (clock-aligned)
				entity.setCs(station);
//				logger.warn(String.format("Clock-aligned meter data %s", station.getEvseID()));
				
			}
		}
		
		return repo.save(entity);
	}

	private MeterRecord findPreviousRecord(Transaction tx, MeasurandType measurand, Integer phase) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("tx", Operations.EQUAL, tx);
		builder.with("measurand", Operations.EQUAL, measurand);
		if(phase!=null)
			builder.with("phase", Operations.EQUAL, phase);
		else
			builder.with("phase", Operations.IS_NULL, null);
		builder.with("sendTime", Operations.ASC, null);
		
		List<MeterRecord> list = repo.findAll(builder.build());
		if(list.isEmpty())
			return null;
		
		return list.get(0);
	}

	/** List of registered meter values not sent yet and ordered by ascendent time **/
	@Override
	public List<MeterRecord> findByTransaction(Transaction tx, boolean complete) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("tx", Operations.EQUAL, tx);
		if(!complete)
			builder.with("sent", Operations.EQUAL, false);
		builder.with("phase", Operations.IS_NULL, null);
		
		builder.with("sendTime", Operations.ASC, null);
		
		return repo.findAll(builder.build());
	}


	@Override
	public MeterRecord findLastEnergyMeter(Transaction tx, boolean alreadySent) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("tx", Operations.EQUAL, tx);
		builder.with("measurand", Operations.EQUAL, MeasurandType.ENERGY);
		builder.with("phase", Operations.IS_NULL, null);
		if(alreadySent) {
			builder.with("sent", Operations.EQUAL, true);
		}
		
		builder.with("sendTime", Operations.DESC, null);
		
		List<MeterRecord> list = repo.findAll(builder.build());
		if(list.isEmpty())
			return null;
		
		return list.get(0);
	}

	
	@Override
	public MeterRecord findLastEnergyMeter(ChargingUnit cu) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("cu", Operations.EQUAL, cu);
		builder.with("measurand", Operations.EQUAL, MeasurandType.ENERGY);
		builder.with("phase", Operations.IS_NULL, null);
		builder.with("sendTime", Operations.DESC, null);
		
		List<MeterRecord> list = repo.findAll(builder.build());
		if(list.isEmpty())
			return null;
		
		return list.get(0);
		
	}

	@Override
	public void addStartTxRecord(Transaction tx) {
		MeterRecord record = new MeterRecord();
		record.setSendTime(tx.getStartDate());
		record.setTimestamp(ZonedDateTime.now());
		record.setMeasurand(MeasurandType.ENERGY);
		record.setTx(tx);
		record.setCu(tx.getConnector().getUnit());
		record.setValue(tx.getMeterStart());
		record.setComputed(true);
		
		repo.save(record);
	}

	@Override
	public void addStandbyTxRecord(Transaction tx) {
		MeterRecord last = findLastEnergyMeter(tx, false);
		
		if(last!=null 
				&& last.getSendTime().isBefore(ZonedDateTime.now().minusMinutes(2))) {
			MeterRecord record = new MeterRecord();
			record.setSendTime(tx.getStopChargingDate());
			record.setTimestamp(ZonedDateTime.now());
			record.setMeasurand(MeasurandType.ENERGY);
			record.setTx(tx);
			record.setCu(tx.getConnector().getUnit());
			record.setValue(last.getValue());
			record.setComputed(true);
			
			repo.save(record);
		}
		
	}
	
	@Override
	public void addStopTxRecord(Transaction tx) {
		MeterRecord record = new MeterRecord();
		record.setSendTime(tx.getStopDate());
		record.setTimestamp(ZonedDateTime.now());
		record.setMeasurand(MeasurandType.ENERGY);
		record.setTx(tx);
		record.setCu(tx.getConnector().getUnit());
		record.setValue(tx.getMeterStop());
		record.setComputed(true);
		
		repo.save(record);
	}
	
	
	@Override
	public List<RechargeMeterData> getRechargeDetails (Transaction tx, boolean complete) {
		
		List<RechargeMeterData> itemList = new ArrayList<RechargeMeterData>();
		int index = 0;
		RechargeMeterData item = null;
		if(tx.getMeterStart()==null)
			return itemList;
		
		
		MeterRecord firstRec = null;
		
		if(!complete)
			firstRec = findLastEnergyMeter(tx, true);
		
		if(firstRec==null) { //never sent anything
			firstRec = new MeterRecord();
			firstRec.setValue(tx.getMeterStart());
			firstRec.setSendTime(tx.getStartDate());
		}
		double startMeter = firstRec.getValue();
		
		Long sampleTime = null; //SECONDS 
		
		//TODO: when roaming we need 2 sent fields
		for (MeterRecord record : findByTransaction(tx, complete) ) { //not sent
			
			//Compute with respect to previous value (or if it's first)
			if ( ! record.getSendTime().isBefore( firstRec.getSendTime() ) && 
					(record.getMeasurand()==MeasurandType.ENERGY || record.getMeasurand()==MeasurandType.POWER) ) {
				
				if (index == 0 || 
						 item.getCuTime().isBefore(record.getSendTime()) )   {
					
					//new item
					
					if (index>0) {
						//Add previous item to list
						if(checkIntegrity(item, tx))
							itemList.add(item);
						
						//recompute sample time with respect to previous item
						sampleTime = Duration.between(item.getCuTime(), record.getSendTime()).toSeconds();
						
					}
					else //first item
						sampleTime = Duration.between(firstRec.getSendTime(), record.getSendTime()).toSeconds();
					
					item = new RechargeMeterData();
					
					item.setSampleTime(sampleTime);
					item.setPowerSetpoint(tx.getLastSetpoint());
					
					item = updateMeasure(record, startMeter, item);
					index++;
					
				}
				else { 
					
					//update Item: same sendTime as in the past
					item = updateMeasure( record, startMeter, item);

				}
				
				if(record.getMeasurand()==MeasurandType.ENERGY)
					startMeter = record.getValue(); //Energy register
				
				record.setSent(true);
				repo.save(record);
			}

		}
		
		//Last item
		if(item!=null) {
			
			if(checkIntegrity(item, tx))
				itemList.add(item);
		}
		 
		return itemList;
	}
	
	private boolean checkIntegrity(RechargeMeterData item, Transaction tx) {
		if(item.getDeliveredEnergy()!= null && item.getDeliveredEnergy()<0) {
			logger.error(String.format("Negative energy for tx %d", tx.getTxID()));
			return false;
		}
		
		//check integrity power / energy
		
		Long sampleTime = item.getSampleTime();
		double tol = Math.max(1, (1+ERROR_ENERGY_TOL/100) * tx.getConnector().getActualMaxPower() * (sampleTime.doubleValue()/3600));
		
		if(item.getMeterRecordPower()==null) { //Estimate power
			if(sampleTime!=0)
				item.setMeterRecordPower(item.getDeliveredEnergy()/(sampleTime.doubleValue()/3600));
		}
		else if(item.getMeterRecordPower()!= null && item.getDeliveredEnergy()!= null &&
				Math.abs(item.getMeterRecordPower()*(sampleTime.doubleValue()/3600) - item.getDeliveredEnergy()) > tol) {
			
			errorService.saveConnectorError(String.format("Incoherent power [%.2f kWh] during recharge session", item.getMeterRecordPower()), 
					ErrorType.RECHARGE_ERROR, tx.getConnector(), tx.getExternalSession());
			
			double estimatedEnergy = item.getMeterRecordPower()*(sampleTime.doubleValue()/3600);
			long txDuration = Duration.between(tx.getStartDate(), item.getCuTime()).toMinutes();
			
			logger.error(String.format("%f kWh estimated vs. %f kWh measured after %d minutes", 
					estimatedEnergy, item.getDeliveredEnergy(), txDuration));
			
			if(item.getDeliveredEnergy() > estimatedEnergy) {
				
				if(tx.isResetMeter() &&
					txDuration < 3*sampleTime/60 ) {
				
					//first records, update tx start
					
					item.setDeliveredEnergy(estimatedEnergy);
					tx.setMeterStart(item.getDeliveredEnergy()-estimatedEnergy);
					
					Map<String, ITxService> implementations = ctx.getBeansOfType(ITxService.class);
					ITxService service = implementations.get("txService");
					service.updateTransaction(tx);
				}
				return false;
			}
			else
				return true;
		}
		
		return true;
	}
	
	
	private RechargeMeterData updateMeasure(MeterRecord record, Double startMeter, 
			RechargeMeterData data) {
		
		if(record.getMeasurand() == MeasurandType.ENERGY) {
			data.setDeliveredEnergy(record.getValue()-startMeter);
			//TODO change when integrate inverse flow
	//			else 
	//				data.setDeschargeEnergy(record.getValue()-startMeter);
		} 
		else if (record.getMeasurand() == MeasurandType.POWER) {
			data.setMeterRecordPower(record.getValue());
			if(record.getSetValue()!=null)
				data.setPowerSetpoint(record.getSetValue());
		}
		
		data.setCuTime(record.getSendTime());
		data.setSystemTime(record.getTimestamp());
	
		return data;
	}

	@Override
	public MeterRecord updateMeter(MeterRecord r) {
		
		return repo.save(r);
	}

	@Override
	public List<MeterRecord> findBySpec(Specification<MeterRecord> spec) {
		
		return repo.findAll(spec);
	}


}
