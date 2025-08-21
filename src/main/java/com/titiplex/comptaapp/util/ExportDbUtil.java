package com.titiplex.comptaapp.util;

import com.titiplex.comptaapp.DBHelper;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ExportDbUtil {
    private ExportDbUtil() {
    }

    /* =================== LISTAGE DES TABLES =================== */
    public static List<String> listTables() {
        List<String> res = new ArrayList<>();
        String q = """
                SELECT TABLE_NAME
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_TYPE='BASE TABLE'
                ORDER BY TABLE_NAME
                """;
        try (Statement st = DBHelper.getConn().createStatement();
             ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) res.add(rs.getString(1));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return res;
    }

    /* =================== EXPORT SQL DUMP =================== */
    public static void exportSqlDump(Window owner) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Exporter en SQL (H2 SCRIPT)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL (*.sql)", "*.sql"));
        fc.setInitialFileName("dump.sql");
        File f = fc.showSaveDialog(owner);
        if (f == null) return;

        doH2ScriptToFile(f);
    }

    private static void doH2ScriptToFile(File f) {
        String path = f.getAbsolutePath().replace("'", "''");
        String sql = "SCRIPT DROP TO '" + path + "'";
        try (Statement st = DBHelper.getConn().createStatement()) {
            st.execute(sql);
            ok("Export SQL terminé : " + f.getAbsolutePath());
        } catch (Exception e) {
            error("Échec export SQL", e);
        }
    }

    /* =================== EXPORT CSV PAR TABLE =================== */
    public static void exportCsvPerTable(Window owner) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choisir le dossier de sortie CSV");
        File dir = dc.showDialog(owner);
        if (dir == null) return;

        List<String> tables = listTables();
        try (Connection c = DBHelper.getConn();
             Statement st = c.createStatement()) {
            for (String t : tables) {
                File out = new File(dir, t + ".csv");
                try (ResultSet rs = st.executeQuery("SELECT * FROM " + t);
                     Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8))) {
                    writeCsv(rs, w);
                }
            }
            ok("CSV exportés dans : " + dir.getAbsolutePath());
        } catch (Exception e) {
            error("Échec export CSV", e);
        }
    }

    /**
     * Nouveau : export CSV pour une sélection de tables, dans un dossier.
     */
    public static void exportCsvSelected(Window owner, Set<String> selected) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choisir le dossier de sortie CSV");
        File dir = dc.showDialog(owner);
        if (dir == null) return;

        try (Connection c = DBHelper.getConn();
             Statement st = c.createStatement()) {
            for (String t : selected) {
                File out = new File(dir, t + ".csv");
                try (ResultSet rs = st.executeQuery("SELECT * FROM " + t);
                     Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8))) {
                    writeCsv(rs, w);
                }
            }
            ok("CSV exportés dans : " + dir.getAbsolutePath());
        } catch (Exception e) {
            error("Échec export CSV", e);
        }
    }

    /* =================== EXPORT EXCEL =================== */
    public static void exportExcel(Window owner) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Exporter en Excel (.xlsx)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fc.setInitialFileName("export.xlsx");
        File f = fc.showSaveDialog(owner);
        if (f == null) return;

        List<String> tables = listTables();
        doWriteXlsxFile(f, tables);
    }

    /**
     * Nouveau : export XLSX pour tables choisies
     */
    public static void exportExcelSelected(Window owner, Set<String> selected) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Exporter en Excel (.xlsx)");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel (*.xlsx)", "*.xlsx"));
        fc.setInitialFileName("export.xlsx");
        File f = fc.showSaveDialog(owner);
        if (f == null) return;

        doWriteXlsxFile(f, new ArrayList<>(selected));
    }

    private static void doWriteXlsxFile(File f, List<String> tables) {
        try (Workbook wb = new XSSFWorkbook();
             Connection c = DBHelper.getConn();
             Statement st = c.createStatement()) {

            for (String t : tables) {
                try (ResultSet rs = st.executeQuery("SELECT * FROM " + t)) {
                    writeSheet(wb, t, rs);
                }
            }
            try (FileOutputStream out = new FileOutputStream(f)) {
                wb.write(out);
            }
            ok("Excel exporté : " + f.getAbsolutePath());
        } catch (Exception e) {
            error("Échec export Excel", e);
        }
    }

    /* =================== EXPORT ZIP BUNDLE =================== */
    public static void exportZipBundle(Window owner) {
        // 1) Formats ?
        var choiceOpt = FormatsDialog.ask(owner);
        if (choiceOpt.isEmpty()) return;
        var choice = choiceOpt.get();
        if (!choice.sql() && !choice.csv() && !choice.xlsx()) {
            error("Aucun format sélectionné", new IllegalArgumentException("Sélection vide"));
            return;
        }

        // 2) Tables ?
        List<String> tables = listTables();
        var selectedOpt = TableFilterDialog.selectTables(owner, tables);
        if (selectedOpt.isEmpty()) return;
        Set<String> selected = selectedOpt.get();

        // 3) Fichier ZIP
        FileChooser fc = new FileChooser();
        fc.setTitle("Exporter tout en ZIP");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP (*.zip)", "*.zip"));
        fc.setInitialFileName("export_bd.zip");
        File zipFile = fc.showSaveDialog(owner);
        if (zipFile == null) return;
        if (!zipFile.getName().toLowerCase().endsWith(".zip")) {
            zipFile = new File(zipFile.getParentFile(), zipFile.getName() + ".zip");
        }

        // 4) Génération
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)))) {

            // 4a) SQL dump (via fichier temp)
            if (choice.sql()) {
                File tmp = File.createTempFile("compta-dump", ".sql");
                try {
                    doH2ScriptToFile(tmp);
                    // ajouter au zip
                    zos.putNextEntry(new ZipEntry("sql/dump.sql"));
                    try (InputStream in = new BufferedInputStream(new FileInputStream(tmp))) {
                        in.transferTo(zos);
                    }
                    zos.closeEntry();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } finally {
                    // cleanup
                    tmp.delete();
                }
            }

            // 4b) CSV (direct vers zip)
            if (choice.csv()) {
                try (Connection c = DBHelper.getConn(); Statement st = c.createStatement()) {
                    for (String t : selected) {
                        zos.putNextEntry(new ZipEntry("csv/" + t + ".csv"));
                        try (ResultSet rs = st.executeQuery("SELECT * FROM " + t);
                             Writer w = new BufferedWriter(new OutputStreamWriter(zos, StandardCharsets.UTF_8))) {
                            writeCsv(rs, w);
                        }
                        // Attention : ne PAS fermer le ZOS via writer ; juste finir l’entrée
                        zos.closeEntry();
                    }
                }
            }

            // 4c) XLSX (mémoire → entry)
            if (choice.xlsx()) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream(1 << 20); // 1MB initial
                try (Workbook wb = new XSSFWorkbook();
                     Connection c = DBHelper.getConn();
                     Statement st = c.createStatement()) {
                    for (String t : selected) {
                        try (ResultSet rs = st.executeQuery("SELECT * FROM " + t)) {
                            writeSheet(wb, t, rs);
                        }
                    }
                    wb.write(bout);
                }
                zos.putNextEntry(new ZipEntry("xlsx/export.xlsx"));
                bout.writeTo(zos);
                zos.closeEntry();
            }

            zos.finish();
            ok("ZIP exporté : " + zipFile.getAbsolutePath());
        } catch (Exception e) {
            error("Échec export ZIP", e);
        }
    }

    /* =================== CSV writer (simple) =================== */
    private static void writeCsv(ResultSet rs, Writer w) throws Exception {
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        // header
        for (int c = 1; c <= cols; c++) {
            if (c > 1) w.write(',');
            w.write(escapeCsv(md.getColumnLabel(c)));
        }
        w.write('\n');

        // rows
        while (rs.next()) {
            for (int c = 1; c <= cols; c++) {
                if (c > 1) w.write(',');
                Object v = rs.getObject(c);
                w.write(escapeCsv(v));
            }
            w.write('\n');
        }
        w.flush();
    }

    private static String escapeCsv(Object val) {
        if (val == null) return "";
        String s = String.valueOf(val);
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (s.contains("\"")) s = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + s + "\"" : s;
    }

    /* =================== XLSX helpers =================== */
    private static void writeSheet(Workbook wb, String table, ResultSet rs) throws SQLException {
        String sheetName = table.length() > 31 ? table.substring(0, 31) : table;
        Sheet sh = wb.createSheet(sheetName);

        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        CellStyle header = wb.createCellStyle();
        Font bold = wb.createFont();
        bold.setBold(true);
        header.setFont(bold);
        header.setBorderBottom(BorderStyle.THIN);

        Row r0 = sh.createRow(0);
        for (int c = 1; c <= cols; c++) {
            Cell cell = r0.createCell(c - 1);
            cell.setCellValue(md.getColumnLabel(c));
            cell.setCellStyle(header);
        }

        int row = 1;
        while (rs.next()) {
            Row rr = sh.createRow(row++);
            for (int c = 1; c <= cols; c++) {
                Object v = rs.getObject(c);
                Cell cell = rr.createCell(c - 1);
                switch (v) {
                    case null -> cell.setBlank();
                    case Number n -> cell.setCellValue(n.doubleValue());
                    case Date d -> cell.setCellValue(d.toLocalDate().toString());
                    case Timestamp ts -> cell.setCellValue(ts.toInstant().toString());
                    default -> cell.setCellValue(String.valueOf(v));
                }
            }
        }
        for (int c = 0; c < cols; c++) {
            try {
                sh.autoSizeColumn(c);
            } catch (Exception ignored) {
            }
        }
    }

    /* =================== Alerts =================== */
    private static void ok(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    private static void error(String title, Exception e) {
        System.err.println(title + " : " + e.getMessage());
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(title);
        a.setContentText(e.getMessage());
        a.showAndWait();
    }
}
