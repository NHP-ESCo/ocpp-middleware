package it.besmart.ocpp.services;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.dtos.ReservationDTO;
import it.besmart.ocpp.enums.ReserveStatus;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.repositories.ReservationRepository;
import it.besmart.ocpp.services.interfaces.IReservationService;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;


@Service
@Transactional
public class ReservationService implements IReservationService {

	
	@Autowired
	private ReservationRepository repo;
	
	
	@Override
	public Reservation addReservation(ReservationDTO res, ChargingUnit cu) {
		
		Reservation entity = new Reservation(res);
		entity.setUnit(cu);
		
		return repo.save(entity);
	}
	
	
	@Override
	public Reservation findById(long id) {
		Optional<Reservation> o = repo.findById(id);
		return o.isPresent()?o.get():null;
	}

	
	@Override
	public Reservation findByDTO(ReservationDTO res) {
		
		return repo.getOne(res.getResID());
	}

	@Override
	public List<Reservation> findActive() {
		
		Set<ReserveStatus> states = new HashSet<>();
		states.add(ReserveStatus.WAITING);
		states.add(ReserveStatus.ACCEPTED);
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("status", Operations.IN_SET, states);
		
		Specification<Reservation> spec = builder.build();
		
		return repo.findAll(spec);
		
	}

	
	@Override
	public Reservation updateReservationStatus(Reservation res, ReserveStatus status) {
		
		res.setStatus(status);
		if(status == ReserveStatus.CANCELED)
			res.setCancelDate(ZonedDateTime.now());
		
		return repo.save(res);
		
	}


	@Override
	public Reservation findActive(ChargingUnit cu) {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("status", Operations.EQUAL, ReserveStatus.ACCEPTED);
		builder.with("unit", Operations.EQUAL, cu);
		
		Specification<Reservation> spec = builder.build();
		
		List<Reservation> list =  repo.findAll(spec);
		
		if (list.size()>0)
			return list.get(0);
		else 
			return null;
	}

}
