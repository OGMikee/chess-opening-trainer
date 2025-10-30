package com.ogmikee.chess.model;

import java.util.Objects;

public class Piece {
    private final PieceType type;
    private final Color color;

    public Piece(PieceType type, Color color) {
        this.type = type;
        this.color = color;
    }

    public PieceType getType() {
        return this.type;
    }

    public Color getColor() {
        return this.color;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof  Piece other &&
                type == other.type &&
                color == other.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    @Override
    public String toString() {
        return color + "_" + type;
    }
}