package it.besmart.ocpp.servers.v16;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;

@Component("coreProfile_16")
@DependsOn("coreProfileHandler_16")
public class CoreProfile_v16 extends ServerCoreProfile {

	private final Logger logger = LoggerFactory.getLogger(CoreProfile_v16.class);
	
	@Autowired
	public CoreProfile_v16(ICoreProfileHandler handler) {
		super(handler);
		logger.debug("Create CoreProfile for 1.6");
	}

}
