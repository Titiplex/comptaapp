package com.titiplex.comptaapp;

import com.titiplex.comptaapp.dao.AccountDao;
import com.titiplex.comptaapp.dao.EventDao;
import com.titiplex.comptaapp.dao.TransactionDao;
import com.titiplex.comptaapp.util.AlertUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class WebApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // init DB + preload observable lists
        AccountDao.loadAllToStore();
        TransactionDao.loadAllToStore();
        EventDao.loadAllToStore();
        FXMLLoader fxmlLoader = new FXMLLoader(WebApp.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        DataStore.loadMetaSync();

        String companyName = DataStore.meta("companyName", "");
        String title = companyName.isBlank()
                ? "Compta App"
                : "Compta App – " + companyName;

        stage.setTitle(title);
        stage.setScene(scene);
        try {
            String logoPath = "/images/app_icon.png";
            URL logo = WebApp.class.getResource(logoPath);
            if (logo == null)
                AlertUtil.warning("Logo cannot be found : " + logoPath);
            else {
                stage.getIcons().add(new Image(logo.openStream()));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }         // pas de logo
        stage.show();

        stage.setOnCloseRequest(e -> {
            DBHelper.shutdown();
            System.exit(0);
            e.consume();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}