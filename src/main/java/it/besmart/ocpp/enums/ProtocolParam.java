package it.besmart.ocpp.enums;

public enum ProtocolParam {
	
	AllowOfflineTxForUnknownId("AllowOfflineTxForUnknownId"),
	AuthorizationCacheEnabled("AuthorizationCacheEnabled"),
	AuthorizeRemoteTxRequests("AuthorizeRemoteTxRequests"),
	BlinkRepeat("BlinkRepeat"),
	ClockAlignedDataInterval("ClockAlignedDataInterval"),
	ConnectionTimeOut("ConnectionTimeOut"),
	ConnectorPhaseRotation("ConnectorPhaseRotation"),
	ConnectorPhaseRotationMaxLength("ConnectorPhaseRotationMaxLength"),
	GetConfigurationMaxKeys("GetConfigurationMaxKeys"),
	HeartbeatInterval("HeartbeatInterval"),
	LightIntensity("LightIntensity"),
	LocalAuthorizeOffline("LocalAuthorizeOffline"),
	LocalPreAuthorize("LocalPreAuthorize"),
	MaxEnergyOnInvalidId("MaxEnergyOnInvalidId"),
	MeterValuesAlignedData("MeterValuesAlignedData"),
	MeterValuesAlignedDataMaxLength("MeterValuesAlignedDataMaxLength"),
	MeterValuesSampledData("MeterValuesSampledData"),
	MeterValuesSampledDataMaxLength("MeterValuesSampledDataMaxLength"),
	MeterValueSampleInterval("MeterValueSampleInterval"),
	MinimumStatusDuration("MinimumStatusDuration"),
	NumberOfConnectors("NumberOfConnectors"),
	ResetRetries("ResetRetries"),
	StopTransactionOnEVSideDisconnect("StopTransactionOnEVSideDisconnect"),
	StopTransactionOnInvalidId("StopTransactionOnInvalidId"),
	StopTxnAlignedData("StopTxnAlignedData"),
	StopTxnAlignedDataMaxLength("StopTxnAlignedDataMaxLength"),
	StopTxnSampledData("StopTxnSampledData"),
	StopTxnSampledDataMaxLength("StopTxnSampledDataMaxLength"),
	SupportedFeatureProfiles("SupportedFeatureProfiles"),
	SupportedFeatureProfilesMaxLength("SupportedFeatureProfilesMaxLength"),
	TransactionMessageAttempts("TransactionMessageAttempts"),
	TransactionMessageRetryInterval("TransactionMessageRetryInterval"),
	UnlockConnectorOnEVSideDisconnect("UnlockConnectorOnEVSideDisconnect"),
	WebSocketPingInterval("WebSocketPingInterval"),
	LocalAuthListEnabled("LocalAuthListEnabled"),
	LocalAuthListMaxLength("LocalAuthListMaxLength"),
	SendLocalListMaxLength("SendLocalListMaxLength"),
	ReserveConnectorZeroSupported("ReserveConnectorZeroSupported"),
	ChargeProfileMaxStackLevel("ChargeProfileMaxStackLevel"),
	ChargingScheduleAllowedChargingRateUnit("ChargingScheduleAllowedChargingRateUnit"),
	ChargingScheduleMaxPeriods("ChargingScheduleMaxPeriods"),
	ConnectorSwitch3to1PhaseSupported("ConnectorSwitch3to1PhaseSupported"),
	MaxChargingProfilesInstalled("MaxChargingProfilesInstalled");

	
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private ProtocolParam(String value) {
		this.value = value;
	}

}
