package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.dao.AccountDao;
import com.titiplex.comptaapp.models.Account;
import com.titiplex.comptaapp.models.Transaction;
import com.titiplex.comptaapp.util.PDFUtil;
import com.titiplex.comptaapp.util.Period;
import com.titiplex.comptaapp.util.PeriodDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;

public class AccountsController {

    /* ---- table comptes ---- */
    @FXML
    private TableView<Account> accountsTable;
    @FXML
    private TableColumn<Account, String> nameCol;
    @FXML
    private TableColumn<Account, Number> balanceCol;

    /* ---- panneaux résumé ---- */
    @FXML
    private Label revLabel, expLabel, balLabel;
    @FXML
    private TableView<Transaction> txTable;
    @FXML
    private TableColumn<Transaction, LocalDate> dateCol;
    @FXML
    private TableColumn<Transaction, String> descCol;
    @FXML
    private TableColumn<Transaction, Number> amtCol;

    /* boutons */
    @FXML
    private Button addAccountBtn, delAccountBtn, pdfBtn;

    @FXML
    private void initialize() {

        /* ------------ table comptes ------------- */
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        accountsTable.setItems(DataStore.accounts);

        /* ------------ panneau transactions ------------- */
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // sélection d’un compte => refresh résumé
        accountsTable.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newAcc) -> refreshSummary(newAcc)
        );

        /* ------------ boutons CRUD + PDF --------- */
        addAccountBtn.setOnAction(_ -> addAccount());
        delAccountBtn.setOnAction(_ -> delAccount());
        pdfBtn.setOnAction(_ -> exportPDF());

        // auto-sélection premier compte s'il existe
        if (!DataStore.accounts.isEmpty())
            accountsTable.getSelectionModel().selectFirst();
    }

    /* --------- résumé ---------- */
    private void refreshSummary(Account acc) {
        if (acc == null) {
            txTable.setItems(FXCollections.emptyObservableList());
            revLabel.setText(expLabel.getText());
            balLabel.setText("");
            return;
        }
        ObservableList<Transaction> list = DataStore.transactions.filtered(
                t -> t.getAccountId() == acc.getId()
        );
        setItemsList(list, txTable, revLabel, expLabel, balLabel);
    }

    static void setItemsList(ObservableList<Transaction> list, TableView<Transaction> txTable, Label revLabel, Label expLabel, Label balLabel) {
        txTable.setItems(list);

        double rev = list.stream().filter(t -> t.getAmount() > 0).mapToDouble(Transaction::getAmount).sum();
        double exp = list.stream().filter(t -> t.getAmount() < 0).mapToDouble(t -> -t.getAmount()).sum();

        revLabel.setText(String.format("%.2f", rev));
        expLabel.setText(String.format("%.2f", exp));
        balLabel.setText(String.format("%.2f", rev - exp));
    }

    /* -------- CRUD ---------- */
    private void addAccount() {
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Nom du compte");
        d.showAndWait().filter(s -> !s.isBlank()).ifPresent(AccountDao::create);
    }

    private void delAccount() {
        Account acc = accountsTable.getSelectionModel().getSelectedItem();
        if (acc == null) return;
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + acc.getName() + " ?", ButtonType.YES, ButtonType.NO);
        if (c.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            AccountDao.deleteAsync(acc.getId(), acc);
        }
    }

    private void exportPDF() {
        Account acc = accountsTable.getSelectionModel().getSelectedItem();
        if (acc == null) return;

        PeriodDialog.ask(accountsTable.getScene().getWindow()).ifPresent(
                period -> doExport(acc, period)
        );
    }

    private void doExport(Account acc, Period period) {
        // Filtrage
        var list = DataStore.transactions.stream()
                .filter(t -> t.getAccountId() == acc.getId())
                .filter(t -> PeriodDialog.inPeriod(t.getDate(), period))
                .toList();

        // FileChooser + filtre PDF
        FileChooser fc = new FileChooser();
        fc.setInitialFileName("Releve_" + acc.getName() + ".pdf");
        FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        fc.getExtensionFilters().add(pdf);
        fc.setSelectedExtensionFilter(pdf);

        File f = fc.showSaveDialog(accountsTable.getScene().getWindow());
        if (f == null) return;

        // Ajoute .pdf si l’utilisateur l’a omis
        if (!f.getName().toLowerCase().endsWith(".pdf")) {
            f = new File(f.getParentFile(), f.getName() + ".pdf");
        }

        try {
            PDFUtil.exportTransactions(f, "Relevé de compte : " + acc.getName(), list);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            new Alert(Alert.AlertType.ERROR, "Erreur export PDF : " + ex.getMessage()).showAndWait();
        }
    }
}