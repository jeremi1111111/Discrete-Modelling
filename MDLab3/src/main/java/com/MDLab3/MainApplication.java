package com.MDLab3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;


public class MainApplication extends Application {
    public static Stage appStage;

    @Override
    public void start(Stage stage) throws IOException {
        appStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("elementary-ca.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cellular 1D Automata");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
