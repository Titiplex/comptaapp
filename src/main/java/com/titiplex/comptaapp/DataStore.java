package com.titiplex.comptaapp;

import com.titiplex.comptaapp.models.Account;
import com.titiplex.comptaapp.models.Event;
import com.titiplex.comptaapp.models.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class DataStore {
    public static final ObservableList<Account> accounts = FXCollections.observableArrayList();
    public static final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    public static final ObservableList<Event> events = FXCollections.observableArrayList();

    public static double totalRevenue() {
        return transactions.stream().filter(t -> t.getAmount() > 0).mapToDouble(Transaction::getAmount).sum();
    }

    public static double totalExpenses() {
        return transactions.stream().filter(t -> t.getAmount() < 0).mapToDouble(t -> -t.getAmount()).sum();
    }

    public static void recomputeBalances() {
        accounts.forEach(a -> a.setBalance(
                transactions.stream().filter(t -> t.getAccountId() == a.getId()).mapToDouble(Transaction::getAmount).sum()
        ));
    }
}