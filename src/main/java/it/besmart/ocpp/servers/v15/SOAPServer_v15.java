package it.besmart.ocpp.servers.v15;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.chargetime.ocpp.SOAPServer;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

@Component("SOAP15")
public class SOAPServer_v15 extends ServerOCPP_v15 {
		
	private final Logger logger = LoggerFactory.getLogger(SOAPServer_v15.class);

	@Autowired
	public SOAPServer_v15(CoreProfile_v15 coreProfile) { 
		
		logger.debug("Create my Server SOAP1.5");
		this.protocol = ProtocolVersion.OCPP15;
		
        //SOAP server
        this.mainServer = new SOAPServer(coreProfile); //IServerAPI interface
        
//        mainServer.addFeatureProfile( firmwareProfile );
//        mainServer.addFeatureProfile(new ServerRemoteTriggerProfile() );
//        mainServer.addFeatureProfile(new ServerReservationProfile() );
//        mainServer.addFeatureProfile(new ServerSmartChargingProfile() );
//        mainServer.addFeatureProfile(new ServerLocalAuthListProfile() );
        
	}	

}
