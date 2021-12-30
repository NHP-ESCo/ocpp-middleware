package it.besmart.ocpp.config;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import it.besmart.ocpp.client.external.ClientService;

@Configuration
public class ExternalClientConfiguration {

	@Value("${url.authStart}")
	String authStart;
	
	@Value("${url.authStop}")
	String authStop;
	
	@Value("${url.startTx}")
	String startTx;
	
	@Value("${url.stopTx}")
	String stopTx;
	
	@Value("${url.diagnResult}")
	String diagnResult;
	
	@Autowired
	public ClientService clientService;
	
		
	private final Logger logger = LoggerFactory.getLogger(ExternalClientConfiguration.class);
	
	@PostConstruct
	public void configureClient() {
			
		/**Configure external url for push messages towards client **/
		
		clientService.configureUrl(authStart, authStop, startTx, stopTx, diagnResult);
		
	}
	
}
