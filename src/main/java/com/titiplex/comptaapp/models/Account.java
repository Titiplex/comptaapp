package com.titiplex.comptaapp.models;

import com.titiplex.comptaapp.DataStore;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.TableColumn;

public class Account {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty balance = new SimpleDoubleProperty();

    public Account(int id, String n, double b) {
        this.id.set(id);
        this.name.set(n);
        this.balance.set(b);
    }

    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public double getBalance() {
        return balance.get();
    }

    public void setBalance(double v) {
        balance.set(v);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

    public static void getAccount(TableColumn<Transaction, String> accCol) {
        accCol.setCellValueFactory(cell -> {
            int aid = cell.getValue().getAccountId();
            Account a = DataStore.accounts.stream()
                    .filter(ac -> ac.getId() == aid)
                    .findFirst().orElse(null);
            return Bindings.createStringBinding(() -> a == null ? "" : a.getName());
        });
    }
}