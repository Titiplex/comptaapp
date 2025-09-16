package com.titiplex.comptaapp.dao;

import com.titiplex.comptaapp.DBHelper;
import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Account;
import com.titiplex.comptaapp.models.Transaction;
import javafx.application.Platform;

import java.sql.*;
import java.time.LocalDate;

public final class TransactionDao {

    public static void loadAllToStore() {
        DBHelper.EXEC.execute(() -> {
            DataStore.transactions.clear();
            try (Statement st = DBHelper.getConn().createStatement();
                 ResultSet rs = st.executeQuery("SELECT id,date,description,amount,account_id,event_id FROM transaction")) {
                while (rs.next()) {
                    Transaction t = new Transaction(rs.getInt(1),
                            rs.getDate(2).toLocalDate(),
                            rs.getString(3),
                            rs.getDouble(4),
                            rs.getInt(5),
                            rs.getInt(6));
                    Platform.runLater(() -> DataStore.transactions.add(t));
                }
                Platform.runLater(DataStore::recomputeBalances);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public static void create(LocalDate d, String desc, double amt, int accId, int evId) {
        DBHelper.EXEC.execute(() -> {
            try (PreparedStatement ps = DBHelper.getConn().prepareStatement(
                    "INSERT INTO transaction(date,description,amount,account_id,event_id) VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, Date.valueOf(d));
                ps.setString(2, desc);
                ps.setDouble(3, amt);
                ps.setInt(4, accId);
                ps.setInt(5, evId);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) {
                        Transaction t = new Transaction(k.getInt(1), d, desc, amt, accId, evId);
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

    public static void createPlanned(LocalDate date, LocalDate due,
                                     String desc, double amount,
                                     int accId, Integer eventId) {
        DBHelper.EXEC.execute(() -> {
            try (PreparedStatement ps = DBHelper.getConn().prepareStatement(
                    "INSERT INTO transaction(date,due_date,description,amount,status,account_id,event_id) " +
                            "VALUES(?,?,?,?,?,?,?)")) {
                ps.setDate(1, Date.valueOf(date));
                ps.setDate(2, due == null ? null : Date.valueOf(due));
                ps.setString(3, desc);
                ps.setDouble(4, amount);
                ps.setString(5, "PLANNED");
                ps.setString(6, amount > 0 ? "INCOME" : "EXPENSE");
                ps.setInt(7, accId);
                if (eventId == null) ps.setNull(8, Types.BIGINT);
                else ps.setInt(8, eventId);
                ps.executeUpdate();
                DBHelper.commit();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public static void markSettled(int txId, LocalDate realDate) {
        DBHelper.EXEC.execute(() -> {
            try (var ps = DBHelper.getConn().prepareStatement(
                    "UPDATE transaction SET status='REALIZED', settled=TRUE, date=? WHERE id=?")) {
                ps.setDate(1, Date.valueOf(realDate));
                ps.setInt(2, txId);
                ps.executeUpdate();
                DBHelper.commit();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public static void deleteAsync(int id, Transaction t) {
        DBHelper.EXEC.execute(() -> {
            try (PreparedStatement ps = DBHelper.getConn().prepareStatement("DELETE FROM transaction WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                DBHelper.commit();
                Platform.runLater(() -> DataStore.transactions.remove(t));
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }
}