package legacy.cctv_general_automation_report.excel;

import legacy.cctv_general_automation_report.excel.ExcelDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExcelRepository extends JpaRepository<ExcelDataEntity, Long> {
    Optional<ExcelDataEntity> findByExcelIdAndColumna(String excelId, String columna);
}
