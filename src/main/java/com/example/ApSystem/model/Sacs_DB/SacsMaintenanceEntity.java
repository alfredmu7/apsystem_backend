package com.example.ApSystem.model.Sacs_DB;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "sacs_maintenance_registry")
@Data // Esto genera automáticamente el metodo setFechaMantenimiento
public class SacsMaintenanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String excelId;

    private LocalDateTime maintenanceDate;


    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExcelId() { return excelId; }
    public void setExcelId(String excelId) { this.excelId = excelId; }
    public LocalDateTime getMaintenanceDate() { return maintenanceDate; }
    public void setMaintenanceDate(LocalDateTime maintenanceDate) { this.maintenanceDate = maintenanceDate; }
}