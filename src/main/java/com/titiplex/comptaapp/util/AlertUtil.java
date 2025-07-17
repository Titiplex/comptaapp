package com.titiplex.comptaapp.util;

import javafx.scene.control.Alert;

public interface AlertUtil {
    static void error(String m) {
        new Alert(Alert.AlertType.ERROR, m).showAndWait();
    }
    static void warning(String m) {
        new Alert(Alert.AlertType.WARNING, m).showAndWait();
    }
    static void info(String m) {
        new Alert(Alert.AlertType.INFORMATION, m).showAndWait();
    }
    static void confirm(String m) {
        new Alert(Alert.AlertType.CONFIRMATION, m).showAndWait();
    }
}
