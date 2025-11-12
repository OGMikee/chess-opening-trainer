package com.ogmikee.chess.service;

import com.ogmikee.chess.logic.Game;
import com.ogmikee.chess.model.Move;
import com.ogmikee.chess.opening.OpeningNode;
import com.ogmikee.chess.opening.OpeningTree;
import com.ogmikee.chess.opening.SanConverter;

import java.util.ArrayList;
import java.util.List;

public class PlaythroughSession {
    private OpeningTree tree;
    private Game game;
    private OpeningNode currentNode;
    private List<String> movesPlayed;
    private boolean isComplete;

    public PlaythroughSession(OpeningTree tree, List<String> startPath) {
        this.tree = tree;
        this.movesPlayed = new ArrayList<>(startPath);
        this.isComplete = false;
        this.game = new Game();
        for (String moveStr : startPath) {
            Move move = SanConverter.sanToMove(moveStr, game);
            if (move != null) {
                game.makeMove(move);
            }
        }
        this.currentNode = tree.findNode(startPath);
        if (this.currentNode == null) {
            this.currentNode = tree.getRoot();
        }
    }

    public PlaythroughSession(OpeningTree tree) {
        this(tree, new ArrayList<>());
    }

    public Game getGame() {
        return game;
    }

    public OpeningNode getCurrentNode() {
        return currentNode;
    }

    public List<String> getMovesPlayed() {
        return new ArrayList<>(movesPlayed);
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void setCurrentNode(OpeningNode node) {
        this.currentNode = node;
    }

    public void addMovePlayed(String move) {
        this.movesPlayed.add(move);
    }
}