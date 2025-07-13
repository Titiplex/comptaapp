package com.titiplex.comptaapp.dao;
import com.titiplex.comptaapp.*;
import com.titiplex.comptaapp.models.Transaction;
import java.sql.*;
import java.time.LocalDate;

public final class TransactionDao {
    public static void loadAllToStore() {
        DataStore.transactions.clear();
        try (Connection c = DBHelper.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,date,description,amount,account_id FROM transaction")) {
            while (rs.next())
                DataStore.transactions.add(new Transaction(
                        rs.getInt(1),
                        rs.getDate(2).toLocalDate(),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getInt(5)
                ));
        } catch (SQLException e) { throw new RuntimeException(e); }
        DataStore.recomputeBalances();
    }

    public static void create(LocalDate d, String desc, double amt, int accId) {
        try (Connection c = DBHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO transaction(date,description,amount,account_id) VALUES(?,?,?,?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(d));
            ps.setString(2, desc);
            ps.setDouble(3, amt);
            ps.setInt(4, accId);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next())
                    DataStore.transactions.add(new Transaction(k.getInt(1), d, desc, amt, accId));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        DataStore.recomputeBalances();
    }
}