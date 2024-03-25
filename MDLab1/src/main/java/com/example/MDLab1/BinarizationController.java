package com.example.MDLab1;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static com.example.MDLab1.ImageApplication.loadNewScene;

public class BinarizationController {
    @FXML
    ImageView imageView;
    @FXML
    TextField field;
    @FXML
    Button applyButton;
    @FXML
    Button backButton;

    BufferedImage originalImage;
    int[] valueCount;

    void setImageView(BufferedImage im) {
        Image imageToSet = SwingFXUtils.toFXImage(im, null);
        imageView.setImage(imageToSet);
    }

    BufferedImage binarize(BufferedImage im, int thresholdPercentage) throws IOException {
        byte[] imData = ((DataBufferByte) im.getData().getDataBuffer()).getData();
        int dataSize = imData.length;
        int thresholdValue = 0;
        int counter = 0;
        int stopCondition = thresholdPercentage * dataSize / 100;
        for (int i = 0; i < valueCount.length; i++) {
            counter += valueCount[i];
            if (counter >= stopCondition) {
                thresholdValue = i;
                break;
            }
        }
        for (int i = 0; i < dataSize; i++) {
            int val = Byte.toUnsignedInt(imData[i]);
            if (val < thresholdValue)
                imData[i] = 0;
            else
                imData[i] = -1;
        }
        DataBufferByte buffer = new DataBufferByte(imData, imData.length);
        WritableRaster raster = Raster.createWritableRaster(im.getSampleModel(), buffer, null);
        BufferedImage result = new BufferedImage(im.getColorModel(), raster, im.getColorModel().isAlphaPremultiplied(), null);
        ImageIO.write(result, "bmp", new File("output/binarization-" + thresholdPercentage + ".bmp"));
        return result;
    }

    void countValues() {
        byte[] imData = ((DataBufferByte) originalImage.getData().getDataBuffer()).getData();
        int[] counters = new int[256];
        Arrays.fill(counters, 0);
        for (byte b : imData) {
            int i = Byte.toUnsignedInt(b);
            counters[i]++;
        }
        valueCount = counters;
    }

    public void initialize() throws IOException {
        originalImage = ImageIO.read(new File(MainViewController.bmpFileName));
        countValues();
        field.setText("50");
        setImageView(binarize(originalImage, 50));
    }

    public void onBackButtonClick() throws IOException {
        loadNewScene("main-view.fxml", 640, 430);
        ImageApplication.appStage.setTitle("Menu");
    }

    public void onApplyButtonClick() throws IOException {
        int threshold = Integer.parseInt(field.getText());
        setImageView(binarize(originalImage, threshold));
    }
}
