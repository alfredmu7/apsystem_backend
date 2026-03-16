package com.example.ApSystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros_mantenimiento_cctv")
@Data
@NoArgsConstructor
public class CctvMaintenanceRecord {
    // En CctvMaintenanceRecord.java
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Cambia Long por Integer

    @Column(name = "fecha_mantenimiento", updatable = false)
    private LocalDateTime fechaMantenimiento = LocalDateTime.now();

    @Column(name = "dispositivo_id", nullable = false)
    private String dispositivoId;

    private String tecnico;

    @Column(nullable = false)
    private String observaciones;

    private String estado = "Completado";
}
