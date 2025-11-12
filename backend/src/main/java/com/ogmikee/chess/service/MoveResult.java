package com.ogmikee.chess.service;

public class MoveResult {
    private boolean correct;
    private String computerMove;
    private boolean lineComplete;

    public MoveResult(boolean correct, String computerMove, boolean lineComplete) {
        this.correct = correct;
        this.computerMove = computerMove;
        this.lineComplete = lineComplete;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getComputerMove() {
        return computerMove;
    }

    public boolean isLineComplete() {
        return lineComplete;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public void setComputerMove(String computerMove) {
        this.computerMove = computerMove;
    }

    public void setLineComplete(boolean lineComplete) {
        this.lineComplete = lineComplete;
    }
}