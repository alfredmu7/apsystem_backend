package com.example.ApSystem.controller;

import com.example.ApSystem.model.CctvMaintenanceRecord;
import com.example.ApSystem.service.CctvMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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

    // Endpoint que activa el botón del Front
    @PostMapping("/execute")
    public ResponseEntity<CctvMaintenanceRecord> save(@RequestBody Map<String, String> body) {
        CctvMaintenanceRecord result = service.ejecutarMantenimiento(
                body.get("id"),
                body.get("observation"),
                body.get("technician")
        );
        return ResponseEntity.ok(result);
    }

    // Endpoint para la pestaña DEVICE
    @GetMapping("/history")
    public ResponseEntity<List<CctvMaintenanceRecord>> getHistory() {
        return ResponseEntity.ok(service.obtenerHistorialCctv());
    }

    @GetMapping("/report/download/{tipo}")
    public ResponseEntity<Resource> downloadReport(@PathVariable String tipo) {
        // Carpeta donde residen tus plantillas actualizadas por el ReportGeneratorService
        String rutaBase = "src/main/resources/reports/";
        String nombreArchivo = determinarNombreFisico(tipo);

        File file = new File(rutaBase + nombreArchivo);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .body(resource);
    }

    private String determinarNombreFisico(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "OTROSI7" -> "informe_general_otrosi_7_cctv.docx"; // [cite: 3]
            case "OTROSI20" -> "informe_mto_otro_si_20_cctv.docx"; // [cite: 5]
            case "SERVIDORES" -> "informe_mto_servidores_cctv.docx"; // [cite: 1]
            case "CISA" -> "informe_mto_exterior_cisa_cctv.docx";
            default -> throw new IllegalArgumentException("Tipo de reporte desconocido");
        };
    }
}