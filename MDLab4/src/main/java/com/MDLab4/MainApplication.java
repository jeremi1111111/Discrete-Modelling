package com.MDLab4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    public static Stage appStage;
    @Override
    public void start(Stage stage) throws IOException {
        appStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("ca-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Cellular 2D automata");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}