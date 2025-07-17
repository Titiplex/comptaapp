package com.titiplex.comptaapp.util;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.Optional;

public final class PeriodDialog {
    private PeriodDialog() {
    }

    public static Optional<Period> ask(Window owner) {
        Dialog<Period> dlg = new Dialog<>();
        dlg.setTitle("Période d’export");
        if (owner != null) dlg.initOwner(owner);

        CheckBox enable = new CheckBox("Filtrer par date");
        DatePicker from = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker to = new DatePicker(LocalDate.now());
        from.setDisable(true);
        to.setDisable(true);

        enable.selectedProperty().addListener((o, b, sel) -> {
            from.setDisable(!sel);
            to.setDisable(!sel);
        });

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(6);
        gp.add(enable, 0, 0, 2, 1);
        gp.addRow(1, new Label("Du :"), from);
        gp.addRow(2, new Label("Au :"), to);
        dlg.getDialogPane().setContent(gp);

        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
        dlg.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;         // Cancel  ➜  Optional.empty()
            return enable.isSelected()
                    ? new Period(from.getValue(), to.getValue())   // filtre choisi
                    : new Period(null, null);                      // AUCUN filtre
        });
        return dlg.showAndWait();
    }

    /* Helper statique */
    public static boolean inPeriod(LocalDate d, Period p){
        if (p == null) return true;

        LocalDate from = p.from();
        LocalDate to   = p.to();

        if (from != null && d.isBefore(from)) return false;
        else return to == null || !d.isAfter(to);
    }
}
