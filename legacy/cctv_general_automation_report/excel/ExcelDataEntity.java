package legacy.cctv_general_automation_report.excel;

import jakarta.persistence.*;

@IdClass(ExcelDataId.class)
@Table(name = "excel_data")
public class ExcelDataEntity {

    @Id
    @Column(name = "excel_id")
    private String excelId;

    @Id
    private String columna;

    private String valor;

    // NUEVA COLUMNA: Guarda "T2", "CISA", "SERVIDORES", "OTROSI-20" o "GENERAL"
    private String categoriaInforme;

    public ExcelDataEntity() {}

    public String getExcelId() {
        return excelId;
    }

    public void setExcelId(String excelId) {
        this.excelId = excelId;
    }

    public String getColumna() {
        return columna;
    }

    public void setColumna(String columna) {
        this.columna = columna;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getCategoriaInforme() { return categoriaInforme; }
    public void setCategoriaInforme(String categoriaInforme) { this.categoriaInforme = categoriaInforme; }
}

