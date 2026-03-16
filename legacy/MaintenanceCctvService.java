package com.example.ApSystem.service;

import com.example.ApSystem.exception.BadRequestException;
import com.example.ApSystem.model.DeviceCctvMaintenance;
import legacy.cctv_general_automation_report.excel.ExcelDataEntity;
import com.example.ApSystem.repository.MaintenanceIdCctvRepository;
import legacy.cctv_general_automation_report.excel.ExcelDataRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class MaintenanceCctvService {

    private final MaintenanceIdCctvRepository maintenanceRepo;
    private final ExcelDataRepository excelRepo;
    private static final List<String> EXCEL_ORDER = List.of(
            "ID",
            "s",
            "ITEM",
            "FECHA MT 1",
            "FECHA MT 2",
            "FASE",
            "UBICACIÓN",
            "CAMARA",
            "TIPO",
            "SERIAL",
            "N° INVENTARIO",
            "TIPO DE ACCESO",
            "USUARIO",
            "OBSERVACION"
    );


    public MaintenanceCctvService(
            MaintenanceIdCctvRepository maintenanceRepo,
            ExcelDataRepository excelRepo
    ) {
        this.maintenanceRepo = maintenanceRepo;
        this.excelRepo = excelRepo;
    }

    public void addMaintenance(String excelId, String observation,  String operator) {

        // 1️⃣ Validación básica
        if (excelId == null || excelId.isBlank()) {
            throw new BadRequestException("Excel ID is required");
        }

        // 2️⃣ Buscar la celda correspondiente a fecha_mt
        ExcelDataEntity fechaMt = excelRepo.findByExcelId(excelId)
                .stream()
                .filter(d -> d.getColumna().replaceAll("\\s", "").equalsIgnoreCase("FECHAMT1"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID no encontrado"));

        // 3️⃣ Generar fecha actual con formato
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String fechaActual =
                LocalDate.now().format(formatter);

        // 4️⃣ SOBRESCRIBIR el valor
        fechaMt.setValor(fechaActual);

        // 5️⃣ Guardar el cambio en el Excel
        excelRepo.save(fechaMt);

        // 6️⃣ (Opcional) registrar que el ID pasó por mantenimiento
        if (!maintenanceRepo.existsByExcelId(excelId)) {
            DeviceCctvMaintenance device = new DeviceCctvMaintenance();
            device.setExcelId(excelId);
            maintenanceRepo.save(device);
        }
    }

    //  Tabla dinámica de mantenimiento (NO TOCAR LÓGICA)
    public Map<String, Object> getMaintenanceTable() {

        List<DeviceCctvMaintenance> maintenances = maintenanceRepo.findAll();

        if (maintenances.isEmpty()) {
            return Map.of("headers", List.of(), "rows", List.of());
        }

        List<String> excelIds = maintenances.stream()
                .map(DeviceCctvMaintenance::getExcelId)
                .toList();

        // 🔹 obtenemos la data del Excel
        List<ExcelDataEntity> excelData = excelRepo.findByExcelIdIn(excelIds);

        // 🔹 obtener columnas únicas
        List<String> headers = excelData.stream()
                .map(ExcelDataEntity::getColumna)
                .distinct()
                .sorted((a, b) -> {
                    int idxA = EXCEL_ORDER.indexOf(a);
                    int idxB = EXCEL_ORDER.indexOf(b);

                    // Si ambas existen en el orden original → se respetan
                    if (idxA != -1 && idxB != -1) return Integer.compare(idxA, idxB);

                    // Si solo A existe → A va primero
                    if (idxA != -1) return -1;

                    // Si solo B existe → B va primero
                    if (idxB != -1) return 1;

                    // Ninguno existe → orden alfabético (fallback)
                    return a.compareToIgnoreCase(b);
                })
                .toList();

        // 🔹 construir filas
        Map<String, Map<String, Object>> rowMap = new LinkedHashMap<>();

        for (ExcelDataEntity data : excelData) {
            rowMap.putIfAbsent(data.getExcelId(), new LinkedHashMap<>());
            rowMap.get(data.getExcelId()).put(data.getColumna(), data.getValor());
        }

        return Map.of(
                "headers", headers,
                "rows", rowMap // ← ya queda tipo {excelId → {...}}
        );

    }




    @Transactional
    public void updateRow(String excelId, Map<String, String> data) {
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String columna = entry.getKey();
            String valor = entry.getValue() == null ? "" : entry.getValue();

            // columnas no editables
            if (List.of("FECHA MT 1", "FECHA MT 2", "CAMARA", "USUARIO").contains(columna)) {
                continue;
            }


            // buscar o crear entidad
            ExcelDataEntity cell = excelRepo.findByExcelIdAndColumna(excelId, columna)
                    .orElse(new ExcelDataEntity());

            cell.setExcelId(excelId);
            cell.setColumna(columna);
            cell.setValor(valor);

            // guardar
            excelRepo.saveAndFlush(cell);
        }
    }

}
