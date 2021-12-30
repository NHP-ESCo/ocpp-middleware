package it.besmart.ocpp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.besmart.ocppLib.enumeration.ConnectionPowerType;

@Entity
public class ModelUnit {
	
	private static final Logger logger = LoggerFactory.getLogger(ModelUnit.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_modelunit_generator")
	@SequenceGenerator(name="z_modelunit_generator", sequenceName = "z_modelunit_seq", allocationSize=1)
	private long unitID;
	
	@NotNull
	@JsonBackReference
	@ManyToOne
	private Model model;
	
	@NotNull
	private int ref;
	
	@JsonManagedReference
	@OneToMany(mappedBy="unit", cascade = CascadeType.ALL, orphanRemoval=true)
	private Set<ModelConnector> connectors = new HashSet<>();

	
	@Override
	public String toString() {
		return "ModelUnit [unitID=" + unitID + ", ref=" + ref + "]";
	}


	public ModelUnit() {
		super();
	}


	public long getUnitID() {
		return unitID;
	}
	
	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}


	public Model getModel() {
		return model;
	}


	public void setModel(Model model) {
		this.model = model;
	}
	
	
	
	public Set<ModelConnector> getConnectors() {
		return connectors;
	}


	public void setConnectors(Set<ModelConnector> connectors) {
		this.connectors = connectors;
	}


	public void addConnector(ModelConnector connEntity) {

		this.connectors.add(connEntity);
		
	}

	//TODO: service
	public double computeMaxPower(ConnectionPowerType powerType) {
		
		double max = 0;
		for (ModelConnector conn : this.connectors) {
			double plugPower = conn.computeMaxPower(powerType);
			if( plugPower > max) {
				max = plugPower;
			}
		}
		return max;
		
	}


	public double computeMinPower(ConnectionPowerType powerType) {
		
		double min = 0;
		for (ModelConnector conn : this.connectors) {
			double plugPower = 0;
			plugPower = conn.computeMinPower(powerType);
			logger.debug(String.format("Minimum for plug %d %s: %f", conn.getRef(), conn.getPlug().getValue(), plugPower));
			if( plugPower > min) {
				min = plugPower;
			}
		}
		
		return min;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ref;
		result = prime * result + (int) (unitID ^ (unitID >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelUnit other = (ModelUnit) obj;
		if (ref != other.ref)
			return false;
		if (unitID != other.unitID)
			return false;
		return true;
	} 


}
