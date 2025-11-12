package com.ogmikee.chess.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MoveResult {
    private boolean correct;
    private String computerMove;
    private boolean lineComplete;

    public MoveResult() {
    }

    @JsonCreator
    public MoveResult(
            @JsonProperty("correct") boolean correct,
            @JsonProperty("computerMove") String computerMove,
            @JsonProperty("lineComplete") boolean lineComplete
    ) {
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