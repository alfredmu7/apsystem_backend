package com.example.ApSystem.controller;

import com.example.ApSystem.model.CctvMaintenanceRecord;
import com.example.ApSystem.service.CctvMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cctv/maintenance")
@CrossOrigin(origins = "*")
public class CctvMaintenanceController {

    @Autowired
    private CctvMaintenanceService service;

    @PostMapping("/execute")
    public ResponseEntity<CctvMaintenanceRecord> save(@RequestBody Map<String, String> body) {
        CctvMaintenanceRecord result = service.ejecutarMantenimiento(
                body.get("id"),
                body.get("observation"),
                body.get("technician")
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<CctvMaintenanceRecord>> getHistory() {
        return ResponseEntity.ok(service.obtenerHistorialCctv());
    }

    @GetMapping("/report/download/{tipo}")
    public ResponseEntity<Resource> downloadReport(@PathVariable String tipo) {
        String rutaBase = "src/main/resources/reports/";
        String nombreArchivo = determinarNombreFisico(tipo);

        File file = new File(rutaBase + nombreArchivo);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        // DETERMINAR EL MEDIA TYPE (Diferenciar entre Word y Excel)
        String contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"; // Default Word
        if (nombreArchivo.endsWith(".xlsx")) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(resource);
    }

    private String determinarNombreFisico(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "OTROSI7" -> "informe_general_otrosi_7_cctv.docx";
            case "OTROSI20" -> "informe_mto_otro_si_20_cctv.docx";
            case "SERVIDORES" -> "informe_mto_servidores_cctv.docx";
            case "CISA" -> "informe_exterior_cisa_cctv.docx";
            case "EXCEL_RMS" -> "reporte_rms_vss_cctv.xlsx";  // El nuevo reporte Excel
            default -> throw new IllegalArgumentException("Tipo de reporte desconocido: " + tipo);
        };
    }
}