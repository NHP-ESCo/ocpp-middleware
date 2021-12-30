package it.besmart.specifications.search.objects;

public class SumWrapper {

	private Object groupObject;
	
	private Double sum;

	public Object getGroupObject() {
		return groupObject;
	}

	public void setGroupObject(Object groupObject) {
		this.groupObject = groupObject;
	}

	public Double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		this.sum = sum;
	}

	public SumWrapper() {
		super();
	}
	
	public SumWrapper(Object o, double d) {
		this.groupObject = o;
		this.sum = d;
	}
	
	public SumWrapper(Object o, long d) {
		this.groupObject = o;
		this.sum = (double) d;
	}

	public SumWrapper(double d) {
		this.sum = d;
	}
	
	public SumWrapper(long d) {
		this.sum = (double) d;
	}
	
	public SumWrapper(int d) {
		this.sum = (double) d;
	}

	@Override
	public String toString() {
		return groupObject + "," + sum + "\n";
	}
}
