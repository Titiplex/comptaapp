package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.dao.EventDao;
import com.titiplex.comptaapp.models.Account;
import com.titiplex.comptaapp.models.Event;
import com.titiplex.comptaapp.models.Transaction;
import com.titiplex.comptaapp.util.PDFUtil;
import com.titiplex.comptaapp.util.Period;
import com.titiplex.comptaapp.util.PeriodDialog;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;

public class EventController {

    // GAUCHE
    @FXML
    private ListView<Event> eventList;
    @FXML
    private TextField nameField, descField;
    @FXML
    private Button addBtn;
    @FXML
    private Button pdfBtn;

    // CENTRE
    @FXML
    private Label revLabel, expLabel, balLabel;
    @FXML
    private TableView<Transaction> txTable;
    @FXML
    private TableColumn<Transaction, ?> dateCol, descCol;
    @FXML
    private TableColumn<Transaction, String> accCol;
    @FXML
    private TableColumn<Transaction, Number> amtCol;

    @FXML
    private void initialize() {

        // liste d'évènements
        eventList.setItems(DataStore.events);
        eventList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Event ev, boolean empty) {
                super.updateItem(ev, empty);
                setText(empty || ev == null ? "" : ev.getName());
            }
        });
        eventList.getSelectionModel().selectedItemProperty()
                .addListener((_, _, n) -> refreshSummary(n));

        // table des transactions
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        accCol.setCellValueFactory(cell -> {
            int aid = cell.getValue().getAccountId();
            Account a = DataStore.accounts.stream()
                    .filter(ac -> ac.getId() == aid)
                    .findFirst().orElse(null);
            return Bindings.createStringBinding(() -> a == null ? "" : a.getName());
        });

        // bouton ajouter
        addBtn.setOnAction(_ -> {
            String n = nameField.getText().trim();
            if (n.isBlank()) return;
            EventDao.create(n, descField.getText().trim());
            nameField.clear();
            descField.clear();
        });

        // auto-sélection premier évènement s'il existe
        if (!DataStore.events.isEmpty())
            eventList.getSelectionModel().selectFirst();

        pdfBtn.setOnAction(_ -> exportPDF());
    }

    private void refreshSummary(Event ev) {
        if (ev == null) {
            txTable.setItems(FXCollections.emptyObservableList());
            revLabel.setText(expLabel.getText());
            balLabel.setText("");
            return;
        }
        var filtered = DataStore.transactions
                .filtered(t -> t.getEventId() == ev.getId());

        AccountsController.setItemsList(filtered, txTable, revLabel, expLabel, balLabel);
    }

    private void exportPDF() {
        Event ev = eventList.getSelectionModel().getSelectedItem();
        if (ev == null) return;

        PeriodDialog.ask(eventList.getScene().getWindow()).ifPresent(
                period -> doExport(ev, period)
        );
    }

    private void doExport(Event ev, Period period) {
        var list = DataStore.transactions.stream()
                .filter(t -> t.getEventId() == ev.getId())
                .filter(t -> PeriodDialog.inPeriod(t.getDate(), period))
                .toList();

        FileChooser fc = new FileChooser();
        fc.setInitialFileName("Releve_" + ev.getName() + ".pdf");
        FileChooser.ExtensionFilter pdf = new FileChooser.ExtensionFilter("PDF (*.pdf)", "*.pdf");
        fc.getExtensionFilters().add(pdf);
        fc.setSelectedExtensionFilter(pdf);

        File f = fc.showSaveDialog(eventList.getScene().getWindow());
        if (f == null) return;

        if (!f.getName().toLowerCase().endsWith(".pdf")) {
            f = new File(f.getParentFile(), f.getName() + ".pdf");
        }
        // Ajoute .pdf si l’utilisateur l’a omis
        try {
            PDFUtil.exportTransactions(f,
                    "Évènement : " + ev.getName(), list);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            new Alert(Alert.AlertType.ERROR, "Erreur export PDF : " + ex.getMessage()).showAndWait();
        }

    }
}
