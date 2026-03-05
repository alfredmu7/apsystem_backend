package com.example.ApSystem.repository.sacs;

import com.example.ApSystem.model.Sacs_DB.SacsDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SacsDataRepository extends JpaRepository<SacsDataEntity, Long> {

    /**
     * Busca todas las filas de un dispositivo por su excelId.
     * Útil para obtener Ubicación, Código, etc.
     */
    List<SacsDataEntity> findByExcelId(String excelId);


    // Buscar información de varios IDs a la vez
    // Esto es vital para cargar la tabla masiva de mantenimiento
    List<SacsDataEntity> findByExcelIdIn(List<String> excelIds);

    /**
     * Busca una celda específica (ID + Columna).
     * Se usará para las ediciones directas en la tabla.
     */
    Optional<SacsDataEntity> findByExcelIdAndColumna(String excelId, String columna);
}