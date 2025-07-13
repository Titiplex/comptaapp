package com.titiplex.comptaapp.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Transaction {
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();

    public Transaction(LocalDate date, String desc, double amount) {
        this.date.set(date);
        this.description.set(desc);
        this.amount.set(amount);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate d) {
        date.set(d);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String d) {
        description.set(d);
    }

    public double getAmount() {
        return amount.get();
    }

    public void setAmount(double a) {
        amount.set(a);
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