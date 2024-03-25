package com.MDLab4;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Nullable;

public class Board {
    double cellSize;
    int height;
    int width;
    Color aliveCellColor;
    Color deadCellColor;
    int currentMaxIteration;
    Cell[][] board;

    public Board(byte[][] stateMatrix, int cellSize, double lineWidth, int boundaryCondition, @Nullable Color aliveCellColor, @Nullable Color deadCellColor) {
        this.cellSize = (double) cellSize - lineWidth;
        this.height = stateMatrix.length;
        this.width = stateMatrix[0].length;
        this.currentMaxIteration = 0;
        this.aliveCellColor = (aliveCellColor != null ? aliveCellColor : Color.BLACK);
        this.deadCellColor = (deadCellColor != null ? deadCellColor : Color.WHITE);
        int boardHeight = stateMatrix.length + 2;
        int boardWidth = stateMatrix[0].length + 2;
        this.board = new Cell[boardHeight][boardWidth];
        double cellOffset = lineWidth / 2;
        for (int y = 1; y < boardHeight - 1; y++)
            for (int x = 1; x < boardWidth - 1; x++)
                board[y][x] = new Cell(x, y, cellSize * (x - 1) + cellOffset, cellSize * (y - 1) + cellOffset, stateMatrix[y - 1][x - 1]);

        int boundaryTop = -1;
        int boundaryBottom = -1;
        int boundaryLeft = -1;
        int boundaryRight = -1;
        Cell cell = null;
        switch (boundaryCondition) {
            case 0: // all 0's
                cell = new Cell((byte) 0);
            case 1: // all 1's
                cell = (cell == null ? new Cell(((byte) 1)) : cell);
                for (int i = 0; i < boardWidth; i++) {
                    board[0][i] = cell;
                    board[boardHeight - 1][i] = cell;
                }
                for (int i = 0; i < boardHeight; i++) {
                    board[i][0] = cell;
                    board[i][boardWidth - 1] = cell;
                }
                break;
            case 2: // periodic
                boundaryTop = boardHeight - 2;
                boundaryBottom = 1;
                boundaryLeft = boardWidth - 2;
                boundaryRight = 1;
            case 3: // reflective
                boundaryTop = (boundaryTop == -1 ? 1 : boundaryTop);
                boundaryBottom = (boundaryBottom == -1 ? boardHeight - 2 : boundaryBottom);
                boundaryLeft = (boundaryLeft == -1 ? 1 : boundaryLeft);
                boundaryRight = (boundaryRight == -1 ? boardWidth - 2 : boundaryRight);
                for (int i = 0; i < boardWidth; i++) {
                    board[0][i] = board[boundaryTop][i];
                    board[boardHeight - 1][i] = board[boundaryBottom][i];
                }
                for (int i = 0; i < boardHeight; i++) {
                    board[i][0] = board[i][boundaryLeft];
                    board[i][boardWidth - 1] = board[i][boundaryRight];
                }
                break;
            case 4: // random
                for (int i = 0; i < boardWidth; i++) {
                    board[0][i] = new Cell((byte) (Math.random() * 2));
                    board[boardHeight - 1][i] = new Cell((byte) (Math.random() * 2));
                }
                for (int i = 0; i < boardHeight; i++) {
                    board[i][0] = new Cell((byte) (Math.random() * 2));
                    board[i][boardWidth - 1] = new Cell((byte) (Math.random() * 2));
                }
                break;
        }
    }

    public void printBoard() {
        for (Cell[] cells : board) {
            for (Cell cell : cells)
                System.out.print(cell.getState(currentMaxIteration) + " ");
            System.out.println();
        }
        System.out.println();
    }

    private void drawCell(GraphicsContext gc, Cell cell, int iteration) {
        switch (cell.getState(iteration)) {
            case 0:
                gc.setFill(deadCellColor);
                break;
            case 1:
                if (aliveCellColor == Color.PINK)
                    gc.setFill(Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
                else
                    gc.setFill(aliveCellColor);
                break;
        }
        gc.fillRect(cell.getCanvasX(), cell.getCanvasY(), cellSize, cellSize);
    }

    public void drawIteration(GraphicsContext gc, int iteration) {
        while (currentMaxIteration < iteration)
            this.nextIteration();
        for (Cell[] cells : board) {
            for (Cell cell : cells) {
                drawCell(gc, cell, iteration);
            }
        }
    }
    public void updateCanvas(GraphicsContext gc, int iteration) {
        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {
                Cell cell = board[i][j];
                if (!cell.isUpdated(iteration)) continue;
                drawCell(gc, cell, iteration);
            }
        }
    }

    int countNeighbours(Cell cell) {
        int x = cell.getBoardX();
        int y = cell.getBoardY();
        int counter = 0;
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) continue;
                counter += board[y + i][x + j].getState(currentMaxIteration);
            }
        return counter;
    }

    public void nextIteration() {
        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {
                Cell cell = board[i][j];
                int neighbours = countNeighbours(cell);
                byte currentState = cell.getState(currentMaxIteration);
                if (neighbours == 3 || (currentState == 1 && neighbours == 2))
                    cell.updateCell((byte) 1);
                else
                    cell.updateCell((byte) 0);
            }
        }
        currentMaxIteration += 1;
    }

    public int getCurrentMaxIteration() {
        return currentMaxIteration;
    }
}
