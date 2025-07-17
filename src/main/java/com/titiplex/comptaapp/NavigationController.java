package com.titiplex.comptaapp;

import com.titiplex.comptaapp.util.PDFUtil;
import com.titiplex.comptaapp.util.Period;
import com.titiplex.comptaapp.util.PeriodDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class NavigationController {
    @FXML
    private BorderPane rootLayout;

    @FXML
    private void initialize() throws IOException {
        showDashboard();
    }

    @FXML
    private void showDashboard() throws IOException {
        load("dashboard-view.fxml");
    }

    @FXML
    private void showAccounts() throws IOException {
        load("accounts-view.fxml");
    }

    @FXML
    private void showTransactions() throws IOException {
        load("transactions-view.fxml");
    }

    @FXML
    private void showReports() throws IOException {
        load("reports-view.fxml");
    }

    private void load(String fxml) throws IOException {
        rootLayout.setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml))));
    }

    @FXML
    private void showEvents() throws IOException {
        load("event-view.fxml");
    }

    @FXML
    private void showCompanySettings() throws IOException {
        load("company-settings.fxml");
    }

    @FXML
    private void exportCompany() {
        PeriodDialog.ask(rootLayout.getScene().getWindow()).ifPresent(
                this::doExport
        );
    }

    private void doExport(Period period) {
        var list = DataStore.transactions.stream()
                .filter(t -> PeriodDialog.inPeriod(t.getDate(), period))
                .toList();

        FileChooser fc = new FileChooser();
        fc.setInitialFileName("Releve_entreprise" + ".pdf");
        FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        fc.getExtensionFilters().add(pdf);
        fc.setSelectedExtensionFilter(pdf);

        File f = fc.showSaveDialog(rootLayout.getScene().getWindow());
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

}