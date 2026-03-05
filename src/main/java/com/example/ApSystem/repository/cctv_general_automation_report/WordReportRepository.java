package com.example.ApSystem.repository.cctv_general_automation_report;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.*;

@Repository
public class WordReportRepository {

    // Carpeta donde se guardarán los informes generados
    private static final String OUTPUT_DIR = "reports/";
    // Carpeta en src/main/resources donde residen las plantillas originales
    private static final String TEMPLATE_DIR = "templates/";

    /**
     * Carga el documento específico basado en la categoría.
     * Si el archivo no existe en /reports, carga la plantilla base de resources.
     */
    public XWPFDocument loadDocument(String fileName) throws IOException {
        // 1. Intentar carpeta externa
        File externalFile = new File("reports/" + fileName);
        if (externalFile.exists()) {
            System.out.println("Cargando desde carpeta externa: " + externalFile.getAbsolutePath());
            return new XWPFDocument(new FileInputStream(externalFile));
        }

        // 2. Intentar desde Resources
        System.out.println("Buscando en resources: excel/" + fileName);
        ClassPathResource resource = new ClassPathResource("excel/" + fileName);

        if (!resource.exists()) {
            // Esto imprimirá en tu consola de IntelliJ la ruta real donde está buscando
            throw new FileNotFoundException("La plantilla no existe en el classpath: resources/excel/" + fileName);
        }

        return new XWPFDocument(resource.getInputStream());
    }

    public void saveDocument(XWPFDocument doc, String fileName) throws IOException {
        File folder = new File("reports");
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Carpeta 'reports' creada exitosamente.");
            }
        }

        File outputFile = new File(folder, fileName);
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            doc.write(out);
            System.out.println("Archivo guardado exitosamente en: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            throw new IOException("Error crítico al escribir el archivo en disco: " + e.getMessage());
        }
    }

    /**
     * Mapeo centralizado de categorías a nombres de archivos físicos.
     */
    private String getFileName(String categoria) {
        if (categoria == null) return "informe_general_cctv.docx";

        switch (categoria.toUpperCase()) {
            case "T2":
                return "informe_t2.docx";
            case "CISA":
            case "EXTERIOR-CISA":
                return "informe_exterior_cisa.docx";
            case "SERVIDORES":
                return "informe_servidores.docx";
            case "OTROSI-20":
                return "informe_otrosi_20.docx";
            default:
                return "informe_general_cctv.docx";
        }
    }
}