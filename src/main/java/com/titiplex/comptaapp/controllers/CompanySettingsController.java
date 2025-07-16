package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CompanySettingsController {

    // champs éditables
    @FXML
    private TextField nameField, countryField, addrField;
    // labels lecture seule
    @FXML
    private Label curName, curCountry, curAddr;
    @FXML
    private Button saveBtn;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        // charge & affiche
        refreshDisplay();

        saveBtn.setOnAction(_ -> save());
    }

    private void refreshDisplay() {
        String n = DataStore.meta("companyName", "");
        String c = DataStore.meta("country", "");
        String a = DataStore.meta("address", "");

        nameField.setText(n);
        countryField.setText(c);
        addrField.setText(a);

        curName.setText(n);
        curCountry.setText(c);
        curAddr.setText(a);
    }

    private void save() {
        String n = nameField.getText().trim();
        String c = countryField.getText().trim();
        String a = addrField.getText().trim();

        DataStore.saveMeta("companyName", n);
        DataStore.saveMeta("country", c);
        DataStore.saveMeta("address", a);

        // met à jour immédiatement les labels lecture seule
        curName.setText(n);
        curCountry.setText(c);
        curAddr.setText(a);

        statusLabel.setText("Enregistré !");
        new Thread(() -> {                // efface après 2 s
            try {
                Thread.sleep(2000);
            } catch (Exception ignored) {
            }
            Platform.runLater(() -> statusLabel.setText(""));
        }).start();
    }

}
