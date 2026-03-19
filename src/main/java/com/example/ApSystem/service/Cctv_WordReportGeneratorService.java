package com.example.ApSystem.service;

import com.example.ApSystem.model.CctvMaintenanceRecord;
import com.example.ApSystem.repository.CctvMaintenanceRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class Cctv_WordReportGeneratorService {

    @Autowired
    private CctvMaintenanceRepository maintenanceRepo;

    private final String RUTA_BASE = "src/main/resources/reports/";
    private final String OTROSI_7 = RUTA_BASE + "informe_general_otrosi_7_cctv.docx";
    private final String OTROSI_20 = RUTA_BASE + "informe_mto_otro_si_20_cctv.docx";
    private final String SERVIDORES = RUTA_BASE + "informe_mto_servidores_cctv.docx";
    private final String EXTERIOR_CISA = RUTA_BASE + "informe_exterior_cisa.docx";

    public void agregarFilaAlInforme(CctvMaintenanceRecord record, String ubicacion, String infoClave) {
        // Determinamos la ruta basándonos en la información de la DB
        String rutaArchivo = determinarRuta(infoClave);
        System.out.println("Escribiendo ID " + record.getDispositivoId() + " en: " + rutaArchivo);

        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             XWPFDocument document = new XWPFDocument(fis)) {

            XWPFTable tablaObjetivo = null;
            for (XWPFTable tabla : document.getTables()) {
                if (!tabla.getRows().isEmpty() && tabla.getRow(0).getCell(0).getText().contains("ITEM")) {
                    tablaObjetivo = tabla;
                    break;
                }
            }

            if (tablaObjetivo != null) {
                if (idYaExisteEnTabla(tablaObjetivo, record.getDispositivoId())) {
                    return;
                }

                int siguienteItem = 1;
                for (int i = 1; i < tablaObjetivo.getRows().size(); i++) {
                    String textoItem = tablaObjetivo.getRow(i).getCell(0).getText().trim();
                    if (!textoItem.isEmpty()) {
                        siguienteItem++;
                    }
                }

                XWPFTableRow fila = obtenerFilaDisponible(tablaObjetivo);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                fila.getCell(0).setText(String.valueOf(siguienteItem));
                fila.getCell(1).setText(LocalDate.now().format(formatter));
                fila.getCell(2).setText("Mantenimiento Preventivo");
                fila.getCell(3).setText(record.getDispositivoId());
                fila.getCell(4).setText(ubicacion != null ? ubicacion : "N/A");
                fila.getCell(5).setText("Agregar foto");

                try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
                    document.write(fos);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al manipular el informe Word: " + e.getMessage());
        }
    }

    private String determinarRuta(String infoClave) {
        if (infoClave == null || infoClave.trim().isEmpty()) {
            return OTROSI_7;
        }

        // Normalizamos el texto: t0odo a mayúsculas y quitamos espacios raros
        String clave = infoClave.toUpperCase().trim();

        // Buscamos palabras clave sin importar dónde estén en el string
        if (clave.contains("SERVIDORES")) {
            return SERVIDORES;
        }
        if (clave.contains("EXTERIOR-CISA") || clave.contains("CISA")) {
            return EXTERIOR_CISA;
        }
        // 3. Prioridad OTRO SI 20 (Cubrimos todas las formas posibles de escribirlo)
        if (clave.contains("OTROSI 20") ||
                clave.contains("OTRO SI 20") ||
                clave.contains("OTROSI20") ||
                clave.contains("OTRO SI20")) {
            return OTROSI_20;
        }

        // Si no contiene ninguna, va al General
        return OTROSI_7;
    }

    private boolean idYaExisteEnTabla(XWPFTable tabla, String id) {
        for (XWPFTableRow fila : tabla.getRows()) {
            if (fila.getCell(3) != null && fila.getCell(3).getText().trim().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private XWPFTableRow obtenerFilaDisponible(XWPFTable tabla) {
        for (int i = 1; i < tabla.getRows().size(); i++) {
            XWPFTableRow fila = tabla.getRow(i);
            // Si la celda de ID está vacía, usamos esa fila
            if (fila.getCell(3).getText().trim().isEmpty()) {
                return fila;
            }
        }
        return tabla.createRow();
    }
}