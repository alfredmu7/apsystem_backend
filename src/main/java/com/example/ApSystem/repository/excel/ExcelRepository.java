package com.example.ApSystem.repository.excel;

import com.example.ApSystem.model.excel.ExcelDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExcelRepository extends JpaRepository<ExcelDataEntity, Long> {
    Optional<ExcelDataEntity> findByExcelIdAndColumna(String excelId, String columna);
}
