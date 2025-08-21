package com.titiplex.comptaapp;

import com.titiplex.comptaapp.models.Account;
import com.titiplex.comptaapp.models.Event;
import com.titiplex.comptaapp.models.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public final class DataStore {
    public static final ObservableList<Account> accounts = FXCollections.observableArrayList();
    public static final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    public static final ObservableList<Event> events = FXCollections.observableArrayList();
    private static final Map<String, String> meta = new HashMap<>();

    public static String meta(String key, String def) {
        if (meta.isEmpty()) loadMeta();
        return meta.getOrDefault(key, def);
    }

    public static void saveMeta(String key, String value) {
        DBHelper.EXEC.execute(() -> {
            try (var ps = DBHelper.getConn().prepareStatement(
                    "MERGE INTO metadata (meta_key,meta_value) VALUES(?,?)")) {
                ps.setString(1, key);
                ps.setString(2, value);
                ps.executeUpdate();
                DBHelper.commit();
                meta.put(key, value);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    private static void loadMeta() {
        DBHelper.EXEC.execute(() -> {
            try (var st = DBHelper.getConn().createStatement();
                 var rs = st.executeQuery("SELECT meta_key,meta_value FROM metadata")) {
                while (rs.next()) meta.put(rs.getString(1), rs.getString(2));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public static void loadMetaSync() {
        if (!meta.isEmpty()) return;                       // déjà chargé
        try (var st = DBHelper.getConn().createStatement();
             var rs = st.executeQuery("SELECT meta_key,meta_value FROM metadata")) {
            while (rs.next()) meta.put(rs.getString(1), rs.getString(2));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


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

    public static final ObservableList<Transaction> planned =
            transactions.filtered(t -> "PLANNED".equals(t.getStatus()));

}