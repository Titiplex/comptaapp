package com.titiplex.comptaapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

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
}