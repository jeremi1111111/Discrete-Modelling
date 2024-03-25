package com.example.MDLab2;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ConvolutionController {

    @FXML
    ImageView imageView;
    @FXML
    Button loadButton;
    @FXML
    Button backButton;
    @FXML
    Label leftLabel;
    @FXML
    Label rightLabel;
    @FXML
    ComboBox<String> comboBox;
    String loadedMaskName;
    BufferedImage currentImage;

    void setImageView(BufferedImage im) {
        Image imageToSet = SwingFXUtils.toFXImage(im, null);
        imageView.setImage(imageToSet);
    }
    int[][] byteArrayToIntMatrix(byte[] array, int height, int width) {
        int[][] matrix = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = Byte.toUnsignedInt(array[i * width + j]);
            }
        }
        return matrix;
    }

    byte[] intMatrixToByteArray(int[][] matrix, int height, int width) {
        byte[] array = new byte[height * width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                array[i * width + j] = (byte) matrix[i][j];
            }
        }
        return array;
    }

    BufferedImage applyMask(BufferedImage original, double[][] mask, int radius) throws IOException {
        byte[] originalData = ((DataBufferByte) original.getData().getDataBuffer()).getData();

        int height = original.getHeight();
        int width = original.getWidth();
        double maskWeight = 0;
        for (double[] dm : mask) {
            for (double v : dm) {
                maskWeight += v;
            }
        }

        int[][] originalMatrix = byteArrayToIntMatrix(originalData, height, width);
        int[][] newMatrix = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double currentWeight = 0;
                double newVal = 0;
                for (int dy = -radius, my = 0; dy <= radius; dy++, my++) {
                    int cy = y + dy;
                    if (cy < 0 || cy >= height) continue;
                    for (int dx = -radius, mx = 0; dx <= radius; dx++, mx++) {
                        int cx = x + dx;
                        if (cx < 0 || cx >= width) continue;
                        currentWeight += mask[my][mx];
                        newVal += mask[my][mx] * originalMatrix[cy][cx];
                    }
                }
                newVal *= maskWeight / currentWeight;
                if (newVal < 0) newVal = 0;
                else if (newVal > 255) newVal = 255;
                newMatrix[y][x] = (int) newVal;
            }
        }

        byte[] newData = intMatrixToByteArray(newMatrix, height, width);
        return MainApplication.generateImage(original, newData);
    }

    public void initialize() throws IOException {
        setImageView(MainApplication.originalImage);

        var files = Files.list(Paths.get("masks"))
                .filter(file -> !Files.isDirectory(file))
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(str -> str.endsWith("csv"))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        comboBox.setItems(files);
    }

    public void onBackButtonClick() throws IOException {
        MainApplication.loadNewScene("main-view.fxml", 640, 500);
        MainApplication.appStage.setTitle("Menu");
    }

    public void onLoadButtonClick() throws IOException {
        String fileName = comboBox.getValue();
        if (fileName == null) {
            rightLabel.setText("No file chosen.");
            rightLabel.setTextFill(Paint.valueOf("RED"));
            return;
        }
        rightLabel.setText("Loaded " + fileName + " file.");
        rightLabel.setTextFill(Paint.valueOf("GREEN"));
        loadedMaskName = fileName.replace(".csv", "");

        ArrayList<double[]> maskData = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("masks/" + fileName));
        String newLine;

        while ((newLine = reader.readLine()) != null) {
            maskData.add(Arrays.stream(newLine.replace("\uFEFF", "").split(",|;"))
                    .mapToDouble(Double::parseDouble)
                    .toArray());
        }
        double[][] mask = new double[maskData.size()][];
        maskData.toArray(mask);

        int radius = (mask.length - 1) / 2;
        leftLabel.setText("Mask radius: " + radius);

        currentImage = applyMask(MainApplication.originalImage, mask, radius);
        setImageView(currentImage);
    }

    public void onSaveButtonClick() throws IOException {
        if (currentImage == null) {
            rightLabel.setText("File not chosen.");
            rightLabel.setTextFill(Paint.valueOf("RED"));
            return;
        }
        MainApplication.saveImage(currentImage, "output/" + loadedMaskName + ".bmp");
        rightLabel.setText("Saved " + loadedMaskName + ".bmp file");
        rightLabel.setTextFill(Paint.valueOf("GREEN"));
    }
}
