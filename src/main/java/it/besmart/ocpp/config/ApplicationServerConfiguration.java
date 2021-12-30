package it.besmart.ocpp.config;

import java.util.Map;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.servers.ServerMap;
import it.besmart.ocpp.servers.ServerOC;
import it.besmart.ocpp.servers.v16.JSONServer_v16;
import it.besmart.ocpp.servers.v16.SOAPServer_v16;
import it.besmart.ocpp.services.interfaces.IStationService;

@Configuration
@EnableWebSocket
public class ApplicationServerConfiguration {

	@Value("${port.ocppJ16}")
	int ocppJ16Port;
	
	@Value("${port.ocppS16}")
	int ocppS16Port;
	
	@Autowired
	private ServerMap serverMap;
	
	@Autowired
	private JSONServer_v16 serverJ6;
	
	@Autowired
	private SOAPServer_v16 serverS6;

	@Autowired
	private IStationService stationService;
		
	@Autowired
	private ApplicationContext ctx;
		
	
	private final Logger logger = LoggerFactory.getLogger(ApplicationServerConfiguration.class);
	
	@PostConstruct
	public void configureServerConnections() {
		
		cleanConnection();
		createServerMap();
		
		/** Start Servers on configured ports **/
		
		serverJ6.started(ocppJ16Port); //start communication: equal for any server
		serverS6.started(ocppS16Port);
		
	}
	
	
	private void cleanConnection() {
		//Disconnect from all stations, previously stored
		for(ChargingStation cs : stationService.findAll() ) {
			stationService.disconnectStation(cs, true);
		}
	}
	
	private void createServerMap() {
		Map<String, ServerOC> servers = ctx.getBeansOfType(ServerOC.class);
		
		for(Map.Entry<String, ServerOC> s : servers.entrySet()) {
			serverMap.putServer(s.getValue());
		}
		logger.debug(serverMap.toString());
	}
}
