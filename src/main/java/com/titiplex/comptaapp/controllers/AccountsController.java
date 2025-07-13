package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Account;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class AccountsController {
    @FXML
    private TableView<Account> accountsTable;
    @FXML
    private TableColumn<Account, String> nameCol;
    @FXML
    private TableColumn<Account, Number> balanceCol;

    @FXML
    private Button addAccountBtn;

    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));

        accountsTable.setItems(DataStore.accounts);

        addAccountBtn.setOnAction(_ -> onAddAccount());
    }

    private void onAddAccount() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("New Account");
        dlg.setHeaderText("Créer un compte");
        dlg.setContentText("Nom du compte :");
        Optional<String> result = dlg.showAndWait();
        result.filter(name -> !name.isBlank()).ifPresent(name -> DataStore.accounts.add(new Account(name, 0.0)));
    }
}