package com.titiplex.comptaapp.controllers;
import com.titiplex.comptaapp.*;
import com.titiplex.comptaapp.models.Transaction;
import eu.hansolo.tilesfx.Tile;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
public class DashboardController{
    @FXML private Tile revenueTile;
    @FXML private Tile expenseTile;
    @FXML private void initialize(){ update(); DataStore.transactions.addListener((ListChangeListener<Transaction>)c->update());}
    private void update(){ revenueTile.setValue(DataStore.totalRevenue()); expenseTile.setValue(DataStore.totalExpenses());}
}