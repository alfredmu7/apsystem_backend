package com.example.ApSystem.service.rms_vss_automation_report;

import com.example.ApSystem.model.rms_vss_automation_report.MaintenanceHistory;
import com.example.ApSystem.repository.excel.ExcelDataRepository;
import com.example.ApSystem.service.cctv_general_automation_report.WordReportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ReportExcelService {

    private static final String BASE_TEMPLATE = "excel/rms_vss_automation_report.xlsx";
    private static final String REPORT_FOLDER = "reports/";
    private static final int ROWS_PER_TEMPLATE = 35;
    private static final int DATE_ROW_OFFSET = 3;
    private static final int DATA_START_OFFSET = 16;

    @Autowired
    private WordReportService wordReportService; // Inyectamos el nuevo servicio de Word

    @Autowired
    private ExcelDataRepository excelRepo;

    public void applyMaintenance(MaintenanceHistory mh) {
        try {
            File reportFile = selectReportFile(mh.getCycle());
            Workbook workbook = loadOrCreateWorkbook(reportFile);
            Sheet sheet = workbook.getSheetAt(0);

            if (!workbook.getSheetName(0).equals("REPORTE_VSS")) {
                workbook.setSheetName(0, "REPORTE_VSS");
            }

            // 1. Buscar o Crear el bloque con posicionamiento dinámico
            int blockStart = findOrCreateDayBlock(sheet, mh);

            // 2. Escribir la fecha y datos
            writeDate(sheet, blockStart, mh);
            addRowToBlock(sheet, blockStart, mh);

            saveWorkbook(workbook, reportFile);
            workbook.close();

            // --- PARTE 2: WORD (La integración) ---
            // 1. Buscamos la categoría del ID en la base de datos (usando el excelRepo que ya tienes inyectado)
            String categoria = excelRepo.findCategoriaByExcelId(mh.getDeviceId());

// 2. Si por alguna razón el ID no tiene categoría asignada, usamos "GENERAL"
            if (categoria == null) {
                categoria = "GENERAL";
            }

// 3. Ahora llamamos al metodo pasando AMBOS parámetros: el objeto mh Y la categoría
// Esto resuelve el error "argument lists differ in length"
            wordReportService.addEntryToSpecificReport(mh, categoria);

            System.out.println("DEBUG: Excel y Word (" + categoria + ") actualizados para el ID: " + mh.getDeviceId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en el proceso de mantenimiento: " + e.getMessage());
        }
    }

    private int findOrCreateDayBlock(Sheet sheet, MaintenanceHistory mh) {
        String targetDate = mh.getMaintenanceDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // BUSCADOR ROBUSTO: Recorre el archivo buscando la celda de fecha
        for (Row row : sheet) {
            Cell cell = row.getCell(2); // Columna C
            if (cell != null && cell.toString().contains(targetDate)) {
                return row.getRowNum() - DATE_ROW_OFFSET;
            }
        }

        // Si no existe, calculamos el final real para no encimar
        int nextStart;
        // Revisamos si la primera página ya tiene datos en la fila 17
        Row firstPageFirstData = sheet.getRow(DATA_START_OFFSET);
        if (firstPageFirstData == null || isCellEmpty(firstPageFirstData.getCell(2))) {
            nextStart = 0;
        } else {
            // El nuevo bloque empieza 5 filas después de la última fila con contenido
            // Esto evita que se pegue al cuadro de firmas anterior
            nextStart = sheet.getLastRowNum() + 6;

            copyTemplateRange(sheet, 0, ROWS_PER_TEMPLATE - 1, nextStart);
            sheet.setRowBreak(nextStart - 1); // Salto de página para impresión limpia
        }

        return nextStart;
    }

    private void addRowToBlock(Sheet sheet, int blockStart, MaintenanceHistory mh) {
        int firstDataRow = blockStart + DATA_START_OFFSET;
        int currentRow = firstDataRow;

        // Buscar la primera fila vacía dentro del bloque actual
        // (Buscamos hasta encontrar una celda vacía en la columna C)
        while (true) {
            Row row = sheet.getRow(currentRow);
            if (row == null) break;
            Cell cellId = row.getCell(2);
            if (isCellEmpty(cellId)) break;
            currentRow++;
        }

        // LÓGICA DE INSERCIÓN: Si vamos a escribir más allá de la fila 14 del bloque (fila 30 original)
        // El bloque original termina en blockStart + 34
        int blockEndOriginal = blockStart + 34;

        // Si estamos cerca del pie de página (firmas), insertamos fila
        if (currentRow >= blockStart + 29) {
            sheet.shiftRows(currentRow, sheet.getLastRowNum(), 1, true, false);
            sheet.createRow(currentRow);
        }

        Row row = sheet.getRow(currentRow);
        if (row == null) row = sheet.createRow(currentRow);

        // Numeración automática de Item
        int itemNumber = (currentRow - firstDataRow) + 1;
        getCell(row, 1).setCellValue(itemNumber);

        // Copiar estilo de la fila superior para mantener bordes y fuente
        copyRowStyle(sheet.getRow(currentRow - 1), row);
        fillRowData(row, mh);
    }

    private void copyTemplateRange(Sheet sheet, int srcStart, int srcEnd, int destStart) {
        for (int i = srcStart; i <= srcEnd; i++) {
            Row srcRow = sheet.getRow(i);
            int destRowNum = destStart + (i - srcStart);
            Row destRow = sheet.createRow(destRowNum);

            if (srcRow != null) {
                destRow.setHeight(srcRow.getHeight());
                for (int j = 0; j < srcRow.getLastCellNum(); j++) {
                    Cell srcCell = srcRow.getCell(j);
                    if (srcCell != null) {
                        Cell destCell = destRow.createCell(j);
                        copyCellFormatAndValue(srcCell, destCell);
                    }
                }
            }
        }

        // Clonar regiones combinadas (Crucial para que el encabezado no se vea roto)
        // Usamos un contador fijo para evitar bucles infinitos si la hoja crece
        int regionsCount = sheet.getNumMergedRegions();
        for (int i = 0; i < regionsCount; i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);

            // Verificamos si la región pertenece al bloque de origen (la plantilla)
            if (region.getFirstRow() >= srcStart && region.getLastRow() <= srcEnd) {
                int newFirstRow = destStart + (region.getFirstRow() - srcStart);
                int newLastRow = destStart + (region.getLastRow() - srcStart);

                CellRangeAddress newRegion = new CellRangeAddress(
                        newFirstRow,
                        newLastRow,
                        region.getFirstColumn(),
                        region.getLastColumn()
                );

                // Validar que no exista ya en el destino para evitar errores de solapamiento
                sheet.addMergedRegion(newRegion);
            }
        }
    }

    private void copyCellFormatAndValue(Cell src, Cell dest) {
        dest.setCellStyle(src.getCellStyle());
        switch (src.getCellType()) {
            case STRING -> dest.setCellValue(src.getStringCellValue());
            case NUMERIC -> dest.setCellValue(src.getNumericCellValue());
            case BOOLEAN -> dest.setCellValue(src.getBooleanCellValue());
            case FORMULA -> dest.setCellFormula(src.getCellFormula());
            default -> {}
        }
    }

    private void writeDate(Sheet sheet, int blockStart, MaintenanceHistory mh) {
        int rowIdx = blockStart + DATE_ROW_OFFSET;
        Row row = sheet.getRow(rowIdx);
        if (row == null) row = sheet.createRow(rowIdx);
        Cell cell = getCell(row, 2);
        cell.setCellValue(mh.getMaintenanceDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    // --- MÉTODOS AUXILIARES (Sin cambios significativos) ---

    private void copyRowStyle(Row sourceRow, Row targetRow) {
        if (sourceRow == null) return;
        targetRow.setHeight(sourceRow.getHeight());
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell oldCell = sourceRow.getCell(i);
            if (oldCell != null) {
                getCell(targetRow, i).setCellStyle(oldCell.getCellStyle());
            }
        }
    }

    private void fillRowData(Row row, MaintenanceHistory mh) {
        row.getSheet().setForceFormulaRecalculation(true);
        getCell(row, 2).setCellValue(mh.getDeviceId());
        getCell(row, 3).setCellValue(safe(mh.getPhase()));
        getCell(row, 4).setCellValue(safe(mh.getLocation()));
        getCell(row, 14).setCellValue(safe(mh.getObservation()));
        int c = 6;
        getCell(row, c++).setCellValue(toCheck(mh.getEstadoInicial()));
        getCell(row, c++).setCellValue(toCheck(mh.getInspVisual()));
        getCell(row, c++).setCellValue(toCheck(mh.getMontajeMastil()));
        getCell(row, c++).setCellValue(toCheck(mh.getConexionesFacePlate()));
        getCell(row, c++).setCellValue(toCheck(mh.getLimpiezaLente()));
        getCell(row, c++).setCellValue(toCheck(mh.getHousing()));
        getCell(row, c++).setCellValue(toCheck(mh.getEstadoFinal()));
        getCell(row, 20).setCellValue(toCheck(mh.getFoco()));
        getCell(row, 21).setCellValue(toCheck(mh.getIris()));
        getCell(row, 22).setCellValue(toCheck(mh.getNitidez()));
    }

    private Workbook loadOrCreateWorkbook(File file) throws Exception {
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return new XSSFWorkbook(fis);
            }
        }
        InputStream is = getClass().getClassLoader().getResourceAsStream(BASE_TEMPLATE);
        if (is == null) throw new FileNotFoundException("Plantilla no encontrada");
        return new XSSFWorkbook(is);
    }

    private File selectReportFile(Integer cycle) {
        String filename = (cycle == 1) ? "vss_manto_1.xlsx" : "vss_manto_2.xlsx";
        File file = new File(REPORT_FOLDER + filename);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        return file;
    }

    private Cell getCell(Row row, int col) {
        Cell c = row.getCell(col);
        return (c == null) ? row.createCell(col) : c;
    }

    private boolean isCellEmpty(Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK || cell.toString().trim().isEmpty();
    }

    private String safe(String s) { return s == null ? "" : s; }
    private String toCheck(Boolean b) { return Boolean.TRUE.equals(b) ? "✔" : ""; }

    private void saveWorkbook(Workbook wb, File file) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            wb.write(fos);
        }
    }

    public byte[] exportRange(LocalDate from, LocalDate to, Integer cycle) {
        try {
            File reportFile = selectReportFile(cycle);
            if (!reportFile.exists()) throw new IllegalStateException("Archivo no generado");
            try (FileInputStream fis = new FileInputStream(reportFile)) {
                Workbook original = new XSSFWorkbook(fis);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                original.write(baos);
                original.close();
                return baos.toByteArray();
            }
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}