package com.ogmikee.chess.logic;

import com.ogmikee.chess.model.*;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Game {
    private Board board;
    private Color currentTurn;
    private boolean whiteKingsideCastle;
    private boolean whiteQueensideCastle;
    private boolean blackKingsideCastle;
    private boolean blackQueensideCastle;
    private Square enPassantSquare;
    private int halfMoveClock;
    private int fullMoveNumber;
    private List<Move> moveHistory;
    private Stack<GameState> stateHistory;

    private static class GameState {
        Board board;
        Color currentTurn;
        boolean whiteKingsideCastle;
        boolean whiteQueensideCastle;
        boolean blackKingsideCastle;
        boolean blackQueensideCastle;
        Square enPassantSquare;
        int halfMoveClock;
        int fullMoveNumber;

        GameState(Game game) {
            this.board = game.board.copy();
            this.currentTurn = game.currentTurn;
            this.whiteKingsideCastle = game.whiteKingsideCastle;
            this.whiteQueensideCastle = game.whiteQueensideCastle;
            this.blackKingsideCastle = game.blackKingsideCastle;
            this.blackQueensideCastle = game.blackQueensideCastle;
            this.enPassantSquare = game.enPassantSquare;
            this.halfMoveClock = game.halfMoveClock;
            this.fullMoveNumber = game.fullMoveNumber;
        }

        void restore(Game game) {
            game.board = this.board.copy();
            game.currentTurn = this.currentTurn;
            game.whiteKingsideCastle = this.whiteKingsideCastle;
            game.whiteQueensideCastle = this.whiteQueensideCastle ;
            game.blackKingsideCastle = this.blackKingsideCastle ;
            game.blackQueensideCastle = this.blackQueensideCastle;
            game.enPassantSquare = this.enPassantSquare;
            game.halfMoveClock = this.halfMoveClock;
            game.fullMoveNumber = this.fullMoveNumber;
        }
    }

    public Game() {
        this.board = new Board();
        this.board.setupInitialPosition();
        this.currentTurn = Color.WHITE;
        this.whiteKingsideCastle = true;
        this.whiteQueensideCastle = true;
        this.blackKingsideCastle = true;
        this.blackQueensideCastle = true;
        this.enPassantSquare = null;
        this.halfMoveClock = 0;
        this.fullMoveNumber = 1;
        this.moveHistory = new ArrayList<>();
        this.stateHistory = new Stack<>();
    }


    public Game(String fen) {
        this();
        fromFEN(fen);
    }

    public boolean makeMove(Move move) {
        if (!isLegalMove(move)){
            return false;
        }
        stateHistory.push(new GameState(this));
        boolean isMoveCapture = board.getPiece(move.getTo()) != null;
        executeMove(move);
        updateCastlingRights(move);
        updateEnPassant(move);
        updateMoveCounters(move, isMoveCapture);
        this.moveHistory.add(move);
        currentTurn = currentTurn.opposite();
        return true;
    }

    public boolean undoMove() {
        if (stateHistory.empty()){
            return false;
        }
        GameState state = stateHistory.pop();
        state.restore(this);
        moveHistory.remove(moveHistory.size() - 1);
        return true;
    }

    public boolean isLegalMove(Move move) {
        List<Move> legalMoves = getLegalMoves(move.getFrom());
        return legalMoves.contains(move);
    }

    public List<Move> getLegalMoves(Square from) {
        Piece piece = board.getPiece(from);
        if (piece == null || piece.getColor() != currentTurn){
            return new ArrayList<>();
        }
        List<Move> moves = MoveGenerator.generateMoves(board, from, enPassantSquare);
        if(piece.getType() == PieceType.KING){
            moves.addAll(generateCastlingMoves(from));
        }
        moves.removeIf(this::doesMoveLeaveKingInCheck);
        return moves;
    }

    public List<Move> getAllLegalMoves() {
        List<Move> allLegalMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Square currentSquare = new Square(i, j);
                Piece piece = board.getPiece(currentSquare);
                if (piece != null && piece.getColor() == currentTurn){
                    allLegalMoves.addAll(getLegalMoves(currentSquare));
                }
            }
        }
        return allLegalMoves;
    }

    public List<Move> getAllLegalMovesForColor(Color color){
        List<Move> allLegalMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Square square = new Square(j, i);
                Piece piece = board.getPiece(square);
                if (piece != null && piece.getColor() == color){
                    allLegalMoves.addAll(MoveGenerator.generateMoves(board, square, enPassantSquare));
                }
            }
        }
        return allLegalMoves;
    }

    public boolean isInCheck() {
        Square kingSquare = null;
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Square square = new Square(file, rank);
                Piece piece = board.getPiece(square);
                if (piece != null && piece.getType() == PieceType.KING && piece.getColor() == currentTurn) {
                    kingSquare = square;
                    break;
                }
            }
            if (kingSquare != null) break;
        }
        List<Move> opponentLegalMoves = getAllLegalMovesForColor(currentTurn.opposite());
        for (Move move: opponentLegalMoves){
            if (move.getTo().equals(kingSquare)){
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return halfMoveClock >= 100 || getAllLegalMoves().isEmpty() || isInsufficientMaterial();
    }

    private boolean isInsufficientMaterial(){
        List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPiece(new Square(j, i));
                if (piece != null && piece.getType() != PieceType.KING) {
                    pieces.add(piece);
                }
            }
        }
        if (pieces.isEmpty()) return true;
        if (pieces.size() == 1){
            PieceType type = pieces.get(0).getType();
            return type == PieceType.BISHOP || type == PieceType.KNIGHT;
        }
        return false;
    }

    public Color getWinner() {
        if (isGameOver()){
            if (isInCheck()){
                return currentTurn.opposite();
            }
        }
        return null;
    }

    private void executeMove(Move move) {
        Square from = move.getFrom();
        Square to = move.getTo();
        Piece piece = move.getPiece();
        switch (move.getType()){
            case NORMAL :;
            case CAPTURE:
                board.setPiece(from, null);
                board.setPiece(to, piece);
                break;
            case CASTLE_KINGSIDE:
                board.setPiece(from, null);
                board.setPiece(to, piece);
                int rank = from.getRank();
                Square rookFrom = new Square(7, rank);
                Square rookTo = new Square(5, rank);
                Piece rook = board.getPiece(rookFrom);
                board.setPiece(rookFrom, null);
                board.setPiece(rookTo, rook);
                break;
            case CASTLE_QUEENSIDE:
                board.setPiece(from, null);
                board.setPiece(to, piece);
                rank = from.getRank();
                rookFrom = new Square(0, rank);
                rookTo = new Square(3, rank);
                rook = board.getPiece(rookFrom);
                board.setPiece(rookFrom, null);
                board.setPiece(rookTo, rook);
                break;
            case EN_PASSANT:
                board.setPiece(from, null);
                board.setPiece(to, piece);
                int capturedPawnnRank = piece.getColor() == Color.WHITE ? to.getRank() - 1 : to.getRank() + 1;
                Square capturedPawnSquare = new Square(to.getFile(), capturedPawnnRank);
                board.setPiece(capturedPawnSquare, null);
                break;
            case PROMOTION:
                board.setPiece(from, null);
                Piece promotedPiece = new Piece(move.getPromotionPiece(), piece.getColor());
                board.setPiece(to, promotedPiece);
                break;
        }
    }

    private void updateCastlingRights(Move move) {
        Color color = move.getPiece().getColor();
        if (move.getPiece().getType() == PieceType.KING){
            if (color == Color.WHITE){
                whiteKingsideCastle = whiteQueensideCastle = false;
            }
            else {
                blackKingsideCastle = blackQueensideCastle = false;
            }
            return;
        }
        Square[] rookSquares = {
                new Square(0, 0),
                new Square(7, 0),
                new Square(0, 7),
                new Square(7, 7),
        };
        for (int i = 0; i < rookSquares.length; i++){
            if (move.getFrom().equals(rookSquares[i]) || move.getTo().equals(rookSquares[i])){
                switch (i){
                    case 0: whiteQueensideCastle = false; break;
                    case 1: whiteKingsideCastle = false; break;
                    case 2: blackQueensideCastle = false; break;
                    case 3: blackKingsideCastle = false; break;
                }
            }
        }
    }

    private void updateEnPassant(Move move) {
        enPassantSquare = null;
        if (move.getPiece().getType() == PieceType.PAWN){
            int rankDiff = Math.abs(move.getTo().getRank() - move.getFrom().getRank());
            if (rankDiff == 2){
                int enPassantRank = (move.getFrom().getRank() + move.getTo().getRank()) / 2;
                enPassantSquare = new Square(move.getTo().getFile(), enPassantRank);
            }
        }
    }

    private void updateMoveCounters(Move move, boolean isCapture) {
        if (move.getPiece().getColor() == Color.BLACK){
            fullMoveNumber += 1;
        }
        if (isCapture || move.getPiece().getType() == PieceType.PAWN){
            halfMoveClock = 0;
        }
        else{
            halfMoveClock += 1;
        }
    }

    public String toFEN() {
        StringBuilder fen = new StringBuilder();

        for (int i = 7; i >= 0; i--) {
            int emptyCount = 0;
            for (int j = 0; j < 8; j++) {
                Piece piece = board.getPiece(new Square(j, i));
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(pieceToFENChar(piece));
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (i > 0) {
                fen.append('/');
            }
        }
        fen.append(' ').append(currentTurn == Color.WHITE ? 'w' : 'b');
        fen.append(' ');
        String castling = "";
        if (whiteKingsideCastle) castling += "K";
        if (whiteQueensideCastle) castling += "Q";
        if (blackKingsideCastle) castling += "k";
        if (blackQueensideCastle) castling += "q";
        fen.append(castling.isEmpty() ? "-" : castling);
        fen.append(' ');
        fen.append(enPassantSquare == null ? "-" : enPassantSquare.toAlgebraic());
        fen.append(' ').append(halfMoveClock);
        fen.append(' ').append(fullMoveNumber);
        return fen.toString();
    }

    private char pieceToFENChar(Piece piece) {
        char c;
        switch (piece.getType()) {
            case PAWN: c = 'p'; break;
            case KNIGHT: c = 'n'; break;
            case BISHOP: c = 'b'; break;
            case ROOK: c = 'r'; break;
            case QUEEN: c = 'q'; break;
            case KING: c = 'k'; break;
            default: c = '?';
        }
        return piece.getColor() == Color.WHITE ? Character.toUpperCase(c) : c;
    }

    public void fromFEN(String fen) {
        String[] parts = fen.split(" ");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid FEN string");
        }
        board.clear();
        String[] ranks = parts[0].split("/");
        for (int i = 0; i < 8; i++) {
            int file = 0;
            String rankStr = ranks[7 - i];
            for (char c : rankStr.toCharArray()) {
                if (Character.isDigit(c)) {
                    file += Character.getNumericValue(c);
                } else {
                    Piece piece = fenCharToPiece(c);
                    board.setPiece(new Square(file, i), piece);
                    file++;
                }
            }
        }
        currentTurn = parts[1].equals("w") ? Color.WHITE : Color.BLACK;
        String castling = parts[2];
        whiteKingsideCastle = castling.contains("K");
        whiteQueensideCastle = castling.contains("Q");
        blackKingsideCastle = castling.contains("k");
        blackQueensideCastle = castling.contains("q");
        enPassantSquare = parts[3].equals("-") ? null : new Square(parts[3]);
        halfMoveClock = Integer.parseInt(parts[4]);
        fullMoveNumber = Integer.parseInt(parts[5]);
        moveHistory.clear();
        stateHistory.clear();
    }

    private Piece fenCharToPiece(char c) {
        Color color = Character.isUpperCase(c) ? Color.WHITE : Color.BLACK;
        PieceType type;
        switch (Character.toLowerCase(c)) {
            case 'p': type = PieceType.PAWN; break;
            case 'n': type = PieceType.KNIGHT; break;
            case 'b': type = PieceType.BISHOP; break;
            case 'r': type = PieceType.ROOK; break;
            case 'q': type = PieceType.QUEEN; break;
            case 'k': type = PieceType.KING; break;
            default: throw new IllegalArgumentException("Invalid piece character: " + c);
        }
        return new Piece(type, color);
    }

    private List<Move> generateCastlingMoves(Square kingSquare) {
        List<Move> castlingMoves = new ArrayList<>();
        Piece king = board.getPiece(kingSquare);
        if (king == null || king.getType() != PieceType.KING || king.getColor() != currentTurn) {
            return castlingMoves;
        }
        if (isInCheck()) {
            return castlingMoves;
        }
        Color color = king.getColor();
        int rank = color == Color.WHITE ? 0 : 7;
        if (canCastleKingside(color)) {
            Square f = new Square(5, rank);
            Square g = new Square(6, rank);
            if (board.isEmpty(f) && board.isEmpty(g)) {
                if (!isSquareAttacked(f, color.opposite())) {
                    if (!isSquareAttacked(g, color.opposite())) {
                        castlingMoves.add(new Move(kingSquare, g, king, MoveType.CASTLE_KINGSIDE));
                    }
                }
            }
        }
        if (canCastleQueenside(color)) {
            Square b = new Square(1, rank);
            Square c = new Square(2, rank);
            Square d = new Square(3, rank);
            if (board.isEmpty(b) && board.isEmpty(c) && board.isEmpty(d)) {
                if (!isSquareAttacked(c, color.opposite()) && !isSquareAttacked(d, color.opposite())) {
                    castlingMoves.add(new Move(kingSquare, c, king, MoveType.CASTLE_QUEENSIDE));
                }
            }
        }
        return castlingMoves;
    }

    private boolean isSquareAttacked(Square square, Color attackerColor) {
        List<Move> attackerMoves = getAllLegalMovesForColor(attackerColor);
        for (Move move : attackerMoves) {
            if (move.getTo().equals(square)) {
                return true;
            }
        }
        return false;
    }

    private boolean doesMoveLeaveKingInCheck(Move move) {
        stateHistory.push(new GameState(this));
        executeMove(move);
        boolean inCheck = isInCheck();
        GameState state = stateHistory.pop();
        state.restore(this);
        return inCheck;
    }


    public Board getBoard() { return board; }
    public Color getCurrentTurn() { return currentTurn; }
    public List<Move> getMoveHistory() { return new ArrayList<>(moveHistory); }
    public Square getEnPassantSquare() { return enPassantSquare; }
    public boolean canCastleKingside(Color color) {
        return color == Color.WHITE ? whiteKingsideCastle : blackKingsideCastle;
    }
    public boolean canCastleQueenside(Color color) {
        return color == Color.WHITE ? whiteQueensideCastle : blackQueensideCastle;
    }
}