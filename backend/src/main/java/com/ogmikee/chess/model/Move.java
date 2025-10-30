package com.ogmikee.chess.model;

import java.util.Objects;

public class Move {
    private final Square from;
    private final Square to;
    private final Piece piece;
    private final MoveType type;
    private final PieceType promotionPiece;  // Only used for promotions
    public Move(Square from, Square to, Piece piece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.type = MoveType.NORMAL;
        this.promotionPiece = null;
    }

    public Move(Square from, Square to, Piece piece, MoveType type) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.type = type;
        this.promotionPiece = null;
    }

    public Move(Square from, Square to, Piece piece, PieceType promotionPiece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.type = MoveType.PROMOTION;
        this.promotionPiece = promotionPiece;
    }

    public Square getFrom() {
        return this.from;
    }

    public Square getTo() {
        return this.to;
    }

    public Piece getPiece() {
        return piece;
    }

    public MoveType getType() {
        return type;
    }

    public PieceType getPromotionPiece() {
        return promotionPiece;
    }

    public boolean isCapture() {
        return type == MoveType.CAPTURE;
    }

    public String toAlgebraic() {
        return from.toAlgebraic() + "-" + to.toAlgebraic();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof  Move other &&
                type == other.type &&
                from.equals(other.from) &&
                to.equals(other.to) &&
                piece.equals(other.piece) &&
                promotionPiece == other.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, piece, type, promotionPiece);
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }
}