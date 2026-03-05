package com.example.ApSystem.dto.sacs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SacsMaintenanceRequest {
    // El ID que buscaste (ej. "2077-03")
    private String excelId;

    // Opcional: El nombre de quien presiona el botón
    private String technicianName;

    // Opcional: Observaciones rápidas que quieras que viajen al Excel
    private String observations;

    // Ciclo de mantenimiento (1 para I semestre, 2 para II semestre)
    private Integer cycle;
}