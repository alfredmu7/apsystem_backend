package com.example.ApSystem.controller.cctv_general_automation_report;

import com.example.ApSystem.model.rms_vss_automation_report.MaintenanceHistory;
import com.example.ApSystem.repository.excel.ExcelDataRepository;
import com.example.ApSystem.service.cctv_general_automation_report.WordReportService;

// CORRECCIÓN AQUÍ: Cambiar jakarta.annotation.Resource por este:
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/reports/word")
@CrossOrigin(origins = "*")
public class WordReportController {

    @Autowired
    private WordReportService wordService;

    @Autowired
    private ExcelDataRepository excelRepo;
    /**
     * Sincroniza el mantenimiento.
     * Ahora el servicio se encargará de detectar la categoría del ID
     * para saber en cuál de los 5 archivos escribir.
     */
    @PostMapping("/sync")
    public ResponseEntity<String> syncMaintenance(@RequestBody MaintenanceHistory mh) {
        // 1. Necesitamos el repositorio de Excel para saber la categoría del ID
        // Asegúrate de inyectar 'excelRepo' en este controlador si no está
        String categoria = excelRepo.findCategoriaByExcelId(mh.getDeviceId());

        if (categoria == null) categoria = "GENERAL";

        // 2. Llamar al metodo con AMBOS parámetros
        // Ajusta el nombre según como lo hayas dejado en el Service
        wordService.addEntryToSpecificReport(mh, categoria);

        return ResponseEntity.ok("Word successfully updated in category: " + categoria);
    }

    /**
     * Descarga el informe específico según el tipo solicitado desde el Front.
     * @param type Puede ser: "T2", "CISA", "SERVIDORES", "OTROSI-20" o "GENERAL"
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadReport(@RequestParam String type) {
        String fileName = getFileNameByType(type);

        // 1. Intentar buscar en la carpeta física "reports" (donde se guardan los actualizados)
        File updatedFile = new File("reports/" + fileName);

        // 2. Intentar buscar en tu carpeta física "excel" (donde dices que están)
        File templateFile = new File("excel/" + fileName);

        Resource resource = null;

        if (updatedFile.exists()) {
            resource = new FileSystemResource(updatedFile);
            System.out.println("Descargando desde reports: " + updatedFile.getAbsolutePath());
        } else if (templateFile.exists()) {
            resource = new FileSystemResource(templateFile);
            System.out.println("Descargando desde carpeta excel: " + templateFile.getAbsolutePath());
        } else {
            // 3. Último recurso: buscar dentro del JAR (resources)
            resource = new ClassPathResource("excel/" + fileName);
        }

        if (!resource.exists()) {
            System.err.println("ERROR: No se encontró " + fileName + " en ninguna ruta.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * Helper para determinar el nombre del archivo según la categoría
     */
    private String getFileNameByType(String type) {
        if (type == null) return "informe_general_cctv_general.docx";

        switch (type.toUpperCase()) {
            case "T2": return "informe_t2.docx";
            case "EXTERIOR-CISA": return "informe_exterior_cisa.docx";
            case "SERVIDORES": return "informe_servidores.docx";
            case "OTROSI-20":
                // ASEGÚRATE que en la carpeta 'excel' el archivo se llame así:
                return "informe_otrosi_20.docx";
            default:
                return "informe_general_cctv_general.docx";
        }
    }
}