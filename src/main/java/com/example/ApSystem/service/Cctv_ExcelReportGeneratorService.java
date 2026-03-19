package com.example.ApSystem.service;

import com.example.ApSystem.model.CctvMaintenanceRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class Cctv_ExcelReportGeneratorService {

    private final String RUTA_EXCEL = "src/main/resources/reports/reporte_rms_vss_cctv.xlsx";

    // Constantes de distancia (Offsets)
    private final int FILA_INICIO_BASE = 12;  // Fila 13
    private final int FILA_FECHA_BASE = 32;   // Fila 33
    private final int DISTANCIA_FILAS = 52;   // Salto entre bloques verticales

    private final int COL_IZQ_ID = 2;         // Columna C
    private final int COL_DER_ID = 29;        // Columna AD
    private final int COL_FECHA_IZQ = 15;     // Columna P
    private final int COL_FECHA_DER = 42;     // Columna AQ

    public void agregarRegistroAExcel(CctvMaintenanceRecord record, String fase, String ubicacion) {
        // Usamos try-with-resources para asegurar que el archivo se cierre incluso si hay error
        try (FileInputStream fis = new FileInputStream(RUTA_EXCEL);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // 1. ENCONTRAR EL BLOQUE DEL DÍA
            int[] bloque = encontrarBloqueDisponible(sheet, fechaHoy);

            int colId = bloque[0];
            int filaInicio = bloque[1];
            int filaFecha = bloque[2];
            int colFecha = bloque[3];

            // 2. BUSCAR ESPACIO PARA EL ITEM (1 a 15)
            int filaDestino = -1;
            for (int i = filaInicio; i < filaInicio + 15; i++) {
                Row row = sheet.getRow(i);
                if (row == null) row = sheet.createRow(i);
                Cell cell = row.getCell(colId);

                if (cell != null && cell.toString().equals(record.getDispositivoId())) return;

                if (cell == null || cell.toString().trim().isEmpty()) {
                    filaDestino = i;
                    break;
                }
            }

            // 3. ESCRIBIR DATOS
            if (filaDestino != -1) {
                Row row = sheet.getRow(filaDestino);
                escribirCelda(row, colId, record.getDispositivoId());
                escribirCelda(row, colId + 1, fase);
                escribirCelda(row, colId + 2, ubicacion);

                // Escribir la fecha del día
                Row rFecha = sheet.getRow(filaFecha);
                if (rFecha == null) rFecha = sheet.createRow(filaFecha);
                Cell cFecha = rFecha.getCell(colFecha);
                if (cFecha == null) cFecha = rFecha.createCell(colFecha);

                if (cFecha.toString().trim().isEmpty()) {
                    cFecha.setCellValue(fechaHoy);
                }
            }

            // --- MEJORAS PARA EVITAR ERRORES DE APERTURA EN EXCEL ---

            // Fuerza a Excel a recalcular todas las fórmulas al abrir el archivo
            workbook.setForceFormulaRecalculation(true);

            // Guardado robusto: Aseguramos que el flujo se limpie (flush) antes de cerrar
            try (FileOutputStream fos = new FileOutputStream(RUTA_EXCEL)) {
                workbook.write(fos);
                fos.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error crítico en el Excel RMS: " + e.getMessage());
        }
    }

    private int[] encontrarBloqueDisponible(Sheet sheet, String fechaHoy) {
        for (int nivel = 0; nivel < 20; nivel++) {
            int filaBaseNivel = FILA_INICIO_BASE + (nivel * DISTANCIA_FILAS);
            int filaFechaNivel = FILA_FECHA_BASE + (nivel * DISTANCIA_FILAS);

            if (esEsteBloque(sheet, filaFechaNivel, COL_FECHA_IZQ, fechaHoy)) {
                return new int[]{COL_IZQ_ID, filaBaseNivel, filaFechaNivel, COL_FECHA_IZQ};
            }
            if (esEsteBloque(sheet, filaFechaNivel, COL_FECHA_DER, fechaHoy)) {
                return new int[]{COL_DER_ID, filaBaseNivel, filaFechaNivel, COL_FECHA_DER};
            }
        }
        return new int[]{COL_IZQ_ID, FILA_INICIO_BASE, FILA_FECHA_BASE, COL_FECHA_IZQ};
    }

    private boolean esEsteBloque(Sheet sheet, int f, int c, String fechaHoy) {
        Row row = sheet.getRow(f);
        if (row == null) return true;
        Cell cell = row.getCell(c);
        if (cell == null || cell.toString().trim().isEmpty()) return true;

        // Manejo de comparación de fecha más robusto
        String valorCelda = cell.toString().trim();
        return valorCelda.equals(fechaHoy);
    }

    private void escribirCelda(Row row, int col, String val) {
        Cell cell = row.getCell(col);
        if (cell == null) cell = row.createCell(col);
        cell.setCellValue(val != null ? val : "");
    }
}