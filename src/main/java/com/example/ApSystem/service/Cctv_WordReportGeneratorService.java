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

@Service // Vital para que @Autowired funcione en otros servicios
public class Cctv_WordReportGeneratorService {

    @Autowired
    private CctvMaintenanceRepository maintenanceRepo;

    // Rutas de archivos basadas en tu estructura
    private final String RUTA_BASE = "src/main/resources/reports/";
    private final String OTROSI_7 = RUTA_BASE + "informe_general_otrosi_7_cctv.docx";
    private final String OTROSI_20 = RUTA_BASE + "informe_mto_otro_si_20_cctv.docx";
    private final String SERVIDORES = RUTA_BASE + "informe_mto_servidores_cctv.docx";
    private final String EXTERIOR_CISA = RUTA_BASE + "informe_exterior_cisa_cctv.docx";

    public void agregarFilaAlInforme(CctvMaintenanceRecord record, String ubicacion, String infoClave) {
        // 1. Determinar ruta (incluye la nueva lógica de EXTERIOR-CISA)
        String rutaArchivo = determinarRuta(infoClave);

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
                // 2. Validación anti-duplicados dentro del Word
                if (idYaExisteEnTabla(tablaObjetivo, record.getDispositivoId())) {
                    System.out.println("ID " + record.getDispositivoId() + " ya existe en el Word. Saltando...");
                    return;
                }

                // 3. Cálculo de secuencia ITEM
                int siguienteItem = 1;
                for (int i = 1; i < tablaObjetivo.getRows().size(); i++) {
                    String textoItem = tablaObjetivo.getRow(i).getCell(0).getText().trim();
                    if (!textoItem.isEmpty()) {
                        siguienteItem++;
                    }
                }

                // 4. Obtener fila y llenar datos
                XWPFTableRow fila = obtenerFilaDisponible(tablaObjetivo);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                fila.getCell(0).setText(String.valueOf(siguienteItem));
                fila.getCell(1).setText(LocalDate.now().format(formatter));
                fila.getCell(2).setText("Mantenimiento Preventivo");
                fila.getCell(3).setText(record.getDispositivoId());
                fila.getCell(4).setText(ubicacion != null ? ubicacion : "N/A");
                fila.getCell(5).setText("Agregar foto"); // Texto solicitado

                // 5. Guardar cambios
                try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
                    document.write(fos);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al manipular el informe Word: " + e.getMessage());
        }
    }

    private String determinarRuta(String infoClave) {
        if (infoClave == null) return OTROSI_7;
        String clave = infoClave.toUpperCase();

        // Lógica de filtrado por prioridad
        if (clave.contains("EXTERIOR-CISA")) return EXTERIOR_CISA;
        if (clave.contains("SERVIDORES")) return SERVIDORES;
        if (clave.contains("OTROSI 20")) return OTROSI_20;

        return OTROSI_7;
    }

    private boolean idYaExisteEnTabla(XWPFTable tabla, String id) {
        for (XWPFTableRow fila : tabla.getRows()) {
            // Columna 3 es ID CAMARA
            if (fila.getCell(3) != null && fila.getCell(3).getText().trim().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private XWPFTableRow obtenerFilaDisponible(XWPFTable tabla) {
        for (int i = 1; i < tabla.getRows().size(); i++) {
            XWPFTableRow fila = tabla.getRow(i);
            if (fila.getCell(3).getText().trim().isEmpty()) {
                return fila;
            }
        }
        return tabla.createRow();
    }
}