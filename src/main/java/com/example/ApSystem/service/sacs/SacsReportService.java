package com.example.ApSystem.service.sacs;

import com.example.ApSystem.model.Sacs_DB.SacsDataEntity;
import com.example.ApSystem.repository.sacs.SacsDataRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SacsReportService {

    private final SacsDataRepository sacsRepo;

    private static final String BASE_TEMPLATE = "excel/rms_sacs_door.xlsx";
    private static final String REPORT_FOLDER = "reports/rms_sacs_otrosi7_door/";
    private static final String FIXED_FILENAME = "sacs_reporte_general.xlsx";

    // --- CONFIGURACIÓN DE MATRIZ (2 columnas x N filas) ---
    private static final int COL_IZQUIERDA = 1;       // B
    private static final int COL_DERECHA = 31;        // AF
    private static final int ROW_INICIAL_D1 = 8;      // Fila 9
    private static final int ROW_INICIAL_D2 = 26;     // Fila 27
    private static final int SALTO_FILAS_BLOQUE = 50; // De Fila 9 a Fila 59

    private static final int TOTAL_PLANTILLAS = 65;

    public SacsReportService(SacsDataRepository sacsRepo) {
        this.sacsRepo = sacsRepo;
    }

    public void fillMaintenanceReport(String targetExcelId) throws IOException {
        File reportFile = new File(REPORT_FOLDER + FIXED_FILENAME);
        Workbook workbook = loadOrCreateWorkbook(reportFile);
        Sheet sheet = workbook.getSheetAt(0);

        List<SacsDataEntity> deviceData = sacsRepo.findByExcelId(targetExcelId);
        String ubicacion = deviceData.stream()
                .filter(d -> d.getColumna().trim().equalsIgnoreCase("UBICACIÓN"))
                .map(SacsDataEntity::getValor)
                .findFirst().orElse("N/A");

        boolean written = false;

        // Iteramos por el número de plantilla (1, 2, 3...)
        for (int p = 0; p < TOTAL_PLANTILLAS; p++) {
            // CÁLCULO DE POSICIÓN EN MATRIZ
            // Si p es par (0, 2, 4...) -> Columna Izquierda. Si es impar (1, 3, 5...) -> Columna Derecha
            int columnaActual = (p % 2 == 0) ? COL_IZQUIERDA : COL_DERECHA;

            // Cada 2 plantillas, bajamos un "piso" de 50 filas
            int filaOffset = (p / 2) * SALTO_FILAS_BLOQUE;

            int filaD1 = ROW_INICIAL_D1 + filaOffset;
            int filaD2 = ROW_INICIAL_D2 + filaOffset;

            // Intento en Puerta 1 de la plantilla actual
            if (isCellAvailable(sheet, filaD1, columnaActual, targetExcelId)) {
                writeDoorData(sheet, filaD1, columnaActual, targetExcelId, ubicacion);
                written = true;
            }
            // Intento en Puerta 2 de la plantilla actual
            else if (isCellAvailable(sheet, filaD2, columnaActual, targetExcelId)) {
                writeDoorData(sheet, filaD2, columnaActual, targetExcelId, ubicacion);
                written = true;
            }

            if (written) break;
        }

        if (written) saveWorkbook(workbook, reportFile);
        workbook.close();
    }

    private void writeDoorData(Sheet sheet, int baseRow, int baseCol, String id, String ubi) {
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // 1. Identificación y Ubicación (Ejem: B60 y C60)
        setCellValue(sheet, baseRow, baseCol, id);
        setCellValue(sheet, baseRow, baseCol + 1, ubi);

        // 2. Gestión de Fechas Relativas
        // Escribimos la fecha SOLO en la fila inferior (+32) para respetar el texto "Fecha" en la (+31)
        // Si baseRow es 59 (Fila 60), esto escribe en la Fila 92 (X92 / AR92)
        setCellValue(sheet, baseRow + 32, baseCol + 22, fechaHoy);

        // Fecha de inspección individual dentro de la tabla (K68 / AO68 relativo)
        setCellValue(sheet, baseRow + 9, baseCol + 9, fechaHoy);

        // 3. LLENADO MASIVO DE CHULITOS
        int firstItemRow = baseRow + 1; // Fila inmediatamente debajo del ID (Fila 61 si base es 60)

        // Bloque Vertical: Columnas I, J, K, L, M (del ítem 1 al 16)
        // Desplazamientos desde baseCol (B=1 o AF=31): I=7, J=8, K=9, L=10, M=11
        for (int i = 0; i < 16; i++) {
            int currentRow = firstItemRow + i;
            setCellValue(sheet, currentRow, baseCol + 7,  "✔"); // Columna I
            setCellValue(sheet, currentRow, baseCol + 8,  "✔"); // Columna J
            setCellValue(sheet, currentRow, baseCol + 9,  "✔"); // Columna K
            setCellValue(sheet, currentRow, baseCol + 10, "✔"); // Columna L
            setCellValue(sheet, currentRow, baseCol + 11, "✔"); // Columna M
        }

        // Bloque Horizontal: Columnas N a V (Solo para la fila del primer ítem)
        // Desplazamientos desde baseCol: N=12, O=13, P=14, Q=15, R=16, S=17, T=18, U=19, V=20
        for (int c = 12; c <= 20; c++) {
            setCellValue(sheet, firstItemRow, baseCol + c, "✔");
        }
    }

    // --- MÉTODOS DE SOPORTE ---

    private Workbook loadOrCreateWorkbook(File file) throws IOException {
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return new XSSFWorkbook(fis);
            }
        }
        InputStream is = getClass().getClassLoader().getResourceAsStream(BASE_TEMPLATE);
        if (is == null) throw new FileNotFoundException("No se halló la plantilla");
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        return new XSSFWorkbook(is);
    }

    private void saveWorkbook(Workbook wb, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wb.write(fos);
            fos.flush();
        }
    }

    private boolean isCellAvailable(Sheet sheet, int r, int c, String targetId) {
        Row row = sheet.getRow(r);
        if (row == null) return true;
        Cell cell = row.getCell(c);
        if (cell == null || cell.toString().trim().isEmpty()) return true;
        return cell.toString().trim().equalsIgnoreCase(targetId.trim());
    }

    private void setCellValue(Sheet sheet, int r, int c, String value) {
        Row row = sheet.getRow(r);
        if (row == null) row = sheet.createRow(r);
        Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(value);
    }

    public Resource exportOtrosi7Report() throws MalformedURLException, FileNotFoundException {
        Path path = Paths.get(REPORT_FOLDER + FIXED_FILENAME);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() && resource.isReadable()) return resource;
        else throw new FileNotFoundException("Reporte no encontrado");
    }
}