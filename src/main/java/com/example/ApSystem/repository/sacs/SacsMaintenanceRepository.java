package com.example.ApSystem.repository.sacs;

import com.example.ApSystem.model.Sacs_DB.SacsMaintenanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SacsMaintenanceRepository extends JpaRepository<SacsMaintenanceEntity, Long> {

    boolean existsByExcelId(String excelId);

    Optional<SacsMaintenanceEntity> findByExcelId(String excelId);
}