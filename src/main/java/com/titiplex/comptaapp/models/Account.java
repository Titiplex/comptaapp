package com.titiplex.comptaapp.models;

import javafx.beans.property.*;

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
}