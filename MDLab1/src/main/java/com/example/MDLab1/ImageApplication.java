package com.example.MDLab1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ImageApplication extends Application {
    public static Stage appStage;
    @Override
    public void start(Stage stage) throws IOException {
        appStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(ImageApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 430);
        stage.setTitle("Menu");
        stage.setScene(scene);
        stage.show();
    }
    public static void loadNewScene(String fxmlName, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ImageApplication.class.getResource(fxmlName));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        appStage.setScene(scene);
        appStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch();
    }
}