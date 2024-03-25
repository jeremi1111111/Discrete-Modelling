package com.example.MDLab1;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.example.MDLab1.ImageApplication.loadNewScene;

public class BrighteningController {
    @FXML
    ImageView image1;
    @FXML
    ImageView image2;
    @FXML
    ImageView image3;
    @FXML
    Slider slider;
    @FXML
    Button applyButton;
    @FXML
    Button backButton;
    BufferedImage originalImage;

    public void setImageView(ImageView imView, BufferedImage im) {
        Image imageToSet = SwingFXUtils.toFXImage(im, null);
        imView.setImage(imageToSet);
    }

    BufferedImage changeBrightness(BufferedImage im, double percentage) throws IOException {
        int change = (int) (percentage * 256 / 100);
        byte[] imData = ((DataBufferByte) im.getData().getDataBuffer()).getData();
        int dataSize = imData.length;
        for (int i = 0; i < dataSize; i++) {
            int val = Byte.toUnsignedInt(imData[i]) + change;
            if (val > 255)
                val = 255;
            else if (val < 0)
                val = 0;
            imData[i] = (byte) val;
        }
        DataBufferByte buffer = new DataBufferByte(imData, imData.length);
        WritableRaster raster = Raster.createWritableRaster(im.getSampleModel(), buffer, null);
        BufferedImage result = new BufferedImage(im.getColorModel(), raster, im.getColorModel().isAlphaPremultiplied(), null);
        ImageIO.write(result, "bmp", new File("output/brightening-" + (int) percentage + ".bmp"));
        return result;
    }

    void brighten(BufferedImage im, double percentage) throws IOException {
        setImageView(image1, changeBrightness(im, percentage));
        setImageView(image2, changeBrightness(im, 2 * percentage));
        setImageView(image3, changeBrightness(im, 3 * percentage));
    }

    public void initialize() throws IOException {
        originalImage = ImageIO.read(new File(MainViewController.bmpFileName));
        brighten(originalImage, 10);
        slider.setValue(10);
    }

    public void onApplyButtonClick() throws IOException {
        double change = slider.getValue();
        brighten(originalImage, change);
    }

    public void onBackButtonClick() throws IOException {
        loadNewScene("main-view.fxml", 640, 430);
        ImageApplication.appStage.setTitle("Menu");
    }
}
