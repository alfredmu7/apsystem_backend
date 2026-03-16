package com.example.ApSystem.service.rms_vss_automation_report;

import com.example.ApSystem.dto.rms_vss_automation_report.RegisterMaintenanceRequest;
import com.example.ApSystem.model.rms_vss_automation_report.MaintenanceHistory;
import com.example.ApSystem.repository.rms_vss_automation_report.MaintenanceHistoryRepository;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.Optional;

public class MaintenanceService {

    private final MaintenanceHistoryRepository historyRepo;
    private final DeviceBaseDataService deviceBaseDataService;
    private final ReportExcelService reportExcelService;

    public MaintenanceService(
            MaintenanceHistoryRepository historyRepo,
            DeviceBaseDataService deviceBaseDataService,
            ReportExcelService reportExcelService
    ) {
        this.historyRepo = historyRepo;
        this.deviceBaseDataService = deviceBaseDataService;
        this.reportExcelService = reportExcelService;
    }

    @Transactional
    public MaintenanceHistory registerMaintenance(RegisterMaintenanceRequest req) {
        LocalDate today = LocalDate.now();

        // Validación: Solo permitir hoy o fechas futuras
        if (req.getMaintenanceDate().isBefore(today)) {
            throw new IllegalArgumentException("No se permite registrar mantenimientos con fecha anterior a hoy.");
        }

        Integer cycle = computeCycle(req.getMaintenanceDate());

        Optional<MaintenanceHistory> existing =
                historyRepo.findByDeviceIdAndCycle(req.getDeviceId(), cycle);

        if (existing.isPresent() && !req.isForceInsert()) {
            return existing.get();
        }

        DeviceBaseDataService.DeviceBaseData data =
                deviceBaseDataService.getBaseDataByDeviceId(req.getDeviceId());

        MaintenanceHistory history = new MaintenanceHistory(
                req.getDeviceId(),
                req.getMaintenanceDate(),
                cycle,
                data.phase(),
                data.location(),
                req.getObservation(),
                true, true, true,
                true, true, true,
                true, true, true, true,
                req.getOperator()
        );

        MaintenanceHistory saved = historyRepo.save(history);

        // Actualiza el Excel (ahora con lógica de bloque continuo)
        reportExcelService.applyMaintenance(saved);

        return saved;
    }



    // ------------ MeTODO DE SOPORTE ------------
    private Integer computeCycle(LocalDate date) {
        int month = date.getMonthValue();
        return (month <= 6) ? 1 : 2;
    }


    public byte[] exportExcel(LocalDate from, LocalDate to, Integer cycle) {


        if (to.isBefore(from)) {
            throw new IllegalArgumentException("La fecha final no puede ser menor que la inicial");
        }

        // verificar ciclo
        Integer cycleStart = computeCycle(from);
        Integer cycleEnd = computeCycle(to);

        if (!cycleStart.equals(cycleEnd)) {
            throw new IllegalArgumentException("El rango abarca 2 ciclos diferentes. Exportar por ciclo");
        }

        return reportExcelService.exportRange(from, to, cycle);
    }


}



