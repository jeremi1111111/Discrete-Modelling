package com.example.MDLab2;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

import static com.example.MDLab2.MainApplication.loadNewScene;

public class MainViewController {
    @FXML
    ImageView imageView;
    @FXML
    Button convolutionButton;
    @FXML
    Button morphologicalButton;
    @FXML
    Button quitButton;

    public void initialize() {
        // only 8-bit bmp
        Image imageToSet = SwingFXUtils.toFXImage(MainApplication.originalImage, null);
        imageView.setImage(imageToSet);
    }

    public void onConvolutionButtonClick() throws IOException {
        loadNewScene("convolution.fxml", 640, 500);
        MainApplication.appStage.setTitle("Convolution");
    }

    public void onMorphologicalButtonClick() throws IOException {
        loadNewScene("morphological.fxml", 640, 500);
        MainApplication.appStage.setTitle("Morphological operations");
    }

    public void onQuitButtonClick() {
        System.exit(0);
    }
}
