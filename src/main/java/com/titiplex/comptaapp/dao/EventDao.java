package com.titiplex.comptaapp.dao;

import com.titiplex.comptaapp.DBHelper;
import com.titiplex.comptaapp.DataStore;
import com.titiplex.comptaapp.models.Event;
import javafx.application.Platform;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class EventDao {

    public static void loadAllToStore() {
        DBHelper.EXEC.execute(() -> {
            DataStore.events.clear();
            try (Statement st = DBHelper.getConn().createStatement();
                 ResultSet rs = st.executeQuery("SELECT id,name,description FROM event")) {
                while (rs.next()) {
                    Event ev = new Event(rs.getInt(1), rs.getString(2), rs.getString(3));
                    Platform.runLater(() -> DataStore.events.add(ev));
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public static void create(String name, String description) {
        DBHelper.EXEC.execute(() -> {
            try (PreparedStatement ps = DBHelper.getConn().prepareStatement(
                    "INSERT INTO event(name,description) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, description);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    if (k.next()) {
                        Event ev = new Event(k.getInt(1), name, description);
                        Platform.runLater(() -> DataStore.events.add(ev));
                    }
                }
                DBHelper.commit();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });
    }
}
