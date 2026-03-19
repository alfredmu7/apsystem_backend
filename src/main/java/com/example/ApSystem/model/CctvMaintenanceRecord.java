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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_mantenimiento", updatable = false)
    private LocalDateTime fechaMantenimiento = LocalDateTime.now();

    @Column(name = "dispositivo_id", nullable = false)
    private String dispositivoId;
    private String fase;


    private String ubicacion;

    private String tecnico;

    private String observacion;

    private String estado = "Completado";
}