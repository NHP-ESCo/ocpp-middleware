package it.besmart.ocpp.services.interfaces;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import it.besmart.ocpp.dtos.MeterRecordDTO;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocppLib.dto.RechargeMeterData;
import it.besmart.specifications.query.GroupByWrapper;
import it.besmart.specifications.search.objects.CountWrapper;

public interface IMeterRecordService {
	

	public MeterRecord addRecord(MeterRecordDTO record, ChargingStation station, Transaction transaction);
	
	public List<MeterRecord> findByTransaction(Transaction tx, boolean complete);

	public MeterRecord findLastEnergyMeter(Transaction tx, boolean alreadySent);
	
	public MeterRecord findLastEnergyMeter(ChargingUnit cu);

	
	public void addStartTxRecord(Transaction tx);

	public void addStandbyTxRecord(Transaction tx);
	
	public void addStopTxRecord(Transaction tx);

	
	public List<RechargeMeterData> getRechargeDetails(Transaction tx, boolean complete);

	public MeterRecord updateMeter(MeterRecord r);

	public List<MeterRecord> findBySpec(Specification<MeterRecord> spec);

	List<CountWrapper> countBy(Specification<MeterRecord> spec, GroupByWrapper groupBy);

	
	
}
