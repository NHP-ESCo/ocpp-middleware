package it.besmart.specifications.search.objects;

public class FieldValue {

	private String key;
	
	private Double value;
	
	
	public FieldValue() {
		super();
	}

	public FieldValue(String key, Double v) {
		this.key = key;
		this.value = v;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	
	
}
