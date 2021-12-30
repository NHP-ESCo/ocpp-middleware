package it.besmart.specifications.enums;

public enum TimeStatType {

	DAY("Day"),
	WEEK("Week"),
	MONTH("Month"),
	YEAR("Year");
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private TimeStatType(String value) {
		this.value = value;
	}
}
