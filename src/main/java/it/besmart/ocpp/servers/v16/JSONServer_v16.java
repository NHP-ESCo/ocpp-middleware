package it.besmart.ocpp.servers.v16;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.feature.profile.ServerLocalAuthListProfile;
import eu.chargetime.ocpp.feature.profile.ServerRemoteTriggerProfile;
import eu.chargetime.ocpp.feature.profile.ServerReservationProfile;
import eu.chargetime.ocpp.feature.profile.ServerSmartChargingProfile;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

@Component("JSON16")
@DependsOn({"coreProfile_16","firmwareProfile_16"})
public class JSONServer_v16 extends ServerOCPP_v16 {
		
	private final Logger logger = LoggerFactory.getLogger(JSONServer_v16.class);
	
	
	@Autowired
	public JSONServer_v16(CoreProfile_v16 coreProfile, FirmwareProfile_v16 firmwareProfile) { 
		
		logger.debug("Create my Server JSON1.6");
		this.protocol = ProtocolVersion.OCPPJ16;
				
		// The core profile is mandatory in OCPP1.6
        
        mainServer = new JSONServer(coreProfile); //IServerAPI interface
        
        mainServer.addFeatureProfile(firmwareProfile );
        mainServer.addFeatureProfile(new ServerRemoteTriggerProfile() );
        mainServer.addFeatureProfile(new ServerReservationProfile() );
        mainServer.addFeatureProfile(new ServerSmartChargingProfile() );
        mainServer.addFeatureProfile(new ServerLocalAuthListProfile() );
        
	}

	

}
