package com.gol;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GameOfLifeController {
    private static final int CELL_SIZE = 10;
    private int gridWidth = 50;
    private int gridHeight = 50;
    private boolean[][] neighborhoodPattern = { { true, true, true }, { true, true, true }, { true, true, true } };
    private int speed = 300;
    private int maxGeneration = Integer.MAX_VALUE;
    private GameOfLifeModel game;
    private Timeline timeline;
    private boolean isRunning = false;

    @FXML
    private int currentGen = 0;
    @FXML
    private Canvas canvas;
    @FXML
    private TextField rowsInput;
    @FXML
    private TextField colsInput;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField generationLimitInput;
    @FXML
    private TextField speedInput;
    @FXML
    private GridPane neighborhoodGrid;
    @FXML
    private Label generationLabel;

    public void initialize() {
        setupGame();
        setupCellClickHandler();
        setupNeighborhoodGrid();
        scrollPane.setFocusTraversable(false);
        scrollPane.setPannable(false);
    }

    private void setupNeighborhoodGrid() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button cell = new Button();
                cell.setMinSize(30, 30);
                cell.setStyle("-fx-background-color: red; -fx-border-color: gray;");

                int finalRow = row;
                int finalCol = col;

                cell.setOnAction(event -> {
                    if (isRunning)
                        return;

                    neighborhoodPattern[finalRow][finalCol] = !neighborhoodPattern[finalRow][finalCol];
                    cell.setStyle("-fx-background-color: " + (neighborhoodPattern[finalRow][finalCol] ? "red" : "white")
                            + "; -fx-border-color: gray;");
                });

                neighborhoodGrid.add(cell, col, row);
            }
        }
    }

    @FXML
    private void applyGenerationLimit() {
        try {
            int newGenerationLimit = Integer.parseInt(generationLimitInput.getText());

            if (newGenerationLimit > 0){
                maxGeneration = newGenerationLimit;
            }
            resetGame();

        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un valor numérico válido.");
        }
    }

    @FXML
private void applySpeed() {
    try {
        int newSpeed = Integer.parseInt(speedInput.getText());

        if (newSpeed > 0) speed = newSpeed;

        if (timeline != null) {
            resetGame();
            setupTimeline();
        }

    } catch (NumberFormatException e) {
        System.out.println("Por favor, ingrese un valor numérico válido.");
    }
}

    @FXML
    private void applyGridSize() {
        try {
            int newRows = Integer.parseInt(rowsInput.getText());
            int newCols = Integer.parseInt(colsInput.getText());

            if (newRows > 0 && newCols > 0) {
                gridWidth = newCols;
                gridHeight = newRows;
                setupGame();
                isRunning = false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese números válidos.");
        }
    }

    private void setupGame() {
        game = new GameOfLifeModel(gridWidth, gridHeight);
        game.setNeighborhoodPattern(neighborhoodPattern);
        canvas.setWidth(gridWidth * CELL_SIZE);
        canvas.setHeight(gridHeight * CELL_SIZE);

        canvas.setWidth(gridWidth * CELL_SIZE);
        canvas.setHeight(gridHeight * CELL_SIZE);

        scrollPane.setContent(canvas);
        scrollPane.setPannable(false);

        if (timeline != null) {
            timeline.stop();
        }

        currentGen = 0;
        updateGenerationLabel();
        draw();
        setupTimeline();
    }

    private void setupTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> newGeneration()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    @FXML
    private void startGame() {
        currentGen = 0;
        updateGenerationLabel();
        timeline.play();
        isRunning = true;
    }

    @FXML
    private void stopGame() {
        timeline.stop();
        isRunning = false;
    }

    @FXML
    private void resetGame() {
        game.clearGrid();
        currentGen = 0;
        updateGenerationLabel();
        stopGame();
        draw();
    }

    @FXML
    private void randomize() {
        if (isRunning)
            return;
        currentGen = 0;
        game.randomize();
        draw();
    }

    private void newGeneration() {
        if (currentGen >= maxGeneration) {
            stopGame();
            return;
        }
        game.updateGrid();
        currentGen++;
        updateGenerationLabel();
        draw();
    }

    private void updateGenerationLabel() {
        generationLabel.setText("Generacion: " + currentGen);
    }

    private void setupCellClickHandler() {
        canvas.setOnMouseClicked(event -> handleCellClick(event));
    }

    private void handleCellClick(MouseEvent event) {
        if (isRunning)
            return;

        int x = (int) (event.getX() / CELL_SIZE);
        int y = (int) (event.getY() / CELL_SIZE);

        if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
            boolean currentState = game.getCell(x, y);
            game.setCell(x, y, !currentState);
            draw();
        }
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                gc.setFill(game.getCell(x, y) ? Color.BLACK : Color.WHITE);
                gc.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                gc.setStroke(Color.GRAY);
                gc.strokeRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }
}