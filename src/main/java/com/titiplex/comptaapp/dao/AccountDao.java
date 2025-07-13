package com.titiplex.comptaapp.dao;
import com.titiplex.comptaapp.*;
import com.titiplex.comptaapp.models.Account;
import java.sql.*;

public final class AccountDao {
    public static void loadAllToStore() {
        DataStore.accounts.clear();
        try (Connection c = DBHelper.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,name,balance FROM account")) {
            while (rs.next())
                DataStore.accounts.add(new Account(rs.getInt(1), rs.getString(2), rs.getDouble(3)));
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static Account create(String name) {
        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO account(name,balance) VALUES(?,0)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) {
                    Account a = new Account(k.getInt(1), name, 0);
                    DataStore.accounts.add(a);
                    return a;
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public static void updateBalance(int id, double bal) {
        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE account SET balance=? WHERE id=?")) {
            ps.setDouble(1, bal); ps.setInt(2, id); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}