package it.besmart.ocpp.utils;

public class BooleanUtils {

	public static boolean isTrue(String val) {
		return val.equals("1") || val.equalsIgnoreCase("true");
	}
	
	public static boolean isFalse(String val) {
		return val.equals("0") || val.equalsIgnoreCase("false");
	}
	
}
