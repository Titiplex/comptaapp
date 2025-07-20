package com.titiplex.comptaapp.controllers;

import com.titiplex.comptaapp.DataStore;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CompanySettingsController {

    @FXML
    private TextField nameField, countryField, addrField;
    @FXML
    private Label curName, curCountry, curAddr, statusLabel;
    @FXML
    private DialogPane dialogPane;

    private boolean dirty = false;   // pour savoir si on a effectivement sauvegardé

    @FXML
    private void initialize() {
        loadValues();
//        setupButtons();
    }

    public void applyIfValidOnOk() {
        // valide, puis save
        save();
        dirty = true;
    }

    private void loadValues() {
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

//    private void setupButtons() {
//        Button saveBtn = (Button) dialogPane.lookupButton(
//                dialogPane.getButtonTypes().stream()
//                        .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
//                        .findFirst().orElseThrow()
//        );
//        Button cancelBtn = (Button) dialogPane.lookupButton(
//                dialogPane.getButtonTypes().stream()
//                        .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE)
//                        .findFirst().orElseThrow()
//        );
//
//        saveBtn.setOnAction(_ -> {
//            save();
//            dirty = true;
//            // fermer après un très léger délai (UI feedback possible)
//            dialogPane.getScene().getWindow().hide();
//        });
//
//        cancelBtn.setOnAction(_ -> {
//            dirty = false;
//            dialogPane.getScene().getWindow().hide();
//        });
//
//        // ESC fermera automatiquement (DialogPane gère CANCEL_CLOSE)
//    }

    private void save() {
        String n = nameField.getText().trim();
        String c = countryField.getText().trim();
        String a = addrField.getText().trim();
        DataStore.saveMeta("companyName", n);
        DataStore.saveMeta("country", c);
        DataStore.saveMeta("address", a);
        curName.setText(n);
        curCountry.setText(c);
        curAddr.setText(a);
        statusLabel.setText("Enregistré !");
        Platform.runLater(() -> {
            // effacer le message après 2 s (optionnel)
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(() -> statusLabel.setText(""));
            }).start();
        });
    }

    /**
     * Appelé par le code qui ouvre la boîte pour savoir si on a enregistré.
     */
    public boolean isDirty() {
        return dirty;
    }
}
