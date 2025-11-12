package com.ogmikee.chess.service;

import java.util.List;

public class QuizQuestion {
    private String fen;                    // Position in FEN notation
    private List<String> correctMoves;     // All correct moves (SAN)
    private List<String> pathToPosition;   // Moves from start to this position

    public QuizQuestion(String fen, List<String> correctMoves, List<String> pathToPosition) {
        this.correctMoves = correctMoves;
        this.fen = fen;
        this.pathToPosition = pathToPosition;
    }

    public List<String> getCorrectMoves() {
        return correctMoves;
    }

    public List<String> getPathToPosition() {
        return pathToPosition;
    }

    public String getFen() {
        return fen;
    }
}