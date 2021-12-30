package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.enums.ReserveStatus;
import it.besmart.ocpp.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

	
	List<Reservation> findByStatus(ReserveStatus status);
}
