package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.util.PDFUtil;
import com.titiplex.comptaapp.util.Period;
import com.titiplex.comptaapp.util.PeriodDialog;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public interface ExportController {

    static void exportCompany(Window window) {
        PeriodDialog.ask(window).ifPresent(period ->
                doExportCompany(period, window)
        );
    }

    private static void doExportCompany(Period period, Window window) {
        var list = DataStore.transactions.stream()
                .filter(t -> PeriodDialog.inPeriod(t.getDate(), period))
                .toList();

        FileChooser fc = new FileChooser();
        fc.setInitialFileName("Releve_entreprise" + ".pdf");
        FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        fc.getExtensionFilters().add(pdf);
        fc.setSelectedExtensionFilter(pdf);

        File f = fc.showSaveDialog(window);
        if (f == null) return;

        // Ajoute .pdf si l’utilisateur l’a omis
        if (!f.getName().toLowerCase().endsWith(".pdf")) {
            f = new File(f.getParentFile(), f.getName() + ".pdf");
        }
        try {
            PDFUtil.exportTransactions(f,
                    "Relevé entreprise", list);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

    }

    static void exportBalance(Window window) {
        PeriodDialog.ask(window).ifPresent(period ->
                doExportBalance(period, window)
        );
    }

    private static void doExportBalance(Period period, Window window) {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("Bilan_financier" + ".pdf");
        FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        fc.getExtensionFilters().add(pdf);
        fc.setSelectedExtensionFilter(pdf);

        File f = fc.showSaveDialog(window);
        if (f == null) return;

        // Ajoute .pdf si l’utilisateur l’a omis
        if (!f.getName().toLowerCase().endsWith(".pdf")) {
            f = new File(f.getParentFile(), f.getName() + ".pdf");
        }

        try {
            PDFUtil.exportFinancialReport(f, period, "/images/app_icon.png");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
