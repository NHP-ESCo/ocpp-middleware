package it.besmart.ocpp.servers;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.besmart.ocppLib.enumeration.ProtocolVersion;


@Component("serverMap")
public class ServerMap {
	
	private final Logger logger = LoggerFactory.getLogger(ServerMap.class);
	
	private HashMap<ProtocolVersion, ServerOC> servers = new HashMap<>();

	public ServerMap() {
		super();
		logger.debug("Create ServerMap");
	}

	
	public void putServer(ServerOC server) {
		servers.put(server.getProtocol(), server);
	}
	
	public ServerOC getServer(ProtocolVersion protocol) {
		return servers.get(protocol);
	}


	@Override
	public String toString() {
		return "ServerMap [servers=" + servers.toString() + "]";
	}
	
	
	
}
