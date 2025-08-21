package com.titiplex.comptaapp.util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.Optional;

public final class FormatsDialog {
    public record Choice(boolean sql, boolean csv, boolean xlsx) {
    }

    private FormatsDialog() {
    }

    public static Optional<Choice> ask(Window owner) {
        Dialog<Choice> d = new Dialog<>();
        d.setTitle("Formats d'export");
        d.initOwner(owner);
        d.initModality(Modality.WINDOW_MODAL);

        ButtonType okType = new ButtonType("Continuer", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        CheckBox sql = new CheckBox("SQL dump (.sql)");
        CheckBox csv = new CheckBox("CSV (fichier par table)");
        CheckBox xlsx = new CheckBox("Excel (.xlsx)");
        sql.setSelected(true);
        csv.setSelected(true);
        xlsx.setSelected(true);

        VBox box = new VBox(12, new Label("Choisissez les formats à inclure dans le ZIP :"), sql, csv, xlsx);
        box.setPadding(new Insets(10));
        d.getDialogPane().setContent(box);

        d.setResultConverter(bt -> {
            if (bt == okType) return new Choice(sql.isSelected(), csv.isSelected(), xlsx.isSelected());
            return null;
        });

        return d.showAndWait();
    }
}
