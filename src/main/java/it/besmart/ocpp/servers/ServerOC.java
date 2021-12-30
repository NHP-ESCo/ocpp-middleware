package it.besmart.ocpp.servers;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.chargetime.ocpp.IServerAPI;
import eu.chargetime.ocpp.NotConnectedException;
import eu.chargetime.ocpp.OccurenceConstraintException;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.UnsupportedFeatureException;
import eu.chargetime.ocpp.model.Confirmation;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.SessionInformation;
import it.besmart.ocpp.enums.CSLifeStatus;
import it.besmart.ocpp.enums.ErrorMessageResponse;
import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.exceptions.ConfigurationException;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.services.interfaces.IConfigParamService;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IModelService;
import it.besmart.ocpp.services.interfaces.IParameterSelectService;
import it.besmart.ocpp.services.interfaces.IParameterTypeService;
import it.besmart.ocpp.services.interfaces.IStationService;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

@Component
public abstract class ServerOC implements RequestsOC {

	private final Logger logger = LoggerFactory.getLogger(ServerOC.class);
	
	protected final static String lowNetworkMsg = "Configuration: Station did not reply to configuration request";
	
	
	private static final int TIMEOUT_SEC = 30;
	
	protected IServerAPI mainServer;  // chargetime.eu -> JSON-WebSocket / SOAP
	
	protected ProtocolVersion protocol;
	
	@Autowired
	protected IErrorService errorService;
	
	@Autowired
	protected IStationService stationService;

	@Autowired
	protected IConfigParamService paramService;
	
	@Autowired
	protected IModelService modelService;    // to enable model for SC
	
	@Autowired
	protected IParameterTypeService paramTypeService;
	
	@Autowired
	protected IParameterSelectService paramSelectService;
	
	
	public ProtocolVersion getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolVersion protocol) {
		this.protocol = protocol;
	}
	

	@Override
	public String toString() {
		return "[mainServer=" + mainServer + ", protocol=" + protocol + "]";
	}


 	public void started(int portNumber) {

        mainServer.open("0.0.0.0", portNumber, new ServerEvents() {
			
			public void newSession(UUID sessionIndex, SessionInformation information) {
				
				//when a new connection is created I receive information about identifier and address of client
				
				String evse = information.getIdentifier();
				
				if( evse.codePointAt(0)=='/') 
					evse = evse.substring(1);
				
				logger.debug("New session from " + evse);
//				logger.debug(information.getAddress().toString());
				
				String addressIP = information.getAddress().getHostString();
				
				if ( information.getAddress().getHostName().equals("0:0:0:0:0:0:0:1") ) 
					evse = "IT*CP1*CP1"; //Test station. 
				
				if(evse.contains("?null")) { //bticino error
					
					evse = evse.replace("?null", "");
					logger.debug("Clean evse: " + evse);
				}
				
				
				ChargingStation cs = stationService.findByEvseID( evse );
				if(cs == null)
					cs = stationService.findBySN( evse ); //Some station only sends their sn
				
				if ( cs!=null ) {
					logger.debug("Stazione " + cs.getEvseID() + " connessa con protocollo: " + protocol);
					
					if(cs.isConnected())
						logger.warn(String.format("Stazione %s sta sovrascrivendo la sua connessione", cs.getEvseID()));
        
					cs = stationService.connectStation(cs,  sessionIndex.toString(), addressIP);
					
					errorService.solveStationErrors(cs, ErrorType.NETWORK_ERROR, null);
					
					
				}
				else {
					String message = String.format("La colonnina con identifier %s (IP: %s) ha provato a connettersi con protocollo %s", 
							evse, information.getAddress().getHostName(), protocol);
					
					errorService.saveError(message, ErrorType.FAULTED_COMMISSIONING, null, null, null);
					
					mainServer.closeSession(sessionIndex);
					
				}
				
			}

			//mainServer.closeSession -> ... -> lostSession
			public void lostSession(UUID sessionIndex) {
				
				ChargingStation cs = stationService.findBySession(sessionIndex) ;
				
				if (cs!= null) {
					errorService.saveError(ErrorMessageResponse.LOST_NETWORK.getValue(), ErrorType.NETWORK_ERROR, cs, null, null);
					logger.debug("Disconnessa colonnina " + cs.getEvseID() );
					
					stationService.disconnectStation(cs, false);
				}
			}
        });

    }
	
	
	public Confirmation getConfirmation(ChargingStation cs, Request request) {

		Confirmation conf = null;
		if(cs.getActualSession()==null)
			return conf;
		
		UUID sessionID = UUID.fromString(cs.getActualSession());
		
		logger.debug(String.format("Send to station %s  ->   :\n %s", 
				cs.getEvseID(), request.toString() ) );
		
		try {
			
			conf = mainServer.send(sessionID, request).toCompletableFuture().get(TIMEOUT_SEC, TimeUnit.SECONDS);	
			
		} catch (OccurenceConstraintException | UnsupportedFeatureException | NotConnectedException | 
				InterruptedException | ExecutionException | TimeoutException e) {
			
			 if (e instanceof NotConnectedException) { // No session found or IO not working (for ECPP)
					logger.warn(String.format("Station %s not connected anymore", cs.getEvseID()));
					
					stationService.disconnectStation(cs, false);
					
					//mainServer.closeSession -> ..(if session is present). -> lostSession
					mainServer.closeSession(UUID.fromString(cs.getActualSession()));
					
			}
			else if (e instanceof TimeoutException ) {
				logger.warn(String.format("Station %s took too long to reply", cs.getEvseID()));
				
				String [] comps = request.getClass().getName().split("\\.");
				
				errorService.saveError(ErrorMessageResponse.LOW_NETWORK.getValue() 
						+"[" + comps[comps.length-1] + "]", ErrorType.NETWORK_ERROR, cs, null, null);
				
			}
			else{
				logger.warn(e.toString());
			}
		}
		
		return conf;
		
	};
	

	public void configureStation(ChargingStation cs, boolean firstConfiguration, boolean boot, boolean auto) 
			throws ConfigurationException{
//		logger.debug(cs.toString());
		
		if(auto) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		if(firstConfiguration) 
			cs = stationService.updateLifeStatus( cs.getCSId(), CSLifeStatus.FIRST_CONFIGURATION);
		else
			cs = stationService.updateLifeStatus( cs.getCSId(), CSLifeStatus.TO_CONFIGURE);
		
//		logger.debug(cs.toString());
		
		cs = updateStationConfiguration(cs);
		
		if(firstConfiguration) {
			logger.debug("Commissioning completed for station " + cs.getEvseID());
			cs.setCommissioningDate(ZonedDateTime.now());
			
		}
		
		cs.setConfigureAtStopTx(false);
		cs.setLifeStatus(CSLifeStatus.ACTIVE);
		cs = stationService.updateStation(cs);
		
		//Correctly configured
		
		
		errorService.solveStationErrors(cs, ErrorType.CONFIGURATION_ERROR, "Configuration" );
		
	}
	
}
