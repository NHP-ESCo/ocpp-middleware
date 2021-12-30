package it.besmart.ocpp.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.chargetime.ocpp.model.core.ChangeConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ChargingSchedulePeriod;
import eu.chargetime.ocpp.model.core.ConfigurationStatus;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsConfirmation;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareConfirmation;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListConfirmation;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListRequest;
import eu.chargetime.ocpp.model.smartcharging.ClearChargingProfileConfirmation;
import eu.chargetime.ocpp.model.smartcharging.ClearChargingProfileRequest;
import eu.chargetime.ocpp.model.smartcharging.GetCompositeScheduleConfirmation;
import eu.chargetime.ocpp.model.smartcharging.GetCompositeScheduleRequest;
import eu.chargetime.ocpp.model.smartcharging.SetChargingProfileConfirmation;
import eu.chargetime.ocpp.model.smartcharging.SetChargingProfileRequest;
import it.besmart.ocpp.enums.ErrorMessageResponse;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.servers.ServerMap;
import it.besmart.ocpp.services.interfaces.IStationService;
import it.besmart.ocpp.wrappers.APIError;
import it.besmart.ocppLib.wrappers.Response.AckResponse;

//Direct Commands

@RestController
@RequestMapping("api/command/ocpp16/")
public class CommandController_16 {
	
	@Autowired
	private ServerMap serverMap;
	
	@Autowired
	private IStationService stationService;
	
	
	@PostMapping("/changeConfiguration/{evseID}")
	public ResponseEntity<AckResponse> changeConfiguration(HttpServletRequest request, 
			@RequestBody ChangeConfigurationRequest bodyRequest, @PathVariable String evseID) {
		
		AckResponse response = new AckResponse(false, null);
		
		ChargingStation station = stationService.findByEvseID(evseID);
		
		if (station==null)
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		
		if(!station.isControllable())
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		if(!bodyRequest.validate())
			return APIError.response("Paylod is not valid");
		
		
		ChangeConfigurationConfirmation result = (ChangeConfigurationConfirmation) 
				serverMap.getServer(station.getProtocol()).getConfirmation(station, bodyRequest);
		
		if(result!=null) {
			response.setResult(result.getStatus().equals(ConfigurationStatus.Accepted));
			response.setMessage(result.getStatus().name());
		}
		
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	@PostMapping("/setChargingProfile/{evseID}")
	public ResponseEntity<AckResponse> setChargingProfile(HttpServletRequest request, 
			@RequestBody SetChargingProfileRequest bodyRequest, @PathVariable String evseID) {
		
		AckResponse response = new AckResponse(false, null);
		
		ChargingStation station = stationService.findByEvseID(evseID);
		
		if (station==null)
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		
		if( !station.isControllable() )
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		if(!bodyRequest.validate())
			return APIError.response("Paylod is not valid");
		
		
		SetChargingProfileConfirmation result = (SetChargingProfileConfirmation) 
				serverMap.getServer(station.getProtocol()).getConfirmation(station, bodyRequest);
		
		if(result!=null)
			response.setResult(true);
		
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	
	@PostMapping("/clearChargingProfile/{evseID}")
	public ResponseEntity<AckResponse> clearChargingProfile(HttpServletRequest request, 
			@RequestBody ClearChargingProfileRequest bodyRequest, @PathVariable String evseID) {
		
		AckResponse response = new AckResponse(false, null);
		
		ChargingStation station = stationService.findByEvseID(evseID);
		
		if (station==null)
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		if(!station.isControllable())
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		if(!bodyRequest.validate())
			return APIError.response("Paylod is not valid");
		
		
		ClearChargingProfileConfirmation result = (ClearChargingProfileConfirmation) 
				serverMap.getServer(station.getProtocol()).getConfirmation(station, bodyRequest);
		
	
		if(result!=null)
			response.setResult(true);
		
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	

	@PostMapping("/getCompositeSchedule/{evseID}")
	public ResponseEntity<AckResponse> getCompositeSchedule(HttpServletRequest request, 
			@RequestBody GetCompositeScheduleRequest bodyRequest, @PathVariable String evseID) {
		
		AckResponse response = new AckResponse(false, null);
		
		ChargingStation station = stationService.findByEvseID(evseID);
		
		if (station==null)
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		if(!station.isControllable())
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		if(!bodyRequest.validate())
			return APIError.response("Paylod is not valid");
		
		
		GetCompositeScheduleConfirmation result = (GetCompositeScheduleConfirmation) 
				serverMap.getServer(station.getProtocol()).getConfirmation(station, bodyRequest);
		
	
		if(result!=null) {
			response.setResult(true);
			String message = result.toString() + "\n";
			for (ChargingSchedulePeriod period : result.getChargingSchedule().getChargingSchedulePeriod()) {
				message += period.toString() + "\n";
			}
			response.setMessage(message);
		}
		
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	
	@PostMapping("/updateFirmware/{evseID}")
	public ResponseEntity<AckResponse> updateFirmware(HttpServletRequest request, 
			@RequestBody UpdateFirmwareRequest bodyRequest, @PathVariable String evseID) {
		
		AckResponse response = new AckResponse(false, null);
		
		ChargingStation station = stationService.findByEvseID(evseID);
		
		if (station==null)
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		
		if(!station.isControllable())
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		if(!bodyRequest.validate())
			return APIError.response("Paylod is not valid");
		
		
		UpdateFirmwareConfirmation result = (UpdateFirmwareConfirmation) 
				serverMap.getServer(station.getProtocol()).getConfirmation(station, bodyRequest);
		
		if(result!=null)
			response.setResult(true);
		
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	@PostMapping("/getDiagnostics/{evseID}")
	public ResponseEntity<AckResponse> getDiagnostics(HttpServletRequest request, 
			@RequestBody GetDiagnosticsRequest bodyRequest, @PathVariable String evseID) {
		
		AckResponse response = new AckResponse(false, null);
		
		ChargingStation station = stationService.findByEvseID(evseID);
		
		if (station==null)
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		if(!station.isControllable())
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		if(!bodyRequest.validate())
			return APIError.response("Paylod is not valid");
		
		
		GetDiagnosticsConfirmation result = (GetDiagnosticsConfirmation) 
				serverMap.getServer(station.getProtocol()).getConfirmation(station, bodyRequest);
		
	
		if(result!=null)
			response.setResult(true);
		
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
	
	@PostMapping("/sendLocalList/{evseID}")
	public ResponseEntity<AckResponse> sendLocalList(HttpServletRequest request, 
			@RequestBody SendLocalListRequest bodyRequest, @PathVariable String evseID) {
		
		AckResponse response = new AckResponse(false, null);
		
		ChargingStation station = stationService.findByEvseID(evseID);
		
		if (station==null)
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		
		if(!station.isControllable())
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		if(!bodyRequest.validate())
			return APIError.response("Paylod is not valid");
		
		
		 SendLocalListConfirmation result = (SendLocalListConfirmation) 
				serverMap.getServer(station.getProtocol()).getConfirmation(station, bodyRequest);
		
		if(result!=null)
			response.setResult(true);
		
		return new ResponseEntity<>(response, HttpStatus.OK);

	}
	
}
