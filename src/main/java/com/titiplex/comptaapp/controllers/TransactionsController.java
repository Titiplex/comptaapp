package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class TransactionsController {
    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, LocalDate> dateCol;
    @FXML
    private TableColumn<Transaction, String> descCol;
    @FXML
    private TableColumn<Transaction, Number> amtCol;

    @FXML
    private DatePicker dateField;
    @FXML
    private TextField descField;
    @FXML
    private TextField amountField;
    @FXML
    private Button addBtn;

    @FXML
    private void initialize() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        transactionsTable.setItems(DataStore.transactions);
        dateField.setValue(LocalDate.now());

        addBtn.setOnAction(_ -> addTransaction());
    }

    private void addTransaction() {
        String desc = descField.getText().trim();
        String amtText = amountField.getText().trim();
        double amt;
        try {
            amt = Double.parseDouble(amtText.replace(',', '.'));
        } catch (NumberFormatException ex) {
            new Alert(Alert.AlertType.ERROR, "Montant invalide").showAndWait();
            return;
        }
        if (desc.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Description vide").showAndWait();
            return;
        }
        Transaction t = new Transaction(dateField.getValue(), desc, amt);
        DataStore.transactions.add(t);

        descField.clear();
        amountField.clear();
    }
}