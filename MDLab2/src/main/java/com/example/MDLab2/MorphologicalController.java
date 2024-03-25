package com.example.MDLab2;

import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class MorphologicalController {
    @FXML
    ImageView imageView;
    @FXML
    Button loadButton;
    @FXML
    Button saveButton;
    @FXML
    Button backButton;
    @FXML
    Button dilationButton;
    @FXML
    Button erosionButton;
    @FXML
    Button openingButton;
    @FXML
    Button closingButton;
    @FXML
    TextField textField;
    @FXML
    Label label;
    @FXML
    ComboBox<String> comboBox;
    BufferedImage loadedImage;
    BufferedImage currentImage;
    String saveFileName;
    int threshold;

    void setImageView(BufferedImage im) {
        Image imageToSet = SwingFXUtils.toFXImage(im, null);
        imageView.setImage(imageToSet);
    }

    BufferedImage dilatation(BufferedImage original, int radius) {
        byte[] originalData = ((DataBufferByte) original.getData().getDataBuffer()).getData();
        byte[] imageData = new byte[originalData.length];

        int height = original.getHeight();
        int width = original.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (originalData[y * width + x] == 0) continue;
                byte val = -1;
                for (int dy = -radius; dy <= radius; dy++) {
                    int cy = y + dy;
                    if (cy < 0 || cy >= height) continue;
                    for (int dx = -radius; dx <= radius; dx++) {
                        int cx = x + dx;
                        if (cx < 0 || cx >= width) continue;
                        if (originalData[cy * width + cx] == 0) {
                            val = 0;
                            break;
                        }
                    }
                    if (val == 0) break;
                }
                imageData[y * width + x] = val;
            }
        }

        return MainApplication.generateImage(original, imageData);
    }
    BufferedImage erosion(BufferedImage original, int radius) {
        byte[] originalData = ((DataBufferByte) original.getData().getDataBuffer()).getData();
        byte[] imageData = new byte[originalData.length];

        int height = original.getHeight();
        int width = original.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (originalData[y * width + x] == -1) {
                    imageData[y * width + x] = -1;
                    continue;
                }
                byte val = 0;
                for (int dy = -radius; dy <= radius; dy++) {
                    int cy = y + dy;
                    if (cy < 0 || cy >= height) continue;
                    for (int dx = -radius; dx <= radius; dx++) {
                        int cx = x + dx;
                        if (cx < 0 || cx >= width) continue;
                        if (originalData[cy * width + cx] == -1) {
                            val = -1;
                            break;
                        }
                    }
                    if (val == -1) break;
                }
                imageData[y * width + x] = val;
            }
        }

        return MainApplication.generateImage(original, imageData);
    }

    public void initialize() throws IOException {
        setImageView(MainApplication.originalImage);

        var files = Files.list(Paths.get("images"))
                .filter(file -> !Files.isDirectory(file))
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(str -> str.startsWith("binarization"))
                .filter(str -> str.endsWith(".bmp"))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        comboBox.setItems(files);
        saveFileName = "";
        threshold = 20;
    }
    public void onBackButtonClick() throws IOException {
        MainApplication.loadNewScene("main-view.fxml", 640, 500);
        MainApplication.appStage.setTitle("Menu");
    }
    public void onLoadButtonClick() throws IOException {
        String fileName = comboBox.getValue();
        if (fileName == null) {
            label.setText("File not chosen.");
            label.setTextFill(Paint.valueOf("RED"));
            return;
        }
        loadedImage = ImageIO.read(new File("images/" + fileName));
        currentImage = loadedImage;
        setImageView(currentImage);
        label.setText("Loaded " + fileName + " file.");
        label.setTextFill(Paint.valueOf("GREEN"));
        saveFileName = fileName.replace(".bmp", "");
    }
    public void onSaveButtonClick() throws IOException {
        MainApplication.saveImage(currentImage, "output/" + saveFileName + ".bmp");
        label.setText("Saved " + saveFileName + ".bmp file.");
        label.setTextFill(Paint.valueOf("GREEN"));
    }
    public void onDilationButtonClick() throws IOException {
        if (loadImage()) return;
        int radius = Integer.parseInt(textField.getText());
        currentImage = dilatation(currentImage, radius);
        setImageView(currentImage);
        saveFileName += "-d" + radius;
        label.setText("Applied dilation.");
        label.setTextFill(Paint.valueOf("BLACK"));
    }

    private boolean loadImage() {
        if (loadedImage == null) {
            label.setText("File not chosen.");
            label.setTextFill(Paint.valueOf("RED"));
            return true;
        }
        if (Integer.parseInt(textField.getText()) > threshold) {
            label.setText("Max value exceeded (" + threshold + ").");
            label.setTextFill(Paint.valueOf("RED"));
            return true;
        }
        return false;
    }

    public void onErosionButtonClick() throws IOException {
        if (loadImage()) return;
        int radius = Integer.parseInt(textField.getText());
        currentImage = erosion(currentImage, radius);
        setImageView(currentImage);
        saveFileName += "-e" + radius;
        label.setText("Applied erosion.");
        label.setTextFill(Paint.valueOf("BLACK"));
    }
    public void onOpeningButtonClick() throws IOException {
        if (loadImage()) return;
        int radius = Integer.parseInt(textField.getText());
        currentImage = dilatation(erosion(currentImage, radius), radius);
        setImageView(currentImage);
        saveFileName += "-o" + radius;
        label.setText("Applied morphological opening.");
        label.setTextFill(Paint.valueOf("BLACK"));
    }
    public void onClosingButtonClick() throws IOException {
        if (loadImage()) return;
        int radius = Integer.parseInt(textField.getText());
        currentImage = erosion(dilatation(currentImage, radius), radius);
        setImageView(currentImage);
        saveFileName += "-c" + radius;
        label.setText("Applied morphological closing.");
        label.setTextFill(Paint.valueOf("BLACK"));
    }
}
