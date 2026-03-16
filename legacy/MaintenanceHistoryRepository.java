package com.example.ApSystem.repository.rms_vss_automation_report;
import com.example.ApSystem.model.rms_vss_automation_report.MaintenanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MaintenanceHistoryRepository extends JpaRepository<MaintenanceHistory, Long> {

    /**
     * Permite saber si ya existe un mantenimiento para un ID
     * dentro del mismo ciclo de 6 meses (política anti-duplicados).
     */
    Optional<MaintenanceHistory> findByDeviceIdAndCycle(String deviceId, Integer cycle);

    /**
     * Obtiene todos los mantenimientos de un día específico
     * → útil para generar la hoja del Excel por día.
     */
    List<MaintenanceHistory> findByMaintenanceDate(LocalDate maintenanceDate);

    /**
     * Obtiene todos los registros de un ciclo completo (6 meses)
     * → útil para exportar el reporte acumulado.
     */
    List<MaintenanceHistory> findByCycle(Integer cycle);

    /**
     * Para vista previa — trae ordenado por fecha.
     */
    List<MaintenanceHistory> findAllByOrderByMaintenanceDateAsc();
}
