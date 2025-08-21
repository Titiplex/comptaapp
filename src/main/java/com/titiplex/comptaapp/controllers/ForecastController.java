package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.dao.TransactionDao;
import com.titiplex.comptaapp.models.Account;
import com.titiplex.comptaapp.models.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class ForecastController {
    @FXML
    private TableView<Transaction> planTable;
    @FXML
    private TableColumn<Transaction, LocalDate> dueCol;
    @FXML
    private TableColumn<Transaction, String> descCol;
    @FXML
    private TableColumn<Transaction, Number> amtCol;
    @FXML
    private TableColumn<Transaction, String> accCol;
    @FXML
    private TableColumn<Transaction, Void> actCol;

    @FXML
    private void initialize() {
        planTable.setItems(DataStore.planned);
        actCol.setCellFactory(_ -> new TableCell<>() {
            private final Button pay = new Button("Marquer réglé");

            {
                pay.setOnAction(_ -> {
                    Transaction t = getTableView().getItems().get(getIndex());
                    TransactionDao.markSettled(t.getId(), LocalDate.now());
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : pay);
            }
        });
        dueCol.setCellValueFactory(new PropertyValueFactory<>("due_date"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        amtCol.setCellValueFactory(new PropertyValueFactory<>("account"));
        Account.getAccount(accCol);
    }
}
