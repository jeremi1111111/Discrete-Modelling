package com.MDLab4;

import java.util.ArrayList;
import java.util.Objects;

public class Cell {
    int boardX;
    int boardY;
    double canvasX;
    double canvasY;
    ArrayList<Byte> stateList;
    ArrayList<Cell> neighboursList;

    public Cell(int boardX, int boardY, double canvasX, double canvasY, Byte initialState) {
        this.boardX = boardX;
        this.boardY = boardY;
        this.canvasX = canvasX;
        this.canvasY = canvasY;
        this.stateList = new ArrayList<Byte>();
        stateList.add(initialState);
        this.neighboursList = new ArrayList<Cell>();
    }

    public Cell(Byte initialState) {
        this.stateList = new ArrayList<Byte>();
        stateList.add(initialState);
    }

    public boolean isUpdated(int iteration) {
        return stateList.size() <= 1
                || !Objects.equals(stateList.get(iteration), stateList.get(iteration - 1));
    }

    public Byte getState(int iteration) {
        if (stateList.size() == 1) return stateList.getFirst();
        return stateList.get(iteration);
    }

    public void addNeighbour(Cell neighbour) {
        neighboursList.add(neighbour);
    }
    public void updateCell(byte value) {
        stateList.add(value);
    }

    public double getCanvasX() {
        return canvasX;
    }

    public double getCanvasY() {
        return canvasY;
    }

    public int getBoardX() { return boardX; }

    public int getBoardY() { return boardY; }
}
