package com.example.ApSystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;



@Table(name = "device_maintenance")
public class DeviceMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_type", nullable = false)
    private String deviceType;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "technician", nullable = false)
    private String technician;

    @Column(name = "systemName")
    private String systemName;

    @Column(name = "trouble")
    private String trouble;

    @Column(nullable = false) // Fecha automática de creación
    private LocalDateTime createdAt;

    // CONSTRUCTORES
    public DeviceMaintenance() {
    }


    public DeviceMaintenance(String deviceType, String location, String deviceId,
                             String technician, String system, String trouble) {
        this.deviceType = deviceType;
        this.location = location;
        this.deviceId = deviceId;
        this.technician = technician;
        this.systemName = system;
        this.trouble = trouble;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    //  GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getTechnician() {
        return technician;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String system) {
        this.systemName = system;
    }

    public String getTrouble() {
        return trouble;
    }

    public void setTrouble(String trouble) {
        this.trouble = trouble;
    }
}
