package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Transaction;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

public class ReportsController {
    @FXML
    private PieChart pie;

    @FXML
    private void initialize() {
        rebuildChart();
        DataStore.transactions.addListener((ListChangeListener<Transaction>) _ -> rebuildChart());
    }

    private void rebuildChart() {
        double rev = DataStore.totalRevenue();
        double exp = DataStore.totalExpenses();
        pie.getData().setAll(
                new PieChart.Data("Revenus", rev),
                new PieChart.Data("Dépenses", exp)
        );
    }
}