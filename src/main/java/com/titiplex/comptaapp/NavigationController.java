package com.titiplex.comptaapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;

public class NavigationController {
    @FXML
    private BorderPane rootLayout;

    @FXML
    private void showDashboard() throws IOException {
        loadCenter("dashboard-view.fxml");
    }

    @FXML
    private void showAccounts() throws IOException {
        loadCenter("accounts-view.fxml");
    }

    @FXML
    private void showTransactions() throws IOException {
        loadCenter("transactions-view.fxml");
    }

    @FXML
    private void showReports() throws IOException {
        loadCenter("reports-view.fxml");
    }

    private void loadCenter(String fxml) throws IOException {
        Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
        rootLayout.setCenter(node);
    }

    @FXML
    private void initialize() {
        try {
            showDashboard();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}