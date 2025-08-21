package com.titiplex.comptaapp.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Transaction {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();
    private final DoubleProperty amount = new SimpleDoubleProperty();
    private final IntegerProperty accountId = new SimpleIntegerProperty();
    private final IntegerProperty eventId = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Transaction(int id, LocalDate d, String desc, double amt, int acc, int ev) {
        this.id.set(id);
        this.date.set(d);
        this.description.set(desc);
        this.amount.set(amt);
        this.accountId.set(acc);
        this.eventId.set(ev);
    }

    public int getId() {
        return id.get();
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

    public int getEventId() {
        return eventId.get();
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

    public String getStatus() {
        return status.get();
    }
}