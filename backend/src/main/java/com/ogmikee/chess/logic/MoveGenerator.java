package com.ogmikee.chess.logic;

import com.ogmikee.chess.model.*;
import java.util.ArrayList;
import java.util.List;


public class MoveGenerator {

    public static List<Move> generateMoves(Board board, Square from, Square enPassantSquare) {
        Piece piece = board.getPiece(from);
        if (piece == null) return new ArrayList<>();
        switch (piece.getType()) {
            case PAWN:
                return generatePawnMoves(board, from, piece, enPassantSquare);
            case KNIGHT:
                return generateKnightMoves(board, from, piece);
            case BISHOP:
                return generateBishopMoves(board, from, piece);
            case ROOK:
                return generateRookMoves(board, from, piece);
            case QUEEN:
                return generateQueenMoves(board, from, piece);
            case KING:
                return generateKingMoves(board, from, piece);
            default:
                return new ArrayList<>();
        }
    }

    private static List<Move> generatePawnMoves(Board board, Square from, Piece piece, Square enPassantSquare) {
        List<Move> moves = new ArrayList<>();
        int file = from.getFile();
        int rank = from.getRank();
        Color color = piece.getColor();
        int direction = (color == Color.WHITE) ? 1 : -1;
        int startRank = (color == Color.WHITE) ? 1 : 6;
        int promotionRank = (color == Color.WHITE) ? 7 : 0;
        try{
            Square oneForward = new Square(file, rank + direction);
            if (board.getPiece(oneForward) == null){
                if (oneForward.getRank() == promotionRank) {
                    addPromotionMoves(moves, from, oneForward, piece);
                } else {
                    moves.add(new Move(from, oneForward, piece));
                }
                if (rank == startRank){
                    Square twoForward = new Square(file, rank + 2 * direction);
                    if (board.getPiece(twoForward) == null){
                        moves.add(new Move(from, twoForward, piece));
                    }
                }
            }
        } catch (IllegalArgumentException e){}
        int [] captureDirs = {-1, 1};
        for (int captureDir : captureDirs){
            try{
                Square diagonalSquare = new Square(file + captureDir, rank + direction);
                Piece target = board.getPiece(diagonalSquare);
                if (target != null && target.getColor() != color){
                    if (diagonalSquare.getRank() == promotionRank) {
                        addPromotionMoves(moves, from, diagonalSquare, piece);
                    } else {
                        moves.add(new Move(from, diagonalSquare, piece));
                    }
                }
            }catch (IllegalArgumentException e){}
        }
        if (enPassantSquare != null){
            int enPassantFile = enPassantSquare.getFile();
            int enPassantRank = enPassantSquare.getRank();
            if (Math.abs(file - enPassantFile) == 1 && rank + direction == enPassantRank){
                moves.add(new Move(from, enPassantSquare, piece, MoveType.EN_PASSANT));
            }
        }

        return moves;
    }

    private static void addPromotionMoves(List<Move> moves, Square from, Square to, Piece piece) {
        moves.add(new Move(from, to, piece, PieceType.QUEEN));
        moves.add(new Move(from, to, piece, PieceType.ROOK));
        moves.add(new Move(from, to, piece, PieceType.BISHOP));
        moves.add(new Move(from, to, piece, PieceType.KNIGHT));
    }

    private static List<Move> generateKnightMoves(Board board, Square from, Piece piece) {
        List<Move> moves = new ArrayList<>();
        int file = from.getFile();
        int rank = from.getRank();
        int[][] offsets = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2},
        };
        for (int[] offset: offsets){
            try{
                Square to = new Square(file + offset[0], rank + offset[1]);
                if (isValidDestination(board, to, piece.getColor())){
                    moves.add(new Move(from, to, piece));
                }
            } catch (IllegalArgumentException e){}
        }
        return moves;
    }

    private static List<Move> generateBishopMoves(Board board, Square from, Piece piece) {
        List<Move> moves = new ArrayList<>();
        int file = from.getFile();
        int rank = from.getRank();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] direction : directions){
            int currentFile = file + direction[0];
            int currentRank = rank + direction[1];

            while (currentFile >= 0 && currentFile <= 7 && currentRank >= 0 && currentRank <= 7){
                try {
                    Square to = new Square(currentFile, currentRank);
                    Piece targetPiece = board.getPiece(to);
                    if (targetPiece == null){
                        moves.add(new Move(from, to, piece));
                    }
                    else if (targetPiece.getColor() != piece.getColor()){
                        moves.add(new Move(from, to, piece));
                        break;
                    }
                    else{
                        break;
                    }
                    currentFile += direction[0];
                    currentRank += direction[1];
                }catch (IllegalArgumentException e){
                    break;
                }
            }
        }
        return moves;
    }

    private static List<Move> generateRookMoves(Board board, Square from, Piece piece) {
        List<Move> moves = new ArrayList<>();
        int file = from.getFile();
        int rank = from.getRank();
        int[][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        for (int[] direction : directions){
            int currentFile = file + direction[0];
            int currentRank = rank + direction[1];

            while (currentFile >= 0 && currentFile <= 7 && currentRank >= 0 && currentRank <= 7){
                try {
                    Square to = new Square(currentFile, currentRank);
                    Piece targetPiece = board.getPiece(to);
                    if (targetPiece == null){
                        moves.add(new Move(from, to, piece));
                    }
                    else if (targetPiece.getColor() != piece.getColor()){
                        moves.add(new Move(from, to, piece));
                        break;
                    }
                    else{
                        break;
                    }
                    currentFile += direction[0];
                    currentRank += direction[1];
                }catch (IllegalArgumentException e){
                    break;
                }
            }
        }
        return moves;
    }

    private static List<Move> generateQueenMoves(Board board, Square from, Piece piece) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(generateRookMoves(board, from, piece));
        moves.addAll(generateBishopMoves(board, from, piece));
        return moves;
    }

    private static List<Move> generateKingMoves(Board board, Square from, Piece piece) {
        List<Move> moves = new ArrayList<>();
        int file = from.getFile();
        int rank = from.getRank();
        int[][] offsets = {
                {1, 0}, {1, 1}, {1, -1},
                {-1, 0}, {-1, 1}, {-1, -1},
                {0, -1}, {0, 1}
        };
        for (int[] offset: offsets){
            try{
                Square to = new Square(file + offset[0], rank + offset[1]);
                if (isValidDestination(board, to, piece.getColor())){
                    moves.add(new Move(from, to, piece));
                }
            } catch (IllegalArgumentException e){}
        }
        return moves;
    }

    private static boolean isValidDestination(Board board, Square square, Color movingPieceColor) {
        Piece piece = board.getPiece(square);
        return piece == null || piece.getColor() != movingPieceColor;
    }
}