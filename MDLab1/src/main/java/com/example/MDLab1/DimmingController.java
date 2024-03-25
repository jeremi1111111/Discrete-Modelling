package com.example.MDLab1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.Buffer;

import static com.example.MDLab1.ImageApplication.loadNewScene;

public class DimmingController {
    @FXML
    ImageView imageView;
    @FXML
    Slider slider;
    @FXML
    Button applyButton;
    @FXML
    Button resetButton;
    @FXML
    Button backButton;
    BufferedImage originalImage;

    public void setImageView(BufferedImage im) {
        Image imageToSet = SwingFXUtils.toFXImage(im, null);
        imageView.setImage(imageToSet);
    }

    public BufferedImage changeBrightness(BufferedImage im, double percentage) throws IOException {
        int change = (int) percentage * 256 / 100;
        byte[] imData = ((DataBufferByte) im.getData().getDataBuffer()).getData();
        int dataSize = imData.length;
        for (int i = 0; i < dataSize; i++) {
            int val = Byte.toUnsignedInt(imData[i]) - change;
            if (val > 255)
                val = 255;
            else if (val < 0)
                val = 0;
            imData[i] = (byte) val;
        }
        DataBufferByte buffer = new DataBufferByte(imData, imData.length);
        WritableRaster raster = Raster.createWritableRaster(im.getSampleModel(), buffer, null);
        BufferedImage result = new BufferedImage(im.getColorModel(), raster, im.getColorModel().isAlphaPremultiplied(), null);
        ImageIO.write(result, "bmp", new File("output/dimming-" + (int) percentage + ".bmp"));
        return result;
    }

    public void initialize() throws IOException {
        originalImage = ImageIO.read(new File(MainViewController.bmpFileName));
        setImageView(originalImage);
    }

    public void onApplyButtonClick() throws IOException {
        double change = slider.getValue();
        BufferedImage alteredImage = changeBrightness(originalImage, change);
        setImageView(alteredImage);
    }

    public void onResetButtonClick() {
        setImageView(originalImage);
        slider.setValue(0);
    }

    public void onBackButtonClick() throws IOException {
        loadNewScene("main-view.fxml", 640, 430);
        ImageApplication.appStage.setTitle("Menu");
    }
}