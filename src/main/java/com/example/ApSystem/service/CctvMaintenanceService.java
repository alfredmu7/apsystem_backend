package com.example.ApSystem.service;

import com.example.ApSystem.model.CctvMaintenanceRecord;
import com.example.ApSystem.model.CctvModel;
import com.example.ApSystem.repository.CctvMaintenanceRepository;
import com.example.ApSystem.repository.CctvRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CctvMaintenanceService {

    @Autowired
    private CctvMaintenanceRepository maintenanceRepo;

    @Autowired
    private CctvRepository inventoryRepo;

    @Autowired
    private Cctv_WordReportGeneratorService reportService; // Servicio de automatización de Word

    /**
     * Ejecuta el proceso de mantenimiento:
     * 1. Valida duplicados en la base de datos (Neon).
     * 2. Registra el mantenimiento en el historial.
     * 3. Automatiza la inserción en el reporte Word correspondiente (Otro Sí 7, 20, Servidores o Cisa).
     */
    @Transactional
    public CctvMaintenanceRecord ejecutarMantenimiento(String id, String obs, String tecnico) {

        // 1. VALIDACIÓN ANTI-DUPLICADOS (REQUISITO FUNDAMENTAL)
        // Evita que un técnico agregue dos veces el mismo ID al informe del mes.
        if (maintenanceRepo.existsByDispositivoId(id)) {
            throw new RuntimeException("El ID " + id + " ya está incluido en el informe actual.");
        }

        // 2. Obtener datos de la cámara desde el inventario (Neon)
        // Necesitamos la ubicación y la observación/proyecto para discriminar el reporte.
        CctvModel camera = inventoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cámara no encontrada: " + id));

        // 3. Crear el registro de historial en la base de datos
        CctvMaintenanceRecord record = new CctvMaintenanceRecord();
        record.setDispositivoId(id);
        record.setObservaciones(obs);
        record.setTecnico(tecnico);
        // La fecha se asigna automáticamente si tienes @PrePersist o por defecto en DB
        CctvMaintenanceRecord savedRecord = maintenanceRepo.save(record);

        // 4. AUTOMATIZACIÓN DEL REPORTE WORD
        // 'infoClave' se extrae de la cámara encontrada para decidir el destino (Word).
        // Se envía camera.getObservacion() que es donde residen los tags: "SERVIDORES", "OTROSI 20", "EXTERIOR-CISA", etc.
        String infoClave = camera.getObservacion();

        // Llamada al servicio que inyecta la fila en la tabla del Word
        reportService.agregarFilaAlInforme(savedRecord, camera.getUbicacion(), infoClave);

        return savedRecord;
    }

    public List<CctvMaintenanceRecord> obtenerHistorialCctv() {
        return maintenanceRepo.findAllByOrderByFechaMantenimientoDesc();
    }
}