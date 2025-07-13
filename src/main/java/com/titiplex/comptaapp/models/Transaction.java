package com.titiplex.comptaapp.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Transaction {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final IntegerProperty accountId = new SimpleIntegerProperty();

    public Transaction(int id, LocalDate d, String desc, double amt, int acc) {
        this.id.set(id);
        this.date.set(d);
        this.description.set(desc);
        this.amount.set(amt);
        this.accountId.set(acc);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public String getDescription() {
        return description.get();
    }

    public double getAmount() {
        return amount.get();
    }

    public int getAccountId() {
        return accountId.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public DoubleProperty amountProperty() {
        return amount;
    }
}