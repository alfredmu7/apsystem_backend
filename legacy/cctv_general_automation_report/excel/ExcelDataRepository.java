package legacy.cctv_general_automation_report.excel;

import legacy.cctv_general_automation_report.excel.ExcelDataEntity;
import legacy.cctv_general_automation_report.excel.ExcelDataId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExcelDataRepository extends JpaRepository<ExcelDataEntity, ExcelDataId> {

    // 1. Para buscar todas las filas de un solo ID (Soluciona error anterior)
    List<ExcelDataEntity> findByExcelId(String excelId);

    // 2. Para buscar filas de múltiples IDs a la vez (Soluciona tu error actual en la línea 98)
    List<ExcelDataEntity> findByExcelIdIn(List<String> excelIds);

    // 3. Para buscar la categoría del informe (Usado para los 5 Word)
    @Query("SELECT DISTINCT e.categoriaInforme FROM ExcelDataEntity e WHERE e.excelId = :excelId")
    String findCategoriaByExcelId(@Param("excelId") String excelId);

    // 4. Para buscar una columna específica
    Optional<ExcelDataEntity> findByExcelIdAndColumna(String excelId, String columna);

    // 5. Para actualizar valores específicos
    @Modifying
    @Query("UPDATE ExcelDataEntity e SET e.valor = :valor WHERE e.excelId = :excelId AND e.columna = :columna")
    void updateValue(@Param("excelId") String excelId,
                     @Param("columna") String columna,
                     @Param("valor") String valor);
}