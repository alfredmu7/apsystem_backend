package com.example.ApSystem.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;




@Table(name = "device_cctv_maintenance")
public class DeviceCctvMaintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID que viene del Excel
    @Column(nullable = false, unique = true)
    private String excelId;

    // fecha (opcional pero recomendado)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DeviceCctvMaintenance() {
    }

    public DeviceCctvMaintenance(Long id, String excelId, LocalDateTime createdAt) {
        this.id = id;
        this.excelId = excelId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExcelId() {
        return excelId;
    }

    public void setExcelId(String excelId) {
        this.excelId = excelId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
