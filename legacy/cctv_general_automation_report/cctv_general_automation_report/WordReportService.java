package legacy.cctv_general_automation_report.cctv_general_automation_report;

import legacy.cctv_general_automation_report.excel.ExcelDataEntity;
import com.example.ApSystem.model.rms_vss_automation_report.MaintenanceHistory;
import com.example.ApSystem.repository.cctv_general_automation_report.WordReportRepository;
import legacy.cctv_general_automation_report.excel.ExcelDataRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class WordReportService {

    @Autowired
    private WordReportRepository repository;

    @Autowired
    private ExcelDataRepository excelRepo;

    public void addEntryToSpecificReport(MaintenanceHistory mh, String categoriaBase) {
        List<ExcelDataEntity> todaLaFila = excelRepo.findByExcelId(mh.getDeviceId());

        // 1. DETERMINAR CATEGORÍA CON FILTROS ESTRICTOS
        String categoriaReal = determinarCategoriaGlobal(todaLaFila);

        String fileName;
        switch (categoriaReal) {
            case "T2":
                fileName = "informe_t2.docx";
                break;
            case "EXTERIOR-CISA":
                fileName = "informe_exterior_cisa.docx";
                break;
            case "SERVIDORES":
                fileName = "informe_servidores.docx";
                break;
            case "OTROSI-20":
                fileName = "informe_otrosi_20.docx";
                break;
            default:
                // Solo si no cumple NADA de lo anterior, va al general
                fileName = "informe_general_cctv_general.docx";
                break;
        }

        System.out.println("LOG: ID [" + mh.getDeviceId() + "] clasificado como [" + categoriaReal + "] -> Archivo: " + fileName);

        // 2. PROCESO DE EDICIÓN (Se mantiene igual pero con log de confirmación)
        try (XWPFDocument doc = repository.loadDocument(fileName)) {
            XWPFTable table = findTargetTable(doc, "ITEM");
            if (table != null) {
                validarDuplicado(table, mh.getDeviceId());

                // Localizamos la fila
                XWPFTableRow rowToFill = findFirstEmptyRow(table);

                // Calculamos el consecutivo (Item #)
                int consecutive = table.getRows().indexOf(rowToFill);

                // Llenamos la fila con los datos de mantenimiento
                fillRow(rowToFill, mh, table.getRows().indexOf(rowToFill));

                // Guardamos los cambios
                repository.saveDocument(doc, fileName);
                System.out.println("EXITO: Guardado en " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error en " + fileName + ": " + e.getMessage());
        }
    }

    private String determinarCategoriaGlobal(List<ExcelDataEntity> datos) {
        if (datos == null || datos.isEmpty()) return "GENERAL";

        // Unimos todo el texto de la fila en una sola cadena para buscar mejor
        StringBuilder contenidoFila = new StringBuilder();
        for (ExcelDataEntity d : datos) {
            contenidoFila.append(d.getValor().toUpperCase()).append(" ");
        }
        String texto = contenidoFila.toString();

        // 2. REGLAS DE EXCLUSIÓN (Orden de prioridad crítico)

        // Regla OTROSI: Buscamos la palabra completa, no solo el "20"
        if (texto.contains("OTROSI") || texto.contains("OTRO SI") || texto.contains("OTRO SÍ")) {
            return "OTROSI-20";
        }

        // Regla T2: Palabra exacta para evitar que "Sector 2" entre aquí
        if (texto.contains(" T2 ") || texto.startsWith("T2 ") || texto.contains("-T2")) {
            return "T2";
        }

        // Regla CISA
        if (texto.contains("CISA") || texto.contains("EXTERIOR-CISA")) {
            return "EXTERIOR-CISA";
        }

        // Regla SERVIDORES
        if (texto.contains("SERVIDOR") || texto.contains("SERVER") || texto.contains("RACK")) {
            return "SERVIDORES";
        }

        // Si no pasó ninguno de los filtros anteriores, es GENERAL
        return "GENERAL";
    }

    private XWPFTableRow findFirstEmptyRow(XWPFTable table) {
        // Recorremos desde la fila 1 (asumiendo que la 0 es el encabezado)
        for (int i = 1; i < table.getRows().size(); i++) {
            XWPFTableRow row = table.getRow(i);
            // Usamos la celda del ID (índice 3) para saber si la fila está libre
            if (isRowEmpty(row)) {
                return row;
            }
        }
        // Si no hay ninguna vacía, creamos una nueva fila al final
        return table.createRow();
    }


    private void fillRow(XWPFTableRow row, MaintenanceHistory mh, int itemNum) {
        setTextSafe(row.getCell(0), String.valueOf(itemNum));
        setTextSafe(row.getCell(1), LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        setTextSafe(row.getCell(2), mh.getPhase());
        setTextSafe(row.getCell(3), mh.getDeviceId());
        setTextSafe(row.getCell(4), mh.getLocation());
        setTextSafe(row.getCell(5), "add Photo");
    }

    private void setTextSafe(XWPFTableCell cell, String text) {
        if (cell == null) return;
        // Limpiamos párrafos existentes para no duplicar texto en la celda
        while (!cell.getParagraphs().isEmpty()) cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        XWPFRun r = p.createRun();
        r.setText(text != null ? text : "");
        r.setFontSize(9);
        r.setFontFamily("Arial");
    }

    private boolean isRowEmpty(XWPFTableRow row) {
        // Consideramos fila vacía si la celda del ID (índice 3) no tiene texto
        XWPFTableCell cell = row.getCell(3);
        return cell == null || cell.getText().trim().isEmpty();
    }

    private void validarDuplicado(XWPFTable table, String id) {
        for (int i = 1; i < table.getRows().size(); i++) {
            XWPFTableCell cell = table.getRow(i).getCell(3);
            if (cell != null && cell.getText().trim().equalsIgnoreCase(id.trim())) {
                throw new RuntimeException("El dispositivo con ID " + id + " ya tiene un registro de mantenimiento en este informe.");
            }
        }
    }

    private XWPFTable findTargetTable(XWPFDocument doc, String keyword) {
        for (XWPFTable table : doc.getTables()) {
            if (!table.getRows().isEmpty()) {
                XWPFTableCell firstCell = table.getRow(0).getCell(0);
                if (firstCell != null && firstCell.getText().toUpperCase().contains(keyword.toUpperCase())) {
                    return table;
                }
            }
        }
        return null;
    }
}