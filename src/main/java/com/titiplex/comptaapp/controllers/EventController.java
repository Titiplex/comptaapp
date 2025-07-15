package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.dao.EventDao;
import com.titiplex.comptaapp.models.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class EventController {
    @FXML
    private TableView<Event> eventTable;
    @FXML
    private TableColumn<Event, String> nameCol;
    @FXML
    private TableColumn<Event, String> descCol;
    @FXML
    private TextField nameField, descField;
    @FXML
    private Button addBtn;

    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        eventTable.setItems(DataStore.events);
        addBtn.setOnAction(e -> add());
    }

    private void add() {
        String n = nameField.getText().trim();
        if (n.isEmpty()) return;
        EventDao.create(n, descField.getText().trim());
        nameField.clear();
        descField.clear();
    }
}
