package com.example.ApSystem.controller.rms_vss_automation_report;

import com.example.ApSystem.dto.rms_vss_automation_report.RegisterMaintenanceRequest;
import com.example.ApSystem.model.rms_vss_automation_report.MaintenanceHistory;
import com.example.ApSystem.service.rms_vss_automation_report.MaintenanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/maintenance_rms_vss")
public class MaintenanceController {

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }


    private final MaintenanceService maintenanceService;

    @PostMapping
    public MaintenanceHistory register(@RequestBody RegisterMaintenanceRequest req) {
        return maintenanceService.registerMaintenance(req);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam("cycle") Integer cycle
    ) {
        byte[] file = maintenanceService.exportExcel(from, to, cycle);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"vss_export.xlsx\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok().headers(headers).body(file);
    }



    @GetMapping("/download-current")
    public ResponseEntity<byte[]> downloadCurrent() throws IOException {
        // Detectamos el ciclo actual para saber qué archivo buscar
        int month = LocalDate.now().getMonthValue();
        int cycle = (month <= 6) ? 1 : 2;
        String filename = (cycle == 1) ? "vss_manto_1.xlsx" : "vss_manto_2.xlsx";

        File file = new File("reports/" + filename);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] data = Files.readAllBytes(file.toPath());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        return ResponseEntity.ok().headers(headers).body(data);
    }


}
