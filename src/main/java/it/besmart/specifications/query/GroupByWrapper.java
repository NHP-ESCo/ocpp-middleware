package it.besmart.specifications.query;

import it.besmart.specifications.enums.TimeStatType;

public class GroupByWrapper {

	private String field;
	
	private String parentJoin;
	
	private TimeStatType timeAggr;
	
	private String nameField;

	public GroupByWrapper(String f) {
		this.field = f;
	}
	
	public GroupByWrapper(String f, TimeStatType t) {
		this.field = f;
		this.timeAggr  = t;
	}
	
	public GroupByWrapper(String field, String parent) {
		this.field = field;
		this.parentJoin = parent;
	}

	public GroupByWrapper() {
		super();
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public TimeStatType getTimeAggr() {
		return timeAggr;
	}

	public void setTimeAggr(TimeStatType timeAggr) {
		this.timeAggr = timeAggr;
	}

	public String getParentJoin() {
		return parentJoin;
	}

	public void setParentJoin(String parentJoin) {
		this.parentJoin = parentJoin;
	}

	public String getNameField() {
		return nameField;
	}

	public void setNameField(String nameField) {
		this.nameField = nameField;
	}
	
}
