package com.titiplex.comptaapp.controllers;
import com.titiplex.comptaapp.*;
import com.titiplex.comptaapp.models.Transaction;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
public class ReportsController{
    @FXML private PieChart pie;
    @FXML private void initialize(){ rebuild(); DataStore.transactions.addListener((ListChangeListener<Transaction>)c->rebuild());}
    private void rebuild(){ pie.getData().setAll(new PieChart.Data("Revenus",DataStore.totalRevenue()),new PieChart.Data("Dépenses",DataStore.totalExpenses())); }
}