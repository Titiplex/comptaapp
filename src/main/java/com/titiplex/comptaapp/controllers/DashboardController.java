package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Transaction;
import com.titiplex.comptaapp.util.PDFUtil;
import com.titiplex.comptaapp.util.Period;
import com.titiplex.comptaapp.util.PeriodDialog;
import eu.hansolo.tilesfx.Tile;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;

public class DashboardController {
    @FXML
    private Tile revenueTile, expenseTile;

    @FXML
    private void initialize() {
        update();
        DataStore.transactions.addListener((ListChangeListener<Transaction>) _ -> update());
    }

    private void update() {
        revenueTile.setValue(DataStore.totalRevenue());
        expenseTile.setValue(DataStore.totalExpenses());
    }
}