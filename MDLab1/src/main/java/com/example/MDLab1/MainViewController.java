package com.example.MDLab1;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.example.MDLab1.ImageApplication.loadNewScene;

public class MainViewController {
    public static final String bmpFileName = "*your_greyscale_photo_here*.bmp";
    @FXML
    ImageView imageView;
    @FXML
    Button dimmingButton;
    @FXML
    Button brighteningButton;
    @FXML
    Button binarizationButton;

    public void initialize() throws IOException {
        BufferedImage im = ImageIO.read(new File(bmpFileName));
        Image imageToSet = SwingFXUtils.toFXImage(im, null);
        imageView.setImage(imageToSet);
    }

    public void onDimmingButtonClick() throws IOException {
        loadNewScene("dimming.fxml", 640, 430);
        ImageApplication.appStage.setTitle("Dimming");
    }

    public void onBinarizationButtonClick() throws IOException {
        loadNewScene("binarization.fxml", 640, 430);
        ImageApplication.appStage.setTitle("Binarization");
    }

    public void onBrighteningButtonClick() throws IOException {
        loadNewScene("brightening.fxml", 1280, 780);
        ImageApplication.appStage.setTitle("Brightening");
    }
}
