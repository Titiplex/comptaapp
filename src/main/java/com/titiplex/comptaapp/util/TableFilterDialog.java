package com.titiplex.comptaapp.util;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.*;
import java.util.stream.Collectors;

public final class TableFilterDialog {
    private TableFilterDialog() {
    }

    public static Optional<Set<String>> selectTables(Window owner, List<String> allTables) {
        Dialog<Set<String>> dialog = new Dialog<>();
        dialog.setTitle("Sélection des tables");
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);

        ButtonType okType = new ButtonType("Exporter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        // UI
        List<CheckBox> checks = new ArrayList<>();
        allTables.forEach(t -> {
            CheckBox cb = new CheckBox(t);
            cb.setSelected(true); // par défaut: tout coché
            checks.add(cb);
        });

        ListView<CheckBox> list = new ListView<>();
        list.getItems().addAll(checks);
        list.setPrefSize(380, 320);

        Button selAll = new Button("Tout cocher");
        Button selNone = new Button("Tout décocher");
        HBox actions = new HBox(10, selAll, selNone);

        selAll.setOnAction(_ -> checks.forEach(cb -> cb.setSelected(true)));
        selNone.setOnAction(_ -> checks.forEach(cb -> cb.setSelected(false)));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(list);
        root.setBottom(actions);
        BorderPane.setMargin(actions, new Insets(10, 0, 0, 0));

        dialog.getDialogPane().setContent(root);

        // Désactiver OK si aucune table cochée
        Node okBtn = dialog.getDialogPane().lookupButton(okType);
        Observable[] dependencies = checks.stream()
                .map(cb -> (Observable) cb.selectedProperty())
                .toArray(Observable[]::new);

        BooleanBinding noSelectionBinding = Bindings.createBooleanBinding(
                () -> checks.stream().noneMatch(CheckBox::isSelected),
                dependencies
        );
        okBtn.disableProperty().bind(noSelectionBinding);

        dialog.setResultConverter(bt -> {
            if (bt == okType) {
                return checks.stream().filter(CheckBox::isSelected)
                        .map(CheckBox::getText).collect(Collectors.toCollection(LinkedHashSet::new));
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
