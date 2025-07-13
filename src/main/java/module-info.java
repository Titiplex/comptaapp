module com.titiplex.comptaapp {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.h2database;

    opens com.titiplex.comptaapp to javafx.fxml;
    opens com.titiplex.comptaapp.controllers to javafx.fxml;
    opens com.titiplex.comptaapp.models to javafx.base;
    exports com.titiplex.comptaapp;
    exports com.titiplex.comptaapp.controllers;
}