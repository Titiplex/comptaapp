package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Transaction;
import eu.hansolo.tilesfx.Tile;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;

public class DashboardController {
    @FXML
    private Tile revenueTile;
    @FXML
    private Tile expenseTile;

    @FXML
    private void initialize() {
        updateTiles();
        DataStore.transactions.addListener((ListChangeListener<Transaction>) _ -> updateTiles());
    }

    private void updateTiles() {
        revenueTile.setValue(DataStore.totalRevenue());
        expenseTile.setValue(DataStore.totalExpenses());
    }
}