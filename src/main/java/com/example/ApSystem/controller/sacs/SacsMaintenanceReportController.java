package com.example.ApSystem.controller.sacs;

import com.example.ApSystem.service.sacs.SacsMaintenanceService;
import com.example.ApSystem.service.sacs.SacsReportService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/sacs/maintenance")
@CrossOrigin(origins = "*") // Permite peticiones desde el Frontend (React)
public class SacsMaintenanceReportController {

    private final SacsMaintenanceService maintenanceService;
    private final SacsReportService sacsReportService;

    public SacsMaintenanceReportController(SacsMaintenanceService maintenanceService,
                                           SacsReportService sacsReportService) {
        this.maintenanceService = maintenanceService;
        this.sacsReportService = sacsReportService;
    }

    /**
     * Endpoint principal: Se dispara al presionar "Apply Maintenance"
     * Registra en DB y rellena la plantilla Excel automáticamente.
     */
    @PostMapping("/apply/{id}")
    public ResponseEntity<?> applyMaintenance(@PathVariable String id) {
        try {
            // 1. Registrar el mantenimiento en la base de datos (Auditoría)
            maintenanceService.addSacsMaintenance(id);

            // 2. Ejecutar la automatización del Excel (Búsqueda de hueco y llenado)
            sacsReportService.fillMaintenanceReport(id);

            return ResponseEntity.ok(Map.of(
                    "message", "Mantenimiento registrado y Excel actualizado correctamente",
                    "excelId", id
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el reporte: " + e.getMessage());
        }
    }

    /**
     * Endpoint para actualizaciones manuales desde la tabla (Celdas editables)
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateRow(@RequestBody Map<String, Object> payload) {
        try {
            String excelId = (String) payload.get("excelId");
            @SuppressWarnings("unchecked")
            Map<String, String> values = (Map<String, String>) payload.get("values");

            maintenanceService.updateRow(excelId, values);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //DESCARGAR ARCHIVO DE REPORTE OTROSI-7

    @GetMapping("/export/otrosi7")
    public ResponseEntity<?> downloadReport() {
        try {
            Resource resource = sacsReportService.exportOtrosi7Report();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rms_sacs_otrosi7_doors_report.xlsx\"")
                    .body(resource);

        } catch (FileNotFoundException e) {
            // Enviamos un 404 con el mensaje de que el archivo no existe aún
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al descargar el reporte: " + e.getMessage());
        }
    }
}