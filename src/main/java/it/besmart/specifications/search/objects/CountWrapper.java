package it.besmart.specifications.search.objects;

public class CountWrapper {

	private Object groupObject;
	
	private Long count;

	public Object getGroupObject() {
		return groupObject;
	}

	public void setGroupObject(Object groupObject) {
		this.groupObject = groupObject;
	}
	
	public CountWrapper() {
		super();
	}

	public CountWrapper(Long count) {
		this.count = count;
	}
	
	public CountWrapper(Object o, Long count) {
		this.groupObject = o;
		this.count = count;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
	
}
