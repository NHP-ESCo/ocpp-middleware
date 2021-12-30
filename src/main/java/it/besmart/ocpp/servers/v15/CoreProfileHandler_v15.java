package it.besmart.ocpp.servers.v15;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;


@Service("coreProfileHandler_15")
@Transactional
@DependsOn("serverMap")
public class CoreProfileHandler_v15 implements ServerCoreEventHandler {

	private final Logger logger = LoggerFactory.getLogger(CoreProfileHandler_v15.class);

	public CoreProfileHandler_v15() {
		super();
		logger.debug("Create CoreProfileHandler for 1.5");
	}

	@Override
	public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex,
			BootNotificationRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex,
			StartTransactionRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex,
			StatusNotificationRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex, StopTransactionRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
	
