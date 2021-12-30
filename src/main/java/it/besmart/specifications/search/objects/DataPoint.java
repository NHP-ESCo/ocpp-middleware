package it.besmart.specifications.search.objects;

public class DataPoint {

	private String point;
	
	private Double value;
	
	public DataPoint() {
		super();
	}

	public DataPoint(String point, Double value) {
		super();
		this.point = point;
		this.value = value;
	}



	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	
}
