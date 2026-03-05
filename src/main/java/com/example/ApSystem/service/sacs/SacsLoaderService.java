package com.example.ApSystem.service.sacs;

import com.example.ApSystem.model.Sacs_DB.SacsDataEntity;
import com.example.ApSystem.repository.sacs.SacsDataRepository;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SacsLoaderService {

    @Autowired
    private SacsDataRepository sacsRepo;

    private static final String COLUMNA_OBJETIVO = "ID PUERTA";

    @PostConstruct
    public void loadSacsExcel() {
        System.out.println("🏁 INICIANDO CARGA DE SACS (MODO ESCANEO TOTAL)...");
        try {
            sacsRepo.deleteAll();
            ClassPathResource resource = new ClassPathResource("excel/sacs_data_backend.xlsx");

            if (!resource.exists()) {
                System.err.println("❌ ERROR: No se encuentra el archivo.");
                return;
            }

            try (Workbook workbook = new XSSFWorkbook(resource.getInputStream())) {
                for (Sheet sheet : workbook) {
                    System.out.println("📄 Analizando Hoja: " + sheet.getSheetName());

                    Cell ubicacionId = buscarCeldaObjetivo(sheet, COLUMNA_OBJETIVO);

                    // Si no encuentra "ID PUERTA", intentamos buscar "I.D." o "ID" (Común en Citofonía)
                    if (ubicacionId == null) {
                        ubicacionId = buscarCeldaObjetivo(sheet, "I.D.");
                    }

                    if (ubicacionId == null) {
                        System.out.println("   ⚠️ No se encontró columna de ID en '" + sheet.getSheetName() + "'. Saltando...");
                        continue;
                    }

                    int headerRowNum = ubicacionId.getRowIndex();
                    int idColIndex = ubicacionId.getColumnIndex();

                    System.out.println("   ✅ ¡COLUMNA DETECTADA! en Fila " + headerRowNum + ", Columna " + idColIndex);

                    Row headerRow = sheet.getRow(headerRowNum);
                    List<String> headers = new ArrayList<>();
                    for (Cell cell : headerRow) {
                        headers.add(getCleanCellValue(cell));
                    }

                    int count = 0;
                    for (int i = headerRowNum + 1; i <= sheet.getLastRowNum(); i++) {
                        Row row = sheet.getRow(i);
                        if (row == null) continue;

                        // --- LÓGICA DE RESCATE PARA FILA 958+ ---
                        // ... dentro del bucle de filas en SacsLoaderService ...

                        // 1. Obtenemos el ID y SOLO limpiamos espacios al inicio y al final
                        String excelId = getCleanCellValue(row.getCell(idColIndex)).trim();

                        // 2. Lógica de rescate si la columna principal está vacía (común después de fila 958)
                        if (excelId.isEmpty()) {
                            for (int colIndex = 0; colIndex < 5; colIndex++) {
                                String testId = getCleanCellValue(row.getCell(colIndex)).trim();
                                // Criterio: que tenga longitud y no sea solo un número de ítem
                                if (testId.length() >= 3 && !testId.matches("\\d+")) {
                                    excelId = testId;
                                    break;
                                }
                            }
                        }

                        if (excelId.isEmpty()) continue;

                        // 3. Guardar con el ID TAL CUAL viene (preservando espacios internos si los hay)
                        for (int j = 0; j < headers.size(); j++) {
                            if (headers.get(j).isEmpty()) continue;
                            SacsDataEntity data = new SacsDataEntity();
                            data.setExcelId(excelId);
                            data.setColumna(headers.get(j));
                            data.setValor(getCleanCellValue(row.getCell(j)));
                            sacsRepo.save(data);
                        }
                        count++;
                    }
                    System.out.println("   🎉 Se extrajeron " + count + " registros de: " + sheet.getSheetName());
                }
            }
            System.out.println("✅ PROCESO COMPLETADO. Revisa tu Front-end.");
        } catch (Exception e) {
            System.err.println("🔥 ERROR CRÍTICO: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Cell buscarCeldaObjetivo(Sheet sheet, String textoBuscar) {
        for (int i = 0; i <= Math.min(sheet.getLastRowNum(), 60); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            for (Cell cell : row) {
                String val = getCleanCellValue(cell).toUpperCase();
                if (val.equals(textoBuscar.toUpperCase()) || val.startsWith(textoBuscar.toUpperCase())) {
                    return cell;
                }
            }
        }
        return null;
    }

    private String getCleanCellValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        String val = formatter.formatCellValue(cell).trim();
        if (cell.getCellType() == CellType.NUMERIC && val.endsWith(".0")) {
            val = val.substring(0, val.length() - 2);
        }
        return val;
    }
}