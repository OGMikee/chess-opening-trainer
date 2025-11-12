package com.ogmikee.chess.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ogmikee.chess.logic.Game;
import com.ogmikee.chess.model.Move;
import com.ogmikee.chess.opening.OpeningNode;
import com.ogmikee.chess.opening.OpeningTree;
import com.ogmikee.chess.opening.SanConverter;

import java.util.ArrayList;
import java.util.List;

public class PlaythroughSession {
    private OpeningTree tree;
    private String gameFen;
    private List<String> movesPlayed;
    private boolean isComplete;

    public PlaythroughSession() {
        this.movesPlayed = new ArrayList<>();
    }

    @JsonCreator
    public PlaythroughSession(
            @JsonProperty("tree") OpeningTree tree,
            @JsonProperty("movesPlayed") List<String> startPath,
            @JsonProperty("gameFen") String gameFen,
            @JsonProperty("complete") boolean isComplete
    ) {
        this.tree = tree;
        this.movesPlayed = new ArrayList<>(startPath);
        this.isComplete = isComplete;
        this.gameFen = gameFen;
    }

    public PlaythroughSession(OpeningTree tree, List<String> startPath) {
        this.tree = tree;
        this.movesPlayed = new ArrayList<>(startPath);
        this.isComplete = false;

        Game game = new Game();
        for (String moveStr : startPath) {
            Move move = SanConverter.sanToMove(moveStr, game);
            if (move != null) {
                game.makeMove(move);
            }
        }
        this.gameFen = game.toFEN();
    }

    public PlaythroughSession(OpeningTree tree) {
        this(tree, new ArrayList<>());
    }

    @JsonIgnore
    public Game getGame() {
        return new Game(gameFen);
    }

    @JsonIgnore
    public OpeningNode getCurrentNode() {
        return tree.findNode(movesPlayed);
    }

    public OpeningTree getTree() {
        return tree;
    }

    public void setTree(OpeningTree tree) {
        this.tree = tree;
    }

    public String getGameFen() {
        return gameFen;
    }

    public void setGameFen(String gameFen) {
        this.gameFen = gameFen;
    }

    public List<String> getMovesPlayed() {
        return new ArrayList<>(movesPlayed);
    }

    public void setMovesPlayed(List<String> movesPlayed) {
        this.movesPlayed = movesPlayed;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void setCurrentNode(OpeningNode node) {
    }

    public void addMovePlayed(String move) {
        this.movesPlayed.add(move);
        Game game = getGame();
        Move m = SanConverter.sanToMove(move, game);
        if (m != null) {
            game.makeMove(m);
            this.gameFen = game.toFEN();
        }
    }
}