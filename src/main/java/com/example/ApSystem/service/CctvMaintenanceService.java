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
    private Cctv_WordReportGeneratorService reportService;

    @Autowired
    private Cctv_ExcelReportGeneratorService excelService;

    /**
     * Ejecuta el proceso de mantenimiento:
     * 1. Valida duplicados.
     * 2. Registra el mantenimiento con Ubicación y Fase (para filtrado en React).
     * 3. Automatiza reportes Word y Excel.
     */
    @Transactional
    public CctvMaintenanceRecord ejecutarMantenimiento(String id, String obs, String tecnico) {

        // 1. VALIDACIÓN ANTI-DUPLICADOS
        if (maintenanceRepo.existsByDispositivoId(id)) {
            throw new RuntimeException("El ID " + id + " ya está incluido en el informe actual.");
        }

        // 2. OBTENER DATOS DEL INVENTARIO (Neon DB - Tabla sql_cctv)
        CctvModel camera = inventoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cámara no encontrada: " + id));

        // --- EXTRAER DATOS DEL INVENTARIO ---
        String ubicacionInv = (camera.getUbicacion() != null) ? camera.getUbicacion() : "N/A";
        String faseOriginal = (camera.getFase() != null) ? camera.getFase() : "";
        String observacionMaestra = (camera.getObservacion() != null) ? camera.getObservacion() : "";
        String observacionDb = (camera.getObservacion() != null) ? camera.getObservacion() : "";

        // 3. CREAR REGISTRO DE HISTORIAL (Persistiendo datos procesados para el Frontend)
        CctvMaintenanceRecord record = new CctvMaintenanceRecord();
        record.setDispositivoId(id);
        record.setUbicacion(ubicacionInv);

        // --- LÓGICA DE INTEGRACIÓN DE FASE ---
        // Si la columna 'observacion' de Neon dice OTRO SI 20, esa es su fase real de reporte
        if (observacionMaestra.toUpperCase().contains("OTRO SI 20") ||
                observacionMaestra.toUpperCase().contains("OS20")) {
            record.setFase("OTRO SI 20");
        } else {
            record.setFase(faseOriginal);
        }

        record.setObservacion(observacionDb);
        record.setTecnico(tecnico);

        // Guardamos en la base de datos registros_mantenimiento_cctv
        CctvMaintenanceRecord savedRecord = maintenanceRepo.save(record);

        // 4. AUTOMATIZACIÓN DE INFORMES
        // Usamos la observación maestra para que el WordGenerator sepa a qué documento enviarlo
        String infoClaveTotal = record.getFase() + " " + observacionMaestra;
        reportService.agregarFilaAlInforme(savedRecord, ubicacionInv, infoClaveTotal);

        // Reporte Excel RMS VSS
        excelService.agregarRegistroAExcel(savedRecord, record.getFase(), ubicacionInv);

        return savedRecord;
    }

    public List<CctvMaintenanceRecord> obtenerHistorialCctv() {
        return maintenanceRepo.findAllByOrderByFechaMantenimientoDesc();
    }
}