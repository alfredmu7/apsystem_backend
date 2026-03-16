package com.example.ApSystem.repository.sacs;

import legacy.cctv_general_automation_report.Sacs_DB.SacsMaintenanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;



public interface SacsMaintenanceRepository extends JpaRepository<SacsMaintenanceEntity, Long> {

    boolean existsByExcelId(String excelId);

    Optional<SacsMaintenanceEntity> findByExcelId(String excelId);
}