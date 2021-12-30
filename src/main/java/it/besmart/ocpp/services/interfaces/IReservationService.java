package it.besmart.ocpp.services.interfaces;

import java.util.List;

import it.besmart.ocpp.dtos.ReservationDTO;
import it.besmart.ocpp.enums.ReserveStatus;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Reservation;

public interface IReservationService {

	public Reservation addReservation(ReservationDTO res, ChargingUnit cu);
	
	public Reservation updateReservationStatus(Reservation res, ReserveStatus status);
	
	
	public Reservation findById(long id);
	
	public Reservation findByDTO(ReservationDTO res);

	public List<Reservation> findActive();
	
	public Reservation findActive(ChargingUnit cu);

	
}
