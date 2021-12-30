package it.besmart.ocpp.utils;

public class ParamUtils {

	public static boolean equalParams(String keyVal, String paramValue) {
		
		if(paramValue==null)
			return false;
		
		if(keyVal.equalsIgnoreCase(paramValue) || (BooleanUtils.isTrue(keyVal) && BooleanUtils.isTrue(paramValue)) 
				|| (BooleanUtils.isFalse(keyVal) && BooleanUtils.isFalse(paramValue)) )
				return true;
		else {
			//logger.warn(String.format("%s is different from %s", val1, val2));
			return false;
		}
		
	}
}
