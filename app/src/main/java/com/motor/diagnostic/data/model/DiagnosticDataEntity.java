package com.motor.diagnostic.data.model;

import com.google.firebase.database.IgnoreExtraProperties;
import com.motor.diagnostic.domain.model.DiagnosticData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Firebase data model for DiagnosticData entity
 */
@IgnoreExtraProperties
public class DiagnosticDataEntity {
    private String id;
    private String motorcycleId;
    private Long timestamp;
    
    // Battery diagnostic
    private Float batteryVoltage;
    private String batteryStatus;
    private String batterySummary;
    
    // Engine diagnostic
    private Integer engineRPM;
    private String engineStatus;
    private String engineSummary;
    
    // Oil diagnostic
    private Float oilLevel;
    private String oilStatus;
    private String oilSummary;
    
    // Temperature diagnostic
    private Float coolantTemp;
    private String temperatureStatus;
    private String temperatureSummary;
    
    // Speed data
    private Float vehicleSpeed;
    private String speedUnit;
    
    // Trip data
    private Integer mileage;
    private Integer tripDistance;
    
    // Overall system status
    private String systemStatus;
    
    // Default constructor required for Firebase
    public DiagnosticDataEntity() {}
    
    public DiagnosticDataEntity(String id, String motorcycleId, Long timestamp, Float batteryVoltage,
                               String batteryStatus, String batterySummary, Integer engineRPM,
                               String engineStatus, String engineSummary, Float oilLevel,
                               String oilStatus, String oilSummary, Float coolantTemp,
                               String temperatureStatus, String temperatureSummary,
                               Float vehicleSpeed, String speedUnit, Integer mileage,
                               Integer tripDistance, String systemStatus) {
        this.id = id;
        this.motorcycleId = motorcycleId;
        this.timestamp = timestamp;
        this.batteryVoltage = batteryVoltage;
        this.batteryStatus = batteryStatus;
        this.batterySummary = batterySummary;
        this.engineRPM = engineRPM;
        this.engineStatus = engineStatus;
        this.engineSummary = engineSummary;
        this.oilLevel = oilLevel;
        this.oilStatus = oilStatus;
        this.oilSummary = oilSummary;
        this.coolantTemp = coolantTemp;
        this.temperatureStatus = temperatureStatus;
        this.temperatureSummary = temperatureSummary;
        this.vehicleSpeed = vehicleSpeed;
        this.speedUnit = speedUnit;
        this.mileage = mileage;
        this.tripDistance = tripDistance;
        this.systemStatus = systemStatus;
    }
    
    // Convert domain model to data entity
    public static DiagnosticDataEntity fromDomain(DiagnosticData diagnosticData) {
        return new DiagnosticDataEntity(
                diagnosticData.getId(),
                diagnosticData.getMotorcycleId(),
                diagnosticData.getTimestamp() != null ? diagnosticData.getTimestamp().getTime() : null,
                diagnosticData.getBatteryVoltage(),
                diagnosticData.getBatteryStatus(),
                diagnosticData.getBatterySummary(),
                diagnosticData.getEngineRPM(),
                diagnosticData.getEngineStatus(),
                diagnosticData.getEngineSummary(),
                diagnosticData.getOilLevel(),
                diagnosticData.getOilStatus(),
                diagnosticData.getOilSummary(),
                diagnosticData.getCoolantTemp(),
                diagnosticData.getTemperatureStatus(),
                diagnosticData.getTemperatureSummary(),
                diagnosticData.getVehicleSpeed(),
                diagnosticData.getSpeedUnit(),
                diagnosticData.getMileage(),
                diagnosticData.getTripDistance(),
                diagnosticData.getSystemStatus()
        );
    }
    
    // Convert data entity to domain model
    public DiagnosticData toDomain() {
        Date timestampDate = timestamp != null ? new Date(timestamp) : null;
        return new DiagnosticData(
                id,
                motorcycleId,
                timestampDate,
                batteryVoltage != null ? batteryVoltage : 0f,
                batteryStatus,
                batterySummary,
                engineRPM != null ? engineRPM : 0,
                engineStatus,
                engineSummary,
                oilLevel != null ? oilLevel : 0f,
                oilStatus,
                oilSummary,
                coolantTemp != null ? coolantTemp : 0f,
                temperatureStatus,
                temperatureSummary,
                vehicleSpeed != null ? vehicleSpeed : 0f,
                speedUnit,
                mileage != null ? mileage : 0,
                tripDistance != null ? tripDistance : 0,
                systemStatus
        );
    }
    
    // Convert to Firebase Map
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("motorcycleId", motorcycleId);
        result.put("timestamp", timestamp);
        result.put("batteryVoltage", batteryVoltage);
        result.put("batteryStatus", batteryStatus);
        result.put("batterySummary", batterySummary);
        result.put("engineRPM", engineRPM);
        result.put("engineStatus", engineStatus);
        result.put("engineSummary", engineSummary);
        result.put("oilLevel", oilLevel);
        result.put("oilStatus", oilStatus);
        result.put("oilSummary", oilSummary);
        result.put("coolantTemp", coolantTemp);
        result.put("temperatureStatus", temperatureStatus);
        result.put("temperatureSummary", temperatureSummary);
        result.put("vehicleSpeed", vehicleSpeed);
        result.put("speedUnit", speedUnit);
        result.put("mileage", mileage);
        result.put("tripDistance", tripDistance);
        result.put("systemStatus", systemStatus);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMotorcycleId() {
        return motorcycleId;
    }

    public void setMotorcycleId(String motorcycleId) {
        this.motorcycleId = motorcycleId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Float getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Float batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public String getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(String batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public String getBatterySummary() {
        return batterySummary;
    }

    public void setBatterySummary(String batterySummary) {
        this.batterySummary = batterySummary;
    }

    public Integer getEngineRPM() {
        return engineRPM;
    }

    public void setEngineRPM(Integer engineRPM) {
        this.engineRPM = engineRPM;
    }

    public String getEngineStatus() {
        return engineStatus;
    }

    public void setEngineStatus(String engineStatus) {
        this.engineStatus = engineStatus;
    }

    public String getEngineSummary() {
        return engineSummary;
    }

    public void setEngineSummary(String engineSummary) {
        this.engineSummary = engineSummary;
    }

    public Float getOilLevel() {
        return oilLevel;
    }

    public void setOilLevel(Float oilLevel) {
        this.oilLevel = oilLevel;
    }

    public String getOilStatus() {
        return oilStatus;
    }

    public void setOilStatus(String oilStatus) {
        this.oilStatus = oilStatus;
    }

    public String getOilSummary() {
        return oilSummary;
    }

    public void setOilSummary(String oilSummary) {
        this.oilSummary = oilSummary;
    }

    public Float getCoolantTemp() {
        return coolantTemp;
    }

    public void setCoolantTemp(Float coolantTemp) {
        this.coolantTemp = coolantTemp;
    }

    public String getTemperatureStatus() {
        return temperatureStatus;
    }

    public void setTemperatureStatus(String temperatureStatus) {
        this.temperatureStatus = temperatureStatus;
    }

    public String getTemperatureSummary() {
        return temperatureSummary;
    }

    public void setTemperatureSummary(String temperatureSummary) {
        this.temperatureSummary = temperatureSummary;
    }

    public Float getVehicleSpeed() {
        return vehicleSpeed;
    }

    public void setVehicleSpeed(Float vehicleSpeed) {
        this.vehicleSpeed = vehicleSpeed;
    }

    public String getSpeedUnit() {
        return speedUnit;
    }

    public void setSpeedUnit(String speedUnit) {
        this.speedUnit = speedUnit;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(Integer tripDistance) {
        this.tripDistance = tripDistance;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }
} 