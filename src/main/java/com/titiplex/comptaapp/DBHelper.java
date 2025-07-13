package com.titiplex.comptaapp;

import java.sql.*;

public final class DBHelper {
    private static final String URL = "jdbc:h2:file:./compta;AUTO_SERVER=TRUE";

    static {
        try (Connection c = DriverManager.getConnection(URL, "sa", "");
             Statement st = c.createStatement()) {

            st.executeUpdate("CREATE TABLE IF NOT EXISTS account(" +
                    "id IDENTITY PRIMARY KEY," +
                    "name VARCHAR(255) UNIQUE NOT NULL," +
                    "balance DOUBLE DEFAULT 0 NOT NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS transaction(" +
                    "id IDENTITY PRIMARY KEY," +
                    "date DATE NOT NULL," +
                    "description VARCHAR(255)," +
                    "amount DOUBLE NOT NULL," +
                    "account_id BIGINT NOT NULL," +
                    "FOREIGN KEY(account_id) REFERENCES account(id))");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }
}