package com.titiplex.comptaapp.dao;

import com.titiplex.comptaapp.DBHelper;
import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Transaction;
import javafx.application.Platform;

import java.sql.*;
import java.time.LocalDate;

public final class TransactionDao {

    public static void loadAllToStore() {
        DBHelper.EXEC.execute(() -> {
            DataStore.transactions.clear();
            try (Statement st = DBHelper.getConn().createStatement();
                 ResultSet rs = st.executeQuery("SELECT id,date,description,amount,account_id FROM transaction")) {
                while (rs.next()) {
                    Transaction t = new Transaction(rs.getInt(1),
                            rs.getDate(2).toLocalDate(),
                            rs.getString(3),
                            rs.getDouble(4),
                            rs.getInt(5));
                    Platform.runLater(() -> DataStore.transactions.add(t));
                }
                Platform.runLater(DataStore::recomputeBalances);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public static void create(LocalDate d, String desc, double amt, int accId) {
        DBHelper.EXEC.execute(() -> {
            try (PreparedStatement ps = DBHelper.getConn().prepareStatement(
                    "INSERT INTO transaction(date,description,amount,account_id) VALUES(?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, Date.valueOf(d));
                ps.setString(2, desc);
                ps.setDouble(3, amt);
                ps.setInt(4, accId);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) {
                        Transaction t = new Transaction(k.getInt(1), d, desc, amt, accId);
                        Platform.runLater(() -> {
                            DataStore.transactions.add(t);
                            DataStore.recomputeBalances();
                        });
                    }
                }
                DBHelper.commit();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }
}