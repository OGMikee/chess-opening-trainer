package com.ogmikee.chess.opening;

import com.ogmikee.chess.logic.Game;
import com.ogmikee.chess.model.*;

import java.util.ArrayList;
import java.util.List;

public class SanConverter {

    public static String moveToSan(Move move, Game game) {
        StringBuilder san = new StringBuilder();
        if (move.getType() == MoveType.CASTLE_KINGSIDE) {
            return "O-O";
        }
        if (move.getType() == MoveType.CASTLE_QUEENSIDE) {
            return "O-O-O";
        }
        Piece piece = move.getPiece();
        boolean isCapture = game.getBoard().getPiece(move.getTo()) != null ||
                move.getType() == MoveType.EN_PASSANT;
        if (piece.getType() != PieceType.PAWN) {
            san.append(getPieceLetter(piece.getType()));
            String disambiguation = getDisambiguation(move, game);
            san.append(disambiguation);
        } else {
            if (isCapture) {
                san.append((char)('a' + move.getFrom().getFile()));
            }
        }
        if (isCapture) {
            san.append('x');
        }
        san.append(move.getTo().toAlgebraic());
        if (move.getType() == MoveType.PROMOTION) {
            san.append('=');
            san.append(getPieceLetter(move.getPromotionPiece()));
        }
        return san.toString();
    }

    private static String getPieceLetter(PieceType type) {
        switch (type){
            case KING -> {return "K";}
            case BISHOP -> {return "B";}
            case KNIGHT -> {return "N";}
            case ROOK -> {return "R";}
            case QUEEN -> {return "Q";}
            case PAWN -> {return "";}
        }
        return "?";
    }

    private static String getDisambiguation(Move move, Game game) {
        List<Move> allMoves = game.getAllLegalMoves();
        List<Move> conflicts = new ArrayList<>();
        for (Move other: allMoves){
            if (other.equals(move)) continue;
            if (other.getPiece().getType() == move.getPiece().getType() &&
                    other.getTo().equals(move.getTo()) &&
                    !other.getFrom().equals(move.getFrom()))
                conflicts.add(other);
        }
        if (conflicts.isEmpty()){
            return "";
        }
        boolean fileUnique = true;
        for (Move conflict : conflicts){
            if (conflict.getFrom().getFile() == move.getFrom().getFile()){
                fileUnique = false;
                break;
            }
        }
        if (fileUnique){
            return String.valueOf((char)('a' + move.getFrom().getFile()));
        }
        boolean rankUnique = true;
        for (Move conflict : conflicts){
            if (conflict.getFrom().getRank() == move.getFrom().getRank()){
                rankUnique = false;
                break;
            }
        }
        if (rankUnique){
            return String.valueOf(move.getFrom().getRank() + 1);
        }
        return move.getFrom().toAlgebraic();
    }

    public static Move sanToMove(String san, Game game) {
        String cleanSan = san.replace("+", "").replace("#", "");
        List<Move> legalMoves = game.getAllLegalMoves();

        for (Move move : legalMoves){
            String currentMove = moveToSan(move, game);
            currentMove = currentMove.replaceAll("[+#]", "");
            if (currentMove.equals(cleanSan)){
                return move;
            }
        }
        return null;
    }
}