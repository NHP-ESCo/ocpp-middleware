package it.besmart.specifications.query;

import it.besmart.specifications.enums.OperationDbType;

//Operations between multiple field in queries
public class DbFieldWrapper {

	private OperationDbType type; //if null simple field1
	
	private String field1;
	
	private String field2;

	public DbFieldWrapper(String string) {
		this.field1 = string;
	}

	public DbFieldWrapper() {
		super();
	}

	public OperationDbType getType() {
		return type;
	}

	public void setType(OperationDbType type) {
		this.type = type;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}
	
}

