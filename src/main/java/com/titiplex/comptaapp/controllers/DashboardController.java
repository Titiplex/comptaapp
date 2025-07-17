package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Transaction;
import com.titiplex.comptaapp.util.PDFUtil;
import com.titiplex.comptaapp.util.Period;
import com.titiplex.comptaapp.util.PeriodDialog;
import eu.hansolo.tilesfx.Tile;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;

public class DashboardController {
    @FXML
    private Tile revenueTile, expenseTile;
    @FXML
    private Button exportBtn;

    @FXML
    private void initialize() {
        update();
        DataStore.transactions.addListener((ListChangeListener<Transaction>) _ -> update());
        exportBtn.setOnAction(_ -> exportBalance());
    }

    private void update() {
        revenueTile.setValue(DataStore.totalRevenue());
        expenseTile.setValue(DataStore.totalExpenses());
    }

    @FXML
    private void exportBalance() {
        PeriodDialog.ask(revenueTile.getScene().getWindow()).ifPresent(
                this::doExport
        );
    }

    private void doExport(Period period) {
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("Bilan_financier" + ".pdf");
        FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        fc.getExtensionFilters().add(pdf);
        fc.setSelectedExtensionFilter(pdf);

        File f = fc.showSaveDialog(revenueTile.getScene().getWindow());
        if (f == null) return;

        // Ajoute .pdf si l’utilisateur l’a omis
        if (!f.getName().toLowerCase().endsWith(".pdf")) {
            f = new File(f.getParentFile(), f.getName() + ".pdf");
        }

        try {
            PDFUtil.exportFinancialReport(f, period, "/images/rvm.png");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}