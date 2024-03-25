package com.MDLab3;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static com.MDLab3.MainApplication.appStage;

public class ElementaryCAController {
    @FXML
    ImageView generatedImage;
    @FXML
    VBox controlPanel;
    @FXML
    Button optionsButton;
    @FXML
    TextField cellCount;
    @FXML
    TextField ruleNumber;
    @FXML
    Button saveButton;
    @FXML
    HBox optionsBox;
    @FXML
    TextField iterationCount;
    @FXML
    CheckBox randomCheck;
    @FXML
    CheckBox BCCheck;
    @FXML
    TextField BCText;
    @FXML
    CheckBox animate;

    private static final int maxWidth = 800;
    private static final int maxHeight = 800;
    int cellSize;
    boolean[][] simulationData;

    private boolean[] getNextIteration(final boolean[] state, final int rule, boolean boundaryLeft, boolean boundaryRight) {
        boolean[] next = new boolean[state.length];
        int check = (boundaryLeft ? 1 : 0);
        check = (check << 1) + (state[0] ? 1 : 0);
        for (int i = 1; i < state.length; i++) {
            check = ((check << 1) + (state[i] ? 1 : 0)) & 0b111;
            next[i - 1] = ((rule & (1 << check)) != 0);
        }
        check = (check << 1) + (boundaryRight ? 1 : 0) & 0b111;
        next[next.length - 1] = ((rule & (1 << check)) != 0);
        return next;
    }

    private void updateImage(BufferedImage image, final boolean[] state, int iteration) {
        for (int i = 0; i < state.length; i++)
            image.setRGB(i, iteration, (state[i] ? Color.black.getRGB() : Color.white.getRGB()));
    }

    BufferedImage initImage(int cells, int iterations) {
        BufferedImage newImage = new BufferedImage(cells, iterations, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.setColor(Color.white);
        graphics2D.fillRect(0, 0, cells, iterations);
        return newImage;
    }

    private void simulateAutomaton(final boolean[] initialState, int rule, boolean isPeriodic, int iterations) {
        controlPanel.setDisable(true);
        boolean[][] result = new boolean[iterations][];
        result[0] = initialState;
        String bcText = BCText.getText();
        final boolean initialBoundaryLeft = (!isPeriodic && bcText.charAt(0) == '1');
        final boolean initialBoundaryRight = (!isPeriodic && bcText.charAt(1) == '1');

        BufferedImage image = initImage(initialState.length, iterations);
        int w = image.getWidth() * cellSize;
        int h = image.getHeight() * cellSize;
        if (w > maxWidth) {
            h = (int) Math.ceil((double) h * maxWidth / w);
            w = maxWidth;
        }
        if (h > maxHeight) {
            w = (int) Math.ceil((double) w * maxHeight / h);
            h = maxHeight;
        }
        double scale = (double) w / image.getWidth();
        AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        updateImage(image, initialState, 0);
        BufferedImage rescaled = new BufferedImage(w, h, image.getType());
        ato.filter(image, rescaled);
        generatedImage.setImage(SwingFXUtils.toFXImage(rescaled, null));
        appStage.sizeToScene();
        appStage.centerOnScreen();

        Thread thread = new Thread(() -> {
            boolean boundaryLeft = initialBoundaryLeft;
            boolean boundaryRight = initialBoundaryRight;
            boolean[] currentState = initialState;

            for (int i = 1; i < iterations; i++) {
                if (isPeriodic) {
                    boundaryLeft = currentState[initialState.length - 1];
                    boundaryRight = currentState[0];
                }
                currentState = getNextIteration(currentState, rule, boundaryLeft, boundaryRight);
                result[i] = currentState;
                updateImage(image, currentState, i);

                if (!animate.isSelected())
                    continue;
                ato.filter(image, rescaled);
                generatedImage.setImage(SwingFXUtils.toFXImage(rescaled, null));
            }

            ato.filter(image, rescaled);
            generatedImage.setImage(SwingFXUtils.toFXImage(rescaled, null));
            controlPanel.setDisable(false);
            simulationData = result;
            saveButton.setDisable(false);
        });
        thread.start();
    }

    public void initialize() {
        generatedImage.setFitHeight(0);
        generatedImage.setFitWidth(0);
        animate.setSelected(true);
        saveButton.setDisable(true);
        optionsBox.setDisable(true);
        optionsBox.setVisible(false);
        BCText.setDisable(true);
    }

    private void produceAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void onOptionsButtonClick() {
        if (optionsBox.isDisabled()) {
            optionsBox.setVisible(true);
            optionsBox.setDisable(false);
            optionsButton.setBlendMode(BlendMode.MULTIPLY);
            iterationCount.setText(cellCount.getText());
        } else {
            optionsBox.setVisible(false);
            optionsBox.setDisable(true);
            optionsButton.setBlendMode(BlendMode.SRC_OVER);
            iterationCount.setText("");
        }
    }

    public void onBCCheckAction() {
        if (BCCheck.isSelected()) {
            BCText.setDisable(false);
            BCText.setText("00");
        } else {
            BCText.setDisable(true);
            BCText.setText("");
        }
    }

    public void onSaveButtonClick() throws IOException {
        String filename = "CA-" + cellCount.getText() + "c-" + ruleNumber.getText() + "r.csv";
        StringBuilder saveText = new StringBuilder();
        for (boolean[] array : simulationData) {
            for (boolean v : array) {
                saveText.append(v ? "1," : "0,");
            }
            saveText.replace(saveText.length() - 1, saveText.length(), "\n");
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(String.valueOf(saveText));
        writer.close();
        saveButton.setDisable(true);
        produceAlert(Alert.AlertType.INFORMATION, "File saved!", "Data saved in \"" + filename + "\" file.", "");
    }

    public void onSimulateButtonClick() {
        String cellCountText = cellCount.getText();
        if (cellCountText.isEmpty()) {
            produceAlert(Alert.AlertType.WARNING, "Invalid number of cells!", "Input number of cells.", "");
            return;
        }
        String ruleNumberText = ruleNumber.getText();
        if (ruleNumberText.isEmpty()) {
            produceAlert(Alert.AlertType.WARNING, "Invalid rule number!", "Input rule number.", "");
            return;
        }
        String iterationsText = iterationCount.getText();
        if (!iterationCount.isDisabled() && iterationsText.isEmpty()) {
            produceAlert(Alert.AlertType.WARNING, "Invalid number of iterations!", "Input number of iterations.", "");
            return;
        }
        int cells;
        int rule;
        int iterations;
        try {
            cells = Integer.parseInt(cellCountText);
            rule = Integer.parseInt(ruleNumberText);
            iterations = cells;
            if (!iterationsText.isEmpty())
                iterations = Integer.parseInt(iterationsText);
        } catch (NumberFormatException exception) {
            if (exception.toString().contains("cells"))
                produceAlert(Alert.AlertType.ERROR, "Invalid number of cells!", "Number of cells must be an integer.", "");
            if (exception.toString().contains("rule"))
                produceAlert(Alert.AlertType.ERROR, "Invalid rule number!", "Rule number must be an integer.", "");
            if (exception.toString().contains("iterations"))
                produceAlert(Alert.AlertType.ERROR, "Invalid number of iterations!", "Number of iterations must be an integer.", "");
            return;
        }
        if (rule < 0 || rule >= 256) {
            produceAlert(Alert.AlertType.ERROR, "Invalid rule number!", "Pick rule number from range 0-255.", "");
            return;
        }
        if (cells < 1) {
            produceAlert(Alert.AlertType.ERROR, "Invalid number of cells!", "Number of cells must be positive.", "");
            return;
        }
        if (iterations < 1) {
            produceAlert(Alert.AlertType.ERROR, "Invalid number of iterations!", "Number of iterations must be positive.", "");
            return;
        }
        if (iterations > maxHeight || cells > maxWidth) {
            produceAlert(Alert.AlertType.INFORMATION, "Small cell size!", "Result image might not be precise.", "Save to file, to get precise data.");
        }

        cellSize = Math.min(maxHeight / iterations, maxWidth / cells);
        if (cellSize == 0)
            cellSize = 1;
        boolean[] initialState = new boolean[cells];
        if (randomCheck.isSelected()) {
            for (int i = 0; i < cells; i++)
                initialState[i] = (((Math.random() * 2.) % 2) >= 1);
        } else {
            initialState[cells / 2] = true;
        }

        simulateAutomaton(initialState, rule, !BCCheck.isSelected(), iterations);
    }
}
