package com.MDLab5;

import java.util.ArrayList;
import java.util.Objects;

public class Cell {
    final int boardX;
    final int boardY;
    final double canvasX;
    final double canvasY;
    ArrayList<CellState> stateList;
    Board board;
    private static final int NB_SIZE = 1;    //probably unused
    private static final int CACHE_SIZE = 10;
    private int burnTimer = 0;
    private int currIter = 0;

    public Cell(int boardX, int boardY, double canvasX, double canvasY, CellState initialState, Board owner) {
        this.boardX = boardX;
        this.boardY = boardY;
        this.canvasX = canvasX;
        this.canvasY = canvasY;
        this.stateList = new ArrayList<>();
        stateList.add(initialState);
        if (initialState == CellState.FIRE)
            burnTimer = CellState.TREE.burningTime;
        this.board = owner;
    }

    public boolean isUpdated(int last, int next) {
        return !Objects.equals(stateList.get(last), stateList.get(next));
    }

    public void updateCell(CellState state) {
        if (stateList.size() > CACHE_SIZE)
            stateList.removeLast();
        if (burnTimer > 0)
            burnTimer--;
        else if (state == CellState.FIRE)
            burnTimer = stateList.getFirst().burningTime;
        else if (state == CellState.WATER && this.getState(0) != CellState.WATER)
            burnTimer = state.burningTime;
        stateList.addFirst(state);
        currIter++;
    }

    private double countBurningNbs() {
        double counter = 0;
        for (int i = 0, y = boardY - board.nbRadius; i < board.nbSize; i++, y++) {
            for (int j = 0, x = boardX -board.nbRadius; j < board.nbSize; j++, x++) {
                Cell other = board.getCell(y, x);
                if (other == null) continue;
                CellState otherState = other.getState(other.currIter - this.currIter);
                if (otherState != CellState.FIRE) continue;
                counter += board.windWeights[i][j];
            }
        }
        return counter;
    }

    public double getBurnChance() {
        double chance = stateList.getFirst().burningChance;
        if (chance == 0)
            return 0;
        double nbCount = countBurningNbs();
        return chance * nbCount;
    }

    public CellState getState(int iter) {
        return stateList.get(iter);
    }

    public double getCanvasX() {
        return canvasX;
    }

    public double getCanvasY() {
        return canvasY;
    }

    public int getBoardX() {
        return boardX;
    }

    public int getBoardY() {
        return boardY;
    }

    public int getBurnTimer() {
        return burnTimer;
    }
}
