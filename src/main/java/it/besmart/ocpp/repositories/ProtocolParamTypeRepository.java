package it.besmart.ocpp.repositories;

import java.util.List;

import it.besmart.ocpp.model.ProtocolParamType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

public interface ProtocolParamTypeRepository extends ParamTypeRepository<ProtocolParamType> {

	public List<ProtocolParamType> findByProtocol(ProtocolVersion s);

	public ProtocolParamType findByNameAndProtocol(String name, ProtocolVersion version);


	
}
