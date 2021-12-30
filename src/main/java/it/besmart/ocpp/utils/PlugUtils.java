package it.besmart.ocpp.utils;

import it.besmart.ocpp.exceptions.ConfigurationException;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;

public class PlugUtils {

	public static final int NOMINAL_MONOPHASE_VOLTAGE = 220;
	
	public static double computePower(ConnectionPowerType powerType, double current) {
		
		switch(powerType) {
		
			case DC :
			case TRIPHASE :
				return MathUtils.round(3*current*NOMINAL_MONOPHASE_VOLTAGE/1000, 1);
			case MONOPHASE :
				return MathUtils.round(current*NOMINAL_MONOPHASE_VOLTAGE/1000, 1);
				
			default:
				throw new ConfigurationException("Unknown power type");
			
		}
	}
	
	
	public static double computeCurrent(ConnectionPowerType powerType, double power) {
		
		switch(powerType) {
		
			case DC :
			case TRIPHASE :
				return MathUtils.round(1000*power/(3*NOMINAL_MONOPHASE_VOLTAGE), 1);
			case MONOPHASE :
				return MathUtils.round(1000*power/NOMINAL_MONOPHASE_VOLTAGE, 1);
				
			default:
				throw new ConfigurationException("Unknown power type");
			
		}
	}
	
	
}
