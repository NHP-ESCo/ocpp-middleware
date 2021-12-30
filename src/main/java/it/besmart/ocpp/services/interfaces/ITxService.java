package it.besmart.ocpp.services.interfaces;

import java.time.ZonedDateTime;
import java.util.List;

import it.besmart.ocpp.dtos.TransactionDTO;
import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocppLib.enumeration.EndTxReason;

public interface ITxService {
	
	Transaction findById(long id);
	
	Transaction findByAuthorization(Authorization auth);

	Transaction findByCsSession(ChargingStation cs, String txId);  //In ocpp cs session does not exist, session is defined by us
	
	
	Transaction addTransaction(TransactionDTO tx, Authorization auth, Connector conn);
	
	Transaction stopTransaction(Transaction tx, ZonedDateTime stopDate, EndTxReason reason, boolean saveMeter, boolean backoffice);
	
	Transaction updateTransaction(Transaction tx);

	void updateTransactionStatus(Transaction tx, ZonedDateTime timestamp);

	
	
	Transaction findOngoingTransaction(Connector conn);

	Transaction findOngoingTransaction(ChargingUnit cu);
	
	List<Transaction> findOngoingTransactions(ChargingStation cs);
	
	List<Transaction> findOngoingTransactions();
	
	
	List<Transaction> findPastTransactions(Connector conn);

	String findOngoingSession(Connector conn);

	Transaction findByExternalSession(String externalSession, ChargingStation cs);


}
