package com.motor.diagnostic.domain.model;

import java.util.Date;

/**
 * Domain model for DiagnosticData entity
 */
public class DiagnosticData {
    private String id;
    private String motorcycleId;
    private Date timestamp;
    
    // Battery diagnostic
    private float batteryVoltage;
    private String batteryStatus; // "Good", "Warning", "Critical"
    private String batterySummary;
    
    // Engine diagnostic
    private int engineRPM;
    private String engineStatus; // "Good", "Warning", "Critical"
    private String engineSummary;
    
    // Oil diagnostic
    private float oilLevel;
    private String oilStatus; // "Good", "Warning", "Critical"
    private String oilSummary;
    
    // Temperature diagnostic
    private float coolantTemp;
    private String temperatureStatus; // "Good", "Warning", "Critical"
    private String temperatureSummary;
    
    // Speed data
    private float vehicleSpeed;
    private String speedUnit; // "km/h" or "mph"
    
    // Trip data
    private int mileage;
    private int tripDistance;
    
    // Overall system status
    private String systemStatus; // "Good", "Warning", "Critical"
    
    // Default constructor required for Firebase
    public DiagnosticData() {}
    
    public DiagnosticData(String id, String motorcycleId, Date timestamp, float batteryVoltage, 
                          String batteryStatus, String batterySummary, int engineRPM, 
                          String engineStatus, String engineSummary, float oilLevel, 
                          String oilStatus, String oilSummary, float coolantTemp, 
                          String temperatureStatus, String temperatureSummary, 
                          float vehicleSpeed, String speedUnit, int mileage, 
                          int tripDistance, String systemStatus) {
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(float batteryVoltage) {
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

    public int getEngineRPM() {
        return engineRPM;
    }

    public void setEngineRPM(int engineRPM) {
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

    public float getOilLevel() {
        return oilLevel;
    }

    public void setOilLevel(float oilLevel) {
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

    public float getCoolantTemp() {
        return coolantTemp;
    }

    public void setCoolantTemp(float coolantTemp) {
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

    public float getVehicleSpeed() {
        return vehicleSpeed;
    }

    public void setVehicleSpeed(float vehicleSpeed) {
        this.vehicleSpeed = vehicleSpeed;
    }

    public String getSpeedUnit() {
        return speedUnit;
    }

    public void setSpeedUnit(String speedUnit) {
        this.speedUnit = speedUnit;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(int tripDistance) {
        this.tripDistance = tripDistance;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }
} 