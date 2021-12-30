package it.besmart.ocpp.model;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class WlCard {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_customer_generator")
	@SequenceGenerator(name="z_customer_generator", sequenceName = "z_customer_seq", allocationSize=1)
	private long customerID;
	
	@NotNull
	@NotEmpty
	private String idTag;
	
	private ZonedDateTime expiryDate;
	
	
	@JsonBackReference
	@ManyToMany(mappedBy="localList")
	private Set<ChargingStation> authorizedStations = new HashSet<>();
	
	
	public WlCard() {
		super();
	}
	
	public long getCustomerId() {
		return customerID;
	}

	public String getIdTag() {
		return idTag;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}


	public ZonedDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(ZonedDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Override
	public String toString() {
		return "[customerID=" + customerID + ", idTag=" + idTag + ", expiryDate=" + expiryDate + "]";
	}

	public boolean isExpired() {

		if(expiryDate!= null && expiryDate.isAfter(ZonedDateTime.now()))
			return true;
		else
			return false;
	}

	

}
