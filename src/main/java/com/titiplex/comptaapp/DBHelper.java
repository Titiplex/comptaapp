package com.titiplex.comptaapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DBHelper {
    private static final String URL = "jdbc:h2:file:./compta;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final Connection CONN;

    static {
        try {
            CONN = DriverManager.getConnection(URL, "sa", "");
            try (Statement st = CONN.createStatement()) {
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS metadata(
                                meta_key   VARCHAR PRIMARY KEY,
                                meta_value VARCHAR
                            )
                        """);
                st.executeUpdate("CREATE TABLE IF NOT EXISTS event(id IDENTITY PRIMARY KEY,name VARCHAR NOT NULL UNIQUE,description VARCHAR)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS account(id IDENTITY PRIMARY KEY,name VARCHAR UNIQUE NOT NULL,balance DOUBLE DEFAULT 0)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS transaction(id IDENTITY PRIMARY KEY,date DATE NOT NULL,description VARCHAR,amount DOUBLE NOT NULL,account_id BIGINT NOT NULL,event_id BIGINT,status VARCHAR,due_date DATE,settled BOOLEAN DEFAULT FALSE,FOREIGN KEY(account_id) REFERENCES account(id),FOREIGN KEY(event_id) references event(id))");
            }
            CONN.setAutoCommit(false);
        } catch (SQLException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static final ExecutorService EXEC = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "db-thread");
        t.setDaemon(true);
        return t;
    });

    private DBHelper() {
    }

    public static Connection getConn() {
        return CONN;
    }

    public static void commit() {
        try {
            CONN.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void shutdown() {
        EXEC.shutdown();
        try {
            Path db = Path.of("compta.mv.db");
            if (Files.exists(db)) {
                Path bak = db.resolveSibling("compta.db.bak");
                Files.copy(db, bak, StandardCopyOption.REPLACE_EXISTING);
            }

            CONN.close();
        } catch (SQLException | IOException ignore) {
        }
    }
}