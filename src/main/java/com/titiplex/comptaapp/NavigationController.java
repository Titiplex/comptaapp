package com.titiplex.comptaapp;

import com.titiplex.comptaapp.controllers.CompanySettingsController;
import com.titiplex.comptaapp.controllers.ExportController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class NavigationController {
    @FXML
    private BorderPane rootLayout;
    @FXML
    private MenuBar menuBar;

    @FXML
    private void initialize() throws IOException {
        showDashboard();
        buildMenus();
    }

    @FXML
    private void showDashboard() throws IOException {
        load("dashboard-view.fxml");
    }

    private void buildMenus() {
        // Exportations pdf et autres
        MenuItem releve = new MenuItem("Relevés d'entreprise");
        releve.setOnAction(_ -> ExportController.exportBalance(rootLayout.getScene().getWindow()));
        MenuItem bilan = new MenuItem("Bilan Financier");
        bilan.setOnAction(_ -> ExportController.exportCompany(rootLayout.getScene().getWindow()));

        Menu m1 = new Menu("Exporter");
        m1.getItems().addAll(releve, bilan);

        // Paramètres
        MenuItem entreprise = new MenuItem("Paramètres d'entreprise");
        entreprise.setOnAction(_ -> showCompanySettings());

        Menu m2 = new Menu("Paramètres");
        m2.getItems().addAll(entreprise);

        // MenuBar
        menuBar.getMenus().addAll(m1, m2);
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

    private void showCompanySettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("company-settings.fxml"));
            DialogPane pane = loader.load();
            CompanySettingsController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            ((Stage) dialog.getDialogPane().getScene().getWindow()).setResizable(true);
            dialog.initOwner(rootLayout.getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setTitle("Paramètres société");

            // Optionnel : icône
            // ((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image(...));

            dialog.setResultConverter(bt -> {
                if (bt != null && bt.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    controller.applyIfValidOnOk();            // fait le save
                }
                return bt;
            });

            dialog.showAndWait();

            if (controller.isDirty()) {
                // rafraîchir le titre principal de l’application par exemple
                Stage stage = (Stage) rootLayout.getScene().getWindow();
                String company = DataStore.meta("companyName", "");
                stage.setTitle(company.isBlank() ? "Compta App" : "Compta App – " + company);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}