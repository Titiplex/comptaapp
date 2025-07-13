package com.titiplex.comptaapp.models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Account {
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty balance = new SimpleDoubleProperty();

    public Account(String name, double balance) {
        this.name.set(name);
        this.balance.set(balance);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public double getBalance() {
        return balance.get();
    }

    public void setBalance(double val) {
        balance.set(val);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }
}