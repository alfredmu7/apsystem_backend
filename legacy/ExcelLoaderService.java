package com.example.ApSystem.service.excel;

import legacy.cctv_general_automation_report.excel.ExcelDataEntity;
import legacy.cctv_general_automation_report.excel.ExcelDataRepository;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;

public class ExcelLoaderService {

    private final ExcelDataRepository excelRepo;

    public ExcelLoaderService(ExcelDataRepository excelRepo) {
        this.excelRepo = excelRepo;
    }

    /**
     * Se ejecuta automáticamente al iniciar Spring Boot.
     * Carga el Excel desde resources y guarda la información
     * de forma dinámica en la base de datos.
     */
    @PostConstruct
    public void loadExcelOnStartup() {
        try {
            excelRepo.deleteAll();
            ClassPathResource resource = new ClassPathResource("excel/excel_cctv_backend.xlsx");
            Workbook workbook = new XSSFWorkbook(resource.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            Row headerRow = findHeaderRow(sheet);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) headers.add(cell.toString().trim());

            for (int i = headerRow.getRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell idCell = row.getCell(0);
                if (idCell == null || idCell.toString().isBlank()) continue;
                String excelId = idCell.toString().trim();

                // 1️⃣ DETERMINAR CATEGORÍA ESCANEANDO TODA LA FILA
                String categoria = determinarCategoria(row);

                // 2️⃣ GUARDAR TODAS LAS COLUMNAS CON ESA CATEGORÍA
                for (int j = 1; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String valor = (cell == null) ? "" : cell.toString().trim();

                    ExcelDataEntity data = new ExcelDataEntity();
                    data.setExcelId(excelId);
                    data.setColumna(headers.get(j));
                    data.setValor(valor);
                    data.setCategoriaInforme(categoria); // Etiqueta asignada

                    excelRepo.save(data);
                }
            }
            workbook.close();
        } catch (Exception e) { throw new RuntimeException("Error cargando Excel", e); }
    }

    private String determinarCategoria(Row row) {
        StringBuilder contenidoFila = new StringBuilder();
        for (Cell cell : row) {
            contenidoFila.append(cell.toString().toUpperCase()).append(" ");
        }

        String texto = contenidoFila.toString();

        if (texto.contains("T2")) return "T2";
        if (texto.contains("EXTERIOR-CISA") || texto.contains("CISA")) return "CISA";
        if (texto.contains("SERVIDORES")) return "SERVIDORES";
        if (texto.contains("OTROSI 20") || texto.contains("OTROSI")) return "OTROSI";

        return "GENERAL"; // Si no encuentra ninguna palabra clave
    }


    /**
     * Busca automáticamente la fila que contiene los encabezados del Excel.
     * Permite que el archivo tenga títulos, filas vacías o formatos variables
     * sin romper el backend.
     */
    private Row findHeaderRow(Sheet sheet) {

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null) continue;

            int nonEmptyCells = 0;

            for (Cell cell : row) {
                if (!cell.toString().isBlank()) {
                    nonEmptyCells++;
                }
            }

            // Heurística: una fila con varias celdas no vacías es encabezado
            if (nonEmptyCells >= 3) {
                return row;
            }
        }

        throw new RuntimeException("No se encontró la fila de encabezados en el Excel");
    }
}
