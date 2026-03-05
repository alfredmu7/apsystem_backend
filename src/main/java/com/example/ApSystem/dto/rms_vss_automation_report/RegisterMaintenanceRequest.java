package com.example.ApSystem.dto.rms_vss_automation_report;
import java.time.LocalDate;

public class RegisterMaintenanceRequest {

    private String deviceId;
    private LocalDate maintenanceDate;
    private String observation;
    private String operator;
    private boolean forceInsert; // para duplicados (política C)

    // getters + setters


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public boolean isForceInsert() {
        return forceInsert;
    }

    public void setForceInsert(boolean forceInsert) {
        this.forceInsert = forceInsert;
    }
}
