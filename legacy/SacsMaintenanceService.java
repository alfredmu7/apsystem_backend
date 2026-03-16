package com.example.ApSystem.service.sacs;

import com.example.ApSystem.model.Sacs_DB.SacsDataEntity;
import legacy.cctv_general_automation_report.Sacs_DB.SacsMaintenanceEntity;
import com.example.ApSystem.repository.sacs.SacsDataRepository;
import com.example.ApSystem.repository.sacs.SacsMaintenanceRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SacsMaintenanceService {

    private final SacsMaintenanceRepository maintenanceRepo;
    private final SacsDataRepository sacsExcelRepo;

    // ORDEN EXACTO SOLICITADO
    private static final List<String> EXCEL_ORDER = List.of(
            "ITEM",
            "CODIGO",
            "ID PUERTA",
            "FECHA MANTENIMIENTO I",
            "FECHA MANTENIMIENTO II",
            "GRUPO",
            "UBICACIÓN",
            "CAMARA",
            "RESPONSABLE A CARGO",
            "TIPO DE EQUIPO",
            "OBSERVACIONES"
    );
    // Constructor para Inyección de Dependencias
    public SacsMaintenanceService(SacsMaintenanceRepository maintenanceRepo, SacsDataRepository sacsExcelRepo) {
        this.maintenanceRepo = maintenanceRepo;
        this.sacsExcelRepo = sacsExcelRepo;
    }


    public Map<String, Object> getMaintenanceTable() {
        List<SacsMaintenanceEntity> registry = maintenanceRepo.findAll();
        List<String> excelIds = registry.stream().map(SacsMaintenanceEntity::getExcelId).toList();

        List<SacsDataEntity> excelData = sacsExcelRepo.findByExcelIdIn(excelIds);

        // Generar headers respetando el orden de EXCEL_ORDER
        List<String> headers = excelData.stream()
                .map(SacsDataEntity::getColumna)
                .distinct()
                .sorted((a, b) -> {
                    int idxA = EXCEL_ORDER.indexOf(a.trim());
                    int idxB = EXCEL_ORDER.indexOf(b.trim());

                    if (idxA != -1 && idxB != -1) return Integer.compare(idxA, idxB);
                    if (idxA != -1) return -1;
                    if (idxB != -1) return 1;
                    return a.compareToIgnoreCase(b);
                })
                .toList();

        // Construir filas (Map de excelId -> Columna -> Valor)
        Map<String, Map<String, String>> rowMap = new LinkedHashMap<>();
        for (SacsDataEntity data : excelData) {
            rowMap.putIfAbsent(data.getExcelId(), new LinkedHashMap<>());
            rowMap.get(data.getExcelId()).put(data.getColumna(), data.getValor());
        }

        return Map.of(
                "headers", headers,
                "rows", rowMap
        );
    }

    // ESTE ES EL MeTODO QUE BUSCA EL CONTROLADOR
    @Transactional
    public void addMaintenance(String excelId) {
        if (excelId == null || excelId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID is required");
        }

        // 1. Buscar la celda de la fecha ("FECHA MANTENIMIENTO I")
        List<SacsDataEntity> deviceCells = sacsExcelRepo.findByExcelId(excelId);

        SacsDataEntity fechaCell = deviceCells.stream()
                .filter(d -> d.getColumna().trim().equalsIgnoreCase("FECHA MANTENIMIENTO I"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campo de fecha no encontrado"));

        // 2. Actualizar valor de la fecha
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        fechaCell.setValor(fechaActual);
        sacsExcelRepo.save(fechaCell);

        // 3. Registrar en la tabla de auditoría/mantenimiento
        if (!maintenanceRepo.existsByExcelId(excelId)) {
            SacsMaintenanceEntity registration = new SacsMaintenanceEntity();
            registration.setExcelId(excelId);
            registration.setMaintenanceDate(LocalDateTime.now());
            maintenanceRepo.save(registration);
        }
    }

    @Transactional
    public void addSacsMaintenance(String excelId) {
        // 1. Opcional: Validar si el ID existe en la data maestra antes de proceder
        // List<SacsDataEntity> exists = sacsDataRepo.findByExcelId(excelId);
        // if (exists.isEmpty()) throw new RuntimeException("El ID no existe en la base de datos de SACS");

        // 2. Crear el registro de mantenimiento
        SacsMaintenanceEntity maintenance = new SacsMaintenanceEntity();
        maintenance.setExcelId(excelId);
        maintenance.setMaintenanceDate(LocalDateTime.now());

        // Si tienes un sistema de usuarios, aquí podrías setear quien lo hizo
        // maintenance.setTecnico("Nombre del Técnico");

        // 3. Guardar en la tabla de historial
        maintenanceRepo.save(maintenance);
    }

    @Transactional
    public void updateRow(String excelId, Map<String, String> data) {
        // Definimos qué columnas NO se pueden editar desde la tabla (por seguridad)
        List<String> protectedColumns = List.of(
                "ITEM", "CODIGO", "ID PUERTA", "FECHA MANTENIMIENTO I", "UBICACIÓN"
        );

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String columna = entry.getKey();
            String valor = entry.getValue() == null ? "" : entry.getValue();

            // Si la columna está protegida, saltamos al siguiente cambio
            if (protectedColumns.contains(columna.trim())) {
                continue;
            }

            // DENTRO DE SacsMaintenanceService.java
// Asegúrate de llamar a sacsExcelRepo (SacsDataRepository)
            SacsDataEntity cell = sacsExcelRepo.findByExcelIdAndColumna(excelId, columna)
                    .orElse(new SacsDataEntity());

            cell.setExcelId(excelId);
            cell.setColumna(columna);
            cell.setValor(valor);

            sacsExcelRepo.save(cell);
        }
    }

}