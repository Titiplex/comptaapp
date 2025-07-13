package com.titiplex.comptaapp.controllers;
import com.titiplex.comptaapp.*;
import com.titiplex.comptaapp.dao.AccountDao;
import com.titiplex.comptaapp.models.Account;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;

public class AccountsController {
    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account,String> nameCol;
    @FXML private TableColumn<Account,Number> balanceCol;
    @FXML private Button addAccountBtn, delAccountBtn;

    @FXML private void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        accountsTable.setItems(DataStore.accounts);
        addAccountBtn.setOnAction(e -> add());
        delAccountBtn.setOnAction(e -> del());
    }

    private void add() {
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Nom du compte");
        Optional<String> r = d.showAndWait();
        r.filter(s -> !s.isBlank()).ifPresent(AccountDao::create);
    }

    private void del() {
        Account a = accountsTable.getSelectionModel().getSelectedItem();
        if (a == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + a.getName() + " ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
            try (Connection c = DBHelper.getConnection();
                 PreparedStatement ps = c.prepareStatement("DELETE FROM account WHERE id=?")) {
                ps.setInt(1, a.getId());
                ps.executeUpdate();
                DataStore.accounts.remove(a);
                DataStore.recomputeBalances();
            } catch (Exception ex) { ex.printStackTrace(); }
        });
    }
}