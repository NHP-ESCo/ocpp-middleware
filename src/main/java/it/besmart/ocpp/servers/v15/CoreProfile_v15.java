package it.besmart.ocpp.servers.v15;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;

@Component("coreProfile_15")
@DependsOn("coreProfileHandler_15")
public class CoreProfile_v15 extends ServerCoreProfile {

	@Autowired
	public CoreProfile_v15(CoreProfileHandler_v15 handler) {
		super(handler);
		
	}

}
