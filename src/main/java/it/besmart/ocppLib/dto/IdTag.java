package it.besmart.ocppLib.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import it.besmart.ocppLib.enumeration.IdTagType;

public class IdTag {


	@JsonProperty("tag_id")
	private String tagId = null;

	@JsonProperty("tag_type")
	private IdTagType tagType = null;

	public IdTag(String idTag) {
		this.tagId = idTag;
		this.tagType = IdTagType.RFID;
	}

	public IdTag() {
		super();
	}
	/**
	 * Identifies the card or contract related to the recharge user. In case of type RFID_ID length must be 32 characters, in the other cases max length is 28 characters
	 * @return tagId
	 **/
	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	/**
	 * Type of identification.
	 * @return tagType
	 **/
	public IdTagType getTagType() {
		return tagType;
	}

	public void setTagType(IdTagType tagType) {
		this.tagType = tagType;
	}

	@Override
	public String toString() {
		return " [tagId=" + tagId + ", tagType=" + tagType + "]";
	}
}