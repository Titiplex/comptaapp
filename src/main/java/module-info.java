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
    requires com.github.librepdf.openpdf;
    requires java.desktop;

    opens com.titiplex.comptaapp to javafx.fxml;
    opens com.titiplex.comptaapp.controllers to javafx.fxml;
    opens com.titiplex.comptaapp.models to javafx.base;
    exports com.titiplex.comptaapp;
    exports com.titiplex.comptaapp.controllers;
    exports com.titiplex.comptaapp.models;
    exports com.titiplex.comptaapp.util;
    opens com.titiplex.comptaapp.util to javafx.base;
}