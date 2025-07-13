package com.titiplex.comptaapp.dao;

import com.titiplex.comptaapp.DBHelper;
import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Account;
import javafx.application.Platform;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class AccountDao {

    public static void loadAllToStore() {
        DBHelper.EXEC.execute(() -> {
            DataStore.accounts.clear();
            try (Statement st = DBHelper.getConn().createStatement();
                 ResultSet rs = st.executeQuery("SELECT id,name,balance FROM account")) {
                while (rs.next()) {
                    Account a = new Account(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                    Platform.runLater(() -> DataStore.accounts.add(a));
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public static void create(String name) {
        DBHelper.EXEC.execute(() -> {
            try (PreparedStatement ps = DBHelper.getConn().prepareStatement(
                    "INSERT INTO account(name,balance) VALUES(?,0)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) {
                        Account a = new Account(k.getInt(1), name, 0);
                        Platform.runLater(() -> DataStore.accounts.add(a));
                    }
                }
                DBHelper.commit();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public static void deleteAsync(int id, Account a) {
        DBHelper.EXEC.execute(() -> {
            try (PreparedStatement ps = DBHelper.getConn().prepareStatement("DELETE FROM account WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                DBHelper.commit();
                Platform.runLater(() -> DataStore.accounts.remove(a));
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }
}