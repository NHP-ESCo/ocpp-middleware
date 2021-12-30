package it.besmart.ocpp.servers.v16;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementProfile;

@Component("firmwareProfile_16")
@DependsOn("firmwareProfileHandler_16")
public class FirmwareProfile_v16 extends ServerFirmwareManagementProfile {

	@Autowired
	public FirmwareProfile_v16(FirmwareManagementProfileHandler_v16 handler) {
		super(handler);
	}

}
