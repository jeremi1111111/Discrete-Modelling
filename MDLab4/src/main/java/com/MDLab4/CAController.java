package com.MDLab4;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CAController {
    @FXML
    Canvas canvas;

    @FXML
    VBox animationControlPanel;
    @FXML
    HBox iterationControlPanel;
    @FXML
    TextField currentIteration;
    @FXML
    Pane iterationChoicePanel;
    @FXML
    Button simulationSpeedButton;
    @FXML
    Button simulateButton;
    @FXML
    Button stopButton;

    @FXML
    VBox setupPanel;
    @FXML
    ComboBox<String> presetComboBox;
    @FXML
    ComboBox<String> BCComboBox;
    @FXML
    Button loadButton;
    @FXML
    HBox presetOptions;
    @FXML
    TextField elementsCount;
    @FXML
    Button densityButton;

    Timeline canvasAnimation;

    int maxImageHeight = 750;
    int maxImageWidth = 1600;
    int baseCellSize = 16;
    double baseLineWidth = 1;
    Board displayedBoard;
    final String[] presetArray = {
            "glider",
            "oscillator",
            "still",
            "random"
    };

    final String[] BCArray = {
            "absorbing",
            "periodic",
            "reflecting"
    };

    final String[] densityArray = {
            "Low",
            "Medium",
            "High"
    };

    final String[] speedArray = {
            "Slow",
            "Normal",
            "Fast",
            "Very fast"
    };

    final HashMap<String, Integer> speedMap = new HashMap<>(Map.of(
            "Slow", 300,
            "Normal", 100,
            "Fast", 50,
            "Very fast", 10
    ));

    final byte[][] gliderPreset = {
            {0, 0, 0, 0, 0},
            {0, 0, 1, 1, 0},
            {0, 1, 1, 0, 0},
            {0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0}
    };

    final byte[][] oscillatorPreset = {
            {0, 0, 0, 0, 0},
            {0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0}
    };

    final byte[][] staticPreset = {
            {0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 0, 0},
            {0, 1, 0, 0, 1, 0},
            {0, 0, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0}
    };

    final byte[][] oscillatorPreset2 = {
            {0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 0, 0},
            {0, 0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0}
    };

    @FXML
    protected void initialize() {
        canvas.setVisible(false);
        canvas.setManaged(false);
        stopButton.setVisible(false);
        stopButton.setManaged(false);
        simulationSpeedButton.setText(speedArray[2]);
        animationControlPanel.setVisible(false);
        animationControlPanel.setManaged(false);
        presetComboBox.setItems(Arrays.stream(presetArray).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        BCComboBox.setItems(Arrays.stream(BCArray).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        loadButton.setDisable(true);
        presetOptions.setVisible(false);
        presetOptions.setManaged(false);

        MainApplication.appStage.centerOnScreen();
    }

    @FXML
    protected void onPresetChoice() {
        if (presetComboBox.getValue() == null)
            return;
        presetOptions.setManaged(true);
        presetOptions.setVisible(true);
        MainApplication.appStage.sizeToScene();
        if (Objects.equals(elementsCount.getText(), ""))
            elementsCount.setText("100");
        if (BCComboBox.getValue() == null)
            return;
        loadButton.setDisable(false);
    }

    @FXML
    protected void onBCChoice() {
        if (BCComboBox.getValue() == null)
            return;
        if (presetComboBox.getValue() == null)
            return;
        loadButton.setDisable(false);
    }

    @FXML
    protected void onDensityButtonClick() {
        for (int i = 0; i < densityArray.length; i++)
            if (Objects.equals(densityButton.getText(), densityArray[i])) {
                densityButton.setText(densityArray[i + 1 == densityArray.length ? 0 : i + 1]);
                return;
            }
    }

    @FXML
    protected void onSimulationSpeedButtonClick() {
        for (int i = 0; i < speedArray.length; i++)
            if (Objects.equals(simulationSpeedButton.getText(), speedArray[i])) {
                simulationSpeedButton.setText(speedArray[i + 1 == speedArray.length ? 0 : i + 1]);
                return;
            }
    }

    @FXML
    protected void onPickIterationClick() {
        displayedBoard.drawIteration(canvas.getGraphicsContext2D(), Integer.parseInt(currentIteration.getText()));
    }

    @FXML
    protected void onPreviousClick() {
        int current = Integer.parseInt(currentIteration.getText());
        if (current > 0)
            current--;
        currentIteration.setText(String.valueOf(current));
        displayedBoard.drawIteration(canvas.getGraphicsContext2D(), current);
    }

    @FXML
    protected void onNextClick() {
        int current = Integer.parseInt(currentIteration.getText());
        current++;
        currentIteration.setText(String.valueOf(current));
        displayedBoard.drawIteration(canvas.getGraphicsContext2D(), current);
    }

    byte[][] rotate(byte[][] original) {
        byte[][] result = new byte[original[0].length][original.length];
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                result[j][original.length - 1 - i] = original[i][j];
            }
        }
        return result;
    }

    byte[][] reflect(byte[][] original, int axis) {
        byte[][] result = new byte[original.length][original[0].length];
        switch (axis) {
            case 0:
                for (int i = 0; i < original.length; i++) {
                    for (int j = 0; j < original[0].length; j++) {
                        result[i][original[0].length - 1 - j] = original[i][j];
                    }
                }
                break;
            case 1:
                for (int i = 0; i < original.length; i++) {
                    System.arraycopy(original[i], 0, result[original.length - 1 - i], 0, original[0].length);
                }
        }
        return result;
    }

    byte[][] generateStateMatrix() {
        byte[][] stateMatrix;
        String presetName = presetComboBox.getValue();
        int counter = Integer.parseInt(elementsCount.getText());
        int spots = (int) Math.sqrt(counter * 2);
        if (counter == 1)
            spots = 5;
        int density = 2;
        for (int i = 0; i < densityArray.length; i++)
            if (Objects.equals(densityArray[i], densityButton.getText())) density = i + 1;
        spots = spots * 3 / density;
        byte[][] chosenPreset;
        if (Objects.equals(presetName, presetArray[0])) chosenPreset = gliderPreset;
        else if (Objects.equals(presetName, presetArray[1])) chosenPreset = oscillatorPreset;
        else if (Objects.equals(presetName, presetArray[2])) chosenPreset = staticPreset;
        else {
            stateMatrix = new byte[spots][spots];
            int c = 0;
            while (c < counter) {
                int x = (int) (Math.random() * spots);
                int y = (int) (Math.random() * spots);
                if (stateMatrix[y][x] == 1)
                    continue;
                stateMatrix[y][x] = 1;
                c++;
            }
            return stateMatrix;
        }

        boolean[][] pasteMatrix = new boolean[spots][spots];
        int c = 0;
        while (c < counter) {
            int y = (int) (Math.random() * spots);
            int x = (int) (Math.random() * spots);
            if (pasteMatrix[y][x])
                continue;
            pasteMatrix[y][x] = true;
            c++;
        }

        int elementSize = Math.max(chosenPreset.length, chosenPreset[0].length);
        int base = elementSize / 2;
        stateMatrix = new byte[elementSize * (spots + 1)][elementSize * (spots + 1)];
        for (int y = 0; y < spots; y++) {
            for (int x = 0; x < spots; x++) {
                if (!pasteMatrix[y][x]) continue;
                int startY = base + elementSize * y;
                int startX = base + elementSize * x;
                chosenPreset = (Math.random() * 2 > 1 ? rotate(chosenPreset) : chosenPreset);
                chosenPreset = (Math.random() * 2 > 1 ? reflect(chosenPreset, (int) (Math.random() * 2)) : chosenPreset);
                for (int i = 0; i < chosenPreset.length; i++) {
                    System.arraycopy(chosenPreset[i], 0, stateMatrix[startY + i], startX, chosenPreset[0].length);
                }
            }
        }

        return stateMatrix;
    }

    @FXML
    protected void onLoadButtonClick() {
        canvas.setManaged(true);
        canvas.setVisible(true);
        currentIteration.setText("0");

        byte[][] stateMatrix = generateStateMatrix();

        int gridSizeX = stateMatrix[0].length;
        int gridSizeY = stateMatrix.length;
        int cellSize = Math.min(maxImageHeight / gridSizeY, maxImageWidth / gridSizeX);
        int imageHeight = gridSizeY * cellSize + 1;
        int imageWidth = gridSizeX * cellSize + 1;

        canvas.setHeight(imageHeight);
        canvas.setWidth(imageWidth);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, imageWidth, imageHeight);

        int boundaryCondition = 0;
        if (Objects.equals(BCComboBox.getValue(), BCArray[1])) boundaryCondition = 2;
        else if (Objects.equals(BCComboBox.getValue(), BCArray[2])) boundaryCondition = 3;

        Color aliveColor = Color.PINK;
        Color deadColor = Color.BLACK;
        displayedBoard = new Board(stateMatrix, cellSize, 0, boundaryCondition, aliveColor, deadColor);
        displayedBoard.drawIteration(gc, 0);
        animationControlPanel.setManaged(true);
        animationControlPanel.setVisible(true);
        simulateButton.setText("Simulate");
        MainApplication.appStage.sizeToScene();
        MainApplication.appStage.centerOnScreen();
    }

    @FXML
    protected void onSimulateButtonClick() {
        simulateButton.setVisible(false);
        simulateButton.setManaged(false);
        stopButton.setVisible(true);
        stopButton.setManaged(true);
        iterationControlPanel.setManaged(true);
        iterationControlPanel.setVisible(true);
        iterationChoicePanel.setDisable(true);
        setupPanel.setDisable(true);
        simulationSpeedButton.setDisable(true);
        currentIteration.setEditable(false);
        MainApplication.appStage.sizeToScene();
        MainApplication.appStage.centerOnScreen();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        canvasAnimation = new Timeline(new KeyFrame(Duration.millis(speedMap.get(simulationSpeedButton.getText())), (event) -> {
            int iteration = Integer.parseInt(currentIteration.getText());
            if (iteration == displayedBoard.getCurrentMaxIteration()) {
                displayedBoard.nextIteration();
            }
            iteration++;
            displayedBoard.updateCanvas(gc, iteration);
            currentIteration.setText(String.valueOf(iteration));
        }));
        canvasAnimation.setCycleCount(Timeline.INDEFINITE);
        canvasAnimation.play();
    }

    @FXML
    protected void onStopButtonClick() {
        canvasAnimation.stop();
        stopButton.setVisible(false);
        stopButton.setManaged(false);
        simulateButton.setVisible(true);
        simulateButton.setManaged(true);
        simulateButton.setText("Continue");
        setupPanel.setDisable(false);
        simulationSpeedButton.setDisable(false);
        iterationChoicePanel.setDisable(false);
        currentIteration.setEditable(true);
    }
}