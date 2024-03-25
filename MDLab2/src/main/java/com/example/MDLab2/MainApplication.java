package com.example.MDLab2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class MainApplication extends Application {
    public static Stage appStage;
    public static BufferedImage originalImage;

    @Override
    public void start(Stage stage) throws IOException {
        appStage = stage;
        originalImage = ImageIO.read(new File("*your_greyscale_photo_here*.bmp"));
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 500);
        stage.setTitle("Menu");
        stage.setScene(scene);
        stage.show();
    }

    public static void loadNewScene(String fxmlName, int width, int height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxmlName));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        appStage.setScene(scene);
        appStage.centerOnScreen();
    }
    public static BufferedImage generateImage(BufferedImage original, byte[] newData) {
        DataBufferByte buffer = new DataBufferByte(newData, newData.length);
        WritableRaster raster = Raster.createWritableRaster(original.getSampleModel(), buffer, null);
        return new BufferedImage(original.getColorModel(), raster, original.getColorModel().isAlphaPremultiplied(), null);
    }

    public static void saveImage(BufferedImage im, String fileName) throws IOException {
        ImageIO.write(im, "bmp", new File(fileName));
    }

    public static void main(String[] args) {
        launch();
    }
}