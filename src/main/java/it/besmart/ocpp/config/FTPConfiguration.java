package it.besmart.ocpp.config;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import it.besmart.ocpp.utils.FileTransferUtility;

@Configuration
public class FTPConfiguration {

	@Value("${ftp.user}")
	String user;
	
	@Value("${ftp.password}")
	String password;
	 
	@Value("${ftp.serverUrl}")
	String serverUrl;
	
	@Value("${ftp.serverDirectory}")
	String serverDirectory;
	
	@Value("${ftp.baseDirectory}")
	String baseDirectory;
	
	@Autowired
	private FileTransferUtility fileUtility;
	
	private final Logger logger = LoggerFactory.getLogger(FTPConfiguration.class);
	
	@PostConstruct
	public void configureFTP() {
		
		/**Configure default ftp server for file exchange with stations (diagnostics, firmware) **/
		
		fileUtility.configureFtpServer(user, password, serverUrl, serverDirectory, baseDirectory);
		
	}
	
	
}
