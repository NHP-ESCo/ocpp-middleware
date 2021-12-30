package it.besmart.specifications.query;

import javax.persistence.criteria.JoinType;

import it.besmart.specifications.enums.Operations;

public class SearchCriteria {

    private String key;
    private Operations operation;
    private Object value;
    private String parentObject;
    private JoinType joinType;

    public SearchCriteria(String key, Operations operation, Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
    }



    public SearchCriteria(String key, Operations operation, Object value, String parentObject) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.parentObject = parentObject;

    }

    public SearchCriteria(String key, Operations operation, Object value, String parentObject, JoinType joinType) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.parentObject = parentObject;
        this.joinType = joinType;

    }


    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public Operations getOperation() {
        return operation;
    }
    public void setOperation(Operations operation) {
        this.operation = operation;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public String getParentObject() {
        return parentObject;
    }
    public void setParentObject(String parentObject) {
        this.parentObject = parentObject;
    }
  



    @Override
    public String toString() {
        return "SearchCriteria [key=" + key + ", operation=" + operation + ", value=" + value + ", parentObject="
                + parentObject + "]";
    }



	public JoinType getJoinType() {
		return joinType;
	}



	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}
}
