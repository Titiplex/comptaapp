package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.dao.EventDao;
import com.titiplex.comptaapp.models.Account;
import com.titiplex.comptaapp.models.Event;
import com.titiplex.comptaapp.models.Transaction;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class EventController {

    // GAUCHE
    @FXML
    private ListView<Event> eventList;
    @FXML
    private TextField nameField, descField;
    @FXML
    private Button addBtn;

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
        eventList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Event ev, boolean empty) {
                super.updateItem(ev, empty);
                setText(empty || ev == null ? "" : ev.getName());
            }
        });
        eventList.getSelectionModel().selectedItemProperty()
                .addListener((obs, o, n) -> refreshSummary(n));

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
        addBtn.setOnAction(e -> {
            String n = nameField.getText().trim();
            if (n.isBlank()) return;
            EventDao.create(n, descField.getText().trim());
            nameField.clear();
            descField.clear();
        });

        // auto-sélection premier évènement s'il existe
        if (!DataStore.events.isEmpty())
            eventList.getSelectionModel().selectFirst();
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

        txTable.setItems(filtered);

        double rev = filtered.stream()
                .filter(t -> t.getAmount() > 0)
                .mapToDouble(Transaction::getAmount).sum();
        double exp = filtered.stream()
                .filter(t -> t.getAmount() < 0)
                .mapToDouble(t -> -t.getAmount()).sum();

        revLabel.setText(String.format("%.2f", rev));
        expLabel.setText(String.format("%.2f", exp));
        balLabel.setText(String.format("%.2f", rev - exp));
    }
}
